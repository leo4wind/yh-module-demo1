import http from './request'

export const getCrfTemplates = (params) => http.get('/crf-templates', { params })
export const getCrfTemplate = (id) => http.get(`/crf-templates/${id}`)
export const createCrfTemplate = (data) => http.post('/crf-templates', data)
export const updateCrfTemplate = (id, data) => http.put(`/crf-templates/${id}`, data)
export const publishCrfTemplate = (id) => http.post(`/crf-templates/${id}/publish`)
export const copyCrfTemplate = (id, data) => http.post(`/crf-templates/${id}/copy`, data)
