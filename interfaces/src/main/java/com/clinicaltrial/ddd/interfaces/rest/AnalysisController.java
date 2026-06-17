package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.interfaces.dto.ApiResponse;
import com.clinicaltrial.ddd.interfaces.dto.IdResponse;
import com.clinicaltrial.ddd.interfaces.dto.request.CreateAnalysisProjectRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.ExecuteAnalysisRequest;
import com.clinicaltrial.ddd.interfaces.dto.response.AnalysisProjectResponse;
import com.clinicaltrial.ddd.statistics.application.command.ExecuteAnalysisCommand;
import com.clinicaltrial.ddd.statistics.application.service.AnalysisExecutionApplicationService;
import com.clinicaltrial.ddd.statistics.application.service.AnalysisProjectApplicationService;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisConfig;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisResult;
import com.clinicaltrial.ddd.statistics.domain.model.entity.VariableDefinition;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisConfigId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.repository.AnalysisProjectRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for BC6: Statistical Analysis (统计分析).
 */
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisProjectRepository analysisProjectRepository;
    private final AnalysisProjectApplicationService analysisProjectAppService;
    private final AnalysisExecutionApplicationService analysisExecAppService;

    public AnalysisController(AnalysisProjectRepository analysisProjectRepository,
                              AnalysisProjectApplicationService analysisProjectAppService,
                              AnalysisExecutionApplicationService analysisExecAppService) {
        this.analysisProjectRepository = analysisProjectRepository;
        this.analysisProjectAppService = analysisProjectAppService;
        this.analysisExecAppService = analysisExecAppService;
    }

    /** 分析项目列表. */
    @GetMapping("/projects")
    public ApiResponse<List<AnalysisProjectResponse>> listAnalysisProjects() {
        List<AnalysisProject> projects = analysisProjectRepository.findAll();
        List<AnalysisProjectResponse> result = projects.stream()
                .map(this::toAnalysisProjectResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(result);
    }

    /** 分析项目详情. */
    @GetMapping("/projects/{id}")
    public ApiResponse<AnalysisProjectResponse> getAnalysisProject(@PathVariable Long id) {
        AnalysisProject project = analysisProjectRepository.getById(new AnalysisProjectId(id));
        return ApiResponse.success(toAnalysisProjectResponse(project));
    }

    /** 创建分析项目. */
    @PostMapping("/projects")
    public ApiResponse<IdResponse> createAnalysisProject(@RequestBody CreateAnalysisProjectRequest req) {
        AnalysisProject project = analysisProjectAppService.createProject(req.getName(), req.getDescription());
        return ApiResponse.success(new IdResponse(project.getId().getValue()));
    }

    /** 执行分析. */
    @PostMapping("/projects/{id}/execute")
    public ApiResponse<IdResponse> executeAnalysis(@PathVariable Long id,
                                                    @RequestBody ExecuteAnalysisRequest req) {
        ExecuteAnalysisCommand cmd = new ExecuteAnalysisCommand(
                new AnalysisProjectId(id), new AnalysisConfigId(req.getConfigId()));
        AnalysisProject project = analysisExecAppService.executeAnalysis(cmd);
        return ApiResponse.success(new IdResponse(project.getId().getValue()));
    }

    // ========== 转换方法 ==========

    private AnalysisProjectResponse toAnalysisProjectResponse(AnalysisProject p) {
        AnalysisProjectResponse r = new AnalysisProjectResponse();
        r.setId(p.getId().getValue());
        r.setName(p.getName());
        r.setDescription(p.getDescription());

        r.setVariables(p.getVariables().stream()
                .map(this::toVariableVo)
                .collect(Collectors.toList()));

        r.setAnalysisConfigs(p.getAnalysisConfigs().stream()
                .map(this::toConfigVo)
                .collect(Collectors.toList()));

        r.setResults(p.getResults().stream()
                .map(this::toResultVo)
                .collect(Collectors.toList()));

        return r;
    }

    private AnalysisProjectResponse.VariableVo toVariableVo(VariableDefinition v) {
        AnalysisProjectResponse.VariableVo vo = new AnalysisProjectResponse.VariableVo();
        vo.setId(v.getId().getValue());
        vo.setName(v.getName());
        vo.setLabel(v.getLabel());
        vo.setVariableType(v.getVariableType() != null ? v.getVariableType().name() : null);
        vo.setSourceField(v.getSourceField());
        vo.setDerived(v.isDerived());
        return vo;
    }

    private AnalysisProjectResponse.AnalysisConfigVo toConfigVo(AnalysisConfig c) {
        AnalysisProjectResponse.AnalysisConfigVo vo = new AnalysisProjectResponse.AnalysisConfigVo();
        vo.setId(c.getId().getValue());
        vo.setName(c.getName());
        vo.setAlgorithmType(c.getAlgorithmType() != null ? c.getAlgorithmType().name() : null);
        vo.setDependentVariable(c.getDependentVariable());
        vo.setIndependentVariables(c.getIndependentVariables());
        vo.setStatus(c.getStatus() != null ? c.getStatus().name() : null);
        return vo;
    }

    private AnalysisProjectResponse.AnalysisResultVo toResultVo(AnalysisResult r) {
        AnalysisProjectResponse.AnalysisResultVo vo = new AnalysisProjectResponse.AnalysisResultVo();
        vo.setId(r.getId().getValue());
        vo.setName(r.getName());
        vo.setMethod(r.getMethod());
        vo.setData(r.getData());
        vo.setResultSummary(r.getResultSummary());
        vo.setIsFavorite(r.isFavorite());
        vo.setCreateTime(r.getCreateTime());
        return vo;
    }
}
