import http from './request'

export const getProjects = (params) => http.get('/projects', { params })
export const getProject = (id) => http.get(`/projects/${id}`)
export const createProject = (data) => http.post('/projects', data)
export const activateProject = (id) => http.post(`/projects/${id}/activate`)
export const closeProject = (id) => http.post(`/projects/${id}/close`)
export const addStage = (id, data) => http.post(`/projects/${id}/stages`, data)
export const addVisitPlan = (id, data) => http.post(`/projects/${id}/visit-plans`, data)
export const bindCrf = (id, data) => http.post(`/projects/${id}/crf-bindings`, data)
export const assignPersonnel = (id, data) => http.post(`/projects/${id}/personnel`, data)
export const getPersonnelOptions = (id) => http.get(`/projects/${id}/personnel-options`)
