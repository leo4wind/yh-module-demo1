package com.clinicaltrial.ddd.trial.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.trial.domain.event.CrfBoundToStageEvent;
import com.clinicaltrial.ddd.trial.domain.event.ProjectActivatedEvent;
import com.clinicaltrial.ddd.trial.domain.event.ProjectCreatedEvent;
import com.clinicaltrial.ddd.trial.domain.event.StageAddedEvent;
import com.clinicaltrial.ddd.trial.domain.event.VisitPlanConfiguredEvent;
import com.clinicaltrial.ddd.trial.domain.model.entity.Stage;
import com.clinicaltrial.ddd.trial.domain.model.entity.StageCrfBinding;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseJudgeType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SiteRole;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageRepeatType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive unit tests for {@link Project}.
 * <p>
 * Covers factory methods, lifecycle state transitions (DRAFT/ACTIVE/CLOSED),
 * stage management, visit plan configuration, CRF binding, adverse event rule
 * configuration, and personnel assignment. Pure domain tests — no mocks.
 * </p>
 */
class ProjectTest {

    // ===============================================================
    // Factory Tests
    // ===============================================================

    @Nested
    @DisplayName("Factory methods")
    class FactoryTests {

        /**
         * 验证通过工厂方法 create() 创建的 Project 初始状态为 DRAFT，所有字段正确设置，
         * 并产生一个 ProjectCreatedEvent 领域事件。
         * 前置条件：使用 ProjectTestFixtures.aDraftProject() 创建项目（含完整元数据）。
         * 预期结果：项目 ID、标题、类型、状态(DRAFT)、缩写、前缀、是否开放筛选、受试者数量、
         *          临床编号、注册号、目的、创建人 ID 均匹配；阶段、人员、不良事件规则、
         *          访视计划、CRF 绑定均为空；拉取领域事件返回一个 ProjectCreatedEvent。
         */
        @Test
        @DisplayName("create sets status to DRAFT and raises ProjectCreatedEvent")
        void create() {
            Project project = ProjectTestFixtures.aDraftProject();

            assertThat(project.getId()).isEqualTo(ProjectTestFixtures.aProjectId());
            assertThat(project.getTitle()).isEqualTo("Test Trial");
            assertThat(project.getType()).isEqualTo(ProjectType.INTERVENTIONAL);
            assertThat(project.getStatus()).isEqualTo(ProjectStatus.DRAFT);
            assertThat(project.getAbbreviation()).isEqualTo("TT");
            assertThat(project.getPrefix()).isEqualTo("TT");
            assertThat(project.isOpenScreen()).isTrue();
            assertThat(project.getExpectedSubjectSize()).isEqualTo(100);
            assertThat(project.getClinicalNumber()).isEqualTo("NCT001");
            assertThat(project.getRegistrationNo()).isEqualTo("R001");
            assertThat(project.getPurpose()).isEqualTo("Testing purpose");
            assertThat(project.getCreateUserId()).isEqualTo("user1");
            assertThat(project.getStages()).isEmpty();
            assertThat(project.getSitePersonnel()).isEmpty();
            assertThat(project.getAdverseEventRules()).isEmpty();
            assertThat(project.getVisitPlans()).isEmpty();
            assertThat(project.getCrfBindings()).isEmpty();

            List<DomainEvent> events = project.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ProjectCreatedEvent.class);
        }

        /**
         * 验证 create() 传入 null projectId 时抛出 IllegalArgumentException。
         * 前置条件：调用 Project.create() 并将第一个参数（projectId）设置为 null。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "Project id must not be null"。
         */
        @Test
        @DisplayName("create with null projectId throws IllegalArgumentException")
        void createNullId() {
            assertThatThrownBy(() -> Project.create(
                    null, "Test", ProjectType.INTERVENTIONAL, "TT", "TT",
                    true, 100, "NCT001", "R001",
                    new java.util.Date(), new java.util.Date(), "Purpose", "user1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Project id must not be null");
        }

        /**
         * 验证 create() 传入空白 title 时抛出 IllegalArgumentException。
         * 前置条件：调用 Project.create()，title 参数设置为 "  "（仅空格）。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "Project title must not be blank"。
         */
        @Test
        @DisplayName("create with blank title throws IllegalArgumentException")
        void createBlankTitle() {
            assertThatThrownBy(() -> Project.create(
                    ProjectTestFixtures.aProjectId(), "  ", ProjectType.INTERVENTIONAL,
                    "TT", "TT", true, 100, null, null,
                    null, null, null, "user1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Project title must not be blank");
        }

        /**
         * 验证 create() 传入 null type 时抛出 IllegalArgumentException。
         * 前置条件：调用 Project.create()，type 参数设置为 null。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "Project type must not be null"。
         */
        @Test
        @DisplayName("create with null type throws IllegalArgumentException")
        void createNullType() {
            assertThatThrownBy(() -> Project.create(
                    ProjectTestFixtures.aProjectId(), "Test", null, "TT", "TT",
                    true, 100, null, null, null, null, null, "user1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Project type must not be null");
        }

        /**
         * 验证 create() 传入空白 prefix 时抛出 IllegalArgumentException。
         * 前置条件：调用 Project.create()，prefix 参数设置为 "  "（仅空格）。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "Project prefix must not be blank"。
         */
        @Test
        @DisplayName("create with blank prefix throws IllegalArgumentException")
        void createBlankPrefix() {
            assertThatThrownBy(() -> Project.create(
                    ProjectTestFixtures.aProjectId(), "Test", ProjectType.INTERVENTIONAL,
                    "TT", "  ", true, 100, null, null,
                    null, null, null, "user1"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Project prefix must not be blank");
        }

        /**
         * 验证 create() 传入空白 createUserId 时抛出 IllegalArgumentException。
         * 前置条件：调用 Project.create()，createUserId 参数设置为 "  "（仅空格）。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "Project createUserId must not be blank"。
         */
        @Test
        @DisplayName("create with blank createUserId throws IllegalArgumentException")
        void createBlankUserId() {
            assertThatThrownBy(() -> Project.create(
                    ProjectTestFixtures.aProjectId(), "Test", ProjectType.INTERVENTIONAL,
                    "TT", "TT", true, 100, null, null,
                    null, null, null, "  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Project createUserId must not be blank");
        }

        /**
         * 验证通过工厂方法 reconstruct() 恢复的 Project 对象保持所有字段值，
         * 且不产生任何领域事件（因为是从持久化数据恢复，非新建聚合）。
         * 前置条件：使用 ProjectTestFixtures.aReconstructedProject() 创建已激活的项目。
         * 预期结果：项目 ID、状态(ACTIVE)、类型(OBSERVATIONAL)均正确；
         *          阶段包含1个初始化阶段；人员、不良事件规则、访视计划、CRF 绑定均为空；
         *          pullDomainEvents() 返回空列表，确认无新事件产生。
         */
        @Test
        @DisplayName("reconstruct preserves all fields and does not raise events")
        void reconstruct() {
            Project project = ProjectTestFixtures.aReconstructedProject();

            assertThat(project.getId()).isEqualTo(ProjectTestFixtures.aProjectId());
            assertThat(project.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
            assertThat(project.getType()).isEqualTo(ProjectType.OBSERVATIONAL);
            assertThat(project.getStages()).hasSize(1);
            assertThat(project.getSitePersonnel()).isEmpty();
            assertThat(project.getAdverseEventRules()).isEmpty();
            assertThat(project.getVisitPlans()).isEmpty();
            assertThat(project.getCrfBindings()).isEmpty();

            assertThat(project.pullDomainEvents()).isEmpty();
        }
    }

    // ===============================================================
    // Activation Transitions
    // ===============================================================

    @Nested
    @DisplayName("Activation transitions")
    class ActivationTransitions {

        /**
         * 验证状态从 DRAFT 到 ACTIVE 的成功转换：当项目已配置阶段时调用 activate()，
         * 状态变更为 ACTIVE 并产生 ProjectActivatedEvent。
         * 前置条件：使用 aDraftProjectWithStage() 创建含阶段的草稿项目，先清除之前的阶段添加事件。
         * 预期结果：getStatus() 返回 ProjectStatus.ACTIVE；pullDomainEvents() 返回1个 ProjectActivatedEvent。
         */
        @Test
        @DisplayName("activate with stages transitions DRAFT to ACTIVE and raises ProjectActivatedEvent")
        void activateWithStages() {
            Project project = ProjectTestFixtures.aDraftProjectWithStage();
            project.pullDomainEvents(); // clear stage added event

            project.activate();

            assertThat(project.getStatus()).isEqualTo(ProjectStatus.ACTIVE);

            List<DomainEvent> events = project.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ProjectActivatedEvent.class);
        }

        /**
         * 验证 DRAFT 项目在未配置任何阶段时调用 activate() 被拒绝，
         * 状态保持 DRAFT 不变，抛出 BusinessRuleViolationException。
         * 前置条件：使用 aDraftProject() 创建无阶段的草稿项目。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NO_STAGES"；
         *          状态仍为 DRAFT，说明缺乏阶段的草稿项目无法激活。
         */
        @Test
        @DisplayName("activate without stages throws BusinessRuleViolationException")
        void activateWithoutStages() {
            Project project = ProjectTestFixtures.aDraftProject();
            project.pullDomainEvents(); // clear creation event

            assertThatThrownBy(project::activate)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NO_STAGES");

            assertThat(project.getStatus()).isEqualTo(ProjectStatus.DRAFT);
        }

        /**
         * 验证已是 ACTIVE 状态的项目再次调用 activate() 被拒绝，状态保持不变。
         * 前置条件：使用 anActiveProject() 创建已激活项目。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_DRAFT"；
         *          说明只有 DRAFT 状态的项目可以激活。
         */
        @Test
        @DisplayName("activate when already ACTIVE throws BusinessRuleViolationException")
        void activateWhenAlreadyActive() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents(); // clear activation event

            assertThatThrownBy(project::activate)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_DRAFT");
        }

        /**
         * 验证 CLOSED 状态的项目调用 activate() 被拒绝，状态保持 CLOSED 不变。
         * 前置条件：先创建 ACTIVE 项目，调用 close() 使其变为 CLOSED 状态。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_DRAFT"；
         *          说明已关闭的项目无法重新激活。
         */
        @Test
        @DisplayName("activate when CLOSED throws BusinessRuleViolationException")
        void activateWhenClosed() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents();
            project.close();

            assertThatThrownBy(project::activate)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_DRAFT");
        }
    }

    // ===============================================================
    // Close Transitions
    // ===============================================================

    @Nested
    @DisplayName("Close transitions")
    class CloseTransitions {

        /**
         * 验证 ACTIVE 状态的项目调用 close() 成功转换为 CLOSED 状态。
         * 前置条件：使用 anActiveProject() 创建已激活项目，清除之前的事件。
         * 预期结果：getStatus() 返回 ProjectStatus.CLOSED，说明关闭操作执行成功。
         */
        @Test
        @DisplayName("close transitions ACTIVE to CLOSED")
        void closeFromActive() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents(); // clear previous events

            project.close();

            assertThat(project.getStatus()).isEqualTo(ProjectStatus.CLOSED);
        }

        /**
         * 验证 DRAFT 状态的项目调用 close() 被拒绝，状态保持 DRAFT 不变。
         * 前置条件：使用 aDraftProject() 创建草稿项目。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_ACTIVE"；
         *          状态仍为 DRAFT，说明只有 ACTIVE 的项目可以关闭。
         */
        @Test
        @DisplayName("close from DRAFT throws BusinessRuleViolationException")
        void closeFromDraft() {
            Project project = ProjectTestFixtures.aDraftProject();
            project.pullDomainEvents();

            assertThatThrownBy(project::close)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_ACTIVE");

            assertThat(project.getStatus()).isEqualTo(ProjectStatus.DRAFT);
        }

        /**
         * 验证已是 CLOSED 状态的项目再次调用 close() 被拒绝，状态保持 CLOSED 不变。
         * 前置条件：先创建 ACTIVE 项目，调用 close() 使其变为 CLOSED 状态。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_ACTIVE"；
         *          说明已关闭的项目不能重复关闭。
         */
        @Test
        @DisplayName("close when already CLOSED throws BusinessRuleViolationException")
        void closeWhenAlreadyClosed() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents();
            project.close();

            assertThatThrownBy(project::close)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_ACTIVE");
        }
    }

    // ===============================================================
    // Stage Management
    // ===============================================================

    @Nested
    @DisplayName("Stage management")
    class StageManagement {

        /**
         * 验证 DRAFT 项目调用 addStage() 添加阶段成功：阶段加入列表并产生 StageAddedEvent。
         * 前置条件：使用 aDraftProject() 创建草稿项目，清除创建事件。
         * 预期结果：getStages() 包含1个阶段，阶段名称、重复类型、自动添加标志均匹配；
         *          pullDomainEvents() 返回1个 StageAddedEvent。
         */
        @Test
        @DisplayName("addStage while DRAFT adds stage to list and raises StageAddedEvent")
        void addStageWhileDraft() {
            Project project = ProjectTestFixtures.aDraftProject();
            project.pullDomainEvents(); // clear creation event

            project.addStage(ProjectTestFixtures.aStageId(), "Screening",
                    StageRepeatType.NONE, true,
                    ProjectTestFixtures.days3(), ProjectTestFixtures.plusMinus2(), "task1");

            assertThat(project.getStages()).hasSize(1);

            Stage stage = project.getStages().get(0);
            assertThat(stage.getId()).isEqualTo(ProjectTestFixtures.aStageId());
            assertThat(stage.getName()).isEqualTo("Screening");
            assertThat(stage.getRepeatType()).isEqualTo(StageRepeatType.NONE);
            assertThat(stage.isAutoAdd()).isTrue();

            List<DomainEvent> events = project.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(StageAddedEvent.class);
        }

        /**
         * 验证添加重复阶段名称被拒绝：当项目中已存在同名阶段时，再次添加抛出异常。
         * 前置条件：先添加名称为 "Screening" 的阶段，再尝试添加另一个同名的阶段。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "DUPLICATE_STAGE_NAME"；
         *          阶段列表大小仍为1，说明重复名称的阶段未被添加。
         */
        @Test
        @DisplayName("addStage with duplicate name throws BusinessRuleViolationException")
        void addStageDuplicateName() {
            Project project = ProjectTestFixtures.aDraftProject();
            project.addStage(new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(1L),
                    "Screening", StageRepeatType.NONE, true, null, null, null);
            project.pullDomainEvents(); // clear stage event

            assertThatThrownBy(() ->
                    project.addStage(new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(2L),
                            "Screening", StageRepeatType.NONE, true, null, null, null))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("DUPLICATE_STAGE_NAME");

            assertThat(project.getStages()).hasSize(1);
        }

        /**
         * 验证 addStage() 传入空白阶段名称时抛出 IllegalArgumentException。
         * 前置条件：调用 addStage()，name 参数设置为 "  "（仅空格）。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "Stage name must not be blank"。
         */
        @Test
        @DisplayName("addStage with blank name throws IllegalArgumentException")
        void addStageBlankName() {
            Project project = ProjectTestFixtures.aDraftProject();
            project.pullDomainEvents();

            assertThatThrownBy(() ->
                    project.addStage(ProjectTestFixtures.aStageId(), "  ",
                            StageRepeatType.NONE, true, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Stage name must not be blank");
        }

        /**
         * 验证 ACTIVE 项目无法再添加阶段：调用 addStage() 抛出 BusinessRuleViolationException。
         * 前置条件：使用 anActiveProject() 创建已激活项目。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_DRAFT"，
         *          说明阶段管理只能在 DRAFT 状态下进行。
         */
        @Test
        @DisplayName("addStage after ACTIVE throws BusinessRuleViolationException")
        void addStageAfterActive() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents();

            assertThatThrownBy(() ->
                    project.addStage(new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(99L),
                            "NewStage", StageRepeatType.NONE, true, null, null, null))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_DRAFT");
        }

        /**
         * 验证 removeStage() 成功移除项目中的已有阶段。
         * 前置条件：使用 aDraftProjectWithMultipleStages() 创建含3个阶段的草稿项目，
         *          尝试移除 ID 为2的阶段。
         * 预期结果：removeStage() 返回 true；阶段列表大小变为2；
         *          剩余阶段名称依次为 "Screening" 和 "Follow-up"，确认目标阶段被正确移除。
         */
        @Test
        @DisplayName("removeStage removes existing stage")
        void removeStageRemovesExisting() {
            Project project = ProjectTestFixtures.aDraftProjectWithMultipleStages();
            project.pullDomainEvents(); // clear stage events

            boolean removed = project.removeStage(new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(2L));

            assertThat(removed).isTrue();
            assertThat(project.getStages()).hasSize(2);
            assertThat(project.getStages())
                    .extracting(Stage::getName)
                    .containsExactly("Screening", "Follow-up");
        }

        /**
         * 验证移除不存在的阶段 ID 时 removeStage() 返回 false，列表不受影响。
         * 前置条件：使用 aDraftProjectWithStage() 创建含1个阶段的草稿项目，
         *          尝试移除 ID 为999（不存在）的阶段。
         * 预期结果：removeStage() 返回 false；阶段列表大小仍为1，说明无变更。
         */
        @Test
        @DisplayName("removeStage with non-existent stage returns false")
        void removeStageNonExistent() {
            Project project = ProjectTestFixtures.aDraftProjectWithStage();
            project.pullDomainEvents();

            boolean removed = project.removeStage(new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(999L));

            assertThat(removed).isFalse();
            assertThat(project.getStages()).hasSize(1);
        }

        /**
         * 验证 ACTIVE 项目无法移除阶段：调用 removeStage() 抛出 BusinessRuleViolationException。
         * 前置条件：使用 anActiveProject() 创建已激活项目。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_DRAFT"，
         *          说明阶段删除只能在 DRAFT 状态下进行。
         */
        @Test
        @DisplayName("removeStage after ACTIVE throws BusinessRuleViolationException")
        void removeStageAfterActive() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents();

            assertThatThrownBy(() ->
                    project.removeStage(ProjectTestFixtures.aStageId()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_DRAFT");
        }

        /**
         * 验证 getAutoAddStages() 仅返回标记为 autoAdd=true 的阶段，过滤掉 autoAdd=false 的阶段。
         * 前置条件：使用 aDraftProjectWithMultipleStages() 创建含3个阶段的草稿项目，
         *          其中 Screening 和 Treatment 的 autoAdd=true，Follow-up 的 autoAdd=false。
         * 预期结果：getAutoAddStages() 返回2个阶段，名称依次为 "Screening" 和 "Treatment"。
         */
        @Test
        @DisplayName("getAutoAddStages returns only autoAdd=true stages")
        void getAutoAddStages() {
            Project project = ProjectTestFixtures.aDraftProjectWithMultipleStages();

            List<Stage> autoAdd = project.getAutoAddStages();

            assertThat(autoAdd)
                    .hasSize(2)
                    .extracting(Stage::getName)
                    .containsExactly("Screening", "Treatment");
        }
    }

    // ===============================================================
    // Visit Plan Configuration
    // ===============================================================

    @Nested
    @DisplayName("Visit plan configuration")
    class VisitPlanConfiguration {

        /**
         * 验证 DRAFT 项目调用 configureVisitPlan() 成功添加访视计划并产生 VisitPlanConfiguredEvent。
         * 前置条件：使用 aDraftProjectWithMultipleStages() 创建草稿项目，清除先前事件。
         * 预期结果：getVisitPlans() 包含1个访视计划；pullDomainEvents() 返回1个 VisitPlanConfiguredEvent。
         */
        @Test
        @DisplayName("configureVisitPlan while DRAFT adds plan and raises VisitPlanConfiguredEvent")
        void configureVisitPlanWhileDraft() {
            Project project = ProjectTestFixtures.aDraftProjectWithMultipleStages();
            project.pullDomainEvents(); // clear stage events

            project.configureVisitPlan(
                    ProjectTestFixtures.aVisitPlanId(),
                    "Week 1 Follow-up",
                    new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(1L),
                    new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(3L),
                    ProjectTestFixtures.days3(),
                    ProjectTestFixtures.plusMinus2(),
                    "comp1");

            assertThat(project.getVisitPlans()).hasSize(1);

            List<DomainEvent> events = project.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(VisitPlanConfiguredEvent.class);
        }

        /**
         * 验证 ACTIVE 项目无法配置访视计划：调用 configureVisitPlan() 抛出 BusinessRuleViolationException。
         * 前置条件：使用 anActiveProject() 创建已激活项目。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_DRAFT"，
         *          说明访视计划配置只能在 DRAFT 状态下进行。
         */
        @Test
        @DisplayName("configureVisitPlan after ACTIVE throws BusinessRuleViolationException")
        void configureVisitPlanAfterActive() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents();

            assertThatThrownBy(() ->
                    project.configureVisitPlan(
                            ProjectTestFixtures.aVisitPlanId(),
                            "Week 1",
                            new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(1L),
                            new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(2L),
                            null, null, null))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_DRAFT");
        }
    }

    // ===============================================================
    // CRF Binding
    // ===============================================================

    @Nested
    @DisplayName("CRF binding")
    class CrfBinding {

        /**
         * 验证 DRAFT 项目调用 bindCrf() 为有效阶段绑定 CRF 成功：绑定加入列表并产生 CrfBoundToStageEvent。
         * 前置条件：使用 aDraftProjectWithMultipleStages() 创建草稿项目，清除先前事件。
         * 预期结果：getCrfBindings() 包含1个绑定，其 stageId 和 userInputEnabled 字段匹配；
         *          pullDomainEvents() 返回1个 CrfBoundToStageEvent。
         */
        @Test
        @DisplayName("bindCrf while DRAFT with valid stage adds binding and raises CrfBoundToStageEvent")
        void bindCrfWhileDraft() {
            Project project = ProjectTestFixtures.aDraftProjectWithMultipleStages();
            project.pullDomainEvents(); // clear stage events

            project.bindCrf(
                    ProjectTestFixtures.aBindingId(),
                    new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(1L),
                    ProjectTestFixtures.aCrfTemplateId(),
                    ProjectTestFixtures.aCrfVersionId(),
                    true);

            assertThat(project.getCrfBindings()).hasSize(1);

            StageCrfBinding binding = project.getCrfBindings().get(0);
            assertThat(binding.getStageId())
                    .isEqualTo(new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(1L));
            assertThat(binding.isUserInputEnabled()).isTrue();

            List<DomainEvent> events = project.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(CrfBoundToStageEvent.class);
        }

        /**
         * 验证为不存在的阶段绑定 CRF 时抛出 BusinessRuleViolationException。
         * 前置条件：使用 aDraftProjectWithMultipleStages() 创建草稿项目，
         *          尝试为不存在的 StageId(999L) 绑定 CRF。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "STAGE_NOT_FOUND"；
         *          CRF 绑定列表仍为空，说明无效阶段不会产生任何绑定。
         */
        @Test
        @DisplayName("bindCrf with non-existent stage throws BusinessRuleViolationException")
        void bindCrfWithNonExistentStage() {
            Project project = ProjectTestFixtures.aDraftProjectWithMultipleStages();
            project.pullDomainEvents();

            com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId nonExistentStageId =
                    new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(999L);

            assertThatThrownBy(() ->
                    project.bindCrf(
                            ProjectTestFixtures.aBindingId(),
                            nonExistentStageId,
                            ProjectTestFixtures.aCrfTemplateId(),
                            ProjectTestFixtures.aCrfVersionId(),
                            true))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("STAGE_NOT_FOUND");

            assertThat(project.getCrfBindings()).isEmpty();
        }

        /**
         * 验证 ACTIVE 项目无法绑定 CRF：调用 bindCrf() 抛出 BusinessRuleViolationException。
         * 前置条件：使用 anActiveProject() 创建已激活项目。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_DRAFT"，
         *          说明 CRF 绑定只能在 DRAFT 状态下进行。
         */
        @Test
        @DisplayName("bindCrf after ACTIVE throws BusinessRuleViolationException")
        void bindCrfAfterActive() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents();

            assertThatThrownBy(() ->
                    project.bindCrf(
                            ProjectTestFixtures.aBindingId(),
                            ProjectTestFixtures.aStageId(),
                            ProjectTestFixtures.aCrfTemplateId(),
                            ProjectTestFixtures.aCrfVersionId(),
                            true))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_DRAFT");
        }
    }

    // ===============================================================
    // Adverse Event Rule Configuration
    // ===============================================================

    @Nested
    @DisplayName("Adverse event rule configuration")
    class AdverseRuleConfiguration {

        /**
         * 验证 DRAFT 项目调用 configureAdverseRule() 成功添加不良事件判定规则。
         * 前置条件：使用 aDraftProjectWithMultipleStages() 创建草稿项目，清除先前事件。
         * 预期结果：getAdverseEventRules() 包含1条规则，其 ID 与传入值匹配。
         */
        @Test
        @DisplayName("configureAdverseRule while DRAFT adds rule")
        void configureAdverseRuleWhileDraft() {
            Project project = ProjectTestFixtures.aDraftProjectWithMultipleStages();
            project.pullDomainEvents();

            project.configureAdverseRule(
                    ProjectTestFixtures.aRuleId(),
                    new com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId(1L),
                    ProjectTestFixtures.aCrfTemplateId(),
                    ProjectTestFixtures.aCrfVersionId(),
                    AdverseJudgeType.FIELD_VALUE,
                    "AE001",
                    "Adverse Event Field",
                    "YES",
                    "Yes");

            assertThat(project.getAdverseEventRules()).hasSize(1);
            assertThat(project.getAdverseEventRules().get(0).getId())
                    .isEqualTo(ProjectTestFixtures.aRuleId());
        }

        /**
         * 验证 ACTIVE 项目无法配置不良事件规则：调用 configureAdverseRule() 抛出 BusinessRuleViolationException。
         * 前置条件：使用 anActiveProject() 创建已激活项目。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_DRAFT"，
         *          说明不良事件规则配置只能在 DRAFT 状态下进行。
         */
        @Test
        @DisplayName("configureAdverseRule after ACTIVE throws BusinessRuleViolationException")
        void configureAdverseRuleAfterActive() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents();

            assertThatThrownBy(() ->
                    project.configureAdverseRule(
                            ProjectTestFixtures.aRuleId(),
                            ProjectTestFixtures.aStageId(),
                            ProjectTestFixtures.aCrfTemplateId(),
                            ProjectTestFixtures.aCrfVersionId(),
                            AdverseJudgeType.FIELD_VALUE,
                            "AE001", null, "YES", null))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_DRAFT");
        }
    }

    // ===============================================================
    // Personnel Assignment
    // ===============================================================

    @Nested
    @DisplayName("Personnel assignment")
    class PersonnelAssignment {

        /**
         * 验证 DRAFT 项目调用 assignPersonnel() 成功分配人员。
         * 前置条件：使用 aDraftProject() 创建草稿项目，清除创建事件。
         * 预期结果：getSitePersonnel() 包含1条人员记录，其 userId、siteId、role 均与传入值匹配。
         */
        @Test
        @DisplayName("assignPersonnel while DRAFT adds personnel")
        void assignPersonnelWhileDraft() {
            Project project = ProjectTestFixtures.aDraftProject();
            project.pullDomainEvents();

            project.assignPersonnel(
                    ProjectTestFixtures.aPersonnelId(),
                    1001L,
                    2001L,
                    SiteRole.PI);

            assertThat(project.getSitePersonnel()).hasSize(1);
            assertThat(project.getSitePersonnel().get(0).getUserId()).isEqualTo(1001L);
            assertThat(project.getSitePersonnel().get(0).getSiteId()).isEqualTo(2001L);
            assertThat(project.getSitePersonnel().get(0).getRole()).isEqualTo(SiteRole.PI);
        }

        /**
         * 验证 ACTIVE 项目无法分配人员：调用 assignPersonnel() 抛出 BusinessRuleViolationException。
         * 前置条件：使用 anActiveProject() 创建已激活项目。
         * 预期结果：抛出 BusinessRuleViolationException，异常消息包含 "PROJECT_NOT_DRAFT"，
         *          说明人员分配只能在 DRAFT 状态下进行。
         */
        @Test
        @DisplayName("assignPersonnel after ACTIVE throws BusinessRuleViolationException")
        void assignPersonnelAfterActive() {
            Project project = ProjectTestFixtures.anActiveProject();
            project.pullDomainEvents();

            assertThatThrownBy(() ->
                    project.assignPersonnel(
                            ProjectTestFixtures.aPersonnelId(),
                            1001L, 2001L, SiteRole.CRC))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PROJECT_NOT_DRAFT");
        }
    }

    // ===============================================================
    // Helper methods and cross-cutting
    // ===============================================================

    @Nested
    @DisplayName("Cross-cutting")
    class CrossCutting {

        /**
         * 验证 getStages()、getSitePersonnel()、getAdverseEventRules()、getVisitPlans()、
         * getCrfBindings() 返回的列表均为不可修改列表（unmodifiable）。
         * 前置条件：使用 aDraftProjectWithMultipleStages() 创建含阶段的草稿项目。
         * 预期结果：对上述任一列表调用 clear()（或任何修改操作）均抛出 UnsupportedOperationException，
         *          说明聚合内部列表对外暴露的是只读视图，防止外部直接修改内部状态。
         */
        @Test
        @DisplayName("stages, sitePersonnel, adverseEventRules return unmodifiable lists")
        void listsAreUnmodifiable() {
            Project project = ProjectTestFixtures.aDraftProjectWithMultipleStages();
            project.pullDomainEvents();

            assertThatThrownBy(() -> project.getStages().clear())
                    .isInstanceOf(UnsupportedOperationException.class);

            // Empty lists should also be unmodifiable
            assertThatThrownBy(() -> project.getSitePersonnel().clear())
                    .isInstanceOf(UnsupportedOperationException.class);

            assertThatThrownBy(() -> project.getAdverseEventRules().clear())
                    .isInstanceOf(UnsupportedOperationException.class);

            assertThatThrownBy(() -> project.getVisitPlans().clear())
                    .isInstanceOf(UnsupportedOperationException.class);

            assertThatThrownBy(() -> project.getCrfBindings().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * 验证 Project 的 equals() 和 hashCode() 基于身份（ProjectId）比较，而非对象引用。
         * 前置条件：创建三个项目——project1 和 project2 具有相同 ID 但不同标题；
         *          project3 具有不同 ID。
         * 预期结果：project1 与 project2 相等且 hashCode 一致（即使内容不同，因 ID 相同）；
         *          project1 与 project3 不相等且 hashCode 不同。
         *          说明聚合根的等价性由其业务标识唯一决定。
         */
        @Test
        @DisplayName("equals and hashCode use identity")
        void equalsAndHashCodeByIdentity() {
            Project project1 = ProjectTestFixtures.aDraftProject();
            Project project2 = Project.create(
                    ProjectTestFixtures.aProjectId(),
                    "Different Title",
                    ProjectType.OBSERVATIONAL,
                    "DT", "DT", false, null,
                    null, null, null, null,
                    "Another purpose", "user2");

            assertThat(project1)
                    .isEqualTo(project2)
                    .hasSameHashCodeAs(project2);

            Project project3 = Project.create(
                    new com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId(999L),
                    "Third", ProjectType.INTERVENTIONAL,
                    "TH", "TH", true, null,
                    null, null, null, null,
                    "Third", "user3");

            assertThat(project1).isNotEqualTo(project3);
        }
    }
}
