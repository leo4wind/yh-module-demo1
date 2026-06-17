package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.interfaces.dto.ApiResponse;
import com.clinicaltrial.ddd.interfaces.dto.IdResponse;
import com.clinicaltrial.ddd.interfaces.dto.PageResult;
import com.clinicaltrial.ddd.interfaces.dto.request.CopyCrfTemplateRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.CrfTemplateRequest;
import com.clinicaltrial.ddd.interfaces.dto.response.CrfTemplateDetailResponse;
import com.clinicaltrial.ddd.interfaces.dto.response.CrfTemplateSummary;
import com.clinicaltrial.ddd.trial.application.command.CreateCrfTemplateCommand;
import com.clinicaltrial.ddd.trial.application.command.UpdateCrfTemplateCommand;
import com.clinicaltrial.ddd.trial.application.service.CrfTemplateApplicationService;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.CrfTemplate;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfField;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfFieldOption;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfForm;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.FieldType;
import com.clinicaltrial.ddd.trial.domain.repository.CrfTemplateRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * REST Controller for CRF template lookup.
 */
@RestController
@RequestMapping("/api/crf-templates")
public class CrfTemplateController {

    private final CrfTemplateRepository crfTemplateRepository;
    private final CrfTemplateApplicationService crfTemplateAppService;

    public CrfTemplateController(CrfTemplateRepository crfTemplateRepository,
                                 CrfTemplateApplicationService crfTemplateAppService) {
        this.crfTemplateRepository = crfTemplateRepository;
        this.crfTemplateAppService = crfTemplateAppService;
    }

    /** 查询CRF模板列表，供项目绑定时搜索选择. */
    @GetMapping
    public ApiResponse<PageResult<CrfTemplateSummary>> listTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {

        List<CrfTemplate> templates = category != null && !category.trim().isEmpty()
                ? crfTemplateRepository.findByCategory(category.trim())
                : crfTemplateRepository.findAll(0, Integer.MAX_VALUE);

        if (status != null && !status.trim().isEmpty()) {
            String expectedStatus = status.trim();
            templates = templates.stream()
                    .filter(template -> expectedStatus.equalsIgnoreCase(template.getStatus()))
                    .collect(Collectors.toList());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
            templates = templates.stream()
                    .filter(template -> containsIgnoreCase(template.getName(), normalizedKeyword)
                            || containsIgnoreCase(template.getCode(), normalizedKeyword)
                            || containsIgnoreCase(template.getCategory(), normalizedKeyword))
                    .collect(Collectors.toList());
        }

        int start = Math.max(page, 0) * Math.max(size, 1);
        int end = Math.min(start + Math.max(size, 1), templates.size());
        List<CrfTemplate> pageContent = start < templates.size()
                ? templates.subList(start, end)
                : new ArrayList<>();

        List<CrfTemplateSummary> summaries = pageContent.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        return ApiResponse.success(new PageResult<>(summaries, page, size, templates.size()));
    }

    /** 获取CRF模板详情. */
    @GetMapping("/{id}")
    public ApiResponse<CrfTemplateDetailResponse> getTemplate(@PathVariable Long id) {
        CrfTemplate template = crfTemplateRepository.getById(new CrfTemplateId(id));
        return ApiResponse.success(toDetail(template));
    }

    /** 创建CRF模板草稿. */
    @PostMapping
    public ApiResponse<IdResponse> createTemplate(@RequestBody CrfTemplateRequest req) {
        CrfTemplate template = crfTemplateAppService.createTemplate(new CreateCrfTemplateCommand(
                req.getName(), req.getCode(), req.getCategory(),
                req.getEstimateTime(), req.getNotice(), req.getIntroduce()));
        if (req.getForms() != null && !req.getForms().isEmpty()) {
            template = crfTemplateAppService.updateTemplate(
                    template.getId(), toUpdateCommand(req));
        }
        return ApiResponse.success(new IdResponse(template.getId().getValue()));
    }

    /** 更新CRF模板草稿基础信息和结构. */
    @PutMapping("/{id}")
    public ApiResponse<IdResponse> updateTemplate(@PathVariable Long id,
                                                   @RequestBody CrfTemplateRequest req) {
        CrfTemplate template = crfTemplateAppService.updateTemplate(
                new CrfTemplateId(id), toUpdateCommand(req));
        return ApiResponse.success(new IdResponse(template.getId().getValue()));
    }

    /** 发布CRF模板. */
    @PostMapping("/{id}/publish")
    public ApiResponse<IdResponse> publishTemplate(@PathVariable Long id) {
        CrfTemplate template = crfTemplateAppService.publishTemplate(new CrfTemplateId(id));
        return ApiResponse.success(new IdResponse(template.getId().getValue()));
    }

    /** 复制CRF模板为草稿. */
    @PostMapping("/{id}/copy")
    public ApiResponse<IdResponse> copyTemplate(@PathVariable Long id,
                                                 @RequestBody(required = false) CopyCrfTemplateRequest req) {
        String newName = req != null && req.getName() != null && !req.getName().trim().isEmpty()
                ? req.getName().trim()
                : crfTemplateRepository.getById(new CrfTemplateId(id)).getName() + " 副本";
        CrfTemplate template = crfTemplateAppService.copyTemplate(new CrfTemplateId(id), newName);
        return ApiResponse.success(new IdResponse(template.getId().getValue()));
    }

    private boolean containsIgnoreCase(String value, String normalizedKeyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    private CrfTemplateSummary toSummary(CrfTemplate template) {
        CrfTemplateSummary summary = new CrfTemplateSummary();
        fillSummary(summary, template);
        return summary;
    }

    private CrfTemplateDetailResponse toDetail(CrfTemplate template) {
        CrfTemplateDetailResponse detail = new CrfTemplateDetailResponse();
        fillSummary(detail, template);
        detail.setNotice(template.getNotice());
        detail.setIntroduce(template.getIntroduce());
        detail.setForms(template.getForms().stream()
                .map(this::toFormVo)
                .collect(Collectors.toList()));
        return detail;
    }

    private void fillSummary(CrfTemplateSummary summary, CrfTemplate template) {
        summary.setId(template.getId() != null ? template.getId().getValue() : null);
        summary.setName(template.getName());
        summary.setCode(template.getCode());
        summary.setDefaultVersionId(template.getDefaultVersionId() != null
                ? template.getDefaultVersionId().getValue() : null);
        summary.setStatus(template.getStatus());
        summary.setCategory(template.getCategory());
        summary.setEstimateTime(template.getEstimateTime());
        summary.setFormCount(template.getForms().size());
        summary.setFieldCount(template.getForms().stream()
                .mapToInt((CrfForm form) -> form.getFields().size())
                .sum());
    }

    private CrfTemplateDetailResponse.FormVo toFormVo(CrfForm form) {
        CrfTemplateDetailResponse.FormVo vo = new CrfTemplateDetailResponse.FormVo();
        vo.setId(form.getId() != null ? form.getId().getValue() : null);
        vo.setModelName(form.getModelName());
        vo.setRefName(form.getRefName());
        vo.setRulesName(form.getRulesName());
        vo.setFields(form.getFields().stream()
                .map(this::toFieldVo)
                .collect(Collectors.toList()));
        return vo;
    }

    private CrfTemplateDetailResponse.FieldVo toFieldVo(CrfField field) {
        CrfTemplateDetailResponse.FieldVo vo = new CrfTemplateDetailResponse.FieldVo();
        vo.setId(field.getId() != null ? field.getId().getValue() : null);
        vo.setFieldCode(field.getFieldCode());
        vo.setFieldLabel(field.getFieldLabel());
        vo.setFieldType(field.getFieldType() != null ? field.getFieldType().name() : null);
        vo.setDefaultValue(field.getDefaultValue());
        vo.setDataUnit(field.getDataUnit());
        vo.setRequired(field.isRequired());
        vo.setHidden(field.isHidden());
        vo.setSortOrder(field.getSortOrder());
        vo.setOptions(field.getOptions().stream()
                .map(this::toOptionVo)
                .collect(Collectors.toList()));
        return vo;
    }

    private CrfTemplateDetailResponse.OptionVo toOptionVo(CrfFieldOption option) {
        CrfTemplateDetailResponse.OptionVo vo = new CrfTemplateDetailResponse.OptionVo();
        vo.setId(option.getId() != null ? option.getId().getValue() : null);
        vo.setOptionLabel(option.getOptionLabel());
        vo.setOptionValue(option.getOptionValue());
        vo.setSortOrder(option.getSortOrder());
        vo.setScore(option.getScore());
        return vo;
    }

    private UpdateCrfTemplateCommand toUpdateCommand(CrfTemplateRequest req) {
        return new UpdateCrfTemplateCommand(
                req.getName(), req.getCode(), req.getCategory(),
                req.getEstimateTime(), req.getNotice(), req.getIntroduce(),
                toFormCommands(req.getForms()));
    }

    private List<UpdateCrfTemplateCommand.FormCommand> toFormCommands(
            List<CrfTemplateRequest.FormRequest> requests) {
        if (requests == null) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(req -> new UpdateCrfTemplateCommand.FormCommand(
                        req.getModelName(),
                        req.getRefName(),
                        req.getRulesName(),
                        toFieldCommands(req.getFields())))
                .collect(Collectors.toList());
    }

    private List<UpdateCrfTemplateCommand.FieldCommand> toFieldCommands(
            List<CrfTemplateRequest.FieldRequest> requests) {
        if (requests == null) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(req -> new UpdateCrfTemplateCommand.FieldCommand(
                        req.getFieldCode(),
                        req.getFieldLabel(),
                        req.getFieldType() != null ? FieldType.valueOf(req.getFieldType()) : FieldType.TEXT,
                        req.getDefaultValue(),
                        req.getDataUnit(),
                        req.getRequired() != null && req.getRequired(),
                        req.getHidden() != null && req.getHidden(),
                        req.getSortOrder(),
                        toOptionCommands(req.getOptions())))
                .collect(Collectors.toList());
    }

    private List<UpdateCrfTemplateCommand.OptionCommand> toOptionCommands(
            List<CrfTemplateRequest.OptionRequest> requests) {
        if (requests == null) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(req -> new UpdateCrfTemplateCommand.OptionCommand(
                        req.getOptionLabel(),
                        req.getOptionValue(),
                        req.getSortOrder(),
                        req.getScore()))
                .collect(Collectors.toList());
    }
}
