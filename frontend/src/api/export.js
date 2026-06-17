import http from './request'

export const getExportTasks = (projectId) => projectId
  ? http.get(`/projects/${projectId}/export-tasks`)
  : http.get('/export-tasks')
export const getExportTask = (id) => http.get(`/export-tasks/${id}`)
export const createExportTask = (data) => http.post('/export-tasks', data)
export const submitExportTask = (id) => http.post(`/export-tasks/${id}/submit`)
export const approveExportTask = (id, data) => http.post(`/export-tasks/${id}/approve`, data)
export const rejectExportTask = (id, data) => http.post(`/export-tasks/${id}/reject`, data)
export const executeExport = (id) => http.post(`/export-tasks/${id}/execute`)
