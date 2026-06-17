<template>
  <div>
    <el-button style="margin-bottom:12px" @click="router.back()">返回</el-button>

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-card>
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-size:16px;font-weight:600">评估详情</span>
              <div style="display:flex;align-items:center;gap:12px">
                <el-badge :value="pendingQueryCount" :hidden="pendingQueryCount === 0" type="danger">
                  <el-button size="small" @click="goToQueries">
                    质疑列表
                  </el-button>
                </el-badge>
                <el-button type="success" size="small" @click="saveFieldDialogVisible = true">
                  添加字段值
                </el-button>
                <el-button type="warning" size="small" :loading="auditSubmitting" @click="handleAudit">
                  稽查
                </el-button>
              </div>
            </div>
          </template>

          <el-descriptions :column="3" border>
            <el-descriptions-item label="评估名称" width="160px">
              {{ assessment.assessmentName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态" width="120px">
              <el-tag :type="statusType[assessment.status]" size="small">
                {{ statusLabel[assessment.status] || assessment.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="评估分数">
              {{ assessment.assessmentScore ?? '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="完成度">
              <el-progress :percentage="assessment.completeness || 0" :stroke-width="14" style="width:200px" />
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card style="margin-top:16px">
          <template #header>
            <span style="font-size:16px;font-weight:600">字段值列表</span>
          </template>

          <el-table :data="fieldValues" v-loading="fieldValuesLoading" stripe style="width:100%">
            <el-table-column prop="fieldLabel" label="字段名称" min-width="160" />
            <el-table-column prop="fieldValue" label="字段值" width="120" />
            <el-table-column prop="fieldValueText" label="文本值" min-width="200" />
            <el-table-column prop="dataUnit" label="单位" width="80" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openEditField(row)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small" @click="openRaiseQuery(row)">
                  发起质疑
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="fieldValues.length === 0 && !fieldValuesLoading" style="text-align:center;color:#999;padding:24px 0">
            暂无字段值数据
          </div>
        </el-card>
      </template>
    </el-skeleton>

    <!-- Save / Edit Field Value Dialog -->
    <el-dialog v-model="saveFieldDialogVisible" :title="editFieldId ? '编辑字段值' : '添加字段值'" width="480px">
      <el-form :model="fieldForm" label-width="100px">
        <el-form-item label="字段名称" prop="fieldLabel">
          <el-input v-model="fieldForm.fieldLabel" placeholder="如: 收缩压" />
        </el-form-item>
        <el-form-item label="字段值" prop="fieldValue">
          <el-input v-model="fieldForm.fieldValue" placeholder="如: 120" />
        </el-form-item>
        <el-form-item label="文本值" prop="fieldValueText">
          <el-input v-model="fieldForm.fieldValueText" placeholder="如: 120 mmHg" />
        </el-form-item>
        <el-form-item label="单位" prop="dataUnit">
          <el-input v-model="fieldForm.dataUnit" placeholder="如: mmHg" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="saveFieldDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="fieldSubmitting" @click="handleSaveField">保存</el-button>
      </template>
    </el-dialog>

    <!-- Raise Query Dialog -->
    <el-dialog v-model="raiseQueryDialogVisible" title="发起质疑" width="480px">
      <el-form :model="queryForm" label-width="80px">
        <el-form-item label="字段">
          <span>{{ queryForm.fieldLabel }}</span>
        </el-form-item>
        <el-form-item label="质疑内容" prop="question">
          <el-input v-model="queryForm.question" type="textarea" :rows="4" placeholder="请输入质疑内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="raiseQueryDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="querySubmitting" @click="handleRaiseQuery">提交质疑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAssessment, saveFieldValue, auditAssessment } from '../../api/assessment'
import { getQueries, raiseQuery } from '../../api/query'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const assessmentId = route.params.id

const loading = ref(true)
const assessment = ref({})
const fieldValues = ref([])
const fieldValuesLoading = ref(false)
const pendingQueryCount = ref(0)

const statusType = {
  PENDING: 'warning',
  IN_PROGRESS: 'info',
  COMPLETED: 'success'
}
const statusLabel = {
  PENDING: '待开始',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成'
}

// Field value dialog
const saveFieldDialogVisible = ref(false)
const editFieldId = ref(null)
const fieldSubmitting = ref(false)
const fieldForm = ref({
  fieldLabel: '',
  fieldValue: '',
  fieldValueText: '',
  dataUnit: ''
})

// Raise query dialog
const raiseQueryDialogVisible = ref(false)
const querySubmitting = ref(false)
const queryForm = ref({
  fieldLabel: '',
  fieldValue: '',
  question: ''
})

// Audit
const auditSubmitting = ref(false)

onMounted(async () => {
  await loadAssessment()
  await loadFieldValues()
  await loadQueryCount()
  loading.value = false
})

async function loadAssessment() {
  try {
    assessment.value = await getAssessment(assessmentId) || {}
  } catch {
    assessment.value = {}
    ElMessage.error('加载评估信息失败')
  }
}

async function loadFieldValues() {
  fieldValuesLoading.value = true
  try {
    const data = await getAssessment(assessmentId)
    fieldValues.value = data.fieldValues || data.fields || []
  } catch {
    fieldValues.value = []
  } finally {
    fieldValuesLoading.value = false
  }
}

async function loadQueryCount() {
  try {
    const queries = await getQueries(assessmentId) || []
    pendingQueryCount.value = queries.filter(q => q.status === 'OPEN' || q.status === 'RESPONDED').length
  } catch {
    pendingQueryCount.value = 0
  }
}

function openEditField(row) {
  editFieldId.value = row.id || null
  fieldForm.value = {
    fieldLabel: row.fieldLabel || '',
    fieldValue: row.fieldValue || '',
    fieldValueText: row.fieldValueText || '',
    dataUnit: row.dataUnit || ''
  }
  saveFieldDialogVisible.value = true
}

function openRaiseQuery(row) {
  queryForm.value = {
    fieldLabel: row.fieldLabel || '',
    fieldValue: row.fieldValue || '',
    question: ''
  }
  raiseQueryDialogVisible.value = true
}

async function handleSaveField() {
  fieldSubmitting.value = true
  try {
    await saveFieldValue(assessmentId, fieldForm.value)
    ElMessage.success('字段值保存成功')
    saveFieldDialogVisible.value = false
    fieldForm.value = { fieldLabel: '', fieldValue: '', fieldValueText: '', dataUnit: '' }
    editFieldId.value = null
    await loadAssessment()
    await loadFieldValues()
  } catch {
    ElMessage.error('保存字段值失败')
  } finally {
    fieldSubmitting.value = false
  }
}

async function handleRaiseQuery() {
  if (!queryForm.value.question) {
    ElMessage.warning('请输入质疑内容')
    return
  }
  querySubmitting.value = true
  try {
    await raiseQuery({
      assessmentId: Number(assessmentId),
      fieldLabel: queryForm.value.fieldLabel,
      fieldValue: queryForm.value.fieldValue,
      question: queryForm.value.question
    })
    ElMessage.success('质疑已提交')
    raiseQueryDialogVisible.value = false
    queryForm.value = { fieldLabel: '', fieldValue: '', question: '' }
    await loadQueryCount()
  } catch {
    ElMessage.error('提交质疑失败')
  } finally {
    querySubmitting.value = false
  }
}

async function handleAudit() {
  auditSubmitting.value = true
  try {
    const res = await auditAssessment(assessmentId, { userId: 1 })
    ElMessage.success('稽查完成')
    await loadAssessment()
  } catch {
    ElMessage.error('稽查失败')
  } finally {
    auditSubmitting.value = false
  }
}

function goToQueries() {
  router.push('/assessments/' + assessmentId + '/queries')
}
</script>

<style scoped>
.el-descriptions {
  margin-top: 8px;
}
</style>
