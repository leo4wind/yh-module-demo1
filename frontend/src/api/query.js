import http from './request'

export const getQueries = (assessmentId) => http.get(`/assessments/${assessmentId}/queries`)
export const getQuery = (id) => http.get(`/queries/${id}`)
export const raiseQuery = (data) => http.post('/queries', data)
export const respondQuery = (id, data) => http.post(`/queries/${id}/respond`, data)
export const closeQuery = (id, data) => http.post(`/queries/${id}/close`, data)
export const reopenQuery = (id, data) => http.post(`/queries/${id}/reopen`, data)
