package com.clinicaltrial.ddd.datacollection.domain.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.util.List;
import java.util.Optional;

/**
 * CrfAssessmentRepository — CRF评估记录仓储接口.
 * <p>
 * 提供CrfAssessment聚合的持久化操作。
 * 遵循仓储模式，每个聚合对应一个仓储。
 * </p>
 */
public interface CrfAssessmentRepository {

    /**
     * 根据ID查找CRF评估.
     *
     * @param id CRF评估ID
     * @return 包含CrfAssessment的Optional
     */
    Optional<CrfAssessment> findById(CrfAssessmentId id);

    /**
     * 根据ID获取CRF评估，不存在时抛出异常.
     *
     * @param id CRF评估ID
     * @return CrfAssessment实例
     * @throws AggregateNotFoundException 如果未找到
     */
    default CrfAssessment getById(CrfAssessmentId id) {
        return findById(id)
                .orElseThrow(() -> new AggregateNotFoundException("CrfAssessment", id));
    }

    /**
     * 保存CRF评估.
     *
     * @param assessment CRF评估实例
     */
    void save(CrfAssessment assessment);

    /**
     * 根据受试者阶段ID查询所有CRF评估.
     *
     * @param subjectStageId 受试者阶段ID
     * @return CRF评估列表
     */
    List<CrfAssessment> findBySubjectsStageId(SubjectStageId subjectStageId);

    /**
     * 根据受试者ID查询所有CRF评估.
     *
     * @param subjectId 受试者ID
     * @return CRF评估列表
     */
    List<CrfAssessment> findBySubjectId(SubjectId subjectId);
}
