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

// 数据源 API
export const datasourceApi = {
  // 分页查询
  list: (params) => {
    return request.get('/datasources', { params })
  },

  // 获取详情
  getById: (id) => {
    return request.get(`/datasources/${id}`)
  },

  // 新增
  add: (data) => {
    return request.post('/datasources', data)
  },

  // 更新
  update: (id, data) => {
    return request.put(`/datasources/${id}`, data)
  },

  // 删除
  delete: (id) => {
    return request.delete(`/datasources/${id}`)
  },

  // 获取启用的数据源列表
  listEnabled: () => {
    return request.get('/datasources/enabled')
  },

  // 测试JDBC连接
  testJdbc: (params) => {
    return request.post('/datasources/test/jdbc', params)
  },

  // 测试API连接
  testApi: (params) => {
    return request.post('/datasources/test/api', params)
  }
}

export default {
  datasourceApi
}