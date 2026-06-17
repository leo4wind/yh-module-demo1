package com.clinicaltrial.ddd.statistics.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisConfig;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisResult;
import com.clinicaltrial.ddd.statistics.domain.model.entity.DataProcessStep;
import com.clinicaltrial.ddd.statistics.domain.model.entity.VariableDefinition;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AlgorithmType;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.DataProcessType;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.ResultStatus;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableType;
import com.clinicaltrial.ddd.statistics.domain.repository.AnalysisProjectRepository;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.AnalysisConfigJpaEntity;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.AnalysisProjectJpaEntity;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.AnalysisProjectSpringDataRepo;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.AnalysisResultJpaEntity;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.DataProcessStepJpaEntity;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.VariableDefinitionJpaEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AnalysisProjectRepositoryImpl.class)
class AnalysisProjectRepositoryImplTest {

    @Autowired
    private AnalysisProjectSpringDataRepo springRepo;

    @Autowired
    private AnalysisProjectRepository repo;

    @Autowired
    private TestEntityManager em;

    @Test
    void saveAndFindById() {
        // Build JPA entity with all scalar fields
        AnalysisProjectJpaEntity entity = new AnalysisProjectJpaEntity();
        entity.setId(1L);
        entity.setName("Cohort Analysis");
        entity.setDescription("Statistical analysis of treatment cohort A");

        springRepo.save(entity);
        em.flush();
        em.clear();

        // Read via domain repository to exercise JPA -> domain mapping
        AnalysisProject found = repo.findById(new AnalysisProjectId(1L)).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId().getValue()).isEqualTo(1L);
        assertThat(found.getName()).isEqualTo("Cohort Analysis");
        assertThat(found.getDescription()).isEqualTo("Statistical analysis of treatment cohort A");
        assertThat(found.getVariables()).isEmpty();
        assertThat(found.getProcessSteps()).isEmpty();
        assertThat(found.getAnalysisConfigs()).isEmpty();
        assertThat(found.getResults()).isEmpty();
    }

    @Test
    void saveWithVariables() {
        AnalysisProjectJpaEntity entity = new AnalysisProjectJpaEntity();
        entity.setId(10L);
        entity.setName("Variable Test");
        entity.setDescription("Testing variable persistence");

        List<VariableDefinitionJpaEntity> variables = new ArrayList<>();

        VariableDefinitionJpaEntity var1 = new VariableDefinitionJpaEntity();
        var1.setId(101L);
        var1.setProjectId(10L);
        var1.setName("age");
        var1.setLabel("Age");
        var1.setType(VariableType.NUMERIC);
        var1.setSourceField("age_yrs");
        var1.setDerived(false);
        variables.add(var1);

        VariableDefinitionJpaEntity var2 = new VariableDefinitionJpaEntity();
        var2.setId(102L);
        var2.setProjectId(10L);
        var2.setName("bmi");
        var2.setLabel("Body Mass Index");
        var2.setType(VariableType.NUMERIC);
        var2.setSourceField("weight_kg");
        var2.setDerived(true);
        var2.setExpression("weight / (height / 100) ^ 2");
        variables.add(var2);

        entity.setVariables(variables);

        springRepo.save(entity);
        em.flush();
        em.clear();

        // Read via domain repo to verify the toDomain mapping works for variables
        AnalysisProject found = repo.findById(new AnalysisProjectId(10L)).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getVariables()).hasSize(2);
        assertThat(found.getVariables())
                .extracting("name")
                .containsExactlyInAnyOrder("age", "bmi");

        // Verify variable details through the domain mapping
        VariableDefinition ageVar = found.getVariables().stream()
                .filter(v -> "age".equals(v.getName()))
                .findFirst()
                .orElse(null);
        assertThat(ageVar).isNotNull();
        assertThat(ageVar.getLabel()).isEqualTo("Age");
        assertThat(ageVar.getVariableType()).isEqualTo(VariableType.NUMERIC);
        assertThat(ageVar.getSourceField()).isEqualTo("age_yrs");
        assertThat(ageVar.isDerived()).isFalse();

        VariableDefinition bmiVar = found.getVariables().stream()
                .filter(v -> "bmi".equals(v.getName()))
                .findFirst()
                .orElse(null);
        assertThat(bmiVar).isNotNull();
        assertThat(bmiVar.getVariableType()).isEqualTo(VariableType.NUMERIC);
        assertThat(bmiVar.isDerived()).isTrue();
        assertThat(bmiVar.getExpression()).isEqualTo("weight / (height / 100) ^ 2");
    }

    @Test
    void saveWithDataProcessSteps() {
        AnalysisProjectJpaEntity entity = new AnalysisProjectJpaEntity();
        entity.setId(20L);
        entity.setName("Process Steps Test");
        entity.setDescription("Testing data process step persistence");

        List<DataProcessStepJpaEntity> steps = new ArrayList<>();

        DataProcessStepJpaEntity step1 = new DataProcessStepJpaEntity();
        step1.setId(201L);
        step1.setProjectId(20L);
        step1.setProcessType(DataProcessType.NORMALIZE);
        step1.setConfigJson("{\"method\":\"z-score\"}");
        step1.setSortOrder(1);
        steps.add(step1);

        DataProcessStepJpaEntity step2 = new DataProcessStepJpaEntity();
        step2.setId(202L);
        step2.setProjectId(20L);
        step2.setProcessType(DataProcessType.FILL_MISSING);
        step2.setConfigJson("{\"strategy\":\"mean\"}");
        step2.setSortOrder(2);
        steps.add(step2);

        entity.setProcessSteps(steps);

        springRepo.save(entity);
        em.flush();
        em.clear();

        AnalysisProject found = repo.findById(new AnalysisProjectId(20L)).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getProcessSteps()).hasSize(2);

        // Verify step details via domain
        DataProcessStep firstStep = found.getProcessSteps().get(0);
        assertThat(firstStep.getProcessType()).isEqualTo(DataProcessType.NORMALIZE);
        assertThat(firstStep.getConfigJson()).contains("z-score");
        assertThat(firstStep.getSortOrder()).isEqualTo(1);
    }

    @Test
    void saveWithAnalysisConfigs() {
        AnalysisProjectJpaEntity entity = new AnalysisProjectJpaEntity();
        entity.setId(30L);
        entity.setName("Config Test");
        entity.setDescription("Testing analysis config with @ElementCollection");

        List<AnalysisConfigJpaEntity> configs = new ArrayList<>();

        AnalysisConfigJpaEntity config = new AnalysisConfigJpaEntity();
        config.setId(301L);
        config.setProjectId(30L);
        config.setName("Logistic Regression");
        config.setAlgorithmType(AlgorithmType.REGRESSION);
        config.setDependentVariable("outcome");
        config.setIndependentVariables(new ArrayList<String>(Arrays.asList("age", "bmi", "treatment")));
        config.setConfigJson("{\"alpha\":0.05,\"max_iter\":100}");
        config.setStatus(ResultStatus.PENDING);
        configs.add(config);

        AnalysisConfigJpaEntity config2 = new AnalysisConfigJpaEntity();
        config2.setId(302L);
        config2.setProjectId(30L);
        config2.setName("Descriptive Stats");
        config2.setAlgorithmType(AlgorithmType.DESCRIPTIVE);
        config2.setDependentVariable("age");
        config2.setIndependentVariables(new ArrayList<String>());
        config2.setConfigJson(null);
        config2.setStatus(ResultStatus.COMPLETED);
        configs.add(config2);

        entity.setAnalysisConfigs(configs);

        springRepo.save(entity);
        em.flush();
        em.clear();

        AnalysisProject found = repo.findById(new AnalysisProjectId(30L)).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getAnalysisConfigs()).hasSize(2);

        // Verify @ElementCollection independentVariables
        AnalysisConfig regressionConfig = found.getAnalysisConfigs().stream()
                .filter(c -> "Logistic Regression".equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertThat(regressionConfig).isNotNull();
        assertThat(regressionConfig.getAlgorithmType()).isEqualTo(AlgorithmType.REGRESSION);
        assertThat(regressionConfig.getDependentVariable()).isEqualTo("outcome");
        assertThat(regressionConfig.getIndependentVariables())
                .containsExactly("age", "bmi", "treatment");
        assertThat(regressionConfig.getConfigJson()).contains("max_iter");
    }

    @Test
    void saveWithResults() {
        AnalysisProjectJpaEntity entity = new AnalysisProjectJpaEntity();
        entity.setId(40L);
        entity.setName("Results Test");
        entity.setDescription("Testing @Lob data/params persistence");

        List<AnalysisResultJpaEntity> results = new ArrayList<>();

        AnalysisResultJpaEntity result = new AnalysisResultJpaEntity();
        result.setId(401L);
        result.setProjectId(40L);
        result.setName("T-Test on Age");
        result.setMethod("T_TEST");
        result.setData("{\"p_value\":0.032,\"t_statistic\":2.14,\"df\":98," +
                "\"ci_95\":[0.12,2.56]}");
        result.setResultSummary("Significant difference in age between groups (p=0.032)");
        result.setParams("{\"alpha\":0.05,\"tails\":2,\"var_equal\":true}");
        result.setIsFavorite(true);
        result.setCreateTime(new Date(1700000000000L));
        results.add(result);

        AnalysisResultJpaEntity result2 = new AnalysisResultJpaEntity();
        result2.setId(402L);
        result2.setProjectId(40L);
        result2.setName("Chi-Square on Gender");
        result2.setMethod("CHI_SQUARE");
        result2.setData("{\"chi_square\":3.84,\"p_value\":0.05,\"df\":1}");
        result2.setResultSummary("No significant association (p=0.05)");
        result2.setParams("{\"correction\":\"yates\"}");
        result2.setIsFavorite(false);
        result2.setCreateTime(new Date(1700001000000L));
        results.add(result2);

        entity.setResults(results);

        springRepo.save(entity);
        em.flush();
        em.clear();

        AnalysisProject found = repo.findById(new AnalysisProjectId(40L)).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getResults()).hasSize(2);

        // Verify @Lob data and params round-trip correctly
        AnalysisResult firstResult = found.getResults().stream()
                .filter(r -> "T-Test on Age".equals(r.getName()))
                .findFirst()
                .orElse(null);
        assertThat(firstResult).isNotNull();
        assertThat(firstResult.getMethod()).isEqualTo("T_TEST");
        assertThat(firstResult.getData()).contains("p_value\":0.032");
        assertThat(firstResult.getResultSummary()).contains("Significant difference");
        assertThat(firstResult.getParams()).contains("var_equal");
        assertThat(firstResult.isFavorite()).isTrue();
        assertThat(firstResult.getCreateTime()).isNotNull();

        // Verify second result
        AnalysisResult secondResult = found.getResults().stream()
                .filter(r -> "Chi-Square on Gender".equals(r.getName()))
                .findFirst()
                .orElse(null);
        assertThat(secondResult).isNotNull();
        assertThat(secondResult.getMethod()).isEqualTo("CHI_SQUARE");
        assertThat(secondResult.getData()).contains("chi_square");
        assertThat(secondResult.isFavorite()).isFalse();
    }

    @Test
    void getByIdNotFound() {
        assertThatThrownBy(() -> repo.getById(new AnalysisProjectId(9999L)))
                .isInstanceOf(AggregateNotFoundException.class);
    }

    @Test
    void saveUpdatesExisting() {
        // Arrange: persist original project
        AnalysisProjectJpaEntity entity = new AnalysisProjectJpaEntity();
        entity.setId(50L);
        entity.setName("Original Name");
        entity.setDescription("Original description");

        springRepo.save(entity);
        em.flush();
        em.clear();

        // Act: reconstruct with updated name, save via domain repo
        AnalysisProject updated = AnalysisProject.reconstruct(
                new AnalysisProjectId(50L),
                "Updated Name",
                "Original description",
                new ArrayList<VariableDefinition>(),
                new ArrayList<DataProcessStep>(),
                new ArrayList<AnalysisConfig>(),
                new ArrayList<AnalysisResult>()
        );

        repo.save(updated);
        em.flush();
        em.clear();

        // Verify update took effect
        AnalysisProject found = repo.findById(new AnalysisProjectId(50L)).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Updated Name");
        assertThat(found.getDescription()).isEqualTo("Original description");
    }

    @Test
    void findAll() {
        AnalysisProjectJpaEntity p1 = new AnalysisProjectJpaEntity();
        p1.setId(60L);
        p1.setName("Project A");
        p1.setDescription("First project");

        AnalysisProjectJpaEntity p2 = new AnalysisProjectJpaEntity();
        p2.setId(61L);
        p2.setName("Project B");
        p2.setDescription("Second project");

        springRepo.save(p1);
        springRepo.save(p2);
        em.flush();
        em.clear();

        List<AnalysisProject> all = repo.findAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting("name")
                .containsExactlyInAnyOrder("Project A", "Project B");
    }

    @Test
    void findByNameContaining() {
        AnalysisProjectJpaEntity p1 = new AnalysisProjectJpaEntity();
        p1.setId(70L);
        p1.setName("Cohort Analysis");
        p1.setDescription(null);

        AnalysisProjectJpaEntity p2 = new AnalysisProjectJpaEntity();
        p2.setId(71L);
        p2.setName("Survival Analysis");
        p2.setDescription(null);

        AnalysisProjectJpaEntity p3 = new AnalysisProjectJpaEntity();
        p3.setId(72L);
        p3.setName("Demographics Summary");
        p3.setDescription(null);

        springRepo.save(p1);
        springRepo.save(p2);
        springRepo.save(p3);
        em.flush();
        em.clear();

        List<AnalysisProject> results = repo.findByNameContaining("Analysis");
        assertThat(results).hasSize(2);
        assertThat(results).extracting("name")
                .containsExactlyInAnyOrder("Cohort Analysis", "Survival Analysis");

        // Empty result for non-matching name
        assertThat(repo.findByNameContaining("NonExistent")).isEmpty();
    }

    @Test
    void delete() {
        AnalysisProjectJpaEntity entity = new AnalysisProjectJpaEntity();
        entity.setId(80L);
        entity.setName("To Delete");
        entity.setDescription("Will be deleted");

        springRepo.save(entity);
        em.flush();
        em.clear();

        repo.delete(new AnalysisProjectId(80L));
        em.flush();
        em.clear();

        assertThat(repo.findById(new AnalysisProjectId(80L))).isNotPresent();
    }
}
