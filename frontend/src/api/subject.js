import http from './request'

export const getSubjects = (projectId, params) => http.get(`/projects/${projectId}/subjects`, { params })
export const getSubject = (id) => http.get(`/subjects/${id}`)
export const screenSubject = (data) => http.post('/subjects/screen', data)
export const enrollSubject = (id, data) => http.post(`/subjects/${id}/enroll`, data)
export const directEnroll = (data) => http.post('/subjects/enroll', data)
export const withdrawSubject = (id, data) => http.post(`/subjects/${id}/withdraw`, data)
export const changeStatus = (id, data) => http.put(`/subjects/${id}/status`, data)
