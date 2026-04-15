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

// 报表模板 API
export const reportTplApi = {
  // 分页查询
  list: (params) => {
    return request.get('/report/tpl', { params })
  },

  // 获取详情
  getById: (id) => {
    return request.get(`/report/tpl/${id}`)
  },

  // 根据编码获取
  getByCode: (tplCode) => {
    return request.get(`/report/tpl/code/${tplCode}`)
  },

  // 新增
  add: (data) => {
    return request.post('/report/tpl', data)
  },

  // 更新
  update: (id, data) => {
    return request.put(`/report/tpl/${id}`, data)
  },

  // 删除
  delete: (id) => {
    return request.delete(`/report/tpl/${id}`)
  },

  // 保存完整模板
  saveFullTemplate: (data) => {
    return request.post('/report/tpl/full', data)
  },

  // 加载完整模板
  loadFullTemplate: (tplId) => {
    return request.get(`/report/tpl/full/${tplId}`)
  },

  // 导出Excel（通过URL下载）
  exportExcelUrl: (tplId) => {
    const token = localStorage.getItem('token')
    return `${import.meta.env.VITE_API_BASE_URL || '/api'}/report/tpl/export/excel/${tplId}?token=${token}`
  },

  // 导出Excel（POST方式）
  exportExcelPost: (templateData) => {
    const token = localStorage.getItem('token')
    return request.post('/report/tpl/export/excel', templateData, {
      headers: {
        Authorization: `Bearer ${token}`
      },
      responseType: 'blob'
    })
  }
}

// 报表 Sheet API
export const reportSheetApi = {
  // 分页查询
  list: (params) => {
    return request.get('/report/sheet', { params })
  },

  // 获取详情
  getById: (id) => {
    return request.get(`/report/sheet/${id}`)
  },

  // 根据模板ID获取
  getByTplId: (tplId) => {
    return request.get(`/report/sheet/tpl/${tplId}`)
  },

  // 新增
  add: (data) => {
    return request.post('/report/sheet', data)
  },

  // 更新
  update: (id, data) => {
    return request.put(`/report/sheet/${id}`, data)
  },

  // 删除
  delete: (id) => {
    return request.delete(`/report/sheet/${id}`)
  }
}

// 单元格 API
export const cellApi = {
  // 分页查询
  list: (params) => {
    return request.get('/report/cell', { params })
  },

  // 获取详情
  getById: (id) => {
    return request.get(`/report/cell/${id}`)
  },

  // 根据Sheet获取
  getBySheetId: (sheetId) => {
    return request.get(`/report/cell/sheet/${sheetId}`)
  },

  // 新增
  add: (data) => {
    return request.post('/report/cell', data)
  },

  // 更新
  update: (id, data) => {
    return request.put(`/report/cell/${id}`, data)
  },

  // 删除
  delete: (id) => {
    return request.delete(`/report/cell/${id}`)
  },

  // 批量保存
  batchSave: (data) => {
    return request.post('/report/cell/batch', data)
  }
}

export default {
  reportTplApi,
  reportSheetApi,
  cellApi
}