package com.clinicaltrial.ddd.query.application.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SnapshotValue;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.query.application.command.CloseQueryCommand;
import com.clinicaltrial.ddd.query.application.command.RaiseQueryCommand;
import com.clinicaltrial.ddd.query.application.command.ReopenQueryCommand;
import com.clinicaltrial.ddd.query.application.command.RespondToQueryCommand;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryFieldIdentifier;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryType;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryUpdateType;
import com.clinicaltrial.ddd.query.domain.repository.QueryRepository;
import com.clinicaltrial.ddd.query.domain.service.QueryDuplicateValidationService;
import com.clinicaltrial.ddd.query.domain.service.QueryLifecycleService;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * QueryApplicationService — 质疑管理应用服务.
 * <p>
 * 编排质疑管理的四个核心用例：提出质疑、回应质疑、关闭质疑、重新打开质疑。
 * 应用服务仅负责协调和编排，不包含业务逻辑——所有业务规则由领域层（聚合、领域服务）执行。
 * </p>
 *
 * <h3>职责</h3>
 * <ul>
 *   <li>接收应用指令并转换为领域操作</li>
 *   <li>校验输入参数的完整性</li>
 *   <li>调用领域服务（如重复校验）</li>
 *   <li>加载和保存聚合</li>
 *   <li>跨聚合协调（如回应质疑时更新CrfAssessment字段值）</li>
 *   <li>发布领域事件</li>
 * </ul>
 */
@Service
public class QueryApplicationService {

    private final QueryRepository queryRepository;
    private final CrfAssessmentRepository assessmentRepository;
    private final QueryDuplicateValidationService duplicateValidationService;
    private final QueryLifecycleService queryLifecycleService;
    private final EventBus eventBus;

    /**
     * 构造QueryApplicationService.
     *
     * @param queryRepository              Query仓储
     * @param assessmentRepository         CrfAssessment仓储
     * @param duplicateValidationService   质疑重复校验领域服务
     * @param queryLifecycleService        质疑生命周期领域服务
     * @param eventBus                     事件总线
     */
    public QueryApplicationService(QueryRepository queryRepository,
                                    CrfAssessmentRepository assessmentRepository,
                                    QueryDuplicateValidationService duplicateValidationService,
                                    QueryLifecycleService queryLifecycleService,
                                    EventBus eventBus) {
        this.queryRepository = queryRepository;
        this.assessmentRepository = assessmentRepository;
        this.duplicateValidationService = duplicateValidationService;
        this.queryLifecycleService = queryLifecycleService;
        this.eventBus = eventBus;
    }

    /**
     * 提出质疑.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>校验是否已存在针对同一CRF评估同一字段的未关闭质疑</li>
     *   <li>从CrfAssessment获取被质疑字段的标签信息</li>
     *   <li>构建质疑时字段值快照（SnapshotValue）</li>
     *   <li>通过工厂方法创建Query聚合（状态：OPEN）</li>
     *   <li>同步CrfAssessment状态为QUERIED</li>
     *   <li>保存Query和CrfAssessment</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 提出质疑指令
     * @return 创建的Query实例
     * @throws IllegalArgumentException                     如果command为null
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *                                                      如果字段已被质疑或状态不允许
     */
    public Query raiseQuery(RaiseQueryCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Build field identifier
        QueryFieldIdentifier fieldIdentifier = new QueryFieldIdentifier(
                command.getFieldCode(),
                command.getSubTableId(),
                command.getFieldType()
        );

        // Step 2: Validate no duplicate OPEN query for the same field
        duplicateValidationService.validateNoDuplicate(command.getAssessmentId(), fieldIdentifier);

        // Step 3: Load CrfAssessment and get field label for snapshot
        CrfAssessment assessment = assessmentRepository.getById(command.getAssessmentId());
        String fieldLabel = findFieldLabel(assessment, command.getFieldCode());

        // Step 4: Build snapshot value of the original field value
        SnapshotValue originalValue = new SnapshotValue(
                command.getOriginalFieldCode(),
                fieldLabel,
                command.getOriginalFieldValue(),
                command.getOriginalFieldValueText(),
                new Date()
        );

        // Step 5: Determine query type (default to MONITOR_QUERY)
        QueryType queryType = QueryType.MONITOR_QUERY;

        // Step 6: Create Query aggregate via factory method
        QueryId queryId = generateQueryId();
        Query query = Query.raise(
                queryId,
                command.getAssessmentId(),
                fieldIdentifier,
                queryType,
                command.getQuestion(),
                originalValue,
                command.getUserId()
        );

        // Step 7: Sync CrfAssessment status (COMPLETED -> QUERIED)
        queryLifecycleService.raiseQuery(assessment);

        // Step 8: Persist both aggregates
        Query savedQuery = queryRepository.save(query);
        assessmentRepository.save(assessment);

        // Step 9: Publish domain events
        eventBus.publishAll(savedQuery);

        return savedQuery;
    }

    /**
     * 回应质疑.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载Query聚合</li>
     *   <li>调用 {@link Query#respond(String, SnapshotValue, Long)} 方法</li>
     *   <li>如果更新类型为MODIFY_VALUE，更新CrfAssessment中对应字段的值</li>
     *   <li>设置Query的更新类型</li>
     *   <li>保存Query</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 回应质疑指令
     * @return 更新后的Query实例
     * @throws IllegalArgumentException                     如果command为null
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *                                                      如果质疑状态不允许回应
     */
    public Query respondToQuery(RespondToQueryCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load Query aggregate
        Query query = queryRepository.getById(command.getQueryId());

        // Step 2: Load CrfAssessment to get field label for snapshot
        CrfAssessment assessment = assessmentRepository.getById(query.getAssessmentId());
        String fieldLabel = findFieldLabel(assessment, query.getFieldIdentifier().getFieldCode());

        // Step 3: Determine the current value for the snapshot
        String currentFieldValue;
        String currentFieldValueText;
        if (command.getUpdateType() == QueryUpdateType.MODIFY_VALUE) {
            currentFieldValue = command.getNewFieldValue();
            currentFieldValueText = command.getNewFieldValueText();
        } else {
            // For CLARIFY_ONLY, use the original value (no change)
            currentFieldValue = query.getOriginalValue() != null
                    ? query.getOriginalValue().getFieldValue() : null;
            currentFieldValueText = query.getOriginalValue() != null
                    ? query.getOriginalValue().getFieldValueText() : null;
        }

        SnapshotValue currentValue = new SnapshotValue(
                query.getFieldIdentifier().getFieldCode(),
                fieldLabel,
                currentFieldValue,
                currentFieldValueText,
                new Date()
        );

        // Step 4: Call domain method (validates state transition OPEN -> RESPONDED)
        query.respond(command.getResponse(), currentValue, command.getUserId());

        // Step 5: Set update type based on command
        query.setUpdateType(command.getUpdateType());

        // Step 6: If MODIFY_VALUE, update the CrfAssessment field value
        if (command.getUpdateType() == QueryUpdateType.MODIFY_VALUE) {
            Optional<CrfFieldValue> existingFieldValue = assessment.findFieldValueByCode(
                    query.getFieldIdentifier().getFieldCode());

            if (existingFieldValue.isPresent()) {
                existingFieldValue.get().updateValue(
                        command.getNewFieldValue(), command.getNewFieldValueText());
                assessmentRepository.save(assessment);
            }
        }

        // Step 7: Save Query
        Query savedQuery = queryRepository.save(query);

        // Step 8: Publish domain events
        eventBus.publishAll(savedQuery);

        return savedQuery;
    }

    /**
     * 关闭质疑.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载Query聚合</li>
     *   <li>调用 {@link Query#close(Long)} 方法</li>
     *   <li>检查该CRF评估下是否还有未关闭的质疑，若无则恢复评估状态</li>
     *   <li>保存Query和CrfAssessment</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 关闭质疑指令
     * @return 更新后的Query实例
     * @throws IllegalArgumentException                     如果command为null
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *                                                      如果质疑状态不允许关闭
     */
    public Query closeQuery(CloseQueryCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load Query aggregate
        Query query = queryRepository.getById(command.getQueryId());

        // Step 2: Call domain method (validates state transition)
        query.close(command.getUserId());

        // Step 3: Load CrfAssessment and check if all queries resolved
        CrfAssessment assessment = assessmentRepository.getById(query.getAssessmentId());
        queryLifecycleService.closeQuery(assessment, query.getAssessmentId());

        // Step 4: Persist aggregates (save query first, then assessment)
        Query savedQuery = queryRepository.save(query);
        assessmentRepository.save(assessment);

        // Step 5: Publish domain events
        eventBus.publishAll(savedQuery);

        return savedQuery;
    }

    /**
     * 重新打开已关闭的质疑.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载Query聚合</li>
     *   <li>调用 {@link Query#reopen(String, Long)} 方法（状态：CLOSED → OPEN）</li>
     *   <li>保存Query</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 重新打开质疑指令
     * @return 更新后的Query实例
     * @throws IllegalArgumentException                     如果command为null
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *                                                      如果质疑状态不允许重新打开
     */
    public Query reopenQuery(ReopenQueryCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load Query aggregate
        Query query = queryRepository.getById(command.getQueryId());

        // Step 2: Call domain method (validates state transition: CLOSED -> OPEN)
        query.reopen(command.getReason(), command.getUserId());

        // Step 3: Save Query
        Query savedQuery = queryRepository.save(query);

        // Step 4: Publish domain events
        eventBus.publishAll(savedQuery);

        return savedQuery;
    }

    // ========== 内部辅助方法 ==========

    /**
     * 从CrfAssessment中查找指定字段的标签.
     * <p>
     * 如果找不到匹配的字段值，使用字段编码作为标签。
     * </p>
     *
     * @param assessment CrfAssessment聚合
     * @param fieldCode  要查找的字段编码
     * @return 字段标签
     */
    private String findFieldLabel(CrfAssessment assessment, String fieldCode) {
        Optional<CrfFieldValue> fieldValue = assessment.findFieldValueByCode(fieldCode);
        return fieldValue.isPresent() ? fieldValue.get().getFieldLabel() : fieldCode;
    }

    /**
     * 生成质疑ID.
     * <p>
     * 当前使用系统时间戳作为ID生成策略的占位实现。
     * 生产环境应替换为分布式ID生成器（如雪花算法或数据库序列）。
     * </p>
     *
     * @return 新的QueryId
     */
    private QueryId generateQueryId() {
        // TODO: Replace with proper ID generation strategy (e.g., database sequence or snowflake)
        return new QueryId(System.currentTimeMillis());
    }
}
