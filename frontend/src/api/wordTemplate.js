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
    // 对于导出接口，直接返回response对象
    if (response.config.responseType === 'blob') {
      return response
    }
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

// Word模板 API
export const wordTemplateApi = {
  /**
   * 上传Word模板
   * @param {File} file - 模板文件
   * @param {String} tplName - 模板名称
   * @param {String} description - 模板描述
   * @returns {Object} - 模板信息
   */
  upload: (file, tplName, description) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('tplName', tplName)
    formData.append('description', description || '')
    return request.post('/word-templates/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  /**
   * 获取模板列表
   * @returns {Array} - 模板列表
   */
  list: () => {
    return request.get('/word-templates')
  },

  /**
   * 获取模板详情
   * @param {Long} id - 模板ID
   * @returns {Object} - 模板详情
   */
  getById: (id) => {
    return request.get(`/word-templates/${id}`)
  },

  /**
   * 删除模板
   * @param {Long} id - 模板ID
   */
  delete: (id) => {
    return request.delete(`/word-templates/${id}`)
  },

  /**
   * 下载模板文件
   * @param {Long} id - 模板ID
   * @returns {Blob} - Word文件blob
   */
  download: (id) => {
    return request.get(`/word-templates/${id}/download`, {
      responseType: 'blob'
    }).then(res => res.data)
  },

  /**
   * 使用poi-tl模板渲染导出Word
   * @param {Long} tplId - 模板ID
   * @param {Object} data - 渲染数据
   * @returns {Blob} - Word文件blob
   */
  render: (tplId, data) => {
    return request.post(`/word-templates/${tplId}/render`, data, {
      responseType: 'blob'
    }).then(res => res.data)
  }
}

export default wordTemplateApi