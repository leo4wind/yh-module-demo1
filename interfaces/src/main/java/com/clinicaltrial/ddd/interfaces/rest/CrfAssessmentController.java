package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.datacollection.application.command.SaveCrfFieldValueCommand;
import com.clinicaltrial.ddd.datacollection.application.service.CrfFillingApplicationService;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.datacollection.domain.repository.SubjectStageRepository;
import com.clinicaltrial.ddd.interfaces.dto.ApiResponse;
import com.clinicaltrial.ddd.interfaces.dto.request.AuditAssessmentRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.SaveFieldValueRequest;
import com.clinicaltrial.ddd.interfaces.dto.response.CrfAssessmentResponse;
import com.clinicaltrial.ddd.interfaces.dto.response.SubjectStageResponse;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for BC3: Data Collection (事件驱动的数据采集).
 */
@RestController
public class CrfAssessmentController {

    private final SubjectStageRepository subjectStageRepository;
    private final CrfAssessmentRepository crfAssessmentRepository;
    private final CrfFillingApplicationService crfFillingAppService;

    public CrfAssessmentController(SubjectStageRepository subjectStageRepository,
                                   CrfAssessmentRepository crfAssessmentRepository,
                                   CrfFillingApplicationService crfFillingAppService) {
        this.subjectStageRepository = subjectStageRepository;
        this.crfAssessmentRepository = crfAssessmentRepository;
        this.crfFillingAppService = crfFillingAppService;
    }

    /** 受试者所有访视列表. */
    @GetMapping("/api/subjects/{subjectId}/stages")
    public ApiResponse<List<SubjectStageResponse>> listSubjectStages(@PathVariable Long subjectId) {
        SubjectId sid = new SubjectId(subjectId);
        List<SubjectStage> stages = subjectStageRepository.findBySubjectId(sid);
        List<SubjectStageResponse> result = stages.stream()
                .map(this::toSubjectStageResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(result);
    }

    /** 访视详情（含CRF评估列表）. */
    @GetMapping("/api/stages/{id}")
    public ApiResponse<SubjectStageResponse> getStageDetail(@PathVariable Long id) {
        SubjectStage stage = subjectStageRepository.getById(new SubjectStageId(id));
        SubjectStageResponse resp = toSubjectStageResponse(stage);

        // Attach CRF assessments
        List<CrfAssessment> assessments =
                crfAssessmentRepository.findBySubjectsStageId(stage.getId());
        List<SubjectStageResponse.CrfAssessmentSummary> summaries = assessments.stream()
                .map(this::toAssessmentSummary)
                .collect(Collectors.toList());
        resp.setCrfAssessments(summaries);

        return ApiResponse.success(resp);
    }

    /** CRF评估详情（含字段值）. */
    @GetMapping("/api/assessments/{id}")
    public ApiResponse<CrfAssessmentResponse> getAssessmentDetail(@PathVariable Long id) {
        CrfAssessment assessment = crfAssessmentRepository.getById(new CrfAssessmentId(id));
        return ApiResponse.success(toAssessmentDetail(assessment));
    }

    /** 保存字段值. */
    @PostMapping("/api/assessments/{id}/field-values")
    public ApiResponse<Void> saveFieldValue(@PathVariable Long id,
                                             @RequestBody SaveFieldValueRequest req) {
        SaveCrfFieldValueCommand cmd = new SaveCrfFieldValueCommand(
                new CrfAssessmentId(id), req.getFieldCode(), req.getFieldLabel(),
                req.getFieldValue(), req.getFieldValueText(), req.getDataUnit(),
                req.getFieldType(), req.getSubTableId(), req.getUserId());
        crfFillingAppService.saveFieldValue(cmd);
        return ApiResponse.success(null);
    }

    /** 稽查评估. */
    @PostMapping("/api/assessments/{id}/audit")
    public ApiResponse<Void> auditAssessment(@PathVariable Long id,
                                              @RequestBody AuditAssessmentRequest req) {
        CrfAssessment assessment = crfAssessmentRepository.getById(new CrfAssessmentId(id));
        assessment.audit(req.getUserId());
        crfAssessmentRepository.save(assessment);
        return ApiResponse.success(null);
    }

    // ========== 转换方法 ==========

    private SubjectStageResponse toSubjectStageResponse(SubjectStage s) {
        SubjectStageResponse r = new SubjectStageResponse();
        r.setId(s.getId().getValue());
        r.setSubjectId(s.getSubjectsUserId() != null ? s.getSubjectsUserId().getValue() : null);
        r.setStageId(s.getStageId() != null ? s.getStageId().getValue() : null);
        r.setStatus(s.getStatus() != null ? s.getStatus().name() : null);
        r.setPlannedDate(s.getStageStartAt());
        r.setActualDate(s.getStageEndAt());
        return r;
    }

    private SubjectStageResponse.CrfAssessmentSummary toAssessmentSummary(CrfAssessment a) {
        SubjectStageResponse.CrfAssessmentSummary s = new SubjectStageResponse.CrfAssessmentSummary();
        s.setId(a.getId().getValue());
        s.setCrfTemplateId(a.getCrfId() != null ? a.getCrfId().getValue() : null);
        s.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
        Completeness comp = a.getCompleteness();
        if (comp != null && comp.getPercentage() != null) {
            s.setCompleteness(comp.getPercentage().doubleValue());
        }
        return s;
    }

    private CrfAssessmentResponse toAssessmentDetail(CrfAssessment a) {
        CrfAssessmentResponse r = new CrfAssessmentResponse();
        r.setId(a.getId().getValue());
        r.setSubjectStageId(a.getSubjectsStageId() != null ? a.getSubjectsStageId().getValue() : null);
        r.setStageId(a.getCrfId() != null ? a.getCrfId().getValue() : null);
        r.setCrfTemplateId(a.getCrfId() != null ? a.getCrfId().getValue() : null);
        r.setCrfVersionId(a.getCrfVersionId() != null ? a.getCrfVersionId().getValue() : null);
        r.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
        Completeness comp = a.getCompleteness();
        if (comp != null && comp.getPercentage() != null) {
            r.setCompleteness(comp.getPercentage().doubleValue());
        }

        // Field values
        List<CrfAssessmentResponse.FieldValueVo> fieldVos = a.getFieldValues().stream()
                .map(this::toFieldValueVo)
                .collect(Collectors.toList());
        r.setFieldValues(fieldVos);

        return r;
    }

    private CrfAssessmentResponse.FieldValueVo toFieldValueVo(CrfFieldValue fv) {
        CrfAssessmentResponse.FieldValueVo vo = new CrfAssessmentResponse.FieldValueVo();
        vo.setFieldCode(fv.getFieldCode());
        vo.setFieldLabel(fv.getFieldLabel());
        vo.setFieldValue(fv.getFieldValue());
        vo.setFieldValueText(fv.getFieldValueText());
        vo.setDataUnit(fv.getDataUnit());
        vo.setFieldType(fv.getFieldType());
        return vo;
    }
}
