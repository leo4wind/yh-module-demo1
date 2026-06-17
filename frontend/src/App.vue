<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background:#304156">
      <div style="color:#fff;padding:16px;font-size:16px;text-align:center;border-bottom:1px solid #495468">
        临床研究数据管理
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
        style="border-right:none">
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/projects">
          <el-icon><Folder /></el-icon>
          <span>项目中心</span>
        </el-menu-item>
        <el-menu-item index="/export-tasks">
          <el-icon><Download /></el-icon>
          <span>数据导出</span>
        </el-menu-item>
        <el-menu-item index="/analysis">
          <el-icon><DataAnalysis /></el-icon>
          <span>统计分析</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main style="background:#f0f2f5;padding:20px">
      <router-view />
    </el-main>
  </el-container>
</template>

<script>
import { DataAnalysis, Download, Folder, HomeFilled } from '@element-plus/icons-vue'

export default {
  components: {
    DataAnalysis,
    Download,
    Folder,
    HomeFilled
  },
  computed: {
    activeMenu() {
      const path = this.$route.path
      if (path.startsWith('/projects')) return '/projects'
      if (path.startsWith('/subjects') || path.startsWith('/assessments')) return '/projects'
      if (path.startsWith('/export-tasks')) return '/export-tasks'
      if (path.startsWith('/analysis')) return '/analysis'
      return '/'
    }
  }
}
</script>
