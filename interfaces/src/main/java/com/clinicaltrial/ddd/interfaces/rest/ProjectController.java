package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.interfaces.dto.ApiResponse;
import com.clinicaltrial.ddd.interfaces.dto.IdResponse;
import com.clinicaltrial.ddd.interfaces.dto.PageResult;
import com.clinicaltrial.ddd.interfaces.dto.request.CreateProjectRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.CrfBindingRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.PersonnelRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.StageRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.VisitPlanRequest;
import com.clinicaltrial.ddd.interfaces.dto.response.PersonnelOptionsResponse;
import com.clinicaltrial.ddd.interfaces.dto.response.ProjectDetailResponse;
import com.clinicaltrial.ddd.interfaces.dto.response.ProjectSummary;
import com.clinicaltrial.ddd.trial.application.command.AddStageCommand;
import com.clinicaltrial.ddd.trial.application.command.AssignSitePersonnelCommand;
import com.clinicaltrial.ddd.trial.application.command.BindCrfToStageCommand;
import com.clinicaltrial.ddd.trial.application.command.ConfigureVisitPlanCommand;
import com.clinicaltrial.ddd.trial.application.command.CreateProjectCommand;
import com.clinicaltrial.ddd.trial.application.service.CrfBindingApplicationService;
import com.clinicaltrial.ddd.trial.application.service.ProjectApplicationService;
import com.clinicaltrial.ddd.trial.application.service.SitePersonnelApplicationService;
import com.clinicaltrial.ddd.trial.application.service.StageApplicationService;
import com.clinicaltrial.ddd.trial.application.service.VisitPlanApplicationService;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
import com.clinicaltrial.ddd.trial.domain.model.entity.Stage;
import com.clinicaltrial.ddd.trial.domain.model.entity.StageCrfBinding;
import com.clinicaltrial.ddd.trial.domain.model.entity.VisitPlan;
import com.clinicaltrial.ddd.trial.domain.model.entity.SitePersonnel;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.BaselineInterval;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SiteRole;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageRepeatType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.WindowPeriod;
import com.clinicaltrial.ddd.trial.domain.repository.ProjectRepository;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.repository.SubjectRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for BC1: Trial Configuration (研究方案配置).
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectApplicationService projectAppService;
    private final StageApplicationService stageAppService;
    private final VisitPlanApplicationService visitPlanAppService;
    private final CrfBindingApplicationService crfBindingAppService;
    private final SitePersonnelApplicationService sitePersonnelAppService;
    private final SubjectRepository subjectRepository;

    public ProjectController(ProjectRepository projectRepository,
                             ProjectApplicationService projectAppService,
                             StageApplicationService stageAppService,
                             VisitPlanApplicationService visitPlanAppService,
                             CrfBindingApplicationService crfBindingAppService,
                             SitePersonnelApplicationService sitePersonnelAppService,
                             SubjectRepository subjectRepository) {
        this.projectRepository = projectRepository;
        this.projectAppService = projectAppService;
        this.stageAppService = stageAppService;
        this.visitPlanAppService = visitPlanAppService;
        this.crfBindingAppService = crfBindingAppService;
        this.sitePersonnelAppService = sitePersonnelAppService;
        this.subjectRepository = subjectRepository;
    }

    /** 分页查询项目列表. */
    @GetMapping
    public ApiResponse<PageResult<ProjectSummary>> listProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {

        List<Project> projects;
        if (status != null) {
            ProjectStatus ps = ProjectStatus.valueOf(status);
            projects = projectRepository.findByStatus(ps);
        } else {
            projects = projectRepository.findAll(page, size);
        }

        if (type != null) {
            ProjectType pt = ProjectType.valueOf(type);
            projects = projects.stream()
                    .filter(p -> p.getType() == pt)
                    .collect(Collectors.toList());
        }

        // In-memory pagination (since repositories return flat lists)
        int start = page * size;
        int end = Math.min(start + size, projects.size());
        List<Project> pageContent = start < projects.size()
                ? projects.subList(start, end)
                : new ArrayList<>();

        List<ProjectSummary> summaries = pageContent.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        return ApiResponse.success(new PageResult<>(summaries, page, size, projects.size()));
    }

    /** 获取项目详情（含阶段、访视计划、CRF绑定、人员）. */
    @GetMapping("/{id}")
    public ApiResponse<ProjectDetailResponse> getProject(@PathVariable Long id) {
        Project project = projectRepository.getById(new ProjectId(id));
        return ApiResponse.success(toDetailResponse(project));
    }

    /** 项目人员分配下拉选项. */
    @GetMapping("/{id}/personnel-options")
    public ApiResponse<PersonnelOptionsResponse> getPersonnelOptions(@PathVariable Long id) {
        ProjectId projectId = new ProjectId(id);
        com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId subjectProjectId =
                new com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId(id);
        Project project = projectRepository.getById(projectId);
        List<Subject> subjects = subjectRepository.findByProjectId(subjectProjectId);

        Map<Long, PersonnelOptionsResponse.OptionVo> users = new LinkedHashMap<>();
        Map<Long, PersonnelOptionsResponse.OptionVo> sites = new LinkedHashMap<>();
        addDefaultPersonnelOptions(users, sites);

        for (SitePersonnel sp : project.getSitePersonnel()) {
            addOption(users, sp.getUserId(), "用户 " + sp.getUserId());
            addOption(sites, sp.getSiteId(), "中心 " + sp.getSiteId());
        }
        for (Subject subject : subjects) {
            addOption(users, subject.getUserId(), "用户 " + subject.getUserId());
            addOption(sites, subject.getSiteId(), "中心 " + subject.getSiteId());
        }

        PersonnelOptionsResponse response = new PersonnelOptionsResponse();
        response.setUsers(new ArrayList<>(users.values()));
        response.setSites(new ArrayList<>(sites.values()));
        return ApiResponse.success(response);
    }

    /** 创建项目. */
    @PostMapping
    public ApiResponse<IdResponse> createProject(@RequestBody CreateProjectRequest req) {
        ProjectType type = req.getType() != null ? ProjectType.valueOf(req.getType()) : null;
        String userId = req.getCreateUserId() != null ? req.getCreateUserId() : "system";
        CreateProjectCommand cmd = new CreateProjectCommand(
                req.getTitle(), type, req.getAbbreviation(), req.getPrefix(),
                req.getOpenScreen() != null && req.getOpenScreen(),
                req.getExpectedSubjectSize(), req.getClinicalNumber(),
                req.getRegistrationNo(), req.getExpectStartAt(), req.getExpectEndAt(),
                req.getPurpose(), userId);
        Project project = projectAppService.createProject(cmd);
        return ApiResponse.success(new IdResponse(project.getId().getValue()));
    }

    /** 激活项目. */
    @PostMapping("/{id}/activate")
    public ApiResponse<IdResponse> activateProject(@PathVariable Long id) {
        projectAppService.activateProject(new ProjectId(id));
        return ApiResponse.success(new IdResponse(id));
    }

    /** 关闭项目. */
    @PostMapping("/{id}/close")
    public ApiResponse<IdResponse> closeProject(@PathVariable Long id) {
        projectAppService.closeProject(new ProjectId(id));
        return ApiResponse.success(new IdResponse(id));
    }

    /** 添加阶段. */
    @PostMapping("/{id}/stages")
    public ApiResponse<IdResponse> addStage(@PathVariable Long id, @RequestBody StageRequest req) {
        StageRepeatType repeatType = req.getRepeatType() != null
                ? StageRepeatType.valueOf(req.getRepeatType()) : null;
        AddStageCommand cmd = new AddStageCommand(
                new ProjectId(id), req.getName(), repeatType,
                req.getAutoAdd() != null && req.getAutoAdd(),
                req.getBaselineDays() != null ? req.getBaselineDays().longValue() : null,
                req.getBaselineDirection(),
                req.getBeforeDays() != null ? req.getBeforeDays().longValue() : null,
                req.getAfterDays() != null ? req.getAfterDays().longValue() : null);
        Project project = stageAppService.addStage(cmd);
        return ApiResponse.success(new IdResponse(project.getId().getValue()));
    }

    /** 配置随访计划. */
    @PostMapping("/{id}/visit-plans")
    public ApiResponse<IdResponse> configureVisitPlan(@PathVariable Long id,
                                                       @RequestBody VisitPlanRequest req) {
        ConfigureVisitPlanCommand cmd = new ConfigureVisitPlanCommand(
                new ProjectId(id), req.getName(),
                new StageId(req.getSourceStageId()),
                new StageId(req.getTargetStageId()),
                req.getBaselineDays() != null ? req.getBaselineDays().longValue() : null,
                req.getBaselineDirection(),
                req.getBeforeDays() != null ? req.getBeforeDays().longValue() : null,
                req.getAfterDays() != null ? req.getAfterDays().longValue() : null,
                req.getCrfComponentId());
        Project project = visitPlanAppService.configureVisitPlan(cmd);
        return ApiResponse.success(new IdResponse(project.getId().getValue()));
    }

    /** 绑定CRF到阶段. */
    @PostMapping("/{id}/crf-bindings")
    public ApiResponse<IdResponse> bindCrf(@PathVariable Long id,
                                            @RequestBody CrfBindingRequest req) {
        BindCrfToStageCommand cmd = new BindCrfToStageCommand(
                new ProjectId(id), new StageId(req.getStageId()),
                new CrfTemplateId(req.getCrfId()),
                req.getCrfVersionId() != null ? new CrfVersionId(req.getCrfVersionId()) : null,
                req.getUserInputEnabled() != null && req.getUserInputEnabled());
        Project project = crfBindingAppService.bindCrf(cmd);
        return ApiResponse.success(new IdResponse(project.getId().getValue()));
    }

    /** 分配人员. */
    @PostMapping("/{id}/personnel")
    public ApiResponse<IdResponse> assignPersonnel(@PathVariable Long id,
                                                    @RequestBody PersonnelRequest req) {
        SiteRole role = req.getRole() != null ? SiteRole.valueOf(req.getRole()) : null;
        AssignSitePersonnelCommand cmd = new AssignSitePersonnelCommand(
                new ProjectId(id), req.getUserId(), req.getSiteId(), role);
        Project project = sitePersonnelAppService.assignPersonnel(cmd);
        return ApiResponse.success(new IdResponse(project.getId().getValue()));
    }

    // ========== 转换方法 ==========

    private ProjectSummary toSummary(Project p) {
        ProjectSummary s = new ProjectSummary();
        s.setId(p.getId().getValue());
        s.setTitle(p.getTitle());
        s.setType(p.getType() != null ? p.getType().name() : null);
        s.setStatus(p.getStatus() != null ? p.getStatus().name() : null);
        s.setAbbreviation(p.getAbbreviation());
        s.setExpectedSubjectSize(p.getExpectedSubjectSize());
        s.setGmtCreate(p.getGmtCreate());
        return s;
    }

    private ProjectDetailResponse toDetailResponse(Project p) {
        ProjectDetailResponse r = new ProjectDetailResponse();
        r.setId(p.getId().getValue());
        r.setTitle(p.getTitle());
        r.setType(p.getType() != null ? p.getType().name() : null);
        r.setStatus(p.getStatus() != null ? p.getStatus().name() : null);
        r.setAbbreviation(p.getAbbreviation());
        r.setPrefix(p.getPrefix());
        r.setOpenScreen(p.isOpenScreen());
        r.setExpectedSubjectSize(p.getExpectedSubjectSize());
        r.setClinicalNumber(p.getClinicalNumber());
        r.setRegistrationNo(p.getRegistrationNo());
        r.setExpectStartAt(p.getExpectStartAt());
        r.setExpectEndAt(p.getExpectEndAt());
        r.setSiteModel(p.getSiteModel());
        r.setEnableQuestion(p.isEnableQuestion());
        r.setEnableRandomGroup(p.isEnableRandomGroup());
        r.setEnableBlind(p.isEnableBlind());
        r.setPurpose(p.getPurpose());
        r.setRemarks(p.getRemarks());
        r.setCreateUserId(p.getCreateUserId());
        r.setGmtCreate(p.getGmtCreate());
        r.setGmtModified(p.getGmtModified());

        r.setStages(p.getStages().stream().map(this::toStageVo).collect(Collectors.toList()));
        r.setVisitPlans(p.getVisitPlans().stream().map(this::toVisitPlanVo).collect(Collectors.toList()));
        r.setCrfBindings(p.getCrfBindings().stream().map(this::toCrfBindingVo).collect(Collectors.toList()));
        r.setSitePersonnel(p.getSitePersonnel().stream().map(this::toSitePersonnelVo).collect(Collectors.toList()));
        return r;
    }

    private ProjectDetailResponse.StageVo toStageVo(Stage s) {
        ProjectDetailResponse.StageVo vo = new ProjectDetailResponse.StageVo();
        vo.setId(s.getId().getValue());
        vo.setName(s.getName());
        vo.setRepeatType(s.getRepeatType() != null ? s.getRepeatType().name() : null);
        vo.setAutoAdd(s.isAutoAdd());
        BaselineInterval bi = s.getBaselineInterval();
        if (bi != null) {
            vo.setBaselineDays(bi.getInterval() != null ? bi.getInterval().intValue() : null);
            vo.setBaselineDirection(bi.getUnit());
        }
        WindowPeriod wp = s.getWindowPeriod();
        if (wp != null) {
            vo.setBeforeDays(wp.getBeforeDays() != null ? wp.getBeforeDays().intValue() : null);
            vo.setAfterDays(wp.getAfterDays() != null ? wp.getAfterDays().intValue() : null);
        }
        vo.setTaskFlag(s.getTaskFlag());
        return vo;
    }

    private ProjectDetailResponse.VisitPlanVo toVisitPlanVo(VisitPlan vp) {
        ProjectDetailResponse.VisitPlanVo vo = new ProjectDetailResponse.VisitPlanVo();
        vo.setId(vp.getId().getValue());
        vo.setName(vp.getName());
        vo.setSourceStageId(vp.getSourceStageId() != null ? vp.getSourceStageId().getValue() : null);
        vo.setTargetStageId(vp.getTargetStageId() != null ? vp.getTargetStageId().getValue() : null);
        BaselineInterval bi = vp.getBaselineInterval();
        if (bi != null) {
            vo.setBaselineDays(bi.getInterval() != null ? bi.getInterval().intValue() : null);
            vo.setBaselineDirection(bi.getUnit());
        }
        WindowPeriod wp = vp.getWindowPeriod();
        if (wp != null) {
            vo.setBeforeDays(wp.getBeforeDays() != null ? wp.getBeforeDays().intValue() : null);
            vo.setAfterDays(wp.getAfterDays() != null ? wp.getAfterDays().intValue() : null);
        }
        vo.setCrfComponentId(vp.getCrfComponentId());
        return vo;
    }

    private ProjectDetailResponse.CrfBindingVo toCrfBindingVo(StageCrfBinding b) {
        ProjectDetailResponse.CrfBindingVo vo = new ProjectDetailResponse.CrfBindingVo();
        vo.setId(b.getId().getValue());
        vo.setStageId(b.getStageId() != null ? b.getStageId().getValue() : null);
        vo.setCrfId(b.getCrfId() != null ? b.getCrfId().getValue() : null);
        vo.setCrfVersionId(b.getCrfVersionId() != null ? b.getCrfVersionId().getValue() : null);
        vo.setUserInputEnabled(b.isUserInputEnabled());
        return vo;
    }

    private ProjectDetailResponse.SitePersonnelVo toSitePersonnelVo(SitePersonnel sp) {
        ProjectDetailResponse.SitePersonnelVo vo = new ProjectDetailResponse.SitePersonnelVo();
        vo.setId(sp.getId().getValue());
        vo.setUserId(sp.getUserId());
        vo.setSiteId(sp.getSiteId());
        vo.setRole(sp.getRole() != null ? sp.getRole().name() : null);
        return vo;
    }

    private void addDefaultPersonnelOptions(Map<Long, PersonnelOptionsResponse.OptionVo> users,
                                            Map<Long, PersonnelOptionsResponse.OptionVo> sites) {
        addOption(users, 1L, "系统用户 1");
        addOption(users, 100L, "研究者 100");
        addOption(users, 200L, "数据管理员 200");
        addOption(sites, 1L, "默认中心 1");
        addOption(sites, 100L, "研究中心 100");
        addOption(sites, 200L, "研究中心 200");
    }

    private void addOption(Map<Long, PersonnelOptionsResponse.OptionVo> options,
                           Long id,
                           String label) {
        if (id == null || options.containsKey(id)) {
            return;
        }
        options.put(id, new PersonnelOptionsResponse.OptionVo(id, label));
    }
}
