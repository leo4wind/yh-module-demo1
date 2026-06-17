<template>
  <div v-loading="loading">
    <el-button style="margin-bottom:12px" @click="$router.push('/crf-templates')">返回列表</el-button>

    <el-card style="margin-bottom:16px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form :model="form" label-width="100px">
            <el-form-item label="名称"><el-input v-model="form.name" :disabled="readonly" /></el-form-item>
            <el-form-item label="编码"><el-input v-model="form.code" :disabled="readonly" /></el-form-item>
            <el-form-item label="分类"><el-input v-model="form.category" :disabled="readonly" /></el-form-item>
            <el-form-item label="预计耗时"><el-input v-model="form.estimateTime" :disabled="readonly" /></el-form-item>
          </el-form>
        </el-col>
        <el-col :span="12">
          <el-form :model="form" label-width="100px">
            <el-form-item label="说明"><el-input v-model="form.notice" type="textarea" :rows="3" :disabled="readonly" /></el-form-item>
            <el-form-item label="介绍"><el-input v-model="form.introduce" type="textarea" :rows="5" :disabled="readonly" /></el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <div style="display:flex;gap:8px;justify-content:flex-end">
        <el-button @click="cloneTemplate">复制</el-button>
        <el-button v-if="!readonly" type="primary" @click="saveTemplate" :loading="saving">保存</el-button>
        <el-button v-if="!readonly" type="success" @click="publishTemplate" :loading="publishing">发布</el-button>
      </div>
    </el-card>

    <el-card>
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
        <strong>表单结构</strong>
        <el-button v-if="!readonly" type="primary" size="small" @click="addForm">添加表单</el-button>
      </div>
      <div v-for="(item, index) in form.forms" :key="index" style="border:1px solid #ebeef5;padding:12px;margin-bottom:12px;border-radius:6px">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
          <strong>表单 {{ index + 1 }}</strong>
          <el-button v-if="!readonly" link type="danger" @click="deleteForm(index)">删除表单</el-button>
        </div>
        <el-row :gutter="12">
          <el-col :span="8"><el-input v-model="item.modelName" placeholder="表单名" :disabled="readonly" /></el-col>
          <el-col :span="8"><el-input v-model="item.refName" placeholder="引用名" :disabled="readonly" /></el-col>
          <el-col :span="8"><el-input v-model="item.rulesName" placeholder="规则名" :disabled="readonly" /></el-col>
        </el-row>
        <div style="margin:12px 0 8px;display:flex;justify-content:space-between;align-items:center">
          <span>字段</span>
          <el-button v-if="!readonly" size="small" @click="addField(item)">添加字段</el-button>
        </div>
        <el-table :data="item.fields" size="small" stripe>
          <el-table-column prop="fieldCode" label="字段编码" min-width="140" />
          <el-table-column prop="fieldLabel" label="字段名称" min-width="140" />
          <el-table-column prop="fieldType" label="类型" width="110" />
          <el-table-column label="必填" width="80">
            <template #default="{ row }">{{ row.required ? '是' : '否' }}</template>
          </el-table-column>
          <el-table-column label="隐藏" width="80">
            <template #default="{ row }">{{ row.hidden ? '是' : '否' }}</template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序" width="90" />
          <el-table-column label="操作" width="150">
            <template #default="{ $index }">
              <el-button link type="primary" @click="editField(item, $index)" :disabled="readonly">编辑</el-button>
              <el-button v-if="!readonly" link type="danger" @click="deleteField(item, $index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="fieldDialogVisible" title="字段编辑" width="720px">
      <el-form :model="fieldForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="字段编码"><el-input v-model="fieldForm.fieldCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="字段名称"><el-input v-model="fieldForm.fieldLabel" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="字段类型">
              <el-select v-model="fieldForm.fieldType" style="width:100%">
                <el-option label="文本" value="TEXT" />
                <el-option label="多行文本" value="TEXTAREA" />
                <el-option label="数字" value="NUMBER" />
                <el-option label="日期" value="DATE" />
                <el-option label="单选" value="RADIO" />
                <el-option label="下拉" value="SELECT" />
                <el-option label="多选" value="CHECKBOX" />
                <el-option label="文件" value="FILE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="排序"><el-input-number v-model="fieldForm.sortOrder" :min="0" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="默认值"><el-input v-model="fieldForm.defaultValue" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="单位"><el-input v-model="fieldForm.dataUnit" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="必填"><el-switch v-model="fieldForm.required" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="隐藏"><el-switch v-model="fieldForm.hidden" /></el-form-item></el-col>
        </el-row>
        <div v-if="supportsOptions(fieldForm.fieldType)">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
            <span>选项</span>
            <el-button size="small" @click="addOption">添加选项</el-button>
          </div>
          <el-table :data="fieldForm.options" size="small">
            <el-table-column label="显示文本" min-width="150">
              <template #default="{ row }"><el-input v-model="row.optionLabel" /></template>
            </el-table-column>
            <el-table-column label="存储值" min-width="150">
              <template #default="{ row }"><el-input v-model="row.optionValue" /></template>
            </el-table-column>
            <el-table-column label="排序" width="110">
              <template #default="{ row }"><el-input-number v-model="row.sortOrder" :min="0" style="width:100%" /></template>
            </el-table-column>
            <el-table-column label="分值" width="120">
              <template #default="{ row }"><el-input-number v-model="row.score" style="width:100%" /></template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ $index }">
                <el-button link type="danger" @click="fieldForm.options.splice($index, 1)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="fieldDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveField">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { copyCrfTemplate, getCrfTemplate, updateCrfTemplate, publishCrfTemplate } from '../../api/crf'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const publishing = ref(false)
const fieldDialogVisible = ref(false)
const editingFieldPath = ref(null)
const form = reactive({ name: '', code: '', category: '', estimateTime: '', notice: '', introduce: '', forms: [] })
const fieldForm = reactive({ fieldCode: '', fieldLabel: '', fieldType: 'TEXT', defaultValue: '', dataUnit: '', required: true, hidden: false, sortOrder: 0, options: [] })

const readonly = computed(() => route.query.edit !== '1' && form.status === 'PUBLISHED')

onMounted(fetchDetail)

watch(() => route.params.id, fetchDetail)

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getCrfTemplate(route.params.id)
    Object.assign(form, res)
    form.forms = res.forms || []
  } finally {
    loading.value = false
  }
}

async function saveTemplate() {
  saving.value = true
  try {
    await updateCrfTemplate(route.params.id, form)
    ElMessage.success('已保存')
    await fetchDetail()
  } finally {
    saving.value = false
  }
}

async function publishTemplate() {
  publishing.value = true
  try {
    await publishCrfTemplate(route.params.id)
    ElMessage.success('已发布')
    await fetchDetail()
  } finally {
    publishing.value = false
  }
}

async function cloneTemplate() {
  const res = await copyCrfTemplate(route.params.id, { name: `${form.name} 副本` })
  router.push(`/crf-templates/${res.id}?edit=1`)
}

function addForm() {
  form.forms.push({ modelName: '', refName: '', rulesName: '', fields: [] })
}

async function deleteForm(index) {
  const target = form.forms[index]
  const fieldCount = target?.fields?.length || 0
  try {
    await ElMessageBox.confirm(
      fieldCount > 0 ? `该表单下有 ${fieldCount} 个字段，删除后会一起移除。确认删除？` : '确认删除该表单？',
      '删除表单',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
    form.forms.splice(index, 1)
  } catch {
    // user cancelled
  }
}

function addField(formItem) {
  editingFieldPath.value = { formItem, index: -1 }
  Object.assign(fieldForm, { fieldCode: '', fieldLabel: '', fieldType: 'TEXT', defaultValue: '', dataUnit: '', required: true, hidden: false, sortOrder: 0, options: [] })
  fieldDialogVisible.value = true
}

function editField(formItem, index) {
  editingFieldPath.value = { formItem, index }
  Object.assign(fieldForm, formItem.fields[index])
  fieldDialogVisible.value = true
}

async function deleteField(formItem, index) {
  try {
    await ElMessageBox.confirm('确认删除该字段？', '删除字段', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    formItem.fields.splice(index, 1)
  } catch {
    // user cancelled
  }
}

function saveField() {
  const payload = { ...fieldForm, options: supportsOptions(fieldForm.fieldType) ? fieldForm.options || [] : [] }
  const target = editingFieldPath.value.formItem.fields
  if (editingFieldPath.value.index >= 0) target.splice(editingFieldPath.value.index, 1, payload)
  else target.push(payload)
  fieldDialogVisible.value = false
}

function addOption() {
  if (!fieldForm.options) fieldForm.options = []
  fieldForm.options.push({ optionLabel: '', optionValue: '', sortOrder: fieldForm.options.length + 1, score: null })
}

function supportsOptions(fieldType) {
  return ['SELECT', 'RADIO', 'CHECKBOX'].includes(fieldType)
}
</script>
