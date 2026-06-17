<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2>项目中心</h2>
      <el-button type="primary" @click="openCreateDialog">
        <el-icon><Plus /></el-icon>
        新建项目
      </el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe style="width:100%" @row-click="goDetail">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="项目标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="120">
        <template #default="{ row }">
          {{ typeLabel[row.type] || row.type }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]" size="small">
            {{ statusLabel[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="prefix" label="缩写" width="100" />
      <el-table-column prop="expectedSubjectSize" label="预计例数" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click.stop="goDetail(row)">
            查看
          </el-button>
        </template>
      </el-table-column>
    </el-table>

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

    <el-dialog v-model="dialogVisible" title="新建项目" width="640px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入项目标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类型" prop="type">
              <el-select v-model="form.type" style="width:100%">
                <el-option label="干预性研究" value="INTERVENTIONAL" />
                <el-option label="观察性研究" value="OBSERVATIONAL" />
                <el-option label="诊断试验" value="DIAGNOSTIC" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目缩写" prop="prefix">
              <el-input v-model="form.prefix" placeholder="如: RCT001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计例数" prop="expectedSubjectSize">
              <el-input-number v-model="form.expectedSubjectSize" :min="0" :max="99999" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开放筛选" prop="openScreen">
              <el-switch v-model="form.openScreen" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleCreate">确认创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { getProjects, createProject } from '../../api/project'
import { ElMessage } from 'element-plus'

const router = useRouter()
const list = ref([])
const loading = ref(false)
const submitting = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({
  title: '',
  type: 'INTERVENTIONAL',
  prefix: '',
  expectedSubjectSize: 0,
  openScreen: true
})

const rules = {
  title: [{ required: true, message: '请输入项目标题', trigger: 'blur' }],
  prefix: [{ required: true, message: '请输入项目缩写', trigger: 'blur' }]
}

const statusType = { DRAFT: 'info', ACTIVE: 'success', CLOSED: 'warning' }
const statusLabel = { DRAFT: '草稿', ACTIVE: '进行中', CLOSED: '已关闭' }
const typeLabel = { INTERVENTIONAL: '干预性研究', OBSERVATIONAL: '观察性研究', DIAGNOSTIC: '诊断试验' }

onMounted(() => fetchList())

async function fetchList() {
  loading.value = true
  try {
    const res = await getProjects({ page: page.value - 1, size: size.value })
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

function openCreateDialog() {
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
  form.value = { title: '', type: 'INTERVENTIONAL', prefix: '', expectedSubjectSize: 0, openScreen: true }
}

async function handleCreate() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await createProject(form.value)
    ElMessage.success('项目创建成功')
    dialogVisible.value = false
    await fetchList()
  } catch {
    // error already handled by interceptor
  } finally {
    submitting.value = false
  }
}

function goDetail(row) {
  router.push('/projects/' + row.id)
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
