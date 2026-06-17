package com.clinicaltrial.ddd.datacollection.domain.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.util.List;
import java.util.Optional;

/**
 * SubjectStageRepository — 受试者阶段仓储接口.
 * <p>
 * 提供SubjectStage聚合的持久化操作。
 * 遵循仓储模式，每个聚合对应一个仓储。
 * </p>
 */
public interface SubjectStageRepository {

    /**
     * 根据ID查找受试者阶段.
     *
     * @param id 受试者阶段ID
     * @return 包含SubjectStage的Optional
     */
    Optional<SubjectStage> findById(SubjectStageId id);

    /**
     * 根据ID获取受试者阶段，不存在时抛出异常.
     *
     * @param id 受试者阶段ID
     * @return SubjectStage实例
     * @throws AggregateNotFoundException 如果未找到
     */
    default SubjectStage getById(SubjectStageId id) {
        return findById(id)
                .orElseThrow(() -> new AggregateNotFoundException("SubjectStage", id));
    }

    /**
     * 保存受试者阶段.
     *
     * @param subjectStage 受试者阶段实例
     */
    void save(SubjectStage subjectStage);

    /**
     * 根据受试者ID查询所有受试者阶段.
     *
     * @param subjectId 受试者ID
     * @return 受试者阶段列表
     */
    List<SubjectStage> findBySubjectId(SubjectId subjectId);

    /**
     * 根据受试者ID和试验阶段ID查询受试者阶段.
     *
     * @param subjectId 受试者ID
     * @param stageId   试验阶段ID
     * @return 包含SubjectStage的Optional
     */
    Optional<SubjectStage> findBySubjectIdAndStageId(SubjectId subjectId, StageId stageId);
}
