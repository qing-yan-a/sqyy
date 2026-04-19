import axios, { type AxiosRequestConfig } from 'axios'
import type { ApiResponse } from '../types'

const http = axios.create({
  baseURL: '/api',
  timeout: 120000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('sqyy-token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(undefined, (error) => {
  // 处理401未授权错误
  if (error.response?.status === 401) {
    localStorage.removeItem('sqyy-token')
    // 跳转到登录页
    window.location.href = '/login'
    return Promise.reject(new Error('登录已过期，请重新登录'))
  }
  
  const message = error.response?.data?.message ?? error.message ?? '请求失败'
  return Promise.reject(new Error(message))
})

function unwrap<T>(payload: ApiResponse<T>): T {
  if (payload.code !== 200) {
    throw new Error(payload.message)
  }
  return payload.data
}

export function get<T>(url: string, config?: AxiosRequestConfig) {
  return http.get<ApiResponse<T>>(url, config).then((response) => unwrap(response.data))
}

export function post<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
  return http.post<ApiResponse<T>>(url, data, config).then((response) => unwrap(response.data))
}

export function put<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
  return http.put<ApiResponse<T>>(url, data, config).then((response) => unwrap(response.data))
}

export function remove<T>(url: string, config?: AxiosRequestConfig) {
  return http.delete<ApiResponse<T>>(url, config).then((response) => unwrap(response.data))
}
