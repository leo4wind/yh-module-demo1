import http from './request'

export const getSubjectStages = (subjectId) => http.get(`/subjects/${subjectId}/stages`)
export const getStage = (id) => http.get(`/stages/${id}`)
export const getAssessment = (id) => http.get(`/assessments/${id}`)
export const saveFieldValue = (id, data) => http.post(`/assessments/${id}/field-values`, data)
export const auditAssessment = (id, data) => http.post(`/assessments/${id}/audit`, data)
