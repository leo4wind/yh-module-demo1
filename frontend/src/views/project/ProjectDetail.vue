<template>
  <div v-loading="loading">
    <el-button style="margin-bottom:12px" @click="$router.push('/projects')">返回项目列表</el-button>

    <el-card style="margin-bottom:16px">
      <div style="display:flex;justify-content:space-between;align-items:flex-start">
        <div>
          <h2 style="margin:0 0 8px">{{ project.title }}</h2>
          <el-descriptions :column="3" size="small" border>
            <el-descriptions-item label="项目ID">{{ project.id }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ typeLabel[project.type] || project.type }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="statusType[project.status]" size="small">
                {{ statusLabel[project.status] || project.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="缩写">{{ project.prefix }}</el-descriptions-item>
            <el-descriptions-item label="临床登记号">{{ project.clinicalNumber || '-' }}</el-descriptions-item>
            <el-descriptions-item label="预计例数">{{ project.expectedSubjectSize || 0 }}</el-descriptions-item>
            <el-descriptions-item label="开放筛选">
              <el-tag :type="project.openScreen ? 'success' : 'info'" size="small">
                {{ project.openScreen ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatTime(project.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div style="display:flex;gap:8px;flex-shrink:0">
          <el-button type="primary" @click="goSubjects">
            受试者列表
          </el-button>
          <el-button v-if="project.status === 'DRAFT'" type="success" @click="handleActivate" :loading="activating">
            激活项目
          </el-button>
          <el-button v-if="project.status === 'ACTIVE'" type="warning" @click="handleClose" :loading="closing">
            关闭项目
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="阶段配置" name="stages">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="stageDialogVisible = true">
              <el-icon><Plus /></el-icon>添加阶段
            </el-button>
          </div>
          <el-table :data="stages" stripe size="small">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="阶段名称" min-width="140" />
            <el-table-column prop="repeatType" label="重复类型" width="120">
              <template #default="{ row }">{{ repeatTypeLabel[row.repeatType] || row.repeatType }}</template>
            </el-table-column>
            <el-table-column label="自动添加" width="90">
              <template #default="{ row }">
                <el-tag :type="row.autoAdd ? 'success' : 'info'" size="small">{{ row.autoAdd ? '是' : '否' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="基线" width="120">
              <template #default="{ row }">{{ formatBaseline(row) }}</template>
            </el-table-column>
            <el-table-column label="窗口" width="120">
              <template #default="{ row }">{{ formatWindow(row) }}</template>
            </el-table-column>
          </el-table>
          <div v-if="stages.length === 0" style="text-align:center;color:#999;padding:24px 0">暂无阶段配置</div>
        </el-tab-pane>

        <el-tab-pane label="访视计划" name="visitPlans">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="visitDialogVisible = true">
              <el-icon><Plus /></el-icon>添加访视
            </el-button>
          </div>
          <div v-if="visitPlans.length === 0" style="text-align:center;color:#999;padding:24px 0">暂无访视计划</div>
          <div v-else class="timeline">
            <div v-for="(vp, idx) in visitPlans" :key="vp.id" class="timeline-item">
              <div class="timeline-dot" :class="{ first: idx === 0, last: idx === visitPlans.length - 1 }" />
              <div class="timeline-card">
                <div class="timeline-header">
                  <strong>{{ stageName(vp.sourceStageId) }}</strong>
                </div>
                <div class="timeline-arrow">→</div>
                <div class="timeline-header">
                  <strong>{{ stageName(vp.targetStageId) }}</strong>
                </div>
                <div class="timeline-meta">
                  {{ formatBaseline(vp) }} / {{ formatWindow(vp) }}
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="CRF绑定" name="crfBindings">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="crfDialogVisible = true">
              <el-icon><Plus /></el-icon>添加CRF绑定
            </el-button>
          </div>
          <el-table :data="crfBindings" stripe size="small">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column label="CRF模板" min-width="180">
              <template #default="{ row }">{{ crfTemplateLabel(row.crfId) }}</template>
            </el-table-column>
            <el-table-column prop="crfVersionId" label="版本ID" width="100" />
            <el-table-column label="关联阶段" min-width="140">
              <template #default="{ row }">{{ stageName(row.stageId) }}</template>
            </el-table-column>
            <el-table-column label="允许录入" width="90">
              <template #default="{ row }">
                <el-tag :type="row.userInputEnabled ? 'success' : 'info'" size="small">{{ row.userInputEnabled ? '是' : '否' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="crfBindings.length === 0" style="text-align:center;color:#999;padding:24px 0">暂无CRF绑定</div>
        </el-tab-pane>

        <el-tab-pane label="中心人员" name="personnel">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="openPersonnelDialog">
              <el-icon><Plus /></el-icon>添加人员
            </el-button>
          </div>
          <el-table :data="personnel" stripe size="small">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column label="用户" min-width="140">
              <template #default="{ row }">{{ userOptionLabel(row.userId) }}</template>
            </el-table-column>
            <el-table-column label="中心" min-width="140">
              <template #default="{ row }">{{ siteOptionLabel(row.siteId) }}</template>
            </el-table-column>
            <el-table-column prop="role" label="角色" width="120">
              <template #default="{ row }">{{ roleLabel[row.role] || row.role }}</template>
            </el-table-column>
          </el-table>
          <div v-if="personnel.length === 0" style="text-align:center;color:#999;padding:24px 0">暂无人员配置</div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- Add Stage Dialog -->
    <el-dialog v-model="stageDialogVisible" title="添加阶段" width="480px">
      <el-form :model="stageForm" label-width="100px">
        <el-form-item label="阶段名称" required>
          <el-input v-model="stageForm.name" placeholder="如: 筛选期" />
        </el-form-item>
        <el-form-item label="重复类型" required>
          <el-select v-model="stageForm.repeatType" style="width:100%">
            <el-option label="不重复" value="NONE" />
            <el-option label="按天" value="DAY" />
            <el-option label="按周" value="WEEK" />
            <el-option label="按月" value="MONTH" />
          </el-select>
        </el-form-item>
        <el-form-item label="自动添加">
          <el-switch v-model="stageForm.autoAdd" />
        </el-form-item>
        <el-form-item label="基线间隔">
          <el-input-number v-model="stageForm.baselineDays" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="基线单位">
          <el-select v-model="stageForm.baselineDirection" style="width:100%">
            <el-option label="天" value="DAY" />
            <el-option label="周" value="WEEK" />
            <el-option label="月" value="MONTH" />
          </el-select>
        </el-form-item>
        <el-form-item label="提前天数">
          <el-input-number v-model="stageForm.beforeDays" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="延后天数">
          <el-input-number v-model="stageForm.afterDays" :min="0" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stageDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="stageSubmitting" @click="handleAddStage">确认添加</el-button>
      </template>
    </el-dialog>

    <!-- Add Visit Plan Dialog -->
    <el-dialog v-model="visitDialogVisible" title="添加访视计划" width="480px">
      <el-form :model="visitForm" label-width="100px">
        <el-form-item label="来源阶段" required>
          <el-select v-model="visitForm.sourceStageId" style="width:100%">
            <el-option v-for="s in stages" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标阶段" required>
          <el-select v-model="visitForm.targetStageId" style="width:100%">
            <el-option v-for="s in stages" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划名称" required>
          <el-input v-model="visitForm.name" placeholder="如: 筛选到治疗" />
        </el-form-item>
        <el-form-item label="基线间隔">
          <el-input-number v-model="visitForm.baselineDays" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="基线单位">
          <el-select v-model="visitForm.baselineDirection" style="width:100%">
            <el-option label="天" value="DAY" />
            <el-option label="周" value="WEEK" />
            <el-option label="月" value="MONTH" />
          </el-select>
        </el-form-item>
        <el-form-item label="提前天数">
          <el-input-number v-model="visitForm.beforeDays" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="延后天数">
          <el-input-number v-model="visitForm.afterDays" :min="0" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visitDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="visitSubmitting" @click="handleAddVisitPlan">确认添加</el-button>
      </template>
    </el-dialog>

    <!-- Add CRF Binding Dialog -->
    <el-dialog v-model="crfDialogVisible" title="添加CRF绑定" width="480px">
      <el-form :model="crfForm" label-width="100px">
        <el-form-item label="CRF模板" required>
          <el-select
            v-model="selectedCrfTemplateId"
            filterable
            remote
            reserve-keyword
            :remote-method="searchCrfTemplates"
            :loading="crfTemplateLoading"
            placeholder="输入名称或编码搜索"
            style="width:100%"
            @change="handleCrfTemplateChange">
            <el-option
              v-for="template in crfTemplates"
              :key="template.id"
              :label="formatCrfTemplateOption(template)"
              :value="template.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联阶段" required>
          <el-select v-model="crfForm.stageId" style="width:100%">
            <el-option v-for="s in stages" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="版本ID">
          <el-input-number v-model="crfForm.crfVersionId" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="允许录入">
          <el-switch v-model="crfForm.userInputEnabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="crfDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="crfSubmitting" @click="handleAddCrf">确认添加</el-button>
      </template>
    </el-dialog>

    <!-- Add Personnel Dialog -->
    <el-dialog v-model="personnelDialogVisible" title="添加中心人员" width="480px">
      <el-form :model="personnelForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户" required>
              <el-select
                v-model="personnelForm.userId"
                filterable
                placeholder="请选择用户"
                style="width:100%">
                <el-option
                  v-for="user in personnelOptions.users"
                  :key="user.id"
                  :label="formatOption(user)"
                  :value="String(user.id)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" required>
              <el-select v-model="personnelForm.role" style="width:100%">
                <el-option label="主要研究者" value="PI" />
                <el-option label="研究协调员" value="CRC" />
                <el-option label="数据管理员" value="DM" />
                <el-option label="监查员" value="CRA" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="中心" required>
              <el-select
                v-model="personnelForm.siteId"
                filterable
                placeholder="请选择中心"
                style="width:100%">
                <el-option
                  v-for="site in personnelOptions.sites"
                  :key="site.id"
                  :label="formatOption(site)"
                  :value="String(site.id)" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="personnelDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="personnelSubmitting" @click="handleAddPersonnel">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { getProject, activateProject, closeProject, addStage, addVisitPlan, bindCrf, assignPersonnel, getPersonnelOptions } from '../../api/project'
import { getCrfTemplates } from '../../api/crf'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const projectId = route.params.id

const loading = ref(true)
const activating = ref(false)
const closing = ref(false)
const activeTab = ref('stages')

const project = ref({})
const stages = ref([])
const visitPlans = ref([])
const crfBindings = ref([])
const personnel = ref([])

const statusType = { DRAFT: 'info', ACTIVE: 'success', CLOSED: 'warning' }
const statusLabel = { DRAFT: '草稿', ACTIVE: '进行中', CLOSED: '已关闭' }
const typeLabel = { INTERVENTIONAL: '干预性研究', OBSERVATIONAL: '观察性研究', DIAGNOSTIC: '诊断试验' }
const repeatTypeLabel = { NONE: '不重复', DAY: '按天', WEEK: '按周', MONTH: '按月' }
const roleLabel = { PI: '主要研究者', CRC: '研究协调员', DM: '数据管理员', CRA: '监查员' }

const stageDialogVisible = ref(false)
const stageSubmitting = ref(false)
const stageForm = ref({ name: '', repeatType: 'NONE', autoAdd: true, baselineDays: 0, baselineDirection: 'DAY', beforeDays: 0, afterDays: 0 })

const visitDialogVisible = ref(false)
const visitSubmitting = ref(false)
const visitForm = ref({ name: '', sourceStageId: null, targetStageId: null, baselineDays: 0, baselineDirection: 'DAY', beforeDays: 0, afterDays: 0, crfComponentId: '' })

const crfDialogVisible = ref(false)
const crfSubmitting = ref(false)
const crfTemplateLoading = ref(false)
const crfTemplates = ref([])
const selectedCrfTemplateId = ref(null)
const crfForm = ref({ stageId: null, crfId: null, crfVersionId: null, userInputEnabled: true })

const personnelDialogVisible = ref(false)
const personnelSubmitting = ref(false)
const personnelForm = ref({ userId: '', siteId: '', role: 'CRC' })
const personnelOptions = ref({
  users: [
    { id: '1', label: '系统用户 1' },
    { id: '100', label: '研究者 100' },
    { id: '200', label: '数据管理员 200' }
  ],
  sites: [
    { id: '1', label: '默认中心 1' },
    { id: '100', label: '研究中心 100' },
    { id: '200', label: '研究中心 200' }
  ]
})

onMounted(async () => {
  await Promise.all([fetchProject(), fetchCrfTemplates(), fetchPersonnelOptions()])
})

async function fetchProject() {
  loading.value = true
  try {
    const res = await getProject(projectId)
    project.value = res || {}
    stages.value = res.stages || res.stageList || []
    visitPlans.value = res.visitPlans || res.visitPlanList || []
    crfBindings.value = res.crfBindings || res.crfBindingList || []
    personnel.value = res.sitePersonnel || res.personnel || res.personnelList || []
    mergePersonnelOptionsFromRows(personnel.value)
  } catch {
    ElMessage.error('加载项目详情失败')
  } finally {
    loading.value = false
  }
}

async function handleActivate() {
  activating.value = true
  try {
    await activateProject(projectId)
    ElMessage.success('项目已激活')
    await fetchProject()
  } finally {
    activating.value = false
  }
}

async function handleClose() {
  closing.value = true
  try {
    await closeProject(projectId)
    ElMessage.success('项目已关闭')
    await fetchProject()
  } finally {
    closing.value = false
  }
}

function goSubjects() {
  router.push(`/projects/${projectId}/subjects`)
}

async function handleAddStage() {
  if (!stageForm.value.name || !stageForm.value.repeatType) {
    ElMessage.warning('请填写阶段名称和重复类型')
    return
  }
  stageSubmitting.value = true
  try {
    await addStage(projectId, normalizeNumbers(stageForm.value, ['baselineDays', 'beforeDays', 'afterDays']))
    ElMessage.success('阶段添加成功')
    stageDialogVisible.value = false
    stageForm.value = { name: '', repeatType: 'NONE', autoAdd: true, baselineDays: 0, baselineDirection: 'DAY', beforeDays: 0, afterDays: 0 }
    await fetchProject()
  } finally {
    stageSubmitting.value = false
  }
}

async function handleAddVisitPlan() {
  if (!visitForm.value.name || !visitForm.value.sourceStageId || !visitForm.value.targetStageId) {
    ElMessage.warning('请选择来源和目标阶段')
    return
  }
  visitSubmitting.value = true
  try {
    await addVisitPlan(projectId, normalizeNumbers(visitForm.value, ['sourceStageId', 'targetStageId', 'baselineDays', 'beforeDays', 'afterDays']))
    ElMessage.success('访视计划添加成功')
    visitDialogVisible.value = false
    visitForm.value = { name: '', sourceStageId: null, targetStageId: null, baselineDays: 0, baselineDirection: 'DAY', beforeDays: 0, afterDays: 0, crfComponentId: '' }
    await fetchProject()
  } finally {
    visitSubmitting.value = false
  }
}

async function handleAddCrf() {
  if (!crfForm.value.crfId || !crfForm.value.stageId) {
    ElMessage.warning('请选择CRF模板和关联阶段')
    return
  }
  crfSubmitting.value = true
  try {
    await bindCrf(projectId, normalizeNumbers(crfForm.value, ['stageId', 'crfVersionId']))
    ElMessage.success('CRF绑定成功')
    crfDialogVisible.value = false
    selectedCrfTemplateId.value = null
    crfForm.value = { stageId: null, crfId: null, crfVersionId: null, userInputEnabled: true }
    await fetchProject()
  } finally {
    crfSubmitting.value = false
  }
}

async function handleAddPersonnel() {
  if (!personnelForm.value.userId || !personnelForm.value.siteId || !personnelForm.value.role) {
    ElMessage.warning('请选择用户、中心和角色')
    return
  }
  personnelSubmitting.value = true
  try {
    await assignPersonnel(projectId, normalizeNumbers(personnelForm.value, ['userId', 'siteId']))
    ElMessage.success('人员添加成功')
    personnelDialogVisible.value = false
    personnelForm.value = { userId: '', siteId: '', role: 'CRC' }
    await Promise.all([fetchProject(), fetchPersonnelOptions()])
  } finally {
    personnelSubmitting.value = false
  }
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function normalizeNumbers(payload, keys) {
  const normalized = { ...payload }
  keys.forEach(key => {
    if (normalized[key] !== null && normalized[key] !== undefined && normalized[key] !== '') {
      normalized[key] = Number(normalized[key])
    }
  })
  return normalized
}

function stageName(id) {
  const stage = stages.value.find(item => Number(item.id) === Number(id))
  return stage ? stage.name : (id ? `阶段 ${id}` : '-')
}

async function fetchCrfTemplates(keyword = '') {
  crfTemplateLoading.value = true
  try {
    const res = await getCrfTemplates({ page: 0, size: 50, keyword })
    crfTemplates.value = res.content || res.records || []
  } finally {
    crfTemplateLoading.value = false
  }
}

function searchCrfTemplates(keyword) {
  fetchCrfTemplates(keyword)
}

function handleCrfTemplateChange(templateId) {
  const template = crfTemplates.value.find(item => String(item.id) === String(templateId))
  crfForm.value.crfId = templateId
  crfForm.value.crfVersionId = template?.defaultVersionId || 1
}

function formatCrfTemplateOption(template) {
  const code = template.code ? ` / ${template.code}` : ''
  const status = template.status ? ` / ${template.status}` : ''
  return `${template.name || `CRF ${template.id}`}${code}${status}`
}

function crfTemplateLabel(id) {
  const template = crfTemplates.value.find(item => String(item.id) === String(id))
  return template ? formatCrfTemplateOption(template) : (id ? `CRF ${id}` : '-')
}

async function fetchPersonnelOptions() {
  const res = await getPersonnelOptions(projectId)
  personnelOptions.value = {
    users: (res.users || []).map(item => ({ ...item, id: String(item.id) })),
    sites: (res.sites || []).map(item => ({ ...item, id: String(item.id) }))
  }
}

async function openPersonnelDialog() {
  personnelDialogVisible.value = true
  await fetchPersonnelOptions()
}

function mergePersonnelOptionsFromRows(rows) {
  rows.forEach(row => {
    addPersonnelOption('users', row.userId, `用户 ${row.userId}`)
    addPersonnelOption('sites', row.siteId, `中心 ${row.siteId}`)
  })
}

function addPersonnelOption(group, id, label) {
  if (id === null || id === undefined || id === '') return
  const values = personnelOptions.value[group] || []
  const stringId = String(id)
  if (!values.some(item => String(item.id) === stringId)) {
    values.push({ id: stringId, label })
  }
  personnelOptions.value[group] = values
}

function formatOption(option) {
  return option?.label ? `${option.label} / ${option.id}` : String(option?.id || '')
}

function userOptionLabel(id) {
  const option = personnelOptions.value.users.find(item => String(item.id) === String(id))
  return option ? formatOption(option) : (id ? `用户 ${id}` : '-')
}

function siteOptionLabel(id) {
  const option = personnelOptions.value.sites.find(item => String(item.id) === String(id))
  return option ? formatOption(option) : (id ? `中心 ${id}` : '-')
}

function formatBaseline(row) {
  if (row.baselineDays === null || row.baselineDays === undefined) return '-'
  return `${row.baselineDays}${row.baselineDirection || 'DAY'}`
}

function formatWindow(row) {
  const before = row.beforeDays ?? 0
  const after = row.afterDays ?? 0
  return `-${before}/+${after}天`
}
</script>

<style scoped>
h2 {
  margin: 0 0 8px;
}
.timeline {
  position: relative;
  padding-left: 30px;
}
.timeline-item {
  position: relative;
  padding-bottom: 20px;
}
.timeline-dot {
  position: absolute;
  left: -22px;
  top: 4px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #409EFF;
  border: 2px solid #a0cfff;
}
.timeline-dot.first {
  background: #67C23A;
  border-color: #b3e19d;
}
.timeline-dot.last {
  background: #E6A23C;
  border-color: #f0c78a;
}
.timeline-dot::before {
  content: '';
  position: absolute;
  left: 4px;
  top: 12px;
  width: 2px;
  height: calc(100% + 20px);
  background: #e4e7ed;
}
.timeline-dot.last::before {
  display: none;
}
.timeline-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 14px;
  background: #f5f7fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}
.timeline-header {
  display: flex;
  align-items: center;
  flex: 1;
}
.timeline-arrow {
  color: #409EFF;
  font-size: 18px;
  font-weight: bold;
}
.timeline-meta {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}
</style>
