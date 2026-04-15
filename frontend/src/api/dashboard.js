import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

// 大屏模板 API
export const dashboardTplApi = {
  // 分页查询
  list: (params) => {
    return request.get('/dashboard/tpl', { params })
  },

  // 获取详情
  get: (id) => {
    return request.get(`/dashboard/tpl/${id}`)
  },

  // 根据编码获取
  getByCode: (tplCode) => {
    return request.get(`/dashboard/tpl/code/${tplCode}`)
  },

  // 新增
  add: (data) => {
    return request.post('/dashboard/tpl', data)
  },

  // 更新
  update: (id, data) => {
    return request.put(`/dashboard/tpl/${id}`, data)
  },

  // 删除
  delete: (id) => {
    return request.delete(`/dashboard/tpl/${id}`)
  }
}

export default {
  dashboardTplApi
}