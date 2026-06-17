<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2 style="margin:0">分析项目</h2>
      <el-button type="primary" @click="createDialogVisible = true">
        <el-icon><Plus /></el-icon>新建分析项目
      </el-button>
    </div>

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-table :data="list" v-loading="loading" stripe style="width:100%" row-key="id">
          <el-table-column prop="name" label="项目名称" min-width="200" />
          <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                type="warning"
                link
                size="small"
                @click="openExecuteDialog(row)"
                :disabled="!(row.analysisConfigs && row.analysisConfigs.length)"
              >
                执行分析
              </el-button>
            </template>
          </el-table-column>
          <el-table-column type="expand" width="40">
            <template #default="{ row }">
              <div style="padding:12px 0">
                <el-tabs>
                  <el-tab-pane label="变量">
                    <div v-if="row.variables && row.variables.length > 0">
                      <el-table :data="row.variables" size="small" stripe>
                        <el-table-column prop="name" label="变量名" width="160" />
                        <el-table-column prop="label" label="标签" min-width="160" />
                        <el-table-column prop="type" label="类型" width="100" />
                      </el-table>
                    </div>
                    <div v-else style="text-align:center;color:#999;padding:16px 0">
                      暂无变量数据
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="配置">
                    <div v-if="row.analysisConfigs && row.analysisConfigs.length > 0" style="display:flex;flex-direction:column;gap:8px">
                      <div v-for="cfg in row.analysisConfigs" :key="cfg.id" style="padding:8px 12px;background:#f5f7fa;border-radius:4px">
                        <div style="font-size:13px;color:#606266">
                          <strong>{{ cfg.configKey || cfg.name }}</strong>:
                          {{ cfg.configValue || cfg.value }}
                        </div>
                      </div>
                    </div>
                    <div v-else style="text-align:center;color:#999;padding:16px 0">
                      暂无配置数据
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="结果">
                    <div v-if="row.results && row.results.length > 0">
                      <el-table :data="row.results" size="small" stripe>
                        <el-table-column prop="resultName" label="结果名称" min-width="160" />
                        <el-table-column prop="resultValue" label="结果值" min-width="160" />
                        <el-table-column prop="resultType" label="类型" width="100" />
                      </el-table>
                    </div>
                    <div v-else style="text-align:center;color:#999;padding:16px 0">
                      暂无结果数据，请先执行分析
                    </div>
                  </el-tab-pane>
                </el-tabs>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="list.length === 0 && !loading" style="text-align:center;color:#999;padding:40px 0">
          暂无分析项目
        </div>
      </template>
    </el-skeleton>

    <!-- Create Analysis Project Dialog -->
    <el-dialog v-model="createDialogVisible" title="新建分析项目" width="480px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="createForm.name" placeholder="如: 安全性分析" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="项目描述（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="createSubmitting" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>

    <!-- Execute Analysis Dialog -->
    <el-dialog v-model="executeDialogVisible" title="执行分析" width="420px">
      <el-form :model="executeForm" label-width="100px">
        <el-form-item label="分析项目">
          <span>{{ executeForm.projectName }}</span>
        </el-form-item>
        <el-form-item label="配置ID" prop="configId">
          <el-select v-model="executeForm.configId" style="width:100%" placeholder="请选择配置">
            <el-option v-for="cfg in executeForm.configs" :key="cfg.id" :label="cfg.name || cfg.id" :value="cfg.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="executeDialogVisible = false">取消</el-button>
        <el-button type="warning" :loading="executeSubmitting" @click="handleExecute">执行</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAnalysisProjects, getAnalysisProject, createAnalysisProject, executeAnalysis } from '../../api/analysis'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(true)
const list = ref([])

// Create dialog
const createDialogVisible = ref(false)
const createSubmitting = ref(false)
const createForm = ref({ name: '', description: '' })

// Execute dialog
const executeDialogVisible = ref(false)
const executeSubmitting = ref(false)
const executeForm = ref({ projectId: null, projectName: '', configId: null, configs: [] })

onMounted(async () => {
  await loadList()
  loading.value = false
})

async function loadList() {
  try {
    list.value = await getAnalysisProjects() || []
  } catch {
    list.value = []
    ElMessage.error('加载分析项目列表失败')
  }
}

async function handleCreate() {
  if (!createForm.value.name) {
    ElMessage.warning('请输入项目名称')
    return
  }
  createSubmitting.value = true
  try {
    await createAnalysisProject(createForm.value)
    ElMessage.success('分析项目创建成功')
    createDialogVisible.value = false
    createForm.value = { name: '', description: '' }
    await loadList()
  } catch {
    ElMessage.error('创建分析项目失败')
  } finally {
    createSubmitting.value = false
  }
}

function openExecuteDialog(row) {
  const configs = row.analysisConfigs || []
  if (!configs.length) {
    ElMessage.warning('该分析项目暂无配置，不能执行')
    return
  }
  executeForm.value = { projectId: row.id, projectName: row.name, configId: configs[0].id, configs }
  executeDialogVisible.value = true
}

async function handleExecute() {
  if (!executeForm.value.configId) {
    ElMessage.warning('请选择分析配置')
    return
  }
  executeSubmitting.value = true
  try {
    await executeAnalysis(executeForm.value.projectId, { configId: executeForm.value.configId })
    ElMessage.success('分析任务已提交执行')
    executeDialogVisible.value = false
  } catch {
    ElMessage.error('执行分析失败')
  } finally {
    executeSubmitting.value = false
  }
}
</script>

<style scoped>
h2 {
  margin: 0;
}
</style>
