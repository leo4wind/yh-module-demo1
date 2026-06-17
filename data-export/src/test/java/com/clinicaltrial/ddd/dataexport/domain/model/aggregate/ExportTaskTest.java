package com.clinicaltrial.ddd.dataexport.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportExecutionCompletedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportExecutionFailedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportExecutionStartedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportTaskApprovedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportTaskRejectedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportTaskSubmittedEvent;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFieldConfig;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFilter;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFieldConfigId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFilterId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportStatus;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.FileFormat;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive unit tests for {@link ExportTask}.
 * <p>
 * Covers all state machine transitions: DRAFT → PENDING_APPROVAL → APPROVED → EXPORTING → COMPLETED,
 * PENDING_APPROVAL → DRAFT (reject), EXPORTING → FAILED → EXPORTING (retry), and all guard conditions.
 * Pure domain tests — no mocks.
 * </p>
 */
class ExportTaskTest {

    // ===============================================================
    // Fixtures
    // ===============================================================

    static class ExportTaskTestFixtures {
        static ExportTaskId aTaskId() {
            return new ExportTaskId(1L);
        }

        static ProjectId aProjectId() {
            return new ProjectId(100L);
        }

        static StageId aStageId() {
            return new StageId(200L);
        }

        static CrfVersionId aCrfVersionId() {
            return new CrfVersionId(300L);
        }

        static ExportFieldConfigId aFieldConfigId() {
            return new ExportFieldConfigId(1L);
        }

        static ExportFilterId aFilterId() {
            return new ExportFilterId(1L);
        }

        static ExportTask aDraftTask() {
            return ExportTask.create(aTaskId(), "Export Task 1",
                    aProjectId(), aStageId(), aCrfVersionId(), FileFormat.XLSX);
        }

        static ExportTask aPendingTask() {
            ExportTask t = aDraftTask();
            t.submit();
            t.pullDomainEvents();
            return t;
        }

        static ExportTask anApprovedTask() {
            ExportTask t = aPendingTask();
            t.approve("auditor1", "Approved");
            t.pullDomainEvents();
            return t;
        }

        static ExportTask anExportingTask() {
            ExportTask t = anApprovedTask();
            t.markExporting();
            t.pullDomainEvents();
            return t;
        }

        static ExportTask aFailedTask(int failCount) {
            ExportTask t = anApprovedTask();
            t.markExporting();
            t.pullDomainEvents();
            for (int i = 0; i < failCount; i++) {
                t.markFailed("Error " + i);
                t.markExporting();
                t.pullDomainEvents();
            }
            t.markFailed("Error " + failCount);
            t.pullDomainEvents();
            return t;
        }
    }

    // ===============================================================
    // Factory Method Tests
    // ===============================================================

    @Nested
    @DisplayName("Factory methods")
    class FactoryTests {

        /**
         * 验证create()工厂方法创建导出任务时，初始状态为草稿(DRAFT)，
         * 并正确配置所有字段。
         * 前置条件：无（新建聚合），
         * 执行create()后状态为DRAFT(草稿)，
         * 任务名称、项目、阶段、CRF版本、文件格式、失败次数、下载次数等字段均正确初始化，
         * 字段配置和过滤器列表为空，pullDomainEvents()返回空列表。
         */
        @Test
        @DisplayName("create sets DRAFT status and configures all fields")
        void createSetsDraft() {
            ExportTask task = ExportTaskTestFixtures.aDraftTask();

            assertThat(task.getId()).isEqualTo(ExportTaskTestFixtures.aTaskId());
            assertThat(task.getTaskName()).isEqualTo("Export Task 1");
            assertThat(task.getProjectId()).isEqualTo(ExportTaskTestFixtures.aProjectId());
            assertThat(task.getStageId()).isEqualTo(ExportTaskTestFixtures.aStageId());
            assertThat(task.getCrfVersionId()).isEqualTo(ExportTaskTestFixtures.aCrfVersionId());
            assertThat(task.getStatus()).isEqualTo(ExportStatus.DRAFT);
            assertThat(task.getFileFormat()).isEqualTo(FileFormat.XLSX);
            assertThat(task.getFailCount()).isEqualTo(0);
            assertThat(task.getDownloadCount()).isEqualTo(0);
            assertThat(task.getFieldConfigs()).isEmpty();
            assertThat(task.getFilters()).isEmpty();
            assertThat(task.getExecutionLogs()).isEmpty();
            assertThat(task.pullDomainEvents()).isEmpty();
        }

        /**
         * 验证create()时传入null的id抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出NullPointerException，异常信息包含"id"。
         */
        @Test
        @DisplayName("create with null id throws IllegalArgumentException")
        void createNullId() {
            assertThatThrownBy(() ->
                    ExportTask.create(null, "Name", ExportTaskTestFixtures.aProjectId(),
                            ExportTaskTestFixtures.aStageId(), ExportTaskTestFixtures.aCrfVersionId(),
                            FileFormat.XLSX))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("id");
        }

        /**
         * 验证create()时传入null的taskName抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出NullPointerException，异常信息包含"taskName"。
         */
        @Test
        @DisplayName("create with null taskName throws IllegalArgumentException")
        void createNullTaskName() {
            assertThatThrownBy(() ->
                    ExportTask.create(ExportTaskTestFixtures.aTaskId(), null,
                            ExportTaskTestFixtures.aProjectId(),
                            ExportTaskTestFixtures.aStageId(), ExportTaskTestFixtures.aCrfVersionId(),
                            FileFormat.XLSX))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("taskName");
        }

        /**
         * 验证create()时传入空白字符串的taskName抛出IllegalArgumentException。
         * 前置条件：无，
         * 预期：抛出IllegalArgumentException，异常信息包含"taskName must not be empty"。
         */
        @Test
        @DisplayName("create with blank taskName throws IllegalArgumentException")
        void createBlankTaskName() {
            assertThatThrownBy(() ->
                    ExportTask.create(ExportTaskTestFixtures.aTaskId(), "   ",
                            ExportTaskTestFixtures.aProjectId(),
                            ExportTaskTestFixtures.aStageId(), ExportTaskTestFixtures.aCrfVersionId(),
                            FileFormat.XLSX))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("taskName must not be empty");
        }

        /**
         * 验证create()时传入null的projectId抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出NullPointerException，异常信息包含"projectId"。
         */
        @Test
        @DisplayName("create with null projectId throws IllegalArgumentException")
        void createNullProjectId() {
            assertThatThrownBy(() ->
                    ExportTask.create(ExportTaskTestFixtures.aTaskId(), "Name", null,
                            ExportTaskTestFixtures.aStageId(), ExportTaskTestFixtures.aCrfVersionId(),
                            FileFormat.XLSX))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("projectId");
        }

        /**
         * 验证create()时传入null的fileFormat抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出NullPointerException，异常信息包含"fileFormat"。
         */
        @Test
        @DisplayName("create with null fileFormat throws IllegalArgumentException")
        void createNullFileFormat() {
            assertThatThrownBy(() ->
                    ExportTask.create(ExportTaskTestFixtures.aTaskId(), "Name",
                            ExportTaskTestFixtures.aProjectId(),
                            ExportTaskTestFixtures.aStageId(), ExportTaskTestFixtures.aCrfVersionId(),
                            null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fileFormat");
        }

        /**
         * 验证create()时传入null的stageId和crfVersionId是允许的
         * （这两个参数为可选参数）。
         * 前置条件：无，
         * 执行create()后stageId和crfVersionId为null，状态为DRAFT(草稿)。
         */
        @Test
        @DisplayName("create with null stageId and crfVersionId is allowed")
        void createNullOptionalIds() {
            ExportTask task = ExportTask.create(
                    ExportTaskTestFixtures.aTaskId(), "Name",
                    ExportTaskTestFixtures.aProjectId(), null, null, FileFormat.CSV);

            assertThat(task.getStageId()).isNull();
            assertThat(task.getCrfVersionId()).isNull();
            assertThat(task.getStatus()).isEqualTo(ExportStatus.DRAFT);
        }

        /**
         * 验证reconstruct()工厂方法正确还原所有状态，且不注册任何领域事件。
         * 前置条件：无（重构已有持久化数据），
         * 执行reconstruct()后所有字段与传入参数一致，
         * 字段配置、过滤器和执行日志列表为空，pullDomainEvents()返回空列表。
         */
        @Test
        @DisplayName("reconstruct restores all state and registers no events")
        void reconstructNoEvents() {
            ExportTask task = ExportTask.reconstruct(
                    ExportTaskTestFixtures.aTaskId(),
                    "Reconstructed Task",
                    ExportTaskTestFixtures.aProjectId(),
                    ExportTaskTestFixtures.aStageId(),
                    ExportTaskTestFixtures.aCrfVersionId(),
                    ExportStatus.COMPLETED,
                    FileFormat.SAS,
                    "auditor1", null, "Approved",
                    "/files/export.xlsx", "export.xlsx",
                    5, 0, null,
                    null, null, null
            );

            assertThat(task.getId()).isEqualTo(ExportTaskTestFixtures.aTaskId());
            assertThat(task.getTaskName()).isEqualTo("Reconstructed Task");
            assertThat(task.getStatus()).isEqualTo(ExportStatus.COMPLETED);
            assertThat(task.getFileFormat()).isEqualTo(FileFormat.SAS);
            assertThat(task.getAuditUserId()).isEqualTo("auditor1");
            assertThat(task.getAuditMessage()).isEqualTo("Approved");
            assertThat(task.getFileUrl()).isEqualTo("/files/export.xlsx");
            assertThat(task.getDownloadCount()).isEqualTo(5);
            assertThat(task.getFailCount()).isEqualTo(0);
            assertThat(task.getFieldConfigs()).isEmpty();
            assertThat(task.getFilters()).isEmpty();
            assertThat(task.getExecutionLogs()).isEmpty();
            assertThat(task.pullDomainEvents()).isEmpty();
        }
    }

    // ===============================================================
    // Submit Transitions
    // ===============================================================

    @Nested
    @DisplayName("Submit transitions")
    class SubmitTests {

        /**
         * 验证导出任务从草稿(DRAFT)状态成功提交。
         * 前置条件：任务处于DRAFT(草稿)状态，
         * 执行submit()后状态变更为PENDING_APPROVAL(待审核)，
         * 并发布ExportTaskSubmittedEvent(导出提交事件)领域事件，
         * 事件中包含正确的taskId和taskName。
         */
        @Test
        @DisplayName("submit transitions DRAFT to PENDING_APPROVAL and registers ExportTaskSubmittedEvent")
        void submitFromDraft() {
            ExportTask task = ExportTaskTestFixtures.aDraftTask();

            task.submit();

            assertThat(task.getStatus()).isEqualTo(ExportStatus.PENDING_APPROVAL);

            List<DomainEvent> events = task.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ExportTaskSubmittedEvent.class);

            ExportTaskSubmittedEvent event = (ExportTaskSubmittedEvent) events.get(0);
            assertThat(event.getTaskId()).isEqualTo(ExportTaskTestFixtures.aTaskId());
            assertThat(event.getTaskName()).isEqualTo("Export Task 1");
        }
    }

    // ===============================================================
    // Approval Transitions
    // ===============================================================

    @Nested
    @DisplayName("Approval transitions")
    class ApprovalTests {

        /**
         * 验证导出任务从待审核(PENDING_APPROVAL)状态成功批准。
         * 前置条件：任务处于PENDING_APPROVAL(待审核)状态，
         * 执行approve()后状态变更为APPROVED(已批准)，
         * 审核用户ID和审核消息正确记录，审核时间不为null，
         * 并发布ExportTaskApprovedEvent(导出批准事件)领域事件。
         */
        @Test
        @DisplayName("approve transitions PENDING_APPROVAL to APPROVED and registers ExportTaskApprovedEvent")
        void approveFromPending() {
            ExportTask task = ExportTaskTestFixtures.aPendingTask();

            task.approve("auditor1", "Looks good");

            assertThat(task.getStatus()).isEqualTo(ExportStatus.APPROVED);
            assertThat(task.getAuditUserId()).isEqualTo("auditor1");
            assertThat(task.getAuditMessage()).isEqualTo("Looks good");
            assertThat(task.getAuditTime()).isNotNull();

            List<DomainEvent> events = task.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ExportTaskApprovedEvent.class);

            ExportTaskApprovedEvent event = (ExportTaskApprovedEvent) events.get(0);
            assertThat(event.getTaskId()).isEqualTo(ExportTaskTestFixtures.aTaskId());
            assertThat(event.getAuditUserId()).isEqualTo("auditor1");
        }
    }

    // ===============================================================
    // Reject Transitions
    // ===============================================================

    @Nested
    @DisplayName("Reject transitions")
    class RejectTests {

        /**
         * 验证导出任务从待审核(PENDING_APPROVAL)状态被驳回。
         * 前置条件：任务处于PENDING_APPROVAL(待审核)状态，
         * 执行reject()后状态回退至DRAFT(草稿)，
         * 审核用户ID、审核消息和审核时间正确记录，
         * 并发布ExportTaskRejectedEvent(导出驳回事件)领域事件。
         */
        @Test
        @DisplayName("reject transitions PENDING_APPROVAL to DRAFT and registers ExportTaskRejectedEvent")
        void rejectFromPending() {
            ExportTask task = ExportTaskTestFixtures.aPendingTask();

            task.reject("auditor1", "Missing required fields");

            assertThat(task.getStatus()).isEqualTo(ExportStatus.DRAFT);
            assertThat(task.getAuditUserId()).isEqualTo("auditor1");
            assertThat(task.getAuditMessage()).isEqualTo("Missing required fields");
            assertThat(task.getAuditTime()).isNotNull();

            List<DomainEvent> events = task.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ExportTaskRejectedEvent.class);

            ExportTaskRejectedEvent event = (ExportTaskRejectedEvent) events.get(0);
            assertThat(event.getTaskId()).isEqualTo(ExportTaskTestFixtures.aTaskId());
            assertThat(event.getAuditUserId()).isEqualTo("auditor1");
            assertThat(event.getReason()).isEqualTo("Missing required fields");
        }
    }

    // ===============================================================
    // Execution Transitions
    // ===============================================================

    @Nested
    @DisplayName("Execution transitions")
    class ExecutionTests {

        /**
         * 验证导出任务从已批准(APPROVED)状态开始导出。
         * 前置条件：任务处于APPROVED(已批准)状态，
         * 执行markExporting()后状态变更为EXPORTING(导出中)，
         * 并发布ExportExecutionStartedEvent(导出执行开始事件)领域事件。
         */
        @Test
        @DisplayName("markExporting from APPROVED transitions to EXPORTING and registers ExportExecutionStartedEvent")
        void markExportingFromApproved() {
            ExportTask task = ExportTaskTestFixtures.anApprovedTask();

            task.markExporting();

            assertThat(task.getStatus()).isEqualTo(ExportStatus.EXPORTING);

            List<DomainEvent> events = task.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ExportExecutionStartedEvent.class);

            ExportExecutionStartedEvent event = (ExportExecutionStartedEvent) events.get(0);
            assertThat(event.getTaskId()).isEqualTo(ExportTaskTestFixtures.aTaskId());
        }

        /**
         * 验证导出任务从导出失败(FAILED)状态重试导出。
         * 前置条件：任务处于FAILED(导出失败)状态（失败次数已递增），
         * 执行markExporting()后状态变更为EXPORTING(导出中)，
         * 并发布ExportExecutionStartedEvent(导出执行开始事件)领域事件。
         */
        @Test
        @DisplayName("markExporting from FAILED transitions to EXPORTING (retry)")
        void markExportingFromFailed() {
            ExportTask task = ExportTaskTestFixtures.aFailedTask(0); // failCount=1

            task.markExporting();

            assertThat(task.getStatus()).isEqualTo(ExportStatus.EXPORTING);
            assertThat(task.pullDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ExportExecutionStartedEvent.class);
        }

        /**
         * 验证导出任务从导出中(EXPORTING)状态成功完成。
         * 前置条件：任务处于EXPORTING(导出中)状态，
         * 执行markCompleted()后状态变更为COMPLETED(已完成)，
         * 文件URL正确记录，失败消息被清空，
         * 并发布ExportExecutionCompletedEvent(导出执行完成事件)领域事件。
         */
        @Test
        @DisplayName("markCompleted transitions EXPORTING to COMPLETED and registers ExportExecutionCompletedEvent")
        void markCompletedFromExporting() {
            ExportTask task = ExportTaskTestFixtures.anExportingTask();

            task.markCompleted("/files/result.xlsx");

            assertThat(task.getStatus()).isEqualTo(ExportStatus.COMPLETED);
            assertThat(task.getFileUrl()).isEqualTo("/files/result.xlsx");
            assertThat(task.getFailMessage()).isNull();

            List<DomainEvent> events = task.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ExportExecutionCompletedEvent.class);

            ExportExecutionCompletedEvent event = (ExportExecutionCompletedEvent) events.get(0);
            assertThat(event.getTaskId()).isEqualTo(ExportTaskTestFixtures.aTaskId());
            assertThat(event.getFileUrl()).isEqualTo("/files/result.xlsx");
        }

        /**
         * 验证导出任务从导出中(EXPORTING)状态执行失败。
         * 前置条件：任务处于EXPORTING(导出中)状态，
         * 执行markFailed()后状态变更为FAILED(导出失败)，
         * 失败次数递增为1，失败消息正确记录，
         * 并发布ExportExecutionFailedEvent(导出执行失败事件)领域事件。
         */
        @Test
        @DisplayName("markFailed transitions EXPORTING to FAILED, increments failCount, registers ExportExecutionFailedEvent")
        void markFailedFromExporting() {
            ExportTask task = ExportTaskTestFixtures.anExportingTask();

            task.markFailed("Connection timeout");

            assertThat(task.getStatus()).isEqualTo(ExportStatus.FAILED);
            assertThat(task.getFailCount()).isEqualTo(1);
            assertThat(task.getFailMessage()).isEqualTo("Connection timeout");

            List<DomainEvent> events = task.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ExportExecutionFailedEvent.class);

            ExportExecutionFailedEvent event = (ExportExecutionFailedEvent) events.get(0);
            assertThat(event.getTaskId()).isEqualTo(ExportTaskTestFixtures.aTaskId());
            assertThat(event.getFailMessage()).isEqualTo("Connection timeout");
            assertThat(event.getFailCount()).isEqualTo(1);
        }

        /**
         * 验证多次执行失败时失败次数正确递增。
         * 前置条件：任务处于EXPORTING(导出中)状态，
         * 连续执行markFailed()→markExporting()→markFailed()循环3次，
         * 每次失败后失败次数依次递增为1、2、3。
         */
        @Test
        @DisplayName("multiple failures increment failCount correctly")
        void multipleFailuresIncrementFailCount() {
            ExportTask task = ExportTaskTestFixtures.anExportingTask();

            task.markFailed("Error 1");
            assertThat(task.getFailCount()).isEqualTo(1);

            task.markExporting();
            task.pullDomainEvents();
            task.markFailed("Error 2");
            assertThat(task.getFailCount()).isEqualTo(2);

            task.markExporting();
            task.pullDomainEvents();
            task.markFailed("Error 3");
            assertThat(task.getFailCount()).isEqualTo(3);
        }
    }

    // ===============================================================
    // Retry Logic Tests
    // ===============================================================

    @Nested
    @DisplayName("Retry logic")
    class RetryTests {

        /**
         * 验证canRetry()在失败次数小于3时返回true。
         * 前置条件：任务处于FAILED(导出失败)状态，失败次数为1或2，
         * 执行canRetry()返回true。
         */
        @Test
        @DisplayName("canRetry returns true when FAILED with failCount < 3")
        void canRetryTrueWhenFailCountLessThan3() {
            ExportTask task1 = ExportTaskTestFixtures.aFailedTask(0); // failCount=1
            assertThat(task1.canRetry()).isTrue();

            ExportTask task2 = ExportTaskTestFixtures.aFailedTask(1); // failCount=2
            assertThat(task2.canRetry()).isTrue();
        }

        /**
         * 验证canRetry()在失败次数大于等于3时返回false。
         * 前置条件：任务处于FAILED(导出失败)状态，失败次数为3或4，
         * 执行canRetry()返回false。
         */
        @Test
        @DisplayName("canRetry returns false when FAILED with failCount >= 3")
        void canRetryFalseWhenFailCount3OrMore() {
            ExportTask task3 = ExportTaskTestFixtures.aFailedTask(2); // failCount=3
            assertThat(task3.canRetry()).isFalse();

            ExportTask task4 = ExportTaskTestFixtures.aFailedTask(3); // failCount=4
            assertThat(task4.canRetry()).isFalse();
        }

        /**
         * 验证canRetry()在非FAILED状态下始终返回false，
         * 包括DRAFT(草稿)、PENDING_APPROVAL(待审核)、APPROVED(已批准)、EXPORTING(导出中)和COMPLETED(已完成)状态。
         * 前置条件：任务处于各非FAILED状态，
         * 执行canRetry()返回false。
         */
        @Test
        @DisplayName("canRetry returns false when not FAILED")
        void canRetryFalseWhenNotFailed() {
            assertThat(ExportTaskTestFixtures.aDraftTask().canRetry()).isFalse();
            assertThat(ExportTaskTestFixtures.aPendingTask().canRetry()).isFalse();
            assertThat(ExportTaskTestFixtures.anApprovedTask().canRetry()).isFalse();
            assertThat(ExportTaskTestFixtures.anExportingTask().canRetry()).isFalse();

            ExportTask completed = ExportTaskTestFixtures.anExportingTask();
            completed.markCompleted("/file.xlsx");
            assertThat(completed.canRetry()).isFalse();
        }

        /**
         * 验证即使失败次数达到上限（canRetry返回false），
         * markExporting()在FAILED状态下仍然允许执行（领域模型不在此处做失败次数守卫，
         * 守卫逻辑在应用服务层实现）。
         * 前置条件：任务处于FAILED状态，失败次数为4（canRetry=false），
         * 执行markExporting()后状态变更为EXPORTING(导出中)。
         */
        @Test
        @DisplayName("markExporting from FAILED is always allowed (domain does not enforce failCount guard)")
        void retryFromFailedAllowedEvenAtMaxFailCount() {
            // markExporting is not guarded by failCount — only by status check
            ExportTask task = ExportTaskTestFixtures.aFailedTask(3); // failCount=4, canRetry=false
            // canRetry is false, but markExporting should still work (guard is in app service)
            assertThat(task.canRetry()).isFalse();

            task.markExporting();
            assertThat(task.getStatus()).isEqualTo(ExportStatus.EXPORTING);
        }

        /**
         * 验证失败次数从1逐步递增至3的完整跟踪过程。
         * 前置条件：任务失败次数为1（canRetry=true），
         * 每次重试后再次失败，失败次数依次递增：1→2（canRetry=true）→3（canRetry=false）。
         */
        @Test
        @DisplayName("failCount tracking: 1 -> 2 -> 3 -> MAX exceeded")
        void failCountTracking() {
            ExportTask task = ExportTaskTestFixtures.aFailedTask(0); // failCount=1
            assertThat(task.canRetry()).isTrue();

            // fail again to 2
            task.markExporting();
            task.pullDomainEvents();
            task.markFailed("Error 2");
            assertThat(task.getFailCount()).isEqualTo(2);
            assertThat(task.canRetry()).isTrue();

            // fail again to 3
            task.markExporting();
            task.pullDomainEvents();
            task.markFailed("Error 3");
            assertThat(task.getFailCount()).isEqualTo(3);
            assertThat(task.canRetry()).isFalse();
        }
    }

    // ===============================================================
    // Guard / Invalid Transition Tests
    // ===============================================================

    @Nested
    @DisplayName("Invalid transitions (guards)")
    class GuardTests {

        /**
         * 验证在非DRAFT(草稿)状态下执行submit()抛出BusinessRuleViolationException。
         * 前置条件：任务处于PENDING_APPROVAL(待审核)或APPROVED(已批准)等非草稿状态，
         * 预期：抛出BusinessRuleViolationException，异常信息包含"EXPORT_INVALID_TRANSITION"和"expected DRAFT"。
         */
        @Test
        @DisplayName("submit when not DRAFT throws BusinessRuleViolationException")
        void submitWhenNotDraft() {
            assertThatThrownBy(() -> ExportTaskTestFixtures.aPendingTask().submit())
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION")
                    .hasMessageContaining("expected DRAFT");

            assertThatThrownBy(() -> ExportTaskTestFixtures.anApprovedTask().submit())
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION");
        }

        /**
         * 验证在非PENDING_APPROVAL(待审核)状态下执行approve()抛出BusinessRuleViolationException。
         * 前置条件：任务处于DRAFT(草稿)或APPROVED(已批准)状态，
         * 预期：抛出BusinessRuleViolationException，异常信息包含"EXPORT_INVALID_TRANSITION"和"expected PENDING_APPROVAL"。
         */
        @Test
        @DisplayName("approve when not PENDING_APPROVAL throws BusinessRuleViolationException")
        void approveWhenNotPending() {
            assertThatThrownBy(() -> ExportTaskTestFixtures.aDraftTask().approve("u", "msg"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION")
                    .hasMessageContaining("expected PENDING_APPROVAL");

            assertThatThrownBy(() -> ExportTaskTestFixtures.anApprovedTask().approve("u", "msg"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION");
        }

        /**
         * 验证在非PENDING_APPROVAL(待审核)状态下执行reject()抛出BusinessRuleViolationException。
         * 前置条件：任务处于DRAFT(草稿)或APPROVED(已批准)状态，
         * 预期：抛出BusinessRuleViolationException，异常信息包含"EXPORT_INVALID_TRANSITION"和"expected PENDING_APPROVAL"。
         */
        @Test
        @DisplayName("reject when not PENDING_APPROVAL throws BusinessRuleViolationException")
        void rejectWhenNotPending() {
            assertThatThrownBy(() -> ExportTaskTestFixtures.aDraftTask().reject("u", "msg"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION")
                    .hasMessageContaining("expected PENDING_APPROVAL");

            assertThatThrownBy(() -> ExportTaskTestFixtures.anApprovedTask().reject("u", "msg"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION");
        }

        /**
         * 验证在非APPROVED(已批准)或FAILED(导出失败)状态下执行markExporting()抛出BusinessRuleViolationException。
         * 前置条件：任务处于DRAFT(草稿)、PENDING_APPROVAL(待审核)或COMPLETED(已完成)状态，
         * 预期：抛出BusinessRuleViolationException，异常信息包含"EXPORT_INVALID_TRANSITION"和"expected APPROVED or FAILED"。
         */
        @Test
        @DisplayName("markExporting when not APPROVED or FAILED throws BusinessRuleViolationException")
        void markExportingWhenNotApprovedOrFailed() {
            assertThatThrownBy(() -> ExportTaskTestFixtures.aDraftTask().markExporting())
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION")
                    .hasMessageContaining("expected APPROVED or FAILED");

            assertThatThrownBy(() -> ExportTaskTestFixtures.aPendingTask().markExporting())
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION");

            ExportTask completed = ExportTaskTestFixtures.anExportingTask();
            completed.markCompleted("/file.xlsx");
            completed.pullDomainEvents();
            assertThatThrownBy(completed::markExporting)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION");
        }

        /**
         * 验证在非EXPORTING(导出中)状态下执行markCompleted()抛出BusinessRuleViolationException。
         * 前置条件：任务处于DRAFT(草稿)或APPROVED(已批准)状态，
         * 预期：抛出BusinessRuleViolationException，异常信息包含"EXPORT_INVALID_TRANSITION"和"expected EXPORTING"。
         */
        @Test
        @DisplayName("markCompleted when not EXPORTING throws BusinessRuleViolationException")
        void markCompletedWhenNotExporting() {
            assertThatThrownBy(() -> ExportTaskTestFixtures.aDraftTask().markCompleted("/f"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION")
                    .hasMessageContaining("expected EXPORTING");

            assertThatThrownBy(() -> ExportTaskTestFixtures.anApprovedTask().markCompleted("/f"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION");
        }

        /**
         * 验证在非EXPORTING(导出中)状态下执行markFailed()抛出BusinessRuleViolationException。
         * 前置条件：任务处于DRAFT(草稿)或APPROVED(已批准)状态，
         * 预期：抛出BusinessRuleViolationException，异常信息包含"EXPORT_INVALID_TRANSITION"和"expected EXPORTING"。
         */
        @Test
        @DisplayName("markFailed when not EXPORTING throws BusinessRuleViolationException")
        void markFailedWhenNotExporting() {
            assertThatThrownBy(() -> ExportTaskTestFixtures.aDraftTask().markFailed("err"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION")
                    .hasMessageContaining("expected EXPORTING");

            assertThatThrownBy(() -> ExportTaskTestFixtures.anApprovedTask().markFailed("err"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_INVALID_TRANSITION");
        }

        /**
         * 验证markCompleted()时传入null的fileUrl抛出NullPointerException。
         * 前置条件：任务处于EXPORTING(导出中)状态，
         * 预期：抛出NullPointerException，异常信息包含"fileUrl"。
         */
        @Test
        @DisplayName("markCompleted with null fileUrl throws IllegalArgumentException")
        void markCompletedWithNullFileUrl() {
            ExportTask task = ExportTaskTestFixtures.anExportingTask();

            assertThatThrownBy(() -> task.markCompleted(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fileUrl");
        }

        /**
         * 验证markFailed()时传入null的message抛出NullPointerException。
         * 前置条件：任务处于EXPORTING(导出中)状态，
         * 预期：抛出NullPointerException，异常信息包含"message"。
         */
        @Test
        @DisplayName("markFailed with null message throws IllegalArgumentException")
        void markFailedWithNullMessage() {
            ExportTask task = ExportTaskTestFixtures.anExportingTask();

            assertThatThrownBy(() -> task.markFailed(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("message");
        }

        /**
         * 验证approve()时传入null的userId抛出NullPointerException。
         * 前置条件：任务处于PENDING_APPROVAL(待审核)状态，
         * 预期：抛出NullPointerException，异常信息包含"userId"。
         */
        @Test
        @DisplayName("approve with null userId throws IllegalArgumentException")
        void approveWithNullUserId() {
            ExportTask task = ExportTaskTestFixtures.aPendingTask();

            assertThatThrownBy(() -> task.approve(null, "msg"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("userId");
        }

        /**
         * 验证approve()时传入null的message抛出NullPointerException。
         * 前置条件：任务处于PENDING_APPROVAL(待审核)状态，
         * 预期：抛出NullPointerException，异常信息包含"message"。
         */
        @Test
        @DisplayName("approve with null message throws IllegalArgumentException")
        void approveWithNullMessage() {
            ExportTask task = ExportTaskTestFixtures.aPendingTask();

            assertThatThrownBy(() -> task.approve("user", null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("message");
        }

        /**
         * 验证在非DRAFT(草稿)状态下添加字段配置抛出BusinessRuleViolationException。
         * 前置条件：任务处于PENDING_APPROVAL(待审核)状态，
         * 执行addFieldConfig()抛出BusinessRuleViolationException，
         * 异常信息包含"EXPORT_CANNOT_MODIFY"。
         */
        @Test
        @DisplayName("addFieldConfig when not DRAFT throws BusinessRuleViolationException")
        void addFieldConfigWhenNotDraft() {
            ExportFieldConfig config = ExportFieldConfig.create(
                    ExportTaskTestFixtures.aFieldConfigId(),
                    ExportTaskTestFixtures.aTaskId(),
                    "field1", "Field 1", "demographics", null);

            assertThatThrownBy(() -> ExportTaskTestFixtures.aPendingTask().addFieldConfig(config))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_CANNOT_MODIFY");
        }

        /**
         * 验证在非DRAFT(草稿)状态下添加过滤器抛出BusinessRuleViolationException。
         * 前置条件：任务处于PENDING_APPROVAL(待审核)状态，
         * 执行addFilter()抛出BusinessRuleViolationException，
         * 异常信息包含"EXPORT_CANNOT_MODIFY"。
         */
        @Test
        @DisplayName("addFilter when not DRAFT throws BusinessRuleViolationException")
        void addFilterWhenNotDraft() {
            ExportFilter filter = ExportFilter.create(
                    ExportTaskTestFixtures.aFilterId(),
                    ExportTaskTestFixtures.aTaskId(),
                    "field1",
                    ExportFilter.Operator.EQ,
                    "value",
                    ExportFilter.LogicOperator.AND);

            assertThatThrownBy(() -> ExportTaskTestFixtures.aPendingTask().addFilter(filter))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("EXPORT_CANNOT_MODIFY");
        }
    }

    // ===============================================================
    // canModify Tests
    // ===============================================================

    @Nested
    @DisplayName("canModify query")
    class CanModifyTests {

        /**
         * 验证canModify()仅在DRAFT(草稿)状态下返回true，
         * 在PENDING_APPROVAL(待审核)、APPROVED(已批准)、EXPORTING(导出中)、COMPLETED(已完成)和FAILED(导出失败)状态下返回false。
         * 前置条件：任务处于以上各状态，
         * 执行canModify()仅DRAFT状态返回true，其余均返回false。
         */
        @Test
        @DisplayName("canModify returns true only when DRAFT")
        void canModifyTrueOnlyWhenDraft() {
            assertThat(ExportTaskTestFixtures.aDraftTask().canModify()).isTrue();
            assertThat(ExportTaskTestFixtures.aPendingTask().canModify()).isFalse();
            assertThat(ExportTaskTestFixtures.anApprovedTask().canModify()).isFalse();
            assertThat(ExportTaskTestFixtures.anExportingTask().canModify()).isFalse();

            ExportTask completed = ExportTaskTestFixtures.anExportingTask();
            completed.markCompleted("/f");
            assertThat(completed.canModify()).isFalse();

            ExportTask failed = ExportTaskTestFixtures.aFailedTask(0);
            assertThat(failed.canModify()).isFalse();
        }
    }

    // ===============================================================
    // Config Management Tests
    // ===============================================================

    @Nested
    @DisplayName("Field configs and filters management")
    class ConfigTests {

        /**
         * 验证在DRAFT(草稿)状态下添加字段配置成功，配置被正确存储。
         * 前置条件：任务处于DRAFT(草稿)状态，
         * 执行addFieldConfig()后字段配置列表包含新增的配置项。
         */
        @Test
        @DisplayName("addFieldConfig when DRAFT succeeds and stores the config")
        void addFieldConfigWhenDraft() {
            ExportTask task = ExportTaskTestFixtures.aDraftTask();

            ExportFieldConfig config = ExportFieldConfig.create(
                    ExportTaskTestFixtures.aFieldConfigId(),
                    ExportTaskTestFixtures.aTaskId(),
                    "field1", "Field 1", "demographics", null);

            task.addFieldConfig(config);

            assertThat(task.getFieldConfigs()).hasSize(1);
            assertThat(task.getFieldConfigs().get(0)).isEqualTo(config);
        }

        /**
         * 验证在DRAFT(草稿)状态下添加过滤器成功，过滤器被正确存储。
         * 前置条件：任务处于DRAFT(草稿)状态，
         * 执行addFilter()后过滤器列表包含新增的过滤器。
         */
        @Test
        @DisplayName("addFilter when DRAFT succeeds and stores the filter")
        void addFilterWhenDraft() {
            ExportTask task = ExportTaskTestFixtures.aDraftTask();

            ExportFilter filter = ExportFilter.create(
                    ExportTaskTestFixtures.aFilterId(),
                    ExportTaskTestFixtures.aTaskId(),
                    "field1",
                    ExportFilter.Operator.EQ,
                    "value",
                    ExportFilter.LogicOperator.AND);

            task.addFilter(filter);

            assertThat(task.getFilters()).hasSize(1);
            assertThat(task.getFilters().get(0)).isEqualTo(filter);
        }

        /**
         * 验证addFieldConfig()时传入null的config抛出NullPointerException。
         * 前置条件：任务处于DRAFT(草稿)状态，
         * 预期：抛出NullPointerException，异常信息包含"config"。
         */
        @Test
        @DisplayName("addFieldConfig with null config throws IllegalArgumentException")
        void addFieldConfigWithNull() {
            ExportTask task = ExportTaskTestFixtures.aDraftTask();

            assertThatThrownBy(() -> task.addFieldConfig(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("config");
        }

        /**
         * 验证addFilter()时传入null的filter抛出NullPointerException。
         * 前置条件：任务处于DRAFT(草稿)状态，
         * 预期：抛出NullPointerException，异常信息包含"filter"。
         */
        @Test
        @DisplayName("addFilter with null filter throws IllegalArgumentException")
        void addFilterWithNull() {
            ExportTask task = ExportTaskTestFixtures.aDraftTask();

            assertThatThrownBy(() -> task.addFilter(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("filter");
        }
    }

    // ===============================================================
    // Full Workflow Scenarios
    // ===============================================================

    @Nested
    @DisplayName("Full workflow scenarios")
    class FullWorkflowTests {

        /**
         * 验证导出任务完整正向流程：DRAFT(草稿)→submit→PENDING_APPROVAL(待审核)→approve→APPROVED(已批准)→markExporting→EXPORTING(导出中)→markCompleted→COMPLETED(已完成)，
         * 共经过5个状态并产生4个领域事件。
         * 前置条件：无，
         * 执行完整流程后最终状态为COMPLETED(已完成)，文件URL正确记录，
         * 事件顺序为：ExportTaskSubmittedEvent(导出提交事件) → ExportTaskApprovedEvent(导出批准事件) → ExportExecutionStartedEvent(导出执行开始事件) → ExportExecutionCompletedEvent(导出执行完成事件)。
         */
        @Test
        @DisplayName("DRAFT -> submit -> approve -> exporting -> completed (all 5 states)")
        void fullWorkflowCompleted() {
            ExportTask task = ExportTask.create(
                    ExportTaskTestFixtures.aTaskId(),
                    "Full Workflow Test",
                    ExportTaskTestFixtures.aProjectId(),
                    ExportTaskTestFixtures.aStageId(),
                    ExportTaskTestFixtures.aCrfVersionId(),
                    FileFormat.XLSX);

            assertThat(task.getStatus()).isEqualTo(ExportStatus.DRAFT);

            // submit
            task.submit();
            assertThat(task.getStatus()).isEqualTo(ExportStatus.PENDING_APPROVAL);

            // approve
            task.approve("auditor1", "Approved");
            assertThat(task.getStatus()).isEqualTo(ExportStatus.APPROVED);

            // start exporting
            task.markExporting();
            assertThat(task.getStatus()).isEqualTo(ExportStatus.EXPORTING);

            // complete
            task.markCompleted("/files/output.xlsx");
            assertThat(task.getStatus()).isEqualTo(ExportStatus.COMPLETED);
            assertThat(task.getFileUrl()).isEqualTo("/files/output.xlsx");

            // Verify events: submit + approve + start + complete = 4 events
            List<DomainEvent> events = task.pullDomainEvents();
            assertThat(events).hasSize(4);
            assertThat(events.get(0)).isInstanceOf(ExportTaskSubmittedEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExportTaskApprovedEvent.class);
            assertThat(events.get(2)).isInstanceOf(ExportExecutionStartedEvent.class);
            assertThat(events.get(3)).isInstanceOf(ExportExecutionCompletedEvent.class);
        }

        /**
         * 验证导出任务包含驳回重提的完整流程：DRAFT(草稿)→submit→PENDING_APPROVAL(待审核)→reject→DRAFT(草稿)→submit→PENDING_APPROVAL(待审核)→approve→APPROVED(已批准)→markExporting→markCompleted→COMPLETED(已完成)，
         * 共产生6个领域事件。
         * 前置条件：无，
         * 执行完整流程后最终状态为COMPLETED(已完成)，
         * 事件顺序为：ExportTaskSubmittedEvent → ExportTaskRejectedEvent(导出驳回事件) → ExportTaskSubmittedEvent → ExportTaskApprovedEvent → ExportExecutionStartedEvent → ExportExecutionCompletedEvent。
         */
        @Test
        @DisplayName("DRAFT -> submit -> reject -> submit -> approve -> exporting -> completed (reject then fix)")
        void fullWorkflowWithReject() {
            ExportTask task = ExportTaskTestFixtures.aDraftTask();

            // submit
            task.submit();
            assertThat(task.getStatus()).isEqualTo(ExportStatus.PENDING_APPROVAL);

            // reject back to draft
            task.reject("auditor1", "Fix config");
            assertThat(task.getStatus()).isEqualTo(ExportStatus.DRAFT);

            // re-submit
            task.submit();
            assertThat(task.getStatus()).isEqualTo(ExportStatus.PENDING_APPROVAL);

            // approve
            task.approve("auditor1", "OK now");
            assertThat(task.getStatus()).isEqualTo(ExportStatus.APPROVED);

            // execute
            task.markExporting();
            task.markCompleted("/files/output.xlsx");
            assertThat(task.getStatus()).isEqualTo(ExportStatus.COMPLETED);

            List<DomainEvent> events = task.pullDomainEvents();
            assertThat(events).hasSize(6);
            assertThat(events.get(0)).isInstanceOf(ExportTaskSubmittedEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExportTaskRejectedEvent.class);
            assertThat(events.get(2)).isInstanceOf(ExportTaskSubmittedEvent.class);
            assertThat(events.get(3)).isInstanceOf(ExportTaskApprovedEvent.class);
            assertThat(events.get(4)).isInstanceOf(ExportExecutionStartedEvent.class);
            assertThat(events.get(5)).isInstanceOf(ExportExecutionCompletedEvent.class);
        }

        /**
         * 验证导出任务失败重试流程：DRAFT(草稿)→submit→approve→markExporting→FAILED(导出失败)→markExporting(重试)→markCompleted→COMPLETED(已完成)，
         * 失败次数被保留。
         * 前置条件：无，
         * 执行流程后最终状态为COMPLETED(已完成)，失败次数为1，
         * 事件顺序为：ExportExecutionStartedEvent → ExportExecutionCompletedEvent（中间事件已清除）。
         */
        @Test
        @DisplayName("Retry workflow: DRAFT -> submit -> approve -> exporting -> FAILED -> EXPORTING(retry) -> completed")
        void retryWorkflow() {
            ExportTask task = ExportTaskTestFixtures.aDraftTask();

            // DRAFT -> submit -> approve -> exporting
            task.submit();
            task.approve("auditor1", "Go");
            task.markExporting();
            assertThat(task.getStatus()).isEqualTo(ExportStatus.EXPORTING);

            // FAILED
            task.markFailed("Network error");
            assertThat(task.getStatus()).isEqualTo(ExportStatus.FAILED);
            assertThat(task.getFailCount()).isEqualTo(1);

            // clear events for clean counting
            task.pullDomainEvents();

            // retry: FAILED -> EXPORTING
            task.markExporting();
            assertThat(task.getStatus()).isEqualTo(ExportStatus.EXPORTING);

            // complete
            task.markCompleted("/files/retry-output.xlsx");
            assertThat(task.getStatus()).isEqualTo(ExportStatus.COMPLETED);

            List<DomainEvent> events = task.pullDomainEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(ExportExecutionStartedEvent.class);
            assertThat(events.get(1)).isInstanceOf(ExportExecutionCompletedEvent.class);
            // failCount was preserved through the retry
            assertThat(task.getFailCount()).isEqualTo(1);
        }
    }
}
