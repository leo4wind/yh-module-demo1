package com.clinicaltrial.ddd.datacollection.infrastructure;

import com.clinicaltrial.ddd.datacollection.domain.model.CrfField;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.datacollection.domain.service.StageConfigurationProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class StageConfigurationProviderStub implements StageConfigurationProvider {

    @Override
    public List<StageId> findAutoAddStageIds(Long projectId) {
        return Collections.emptyList();
    }

    @Override
    public Map<CrfTemplateId, CrfVersionId> findStageCrfBindings(StageId stageId) {
        return Collections.emptyMap();
    }

    @Override
    public List<CrfField> findCrfTemplateFields(CrfTemplateId crfId, CrfVersionId crfVersionId) {
        return Collections.emptyList();
    }

    @Override
    public VisitPlanId findVisitPlanId(Long projectId, StageId stageId) {
        return null;
    }
}
