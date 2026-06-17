package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.interfaces.dto.ApiResponse;
import com.clinicaltrial.ddd.interfaces.dto.IdResponse;
import com.clinicaltrial.ddd.interfaces.dto.request.CloseQueryRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.RaiseQueryRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.ReopenQueryRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.RespondQueryRequest;
import com.clinicaltrial.ddd.interfaces.dto.response.QueryResponse;
import com.clinicaltrial.ddd.query.application.command.CloseQueryCommand;
import com.clinicaltrial.ddd.query.application.command.RaiseQueryCommand;
import com.clinicaltrial.ddd.query.application.command.ReopenQueryCommand;
import com.clinicaltrial.ddd.query.application.command.RespondToQueryCommand;
import com.clinicaltrial.ddd.query.application.service.QueryApplicationService;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryUpdateType;
import com.clinicaltrial.ddd.query.domain.repository.QueryRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for BC4: Query Management (质疑管理).
 */
@RestController
public class QueryController {

    private final QueryRepository queryRepository;
    private final QueryApplicationService queryAppService;

    public QueryController(QueryRepository queryRepository,
                           QueryApplicationService queryAppService) {
        this.queryRepository = queryRepository;
        this.queryAppService = queryAppService;
    }

    /** 评估的所有质疑. */
    @GetMapping("/api/assessments/{assessmentId}/queries")
    public ApiResponse<List<QueryResponse>> listQueriesByAssessment(@PathVariable Long assessmentId) {
        List<Query> queries = queryRepository.findByAssessmentId(new CrfAssessmentId(assessmentId));
        List<QueryResponse> result = queries.stream()
                .map(this::toQueryResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(result);
    }

    /** 质疑详情. */
    @GetMapping("/api/queries/{id}")
    public ApiResponse<QueryResponse> getQuery(@PathVariable Long id) {
        Query query = queryRepository.getById(new QueryId(id));
        return ApiResponse.success(toQueryResponse(query));
    }

    /** 发起质疑. */
    @PostMapping("/api/queries")
    public ApiResponse<IdResponse> raiseQuery(@RequestBody RaiseQueryRequest req) {
        RaiseQueryCommand cmd = new RaiseQueryCommand(
                new CrfAssessmentId(req.getAssessmentId()),
                req.getFieldCode(), req.getSubTableId(), req.getFieldType(),
                req.getQuestion(), req.getOriginalFieldCode(),
                req.getOriginalFieldValue(), req.getOriginalFieldValueText(),
                req.getUserId());
        Query query = queryAppService.raiseQuery(cmd);
        return ApiResponse.success(new IdResponse(query.getId().getValue()));
    }

    /** 回应质疑. */
    @PostMapping("/api/queries/{id}/respond")
    public ApiResponse<IdResponse> respondToQuery(@PathVariable Long id,
                                                   @RequestBody RespondQueryRequest req) {
        QueryUpdateType updateType = req.getUpdateType() != null
                ? QueryUpdateType.valueOf(req.getUpdateType()) : QueryUpdateType.CLARIFY_ONLY;
        RespondToQueryCommand cmd = new RespondToQueryCommand(
                new QueryId(id), req.getResponse(), updateType,
                req.getNewFieldValue(), req.getNewFieldValueText(), req.getUserId());
        Query query = queryAppService.respondToQuery(cmd);
        return ApiResponse.success(new IdResponse(query.getId().getValue()));
    }

    /** 关闭质疑. */
    @PostMapping("/api/queries/{id}/close")
    public ApiResponse<IdResponse> closeQuery(@PathVariable Long id,
                                               @RequestBody CloseQueryRequest req) {
        CloseQueryCommand cmd = new CloseQueryCommand(new QueryId(id), req.getUserId());
        Query query = queryAppService.closeQuery(cmd);
        return ApiResponse.success(new IdResponse(query.getId().getValue()));
    }

    /** 重新打开质疑. */
    @PostMapping("/api/queries/{id}/reopen")
    public ApiResponse<IdResponse> reopenQuery(@PathVariable Long id,
                                                @RequestBody ReopenQueryRequest req) {
        ReopenQueryCommand cmd = new ReopenQueryCommand(
                new QueryId(id), req.getReason(), req.getUserId());
        Query query = queryAppService.reopenQuery(cmd);
        return ApiResponse.success(new IdResponse(query.getId().getValue()));
    }

    // ========== 转换方法 ==========

    private QueryResponse toQueryResponse(Query q) {
        QueryResponse r = new QueryResponse();
        r.setId(q.getId().getValue());
        r.setAssessmentId(q.getAssessmentId() != null ? q.getAssessmentId().getValue() : null);

        if (q.getFieldIdentifier() != null) {
            r.setFieldCode(q.getFieldIdentifier().getFieldCode());
            r.setSubTableId(q.getFieldIdentifier().getSubTableId());
            r.setFieldType(q.getFieldIdentifier().getFieldType());
        }

        r.setStatus(q.getStatus() != null ? q.getStatus().name() : null);
        r.setType(q.getType() != null ? q.getType().name() : null);
        r.setQuestion(q.getQuestion());
        r.setResponse(q.getResponse());
        r.setUpdateType(q.getUpdateType() != null ? q.getUpdateType().name() : null);
        r.setCreateUserId(q.getCreateUserId());
        r.setUpdateUserId(q.getUpdateUserId());
        r.setCreateTime(q.getCreateTime());
        r.setUpdateTime(q.getUpdateTime());

        if (q.getOriginalValue() != null) {
            r.setOriginalFieldValue(q.getOriginalValue().getFieldValue());
            r.setOriginalFieldValueText(q.getOriginalValue().getFieldValueText());
        }
        if (q.getCurrentValue() != null) {
            r.setCurrentFieldValue(q.getCurrentValue().getFieldValue());
            r.setCurrentFieldValueText(q.getCurrentValue().getFieldValueText());
        }

        return r;
    }
}
