<template>
  <div>
    <el-button style="margin-bottom:12px" @click="$router.push('/projects/' + projectId)">返回项目详情</el-button>

    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2 style="margin:0">受试者列表</h2>
      <div style="display:flex;gap:8px">
        <el-button type="primary" @click="filterDialogVisible = true">
          <el-icon><Search /></el-icon>筛选受试者
        </el-button>
        <el-button type="success" @click="openEnrollDialog">
          <el-icon><Plus /></el-icon>直接入组
        </el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" stripe style="width:100%" @row-click="goDetail">
      <el-table-column prop="code" label="受试者编号" width="140" />
      <el-table-column prop="blh" label="病历号" width="140" />
      <el-table-column prop="syxh" label="试验序号" width="100" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]" size="small">
            {{ statusLabel[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="gender" label="性别" width="70">
        <template #default="{ row }">{{ row.gender === 'MALE' ? '男' : row.gender === 'FEMALE' ? '女' : row.gender }}</template>
      </el-table-column>
      <el-table-column label="年龄" width="70">
        <template #default="{ row }">{{ row.age || '-' }}</template>
      </el-table-column>
      <el-table-column prop="enrollDate" label="入组日期" width="120">
        <template #default="{ row }">{{ formatDate(row.enrollDate) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click.stop="goDetail(row)">
            查看
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="list.length === 0 && !loading" style="text-align:center;color:#999;padding:40px 0">
      暂无受试者数据
    </div>

    <div style="margin-top:16px;display:flex;justify-content:flex-end">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="fetchList"
        @size-change="fetchList"
      />
    </div>

    <!-- Enroll Subject Dialog -->
    <el-dialog v-model="enrollDialogVisible" title="新增受试者并直接入组" width="520px" @close="resetEnrollForm">
      <el-form ref="enrollFormRef" :model="enrollForm" :rules="enrollRules" label-width="110px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="enrollForm.name" placeholder="填写受试者姓名" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="enrollForm.gender" placeholder="请选择性别" style="width:100%">
                <el-option label="男" value="MALE" />
                <el-option label="女" value="FEMALE" />
                <el-option label="未知" value="UNKNOWN" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="enrollForm.age" :min="0" :max="130" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="病历号" prop="blh">
          <el-input v-model="enrollForm.blh" placeholder="填写院内病历号" />
        </el-form-item>
        <el-form-item label="试验序号" prop="syxh">
          <el-input v-model="enrollForm.syxh" placeholder="填写或留空自动生成" />
        </el-form-item>
        <el-form-item label="研究中心" prop="siteId">
          <el-select v-model="enrollForm.siteId" filterable placeholder="请选择研究中心" style="width:100%">
            <el-option
              v-for="site in personnelOptions.sites"
              :key="site.id"
              :label="formatOption(site)"
              :value="String(site.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="入组人员" prop="userId">
          <el-select v-model="enrollForm.userId" filterable placeholder="请选择入组操作人" style="width:100%">
            <el-option
              v-for="user in personnelOptions.users"
              :key="user.id"
              :label="formatOption(user)"
              :value="String(user.id)" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="enrollDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="enrollSubmitting" @click="handleDirectEnroll">确认入组</el-button>
      </template>
    </el-dialog>

    <!-- Filter Dialog -->
    <el-dialog v-model="filterDialogVisible" title="筛选受试者" width="480px">
      <el-form :model="filterForm" label-width="100px">
        <el-form-item label="受试者编号">
          <el-input v-model="filterForm.code" placeholder="模糊搜索" />
        </el-form-item>
        <el-form-item label="病历号">
          <el-input v-model="filterForm.blh" placeholder="模糊搜索" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" style="width:100%" clearable placeholder="全部">
            <el-option label="筛选入组" value="SCREENING" />
            <el-option label="已入组" value="ENROLLED" />
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="提前终止" value="TERMINATED" />
            <el-option label="退出" value="WITHDRAWN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="filterDialogVisible = false; filterForm = {}; fetchList()">重置</el-button>
        <el-button type="primary" @click="filterDialogVisible = false; fetchList()">确认筛选</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Plus } from '@element-plus/icons-vue'
import { getSubjects, directEnroll } from '../../api/subject'
import { getPersonnelOptions } from '../../api/project'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const projectId = route.params.projectId

const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)

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

// Enroll form
const enrollDialogVisible = ref(false)
const enrollSubmitting = ref(false)
const enrollFormRef = ref(null)
const enrollForm = ref({
  userId: '',
  siteId: '',
  name: '',
  gender: '',
  age: null,
  blh: '',
  syxh: ''
})
const enrollRules = {
  name: [{ required: true, message: '请填写受试者姓名', trigger: 'blur' }],
  blh: [{ required: true, message: '请填写病历号', trigger: 'blur' }],
  userId: [{ required: true, message: '请选择入组人员', trigger: 'change' }],
  siteId: [{ required: true, message: '请选择研究中心', trigger: 'change' }]
}
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

// Filter form
const filterDialogVisible = ref(false)
const filterForm = ref({})

onMounted(async () => {
  await Promise.all([fetchList(), fetchPersonnelOptions()])
})

async function fetchList() {
  loading.value = true
  try {
    const params = { page: page.value - 1, size: size.value, ...filterForm.value }
    const res = await getSubjects(projectId, params)
    if (Array.isArray(res)) {
      list.value = res
      total.value = res.length
    } else if (res && res.content) {
      list.value = res.content
      total.value = res.total || res.content.length
    } else {
      list.value = res || []
      total.value = (res && res.total) || list.value.length
    }
  } catch {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function resetEnrollForm() {
  enrollFormRef.value?.resetFields()
  enrollForm.value = { userId: '', siteId: '', name: '', gender: '', age: null, blh: '', syxh: '' }
}

async function openEnrollDialog() {
  enrollDialogVisible.value = true
  await fetchPersonnelOptions()
}

async function handleDirectEnroll() {
  const valid = await enrollFormRef.value?.validate().catch(() => false)
  if (!valid) return
  enrollSubmitting.value = true
  try {
    await directEnroll({
      projectId: Number(projectId),
      siteId: Number(enrollForm.value.siteId),
      userId: Number(enrollForm.value.userId),
      name: enrollForm.value.name,
      gender: enrollForm.value.gender,
      age: enrollForm.value.age,
      blh: enrollForm.value.blh,
      syxh: String(enrollForm.value.syxh || ''),
      groupSubsetIds: []
    })
    ElMessage.success('受试者入组成功')
    enrollDialogVisible.value = false
    await fetchList()
  } finally {
    enrollSubmitting.value = false
  }
}

function goDetail(row) {
  router.push('/subjects/' + row.id)
}

async function fetchPersonnelOptions() {
  const res = await getPersonnelOptions(projectId)
  personnelOptions.value = {
    users: (res.users || []).map(item => ({ ...item, id: String(item.id) })),
    sites: (res.sites || []).map(item => ({ ...item, id: String(item.id) }))
  }
}

function formatOption(option) {
  return option?.label ? `${option.label} / ${option.id}` : String(option?.id || '')
}

function formatDate(d) {
  if (!d) return ''
  const dt = new Date(d)
  const pad = (n) => String(n).padStart(2, '0')
  return `${dt.getFullYear()}-${pad(dt.getMonth() + 1)}-${pad(dt.getDate())}`
}
</script>

<style scoped>
h2 {
  margin: 0;
}
</style>
