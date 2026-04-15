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

// 分享 API
export const shareApi = {
  // 生成分享链接
  create: (params) => {
    return request.post('/share/create', null, { params })
  },

  // 公开访问分享内容
  access: (shareToken) => {
    return request.get(`/share/access/${shareToken}`)
  },

  // 获取分享信息
  getInfo: (shareToken) => {
    return request.get(`/share/info/${shareToken}`)
  },

  // 分页查询分享列表
  list: (params) => {
    return request.get('/share/list', { params })
  },

  // 删除分享
  delete: (shareToken) => {
    return request.delete(`/share/${shareToken}`)
  },

  // 更新分享状态
  updateStatus: (shareToken, status) => {
    return request.put(`/share/${shareToken}/status`, null, { params: { status } })
  }
}

export default {
  shareApi
}