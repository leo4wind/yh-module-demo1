<template>
  <div>
    <el-button style="margin-bottom:12px" @click="router.push('/subjects')">返回受试者列表</el-button>

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-card>
          <template #header>
            <span style="font-size:16px;font-weight:600">受试者详情</span>
          </template>

          <el-descriptions :column="3" border>
            <el-descriptions-item label="受试者编号" width="140px">
              {{ subject.code || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态" width="140px">
              <el-tag :type="statusType[subject.status]" size="small">
                {{ statusLabel[subject.status] || subject.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="病历号">
              {{ subject.blh || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="试验序号">
              {{ subject.syxh || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="筛选信息">
              {{ subject.screeningInfo || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="脱落原因">
              {{ subject.withdrawReason || '-' }}
            </el-descriptions-item>
          </el-descriptions>

          <div style="margin-top:16px;display:flex;gap:8px">
            <el-button type="danger" @click="withdrawDialogVisible = true">脱落</el-button>
            <el-button type="warning" @click="changeStatusDialogVisible = true">修改状态</el-button>
          </div>
        </el-card>

        <el-card style="margin-top:16px">
          <template #header>
            <span style="font-size:16px;font-weight:600">访视事件列表</span>
          </template>

          <el-table :data="stages" v-loading="stagesLoading" stripe style="width:100%" row-key="id">
            <el-table-column prop="id" label="Stage ID" width="80" />
            <el-table-column prop="stageName" label="访视名称" min-width="160" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="stageStatusType[row.status]" size="small">
                  {{ stageStatusLabel[row.status] || row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="plannedDate" label="计划日期" width="120">
              <template #default="{ row }">{{ formatDate(row.plannedDate) }}</template>
            </el-table-column>
            <el-table-column prop="actualDate" label="实际日期" width="120">
              <template #default="{ row }">{{ formatDate(row.actualDate) }}</template>
            </el-table-column>
            <el-table-column prop="completeness" label="完成度" width="160">
              <template #default="{ row }">
                <el-progress :percentage="row.completeness || 0" :stroke-width="14" />
              </template>
            </el-table-column>
            <el-table-column type="expand" width="40">
              <template #default="{ row }">
                <div v-if="row.assessments && row.assessments.length > 0">
                  <el-table :data="row.assessments" stripe size="small" @row-click="goAssessment">
                    <el-table-column prop="id" label="Assessment ID" width="100" />
                    <el-table-column prop="assessmentName" label="CRF评估" min-width="160" />
                    <el-table-column prop="status" label="状态" width="100">
                      <template #default="{ row: ass }">
                        <el-tag :type="stageStatusType[ass.status]" size="small">
                          {{ stageStatusLabel[ass.status] || ass.status }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="completeness" label="完成度" width="120">
                      <template #default="{ row: ass }">
                        <el-progress :percentage="ass.completeness || 0" :stroke-width="12" />
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
                <div v-else style="text-align:center;color:#999;padding:12px 0">
                  暂无CRF评估数据
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="stages.length === 0 && !stagesLoading" style="text-align:center;color:#999;padding:24px 0">
            暂无访视事件
          </div>
        </el-card>
      </template>
    </el-skeleton>

    <!-- Withdraw Dialog -->
    <el-dialog v-model="withdrawDialogVisible" title="受试者脱落" width="420px">
      <el-form :model="withdrawForm" label-width="100px">
        <el-form-item label="原因代码" prop="reasonCode">
          <el-input v-model="withdrawForm.reasonCode" placeholder="如: WITHDRAWN" />
        </el-form-item>
        <el-form-item label="脱落原因" prop="reasonDescription">
          <el-input v-model="withdrawForm.reasonDescription" type="textarea" :rows="3" placeholder="请输入脱落原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="withdrawDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="withdrawSubmitting" @click="handleWithdraw">确认脱落</el-button>
      </template>
    </el-dialog>

    <!-- Change Status Dialog -->
    <el-dialog v-model="changeStatusDialogVisible" title="修改受试者状态" width="420px">
      <el-form :model="changeStatusForm" label-width="100px">
        <el-form-item label="新状态" prop="status">
          <el-select v-model="changeStatusForm.status" style="width:100%" placeholder="请选择状态">
            <el-option label="筛选入组" value="SCREENING" />
            <el-option label="已入组" value="ENROLLED" />
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="提前终止" value="TERMINATED" />
            <el-option label="退出" value="WITHDRAWN" />
          </el-select>
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="changeStatusForm.reason" type="textarea" :rows="3" placeholder="变更原因（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeStatusDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="changeStatusSubmitting" @click="handleChangeStatus">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSubject, withdrawSubject, changeStatus } from '../../api/subject'
import { getSubjectStages } from '../../api/assessment'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const subjectId = route.params.id

const loading = ref(true)
const subject = ref({})

const stages = ref([])
const stagesLoading = ref(false)

const statusType = {
  SCREENING: 'warning',
  ENROLLED: 'info',
  ACTIVE: 'success',
  COMPLETED: '',
  TERMINATED: 'danger',
  WITHDRAWN: 'danger'
}
const statusLabel = {
  SCREENING: '筛选入组',
  ENROLLED: '已入组',
  ACTIVE: '进行中',
  COMPLETED: '已完成',
  TERMINATED: '提前终止',
  WITHDRAWN: '退出'
}

const stageStatusType = {
  PENDING: 'warning',
  IN_PROGRESS: 'info',
  COMPLETED: 'success'
}
const stageStatusLabel = {
  PENDING: '待开始',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成'
}

// Withdraw
const withdrawDialogVisible = ref(false)
const withdrawSubmitting = ref(false)
const withdrawForm = ref({ reasonCode: 'WITHDRAWN', reasonDescription: '' })

// Change status
const changeStatusDialogVisible = ref(false)
const changeStatusSubmitting = ref(false)
const changeStatusForm = ref({ status: '', reason: '' })

onMounted(async () => {
  await loadSubject()
  await loadStages()
  loading.value = false
})

async function loadSubject() {
  try {
    subject.value = await getSubject(subjectId) || {}
  } catch {
    subject.value = {}
    ElMessage.error('加载受试者信息失败')
  }
}

async function loadStages() {
  stagesLoading.value = true
  try {
    stages.value = await getSubjectStages(subjectId) || []
  } catch {
    stages.value = []
    ElMessage.error('加载访视事件失败')
  } finally {
    stagesLoading.value = false
  }
}

async function handleWithdraw() {
  if (!withdrawForm.value.reasonDescription) {
    ElMessage.warning('请输入脱落原因')
    return
  }
  withdrawSubmitting.value = true
  try {
    await withdrawSubject(subjectId, {
      reasonCode: withdrawForm.value.reasonCode || 'WITHDRAWN',
      reasonDescription: withdrawForm.value.reasonDescription
    })
    ElMessage.success('受试者已标记为脱落')
    withdrawDialogVisible.value = false
    withdrawForm.value = { reasonCode: 'WITHDRAWN', reasonDescription: '' }
    await loadSubject()
  } finally {
    withdrawSubmitting.value = false
  }
}

async function handleChangeStatus() {
  if (!changeStatusForm.value.status) {
    ElMessage.warning('请选择新状态')
    return
  }
  changeStatusSubmitting.value = true
  try {
    await changeStatus(subjectId, {
      newStatus: changeStatusForm.value.status,
      reason: changeStatusForm.value.reason
    })
    ElMessage.success('状态修改成功')
    changeStatusDialogVisible.value = false
    changeStatusForm.value = { status: '', reason: '' }
    await loadSubject()
  } finally {
    changeStatusSubmitting.value = false
  }
}

function goAssessment(ass) {
  router.push('/assessments/' + ass.id)
}

function formatDate(d) {
  if (!d) return ''
  const dt = new Date(d)
  const pad = (n) => String(n).padStart(2, '0')
  return `${dt.getFullYear()}-${pad(dt.getMonth() + 1)}-${pad(dt.getDate())}`
}
</script>

<style scoped>
.el-descriptions {
  margin-top: 8px;
}
</style>
