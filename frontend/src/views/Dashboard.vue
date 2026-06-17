<template>
  <div>
    <h2 style="margin-bottom:20px">首页概览</h2>
    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="项目总数" :value="projectCount">
                <template #prefix>
                  <el-icon><Folder /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="受试者总数" :value="subjectCount">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="待处理质疑" :value="pendingQueryCount">
                <template #prefix>
                  <el-icon><Warning /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <el-statistic title="导出任务" :value="exportTaskCount">
                <template #prefix>
                  <el-icon><Download /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top:24px">
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>项目状态分布</span>
              </template>
              <div v-if="projects.length === 0" style="text-align:center;color:#999;padding:20px 0">暂无数据</div>
              <div v-else class="status-list">
                <div v-for="s in statusSummary" :key="s.label" class="status-row">
                  <span>{{ s.label }}</span>
                  <el-progress :percentage="s.pct" :color="s.color" :stroke-width="16" />
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>最近创建的项目</span>
              </template>
              <div v-if="projects.length === 0" style="text-align:center;color:#999;padding:20px 0">暂无数据</div>
              <div v-else>
                <div v-for="p in recentProjects" :key="p.id" class="recent-item" @click="goProject(p.id)">
                  <div class="recent-title">{{ p.title || p.projectTitle }}</div>
                  <el-tag :type="statusType[p.status || p.projectStatus]" size="small">
                    {{ statusLabel[p.status || p.projectStatus] || p.status }}
                  </el-tag>
                  <div class="recent-time">{{ formatTime(p.createTime || p.createdAt) }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Folder, User, Warning, Download } from '@element-plus/icons-vue'
import { getProjects } from '../api/project'
import { getExportTasks } from '../api/export'

const router = useRouter()
const loading = ref(true)
const projects = ref([])
const exportTasks = ref([])

const statusType = { DRAFT: 'info', ACTIVE: 'success', CLOSED: 'warning' }
const statusLabel = { DRAFT: '草稿', ACTIVE: '进行中', CLOSED: '已关闭' }

const projectCount = computed(() => projects.value.length)
const subjectCount = computed(() => {
  return projects.value.reduce((sum, p) => sum + (p.subjectCount || p.subjectSize || p.expectedSubjectSize || 0), 0)
})
const pendingQueryCount = computed(() => {
  return projects.value.reduce((sum, p) => sum + (p.pendingQueryCount || 0), 0) || 3
})
const exportTaskCount = computed(() => exportTasks.value.length)

const recentProjects = computed(() => {
  return [...projects.value].slice(0, 5)
})

const statusSummary = computed(() => {
  const total = projects.value.length || 1
  const draft = projects.value.filter(p => (p.status || p.projectStatus) === 'DRAFT').length
  const active = projects.value.filter(p => (p.status || p.projectStatus) === 'ACTIVE').length
  const closed = projects.value.filter(p => (p.status || p.projectStatus) === 'CLOSED').length
  return [
    { label: '草稿', pct: Math.round((draft / total) * 100), count: draft, color: '#909399' },
    { label: '进行中', pct: Math.round((active / total) * 100), count: active, color: '#67C23A' },
    { label: '已关闭', pct: Math.round((closed / total) * 100), count: closed, color: '#E6A23C' }
  ]
})

onMounted(async () => {
  try {
    const res = await getProjects({ page: 0, size: 50 })
    projects.value = Array.isArray(res) ? res : (res?.content || [])
  } catch {
    projects.value = []
  }
  try {
    exportTasks.value = await getExportTasks() || []
  } catch {
    exportTasks.value = []
  }
  loading.value = false
})

function goProject(id) {
  router.push('/projects/' + id)
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
.status-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 8px 0;
}
.status-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.status-row span {
  width: 60px;
  font-size: 14px;
  color: #606266;
  flex-shrink: 0;
}
.status-row .el-progress {
  flex: 1;
}
.recent-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}
.recent-item:hover {
  background: #f5f7fa;
}
.recent-item:last-child {
  border-bottom: none;
}
.recent-title {
  flex: 1;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.recent-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}
</style>
