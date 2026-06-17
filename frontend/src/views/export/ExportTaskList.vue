<template>
  <div>
    <el-button style="margin-bottom:12px" @click="router.back()">返回</el-button>

    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2 style="margin:0">导出任务列表</h2>
      <el-button type="primary" @click="createDialogVisible = true">
        <el-icon><Plus /></el-icon>新建导出
      </el-button>
    </div>

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-table :data="list" v-loading="loading" stripe style="width:100%">
          <el-table-column prop="taskName" label="任务名称" min-width="160" />
          <el-table-column prop="projectId" label="项目ID" width="100" />
          <el-table-column prop="status" label="状态" width="140">
            <template #default="{ row }">
              <el-tag :type="statusType[row.status]" size="small" effect="dark">
                {{ statusLabel[row.status] || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="fileFormat" label="文件格式" width="100" />
          <el-table-column prop="auditUserId" label="审批人" width="100">
            <template #default="{ row }">{{ row.auditUserId || '-' }}</template>
          </el-table-column>
          <el-table-column prop="auditTime" label="审批时间" width="140">
            <template #default="{ row }">{{ formatTime(row.auditTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="260" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 'DRAFT'"
                type="primary"
                link
                size="small"
                :loading="submittingId === row.id"
                @click="handleSubmit(row)"
              >
                提交
              </el-button>
              <template v-if="row.status === 'PENDING_APPROVAL'">
                <el-button
                  type="success"
                  link
                  size="small"
                  @click="openApproveDialog(row)"
                >
                  批准
                </el-button>
                <el-button
                  type="danger"
                  link
                  size="small"
                  @click="openRejectDialog(row)"
                >
                  驳回
                </el-button>
              </template>
              <el-button
                v-if="row.status === 'APPROVED'"
                type="warning"
                link
                size="small"
                :loading="submittingId === row.id"
                @click="handleExecute(row)"
              >
                执行
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="list.length === 0 && !loading" style="text-align:center;color:#999;padding:40px 0">
          暂无导出任务
        </div>
      </template>
    </el-skeleton>

    <!-- Create Export Task Dialog -->
    <el-dialog v-model="createDialogVisible" title="新建导出任务" width="480px">
      <el-form :model="createForm" label-width="120px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="createForm.taskName" placeholder="如: 数据导出-20240601" />
        </el-form-item>
        <el-form-item label="项目ID" prop="projectId">
          <el-input-number v-model="createForm.projectId" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="Stage ID" prop="stageId">
          <el-input-number v-model="createForm.stageId" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="CRF版本ID" prop="crfVersionId">
          <el-input-number v-model="createForm.crfVersionId" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="文件格式" prop="fileFormat">
          <el-select v-model="createForm.fileFormat" style="width:100%" placeholder="请选择文件格式">
            <el-option label="Excel (.xlsx)" value="XLSX" />
            <el-option label="CSV" value="CSV" />
            <el-option label="PDF" value="PDF" />
            <el-option label="SAS (.sas7bdat)" value="SAS" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="createSubmitting" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>

    <!-- Approve Dialog -->
    <el-dialog v-model="approveDialogVisible" title="批准导出任务" width="420px">
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="审批意见" prop="comment">
          <el-input v-model="approveForm.comment" type="textarea" :rows="3" placeholder="审批意见（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="success" :loading="approveSubmitting" @click="handleApprove">确认批准</el-button>
      </template>
    </el-dialog>

    <!-- Reject Dialog -->
    <el-dialog v-model="rejectDialogVisible" title="驳回导出任务" width="420px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="驳回原因" prop="reason">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="rejectSubmitting" @click="handleReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getExportTasks,
  createExportTask,
  submitExportTask,
  approveExportTask,
  rejectExportTask,
  executeExport
} from '../../api/export'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const projectId = route.params.projectId

const loading = ref(true)
const list = ref([])

const statusType = {
  DRAFT: 'info',
  PENDING_APPROVAL: 'warning',
  APPROVED: 'success',
  EXPORTING: 'primary',
  COMPLETED: 'success',
  FAILED: 'danger'
}
const statusLabel = {
  DRAFT: '草稿',
  PENDING_APPROVAL: '待审批',
  APPROVED: '已批准',
  EXPORTING: '导出中',
  COMPLETED: '已完成',
  FAILED: '失败'
}

// Create dialog
const createDialogVisible = ref(false)
const createSubmitting = ref(false)
const createForm = ref({
  taskName: '',
  projectId: 1,
  stageId: 1,
  crfVersionId: 1,
  fileFormat: 'XLSX'
})

// Submit
const submittingId = ref(null)

// Approve dialog
const approveDialogVisible = ref(false)
const approveSubmitting = ref(false)
const approveForm = ref({ id: null, comment: '' })

// Reject dialog
const rejectDialogVisible = ref(false)
const rejectSubmitting = ref(false)
const rejectForm = ref({ id: null, reason: '' })

onMounted(async () => {
  await loadList()
  loading.value = false
})

async function loadList() {
  try {
    const res = await getExportTasks(projectId)
    list.value = Array.isArray(res) ? res : (res || [])
  } catch {
    list.value = []
    ElMessage.error('加载导出任务列表失败')
  }
}

async function handleCreate() {
  if (!createForm.value.taskName) {
    ElMessage.warning('请输入任务名称')
    return
  }
  createSubmitting.value = true
  try {
    await createExportTask(createForm.value)
    ElMessage.success('导出任务创建成功')
    createDialogVisible.value = false
    createForm.value = { taskName: '', projectId: 1, stageId: 1, crfVersionId: 1, fileFormat: 'XLSX' }
    await loadList()
  } catch {
    ElMessage.error('创建导出任务失败')
  } finally {
    createSubmitting.value = false
  }
}

async function handleSubmit(row) {
  submittingId.value = row.id
  try {
    await submitExportTask(row.id)
    ElMessage.success('提交成功')
    await loadList()
  } catch {
    ElMessage.error('提交失败')
  } finally {
    submittingId.value = null
  }
}

function openApproveDialog(row) {
  approveForm.value = { id: row.id, comment: '' }
  approveDialogVisible.value = true
}

async function handleApprove() {
  approveSubmitting.value = true
  try {
    await approveExportTask(approveForm.value.id, { userId: 'ui-user', message: approveForm.value.comment })
    ElMessage.success('已批准')
    approveDialogVisible.value = false
    await loadList()
  } catch {
    ElMessage.error('批准失败')
  } finally {
    approveSubmitting.value = false
  }
}

function openRejectDialog(row) {
  rejectForm.value = { id: row.id, reason: '' }
  rejectDialogVisible.value = true
}

async function handleReject() {
  if (!rejectForm.value.reason) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  rejectSubmitting.value = true
  try {
    await rejectExportTask(rejectForm.value.id, { userId: 'ui-user', reason: rejectForm.value.reason })
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    await loadList()
  } catch {
    ElMessage.error('驳回失败')
  } finally {
    rejectSubmitting.value = false
  }
}

async function handleExecute(row) {
  submittingId.value = row.id
  try {
    await executeExport(row.id)
    ElMessage.success('导出任务已开始执行')
    await loadList()
  } catch {
    ElMessage.error('执行导出失败')
  } finally {
    submittingId.value = null
  }
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
h2 {
  margin: 0;
}
</style>
