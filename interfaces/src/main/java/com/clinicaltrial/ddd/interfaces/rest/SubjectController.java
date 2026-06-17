package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.interfaces.dto.ApiResponse;
import com.clinicaltrial.ddd.interfaces.dto.IdResponse;
import com.clinicaltrial.ddd.interfaces.dto.request.ChangeSubjectStatusRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.DirectEnrollRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.EnrollSubjectRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.ScreenSubjectRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.WithdrawRequest;
import com.clinicaltrial.ddd.interfaces.dto.response.SubjectDetailResponse;
import com.clinicaltrial.ddd.interfaces.dto.response.SubjectSummary;
import com.clinicaltrial.ddd.subject.application.command.ChangeSubjectStatusCommand;
import com.clinicaltrial.ddd.subject.application.command.EnrollSubjectCommand;
import com.clinicaltrial.ddd.subject.application.command.ScreenSubjectCommand;
import com.clinicaltrial.ddd.subject.application.command.WithdrawSubjectCommand;
import com.clinicaltrial.ddd.subject.application.service.SubjectApplicationService;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;
import com.clinicaltrial.ddd.subject.domain.repository.SubjectRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for BC2: Subject Management (受试者管理).
 */
@RestController
@RequestMapping
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final SubjectApplicationService subjectAppService;

    public SubjectController(SubjectRepository subjectRepository,
                             SubjectApplicationService subjectAppService) {
        this.subjectRepository = subjectRepository;
        this.subjectAppService = subjectAppService;
    }

    /** 项目下受试者列表. */
    @GetMapping("/api/projects/{projectId}/subjects")
    public ApiResponse<List<SubjectSummary>> listSubjectsByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) String status) {
        ProjectId pid = new ProjectId(projectId);
        List<Subject> subjects = subjectRepository.findByProjectId(pid);

        if (status != null) {
            SubjectStatus ss = SubjectStatus.valueOf(status);
            subjects = subjects.stream()
                    .filter(s -> s.getStatus() == ss)
                    .collect(Collectors.toList());
        }

        List<SubjectSummary> summaries = subjects.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
        return ApiResponse.success(summaries);
    }

    /** 受试者详情. */
    @GetMapping("/api/subjects/{id}")
    public ApiResponse<SubjectDetailResponse> getSubject(@PathVariable Long id) {
        Subject subject = subjectRepository.getById(new SubjectId(id));
        return ApiResponse.success(toDetailResponse(subject));
    }

    /** 筛选受试者. */
    @PostMapping("/api/subjects/screen")
    public ApiResponse<IdResponse> screenSubject(@RequestBody ScreenSubjectRequest req) {
        ScreenSubjectCommand cmd = new ScreenSubjectCommand(
                new ProjectId(req.getProjectId()), req.getSiteId(),
                req.getScreeningDate(),
                ScreeningInfo.ScreeningResult.valueOf(req.getScreeningResult()),
                req.getRemarks(), req.getUserId(), req.getBlh(), req.getSyxh(),
                req.getName(), req.getGender(), req.getAge());
        Subject subject = subjectAppService.screenSubject(cmd);
        return ApiResponse.success(new IdResponse(subject.getId().getValue()));
    }

    /** 入组（已筛选受试者）. */
    @PostMapping("/api/subjects/{id}/enroll")
    public ApiResponse<IdResponse> enrollSubject(@PathVariable Long id,
                                                  @RequestBody EnrollSubjectRequest req) {
        if (req.getProjectId() == null) {
            throw new IllegalArgumentException("projectId must not be null");
        }
        EnrollSubjectCommand cmd = new EnrollSubjectCommand(
                new SubjectId(id), new ProjectId(req.getProjectId()),
                req.getSiteId(), req.getUserId(), req.getBlh(), req.getSyxh(),
                req.getName(), req.getGender(), req.getAge(),
                req.getGroupSubsetIds() != null ? req.getGroupSubsetIds()
                        : Collections.<String>emptyList());
        Subject subject = subjectAppService.enrollSubject(cmd);
        return ApiResponse.success(new IdResponse(subject.getId().getValue()));
    }

    /** 直接入组（跳过筛选）. */
    @PostMapping("/api/subjects/enroll")
    public ApiResponse<IdResponse> directEnroll(@RequestBody DirectEnrollRequest req) {
        EnrollSubjectCommand cmd = new EnrollSubjectCommand(
                new ProjectId(req.getProjectId()), req.getSiteId(), req.getUserId(),
                req.getBlh(), req.getSyxh(),
                req.getName(), req.getGender(), req.getAge(),
                req.getGroupSubsetIds() != null ? req.getGroupSubsetIds()
                        : Collections.<String>emptyList());
        Subject subject = subjectAppService.enrollSubject(cmd);
        return ApiResponse.success(new IdResponse(subject.getId().getValue()));
    }

    /** 脱落. */
    @PostMapping("/api/subjects/{id}/withdraw")
    public ApiResponse<IdResponse> withdrawSubject(@PathVariable Long id,
                                                    @RequestBody WithdrawRequest req) {
        WithdrawSubjectCommand cmd = new WithdrawSubjectCommand(
                new SubjectId(id), req.getReasonCode(), req.getReasonDescription());
        Subject subject = subjectAppService.withdrawSubject(cmd);
        return ApiResponse.success(new IdResponse(subject.getId().getValue()));
    }

    /** 修改状态. */
    @PutMapping("/api/subjects/{id}/status")
    public ApiResponse<IdResponse> changeStatus(@PathVariable Long id,
                                                 @RequestBody ChangeSubjectStatusRequest req) {
        if (req.getNewStatus() == null) {
            throw new IllegalArgumentException("newStatus must not be null");
        }
        ChangeSubjectStatusCommand cmd = new ChangeSubjectStatusCommand(
                new SubjectId(id), SubjectStatus.valueOf(req.getNewStatus()), req.getReason());
        Subject subject = subjectAppService.changeStatus(cmd);
        return ApiResponse.success(new IdResponse(subject.getId().getValue()));
    }

    // ========== 转换方法 ==========

    private SubjectSummary toSummary(Subject s) {
        SubjectSummary sum = new SubjectSummary();
        sum.setId(s.getId().getValue());
        sum.setProjectId(s.getProjectId() != null ? s.getProjectId().getValue() : null);
        sum.setCode(s.getCode() != null ? s.getCode().getFullCode() : null);
        sum.setStatus(s.getStatus() != null ? s.getStatus().name() : null);
        sum.setBlh(s.getBlh());
        sum.setSyxh(s.getSyxh());
        sum.setName(s.getName());
        sum.setGender(s.getGender());
        sum.setAge(s.getAge());
        return sum;
    }

    private SubjectDetailResponse toDetailResponse(Subject s) {
        SubjectDetailResponse r = new SubjectDetailResponse();
        r.setId(s.getId().getValue());
        r.setProjectId(s.getProjectId() != null ? s.getProjectId().getValue() : null);
        r.setCode(s.getCode() != null ? s.getCode().getFullCode() : null);
        r.setStatus(s.getStatus() != null ? s.getStatus().name() : null);
        r.setUserId(s.getUserId());
        r.setSiteId(s.getSiteId());
        r.setBlh(s.getBlh());
        r.setSyxh(s.getSyxh());
        r.setName(s.getName());
        r.setGender(s.getGender());
        r.setAge(s.getAge());
        r.setGroupSubsetIds(s.getGroupSubsetIds());
        r.setRemarks(s.getRemarks());
        r.setTrackDownId(s.getTrackDownId());
        r.setSupervisorId(s.getSupervisorId());

        // Screening info
        ScreeningInfo si = s.getScreeningInfo();
        if (si != null) {
            SubjectDetailResponse.ScreeningInfoVo siVo =
                    new SubjectDetailResponse.ScreeningInfoVo();
            siVo.setScreeningDate(si.getScreeningDate());
            siVo.setScreeningResult(si.getScreeningResult() != null
                    ? si.getScreeningResult().name() : null);
            siVo.setRemarks(si.getRemarks());
            r.setScreeningInfo(siVo);
        }

        // Fall-off reason
        if (s.getFallOffReason() != null) {
            SubjectDetailResponse.SubjectFallOffVo foVo =
                    new SubjectDetailResponse.SubjectFallOffVo();
            foVo.setReasonCode(s.getFallOffReason().getReasonCode());
            foVo.setReasonDescription(s.getFallOffReason().getReasonDescription());
            foVo.setFallOffDate(s.getFallOffReason().getFallOffDate());
            r.setFallOffReason(foVo);
        }

        return r;
    }
}
