package com.clinicaltrial.ddd.trial.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.CrfTemplate;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.FieldType;
import com.clinicaltrial.ddd.trial.domain.repository.CrfTemplateRepository;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfFieldJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfFieldOptionJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfFormJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfTemplateJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfTemplateSpringDataRepo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CrfTemplateRepositoryImpl.class)
class CrfTemplateRepositoryImplTest {

    @Autowired
    private CrfTemplateSpringDataRepo springRepo;

    @Autowired
    private CrfTemplateRepository repo;

    @Autowired
    private TestEntityManager em;

    @Test
    void saveAndFindById() {
        CrfTemplateJpaEntity jpa = new CrfTemplateJpaEntity();
        jpa.setId(1L);
        jpa.setName("Adverse Event Form");
        jpa.setCode("AE_FORM");
        jpa.setStatus("DRAFT");
        jpa.setCategory("Safety");
        jpa.setEstimateTime("10 min");
        jpa.setNotice("Complete all fields");
        jpa.setIntroduce("Standard AE reporting form");

        springRepo.save(jpa);
        em.flush();
        em.clear();

        CrfTemplate result = repo.getById(new CrfTemplateId(1L));

        assertThat(result.getName()).isEqualTo("Adverse Event Form");
        assertThat(result.getCode()).isEqualTo("AE_FORM");
        assertThat(result.getStatus()).isEqualTo("DRAFT");
        assertThat(result.getCategory()).isEqualTo("Safety");
    }

    @Test
    void saveWithFullGraph() {
        // Build template
        CrfTemplateJpaEntity template = new CrfTemplateJpaEntity();
        template.setId(10L);
        template.setName("Demographics Form");
        template.setCode("DEMO");
        template.setStatus("DRAFT");

        // Build form
        CrfFormJpaEntity form = new CrfFormJpaEntity();
        form.setId(101L);
        form.setTemplateId(10L);
        form.setModelName("Patient Info");
        form.setRefName("PATIENT_INFO");
        form.setRulesName("demo_rules");

        // Build field with options
        CrfFieldJpaEntity genderField = new CrfFieldJpaEntity();
        genderField.setId(1001L);
        genderField.setFieldCode("GENDER");
        genderField.setFieldLabel("Gender");
        genderField.setFieldType(FieldType.RADIO);
        genderField.setRequired(true);
        genderField.setSortOrder(1);

        CrfFieldOptionJpaEntity male = new CrfFieldOptionJpaEntity();
        male.setId(10001L);
        male.setOptionLabel("Male");
        male.setOptionValue("M");
        male.setSortOrder(1);
        male.setScore(new BigDecimal("0"));

        CrfFieldOptionJpaEntity female = new CrfFieldOptionJpaEntity();
        female.setId(10002L);
        female.setOptionLabel("Female");
        female.setOptionValue("F");
        female.setSortOrder(2);
        female.setScore(new BigDecimal("0"));

        genderField.getOptions().add(male);
        genderField.getOptions().add(female);

        // Build text field
        CrfFieldJpaEntity nameField = new CrfFieldJpaEntity();
        nameField.setId(1002L);
        nameField.setFieldCode("PATIENT_NAME");
        nameField.setFieldLabel("Patient Name");
        nameField.setFieldType(FieldType.TEXT);
        nameField.setRequired(true);
        nameField.setSortOrder(2);

        form.getFields().add(genderField);
        form.getFields().add(nameField);
        template.getForms().add(form);

        springRepo.save(template);
        em.flush();
        em.clear();

        CrfTemplate result = repo.getById(new CrfTemplateId(10L));

        assertThat(result.getName()).isEqualTo("Demographics Form");
        assertThat(result.getForms()).hasSize(1);
        assertThat(result.getForms().get(0).getModelName()).isEqualTo("Patient Info");
        assertThat(result.getForms().get(0).getFields()).hasSize(2);

        assertThat(result.getForms().get(0).getFields().get(0).getFieldCode()).isEqualTo("GENDER");
        assertThat(result.getForms().get(0).getFields().get(0).getFieldType()).isEqualTo(FieldType.RADIO);
        assertThat(result.getForms().get(0).getFields().get(0).isRequired()).isTrue();

        assertThat(result.getForms().get(0).getFields().get(0).getOptions()).hasSize(2);
        assertThat(result.getForms().get(0).getFields().get(0).getOptions().get(0).getOptionValue()).isEqualTo("M");
        assertThat(result.getForms().get(0).getFields().get(0).getOptions().get(1).getOptionValue()).isEqualTo("F");

        assertThat(result.getForms().get(0).getFields().get(1).getFieldCode()).isEqualTo("PATIENT_NAME");
        assertThat(result.getForms().get(0).getFields().get(1).getFieldType()).isEqualTo(FieldType.TEXT);
    }

    @Test
    void getByIdNotFound() {
        assertThrows(AggregateNotFoundException.class,
                () -> repo.getById(new CrfTemplateId(9999L)));
    }
}
