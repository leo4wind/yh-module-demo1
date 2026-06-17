package com.clinicaltrial.ddd.datacollection.infrastructure;

import com.clinicaltrial.ddd.datacollection.domain.model.CrfField;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.datacollection.domain.service.StageConfigurationProvider;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
import com.clinicaltrial.ddd.trial.domain.model.entity.Stage;
import com.clinicaltrial.ddd.trial.domain.model.entity.StageCrfBinding;
import com.clinicaltrial.ddd.trial.domain.model.entity.VisitPlan;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.repository.CrfTemplateRepository;
import com.clinicaltrial.ddd.trial.domain.repository.ProjectRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProjectStageConfigurationProvider implements StageConfigurationProvider {

    private final ProjectRepository projectRepository;
    private final CrfTemplateRepository crfTemplateRepository;

    public ProjectStageConfigurationProvider(ProjectRepository projectRepository,
                                             CrfTemplateRepository crfTemplateRepository) {
        this.projectRepository = projectRepository;
        this.crfTemplateRepository = crfTemplateRepository;
    }

    @Override
    public List<StageId> findAutoAddStageIds(Long projectId) {
        if (projectId == null) {
            return Collections.emptyList();
        }
        return projectRepository.findById(new ProjectId(projectId))
                .map(Project::getAutoAddStages)
                .orElse(Collections.emptyList())
                .stream()
                .map(Stage::getId)
                .map(id -> new StageId(id.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<CrfTemplateId, CrfVersionId> findStageCrfBindings(StageId stageId) {
        if (stageId == null || stageId.getValue() == null) {
            return Collections.emptyMap();
        }
        Map<CrfTemplateId, CrfVersionId> bindings = new LinkedHashMap<>();
        for (Project project : projectRepository.findAll(0, Integer.MAX_VALUE)) {
            for (StageCrfBinding binding : project.getCrfBindings()) {
                if (binding.getStageId() != null
                        && stageId.getValue().equals(binding.getStageId().getValue())
                        && binding.getCrfId() != null) {
                    CrfVersionId versionId = binding.getCrfVersionId() != null
                            ? new CrfVersionId(binding.getCrfVersionId().getValue())
                            : null;
                    bindings.put(new CrfTemplateId(binding.getCrfId().getValue()), versionId);
                }
            }
        }
        return bindings;
    }

    @Override
    public List<CrfField> findCrfTemplateFields(CrfTemplateId crfId, CrfVersionId crfVersionId) {
        if (crfId == null || crfId.getValue() == null) {
            return Collections.emptyList();
        }
        return crfTemplateRepository
                .findById(new com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId(crfId.getValue()))
                .map(template -> template.getForms().stream()
                        .flatMap(form -> form.getFields().stream())
                        .map(field -> new CrfField(
                                field.getFieldCode(),
                                field.isRequired(),
                                field.isHidden(),
                                false))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public VisitPlanId findVisitPlanId(Long projectId, StageId stageId) {
        if (projectId == null || stageId == null || stageId.getValue() == null) {
            return null;
        }
        Optional<VisitPlan> plan = projectRepository.findById(new ProjectId(projectId))
                .map(Project::getVisitPlans)
                .orElse(Collections.emptyList())
                .stream()
                .filter(vp -> vp.getTargetStageId() != null
                        && stageId.getValue().equals(vp.getTargetStageId().getValue()))
                .findFirst();
        return plan.map(vp -> new VisitPlanId(vp.getId().getValue())).orElse(null);
    }
}
