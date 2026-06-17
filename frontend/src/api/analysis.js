import http from './request'

export const getAnalysisProjects = () => http.get('/analysis/projects')
export const getAnalysisProject = (id) => http.get(`/analysis/projects/${id}`)
export const createAnalysisProject = (data) => http.post('/analysis/projects', data)
export const executeAnalysis = (id, data) => http.post(`/analysis/projects/${id}/execute`, data)
