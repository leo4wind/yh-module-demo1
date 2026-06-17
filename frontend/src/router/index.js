import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
  { path: '/projects', name: 'ProjectList', component: () => import('../views/project/ProjectList.vue') },
  { path: '/projects/:id', name: 'ProjectDetail', component: () => import('../views/project/ProjectDetail.vue') },
  { path: '/projects/:projectId/subjects', name: 'SubjectList', component: () => import('../views/subject/SubjectList.vue') },
  { path: '/subjects/:id', name: 'SubjectDetail', component: () => import('../views/subject/SubjectDetail.vue') },
  { path: '/assessments/:id', name: 'AssessmentDetail', component: () => import('../views/assessment/AssessmentDetail.vue') },
  { path: '/assessments/:assessmentId/queries', name: 'QueryList', component: () => import('../views/query/QueryList.vue') },
  { path: '/export-tasks/:projectId?', name: 'ExportTaskList', component: () => import('../views/export/ExportTaskList.vue') },
  { path: '/crf-templates', name: 'CrfTemplateList', component: () => import('../views/crf/CrfTemplateList.vue') },
  { path: '/crf-templates/:id', name: 'CrfTemplateDetail', component: () => import('../views/crf/CrfTemplateDetail.vue') },
  { path: '/analysis', name: 'AnalysisProject', component: () => import('../views/analysis/AnalysisProject.vue') }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
