<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2>CRF模板管理</h2>
      <el-button type="primary" @click="openCreateDialog">
        <el-icon><Plus /></el-icon>
        新建模板
      </el-button>
    </div>

    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="名称/编码/分类" clearable style="width:240px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width:160px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="query.category" clearable style="width:180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="list" v-loading="loading" stripe style="width:100%">
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="name" label="模板名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="code" label="编码" width="140" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'" size="small">
            {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="formCount" label="表单数" width="90" />
      <el-table-column prop="fieldCount" label="字段数" width="90" />
      <el-table-column prop="estimateTime" label="预计耗时" width="120" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goDetail(row.id)">查看</el-button>
          <el-button link type="primary" @click="copyRow(row)">复制</el-button>
          <el-button v-if="row.status === 'DRAFT'" link type="success" @click="goDetail(row.id, true)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" title="新建模板" width="640px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="预计耗时"><el-input v-model="form.estimateTime" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.notice" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="介绍"><el-input v-model="form.introduce" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleCreate">确认创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { copyCrfTemplate, createCrfTemplate, getCrfTemplates } from '../../api/crf'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const list = ref([])
const query = reactive({ keyword: '', status: '', category: '' })
const form = reactive({ name: '', code: '', category: '', estimateTime: '', notice: '', introduce: '' })

onMounted(fetchList)

async function fetchList() {
  loading.value = true
  try {
    const res = await getCrfTemplates({
      page: page.value - 1,
      size: size.value,
      keyword: query.keyword || undefined,
      status: query.status || undefined,
      category: query.category || undefined
    })
    list.value = res.content || []
    total.value = res.total || list.value.length
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  dialogVisible.value = true
}

async function handleCreate() {
  submitting.value = true
  try {
    const res = await createCrfTemplate({ ...form, forms: [] })
    dialogVisible.value = false
    ElMessage.success('模板已创建')
    goDetail(res.id)
  } finally {
    submitting.value = false
  }
}

async function copyRow(row) {
  const res = await copyCrfTemplate(row.id, { name: `${row.name} 副本` })
  ElMessage.success('模板已复制')
  goDetail(res.id)
}

function goDetail(id, edit = false) {
  router.push(`/crf-templates/${id}${edit ? '?edit=1' : ''}`)
}
</script>
