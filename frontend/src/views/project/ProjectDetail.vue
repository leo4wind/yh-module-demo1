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
            <el-table-column prop="orderNum" label="序号" width="70" />
            <el-table-column prop="type" label="阶段类型" width="120">
              <template #default="{ row }">{{ stageTypeLabel[row.type] || row.type }}</template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
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
                  <strong>{{ vp.sourceName || vp.source }}</strong>
                  <el-tag size="small" v-if="vp.sourceStageName" style="margin-left:8px">{{ vp.sourceStageName }}</el-tag>
                </div>
                <div class="timeline-arrow">→</div>
                <div class="timeline-header">
                  <strong>{{ vp.targetName || vp.target }}</strong>
                  <el-tag size="small" v-if="vp.targetStageName" style="margin-left:8px">{{ vp.targetStageName }}</el-tag>
                </div>
                <div v-if="vp.allowDays || vp.dayWindow" class="timeline-meta">
                  允许间隔: {{ vp.allowDays || vp.dayWindow }} 天
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
            <el-table-column prop="crfName" label="CRF名称" min-width="160" />
            <el-table-column prop="stageName" label="关联阶段" width="140" />
            <el-table-column prop="visitPlanName" label="关联访视" width="140" />
            <el-table-column prop="category" label="分类" width="100">
              <template #default="{ row }">{{ crfCategoryLabel[row.category] || row.category }}</template>
            </el-table-column>
            <el-table-column label="必填" width="70">
              <template #default="{ row }">
                <el-tag :type="row.required ? 'danger' : 'info'" size="small">{{ row.required ? '是' : '否' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="crfBindings.length === 0" style="text-align:center;color:#999;padding:24px 0">暂无CRF绑定</div>
        </el-tab-pane>

        <el-tab-pane label="中心人员" name="personnel">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="personnelDialogVisible = true">
              <el-icon><Plus /></el-icon>添加人员
            </el-button>
          </div>
          <el-table :data="personnel" stripe size="small">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="role" label="角色" width="120">
              <template #default="{ row }">{{ roleLabel[row.role] || row.role }}</template>
            </el-table-column>
            <el-table-column prop="phone" label="电话" width="140" />
            <el-table-column prop="email" label="邮箱" min-width="180" />
            <el-table-column prop="centerName" label="中心" width="120" />
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
        <el-form-item label="阶段类型" required>
          <el-select v-model="stageForm.type" style="width:100%">
            <el-option label="筛选" value="SCREENING" />
            <el-option label="治疗" value="TREATMENT" />
            <el-option label="随访" value="FOLLOWUP" />
            <el-option label="结束" value="END" />
          </el-select>
        </el-form-item>
        <el-form-item label="序号">
          <el-input-number v-model="stageForm.orderNum" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="stageForm.description" type="textarea" :rows="3" />
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
          <el-select v-model="visitForm.source" style="width:100%">
            <el-option v-for="s in stages" :key="s.id" :label="s.name" :value="s.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标阶段" required>
          <el-select v-model="visitForm.target" style="width:100%">
            <el-option v-for="s in stages" :key="s.id" :label="s.name" :value="s.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="允许间隔(天)">
          <el-input-number v-model="visitForm.allowDays" :min="0" style="width:100%" />
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
        <el-form-item label="CRF名称" required>
          <el-input v-model="crfForm.crfName" placeholder="如: 知情同意书" />
        </el-form-item>
        <el-form-item label="关联阶段" required>
          <el-select v-model="crfForm.stageName" style="width:100%">
            <el-option v-for="s in stages" :key="s.id" :label="s.name" :value="s.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联访视">
          <el-select v-model="crfForm.visitPlanName" style="width:100%" clearable>
            <el-option v-for="vp in visitPlans" :key="vp.id" :label="(vp.sourceName||vp.source) + '→' + (vp.targetName||vp.target)" :value="vp.sourceName||vp.source" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="crfForm.category" style="width:100%">
            <el-option label="一般信息" value="GENERAL" />
            <el-option label="疗效指标" value="EFFICACY" />
            <el-option label="安全性" value="SAFETY" />
            <el-option label="实验室" value="LAB" />
          </el-select>
        </el-form-item>
        <el-form-item label="必填">
          <el-switch v-model="crfForm.required" />
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
            <el-form-item label="姓名" required>
              <el-input v-model="personnelForm.name" />
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
            <el-form-item label="电话">
              <el-input v-model="personnelForm.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="personnelForm.email" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="中心名称">
          <el-input v-model="personnelForm.centerName" />
        </el-form-item>
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
import { getProject, activateProject, closeProject, addStage, addVisitPlan, bindCrf, assignPersonnel } from '../../api/project'
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
const stageTypeLabel = { SCREENING: '筛选', TREATMENT: '治疗', FOLLOWUP: '随访', END: '结束' }
const crfCategoryLabel = { GENERAL: '一般信息', EFFICACY: '疗效指标', SAFETY: '安全性', LAB: '实验室' }
const roleLabel = { PI: '主要研究者', CRC: '研究协调员', DM: '数据管理员', CRA: '监查员' }

const stageDialogVisible = ref(false)
const stageSubmitting = ref(false)
const stageForm = ref({ name: '', type: 'SCREENING', orderNum: 1, description: '' })

const visitDialogVisible = ref(false)
const visitSubmitting = ref(false)
const visitForm = ref({ source: '', target: '', allowDays: 0 })

const crfDialogVisible = ref(false)
const crfSubmitting = ref(false)
const crfForm = ref({ crfName: '', stageName: '', visitPlanName: '', category: 'GENERAL', required: false })

const personnelDialogVisible = ref(false)
const personnelSubmitting = ref(false)
const personnelForm = ref({ name: '', role: 'CRC', phone: '', email: '', centerName: '' })

onMounted(() => fetchProject())

async function fetchProject() {
  loading.value = true
  try {
    const res = await getProject(projectId)
    project.value = res || {}
    stages.value = res.stages || res.stageList || []
    visitPlans.value = res.visitPlans || res.visitPlanList || []
    crfBindings.value = res.crfBindings || res.crfBindingList || []
    personnel.value = res.personnel || res.personnelList || []
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

async function handleAddStage() {
  if (!stageForm.value.name || !stageForm.value.type) {
    ElMessage.warning('请填写阶段名称和类型')
    return
  }
  stageSubmitting.value = true
  try {
    await addStage(projectId, stageForm.value)
    ElMessage.success('阶段添加成功')
    stageDialogVisible.value = false
    stageForm.value = { name: '', type: 'SCREENING', orderNum: 1, description: '' }
    await fetchProject()
  } finally {
    stageSubmitting.value = false
  }
}

async function handleAddVisitPlan() {
  if (!visitForm.value.source || !visitForm.value.target) {
    ElMessage.warning('请选择来源和目标阶段')
    return
  }
  visitSubmitting.value = true
  try {
    await addVisitPlan(projectId, visitForm.value)
    ElMessage.success('访视计划添加成功')
    visitDialogVisible.value = false
    visitForm.value = { source: '', target: '', allowDays: 0 }
    await fetchProject()
  } finally {
    visitSubmitting.value = false
  }
}

async function handleAddCrf() {
  if (!crfForm.value.crfName || !crfForm.value.stageName) {
    ElMessage.warning('请填写CRF名称并选择关联阶段')
    return
  }
  crfSubmitting.value = true
  try {
    await bindCrf(projectId, crfForm.value)
    ElMessage.success('CRF绑定成功')
    crfDialogVisible.value = false
    crfForm.value = { crfName: '', stageName: '', visitPlanName: '', category: 'GENERAL', required: false }
    await fetchProject()
  } finally {
    crfSubmitting.value = false
  }
}

async function handleAddPersonnel() {
  if (!personnelForm.value.name || !personnelForm.value.role) {
    ElMessage.warning('请填写姓名和角色')
    return
  }
  personnelSubmitting.value = true
  try {
    await assignPersonnel(projectId, personnelForm.value)
    ElMessage.success('人员添加成功')
    personnelDialogVisible.value = false
    personnelForm.value = { name: '', role: 'CRC', phone: '', email: '', centerName: '' }
    await fetchProject()
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
