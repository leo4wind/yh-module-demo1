<template>
  <div>
    <el-button style="margin-bottom:12px" @click="router.back()">返回评估详情</el-button>

    <h2 style="margin-bottom:16px">质疑列表</h2>

    <el-skeleton :loading="loading" animated>
      <template #default>
        <div v-if="queries.length === 0" style="text-align:center;color:#999;padding:40px 0">
          暂无质疑数据
        </div>

        <el-timeline v-else>
          <el-timeline-item
            v-for="q in queries"
            :key="q.id"
            :timestamp="formatTime(q.createdAt || q.createTime)"
            :color="timelineColor[q.status]"
          >
            <el-card shadow="hover">
              <div style="display:flex;justify-content:space-between;align-items:flex-start">
                <div style="flex:1">
                  <div style="margin-bottom:8px">
                    <el-tag :type="statusType[q.status]" size="small" effect="dark">
                      {{ statusLabel[q.status] || q.status }}
                    </el-tag>
                    <span v-if="q.fieldLabel" style="margin-left:8px;font-size:13px;color:#909399">
                      字段: {{ q.fieldLabel }}
                    </span>
                  </div>

                  <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6">{{ q.question }}</p>

                  <div v-if="q.respondedAt || q.response" style="margin-top:8px;padding:8px 12px;background:#f5f7fa;border-radius:4px">
                    <div style="font-size:12px;color:#909399;margin-bottom:4px">回应 ({{ formatTime(q.respondedAt || q.responseTime) }})</div>
                    <div style="font-size:13px">{{ q.response || q.responseText }}</div>
                  </div>

                  <div style="margin-top:4px;font-size:12px;color:#c0c4cc">
                    创建: {{ formatTime(q.createdAt || q.createTime) }}
                    <span v-if="q.closedAt" style="margin-left:12px">关闭: {{ formatTime(q.closedAt) }}</span>
                  </div>
                </div>

                <div style="margin-left:16px;display:flex;flex-direction:column;gap:4px;flex-shrink:0">
                  <el-button
                    v-if="q.status === 'OPEN'"
                    type="primary"
                    size="small"
                    @click="openRespondDialog(q)"
                  >
                    回应
                  </el-button>
                  <el-button
                    v-if="q.status === 'OPEN' || q.status === 'RESPONDED'"
                    type="info"
                    size="small"
                    @click="handleCloseQuery(q)"
                  >
                    关闭
                  </el-button>
                  <el-button
                    v-if="q.status === 'CLOSED'"
                    type="warning"
                    size="small"
                    @click="openReopenDialog(q)"
                  >
                    重新打开
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </template>
    </el-skeleton>

    <!-- Respond Dialog -->
    <el-dialog v-model="respondDialogVisible" title="回应质疑" width="480px">
      <el-form :model="respondForm" label-width="80px">
        <el-form-item label="质疑内容">
          <p style="margin:0;font-size:14px;color:#606266">{{ respondForm.question }}</p>
        </el-form-item>
        <el-form-item label="回应" prop="response">
          <el-input v-model="respondForm.response" type="textarea" :rows="4" placeholder="请输入回应内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="respondDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="respondSubmitting" @click="handleRespond">提交回应</el-button>
      </template>
    </el-dialog>

    <!-- Reopen Dialog -->
    <el-dialog v-model="reopenDialogVisible" title="重新打开质疑" width="480px">
      <el-form :model="reopenForm" label-width="80px">
        <el-form-item label="质疑内容">
          <p style="margin:0;font-size:14px;color:#606266">{{ reopenForm.question }}</p>
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="reopenForm.reason" type="textarea" :rows="4" placeholder="请输入重新打开的原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reopenDialogVisible = false">取消</el-button>
        <el-button type="warning" :loading="reopenSubmitting" @click="handleReopen">确认重新打开</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getQueries, getQuery, respondQuery, closeQuery, reopenQuery } from '../../api/query'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const assessmentId = route.params.assessmentId

const loading = ref(true)
const queries = ref([])

const statusType = {
  OPEN: 'danger',
  RESPONDED: 'warning',
  CLOSED: 'success'
}
const statusLabel = {
  OPEN: '待回应',
  RESPONDED: '已回应',
  CLOSED: '已关闭'
}
const timelineColor = {
  OPEN: '#F56C6C',
  RESPONDED: '#E6A23C',
  CLOSED: '#67C23A'
}

// Respond dialog
const respondDialogVisible = ref(false)
const respondSubmitting = ref(false)
const respondForm = ref({ id: null, question: '', response: '' })

// Reopen dialog
const reopenDialogVisible = ref(false)
const reopenSubmitting = ref(false)
const reopenForm = ref({ id: null, question: '', reason: '' })

onMounted(async () => {
  await loadQueries()
  loading.value = false
})

async function loadQueries() {
  try {
    queries.value = await getQueries(assessmentId) || []
  } catch {
    queries.value = []
    ElMessage.error('加载质疑列表失败')
  }
}

function openRespondDialog(q) {
  respondForm.value = { id: q.id, question: q.question, response: '' }
  respondDialogVisible.value = true
}

async function handleRespond() {
  if (!respondForm.value.response) {
    ElMessage.warning('请输入回应内容')
    return
  }
  respondSubmitting.value = true
  try {
    await respondQuery(respondForm.value.id, { response: respondForm.value.response })
    ElMessage.success('回应已提交')
    respondDialogVisible.value = false
    await loadQueries()
  } catch {
    ElMessage.error('提交回应失败')
  } finally {
    respondSubmitting.value = false
  }
}

async function handleCloseQuery(q) {
  try {
    await ElMessageBox.confirm('确认关闭此质疑?', '关闭质疑', {
      confirmButtonText: '确认关闭',
      cancelButtonText: '取消',
      type: 'info'
    })
    await closeQuery(q.id, {})
    ElMessage.success('质疑已关闭')
    await loadQueries()
  } catch {
    // cancelled or error
  }
}

function openReopenDialog(q) {
  reopenForm.value = { id: q.id, question: q.question, reason: '' }
  reopenDialogVisible.value = true
}

async function handleReopen() {
  if (!reopenForm.value.reason) {
    ElMessage.warning('请输入重新打开的原因')
    return
  }
  reopenSubmitting.value = true
  try {
    await reopenQuery(reopenForm.value.id, { reason: reopenForm.value.reason })
    ElMessage.success('质疑已重新打开')
    reopenDialogVisible.value = false
    await loadQueries()
  } catch {
    ElMessage.error('重新打开质疑失败')
  } finally {
    reopenSubmitting.value = false
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
  margin: 0 0 16px 0;
}
.el-timeline {
  padding-left: 0;
}
</style>
