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

/**
 * 转换模板数据为导出请求格式
 * @param {Object} templateData - loadFullTemplate返回的数据
 * @returns {Object} - ExportRequest格式
 */
const convertToExportRequest = (templateData) => {
  const sheets = (templateData.sheets || []).map(sheet => ({
    // 后端 ExportController 期望字段名: id, name, celldata, config
    id: sheet.id,
    name: sheet.sheetName || sheet.name,
    index: sheet.sheetIndex || sheet.index,
    celldata: sheet.celldata || sheet.cells || [],
    config: sheet.config || {}
  }))

  return {
    tplId: templateData.id,
    tplName: templateData.tplName,
    sheets: sheets,
    parameters: templateData.parameters || {}
  }
}

// 导出 API
export const exportApi = {
  /**
   * 导出Excel
   * @param {Object} templateData - loadFullTemplate返回的模板数据
   * @returns {Blob} - Excel文件blob
   */
  exportExcel: (templateData) => {
    const exportRequest = convertToExportRequest(templateData)
    return request.post('/export/excel', exportRequest, {
      responseType: 'blob'
    }).then(res => res.data)
  },

  /**
   * 导出PDF
   * @param {Object} templateData - loadFullTemplate返回的模板数据
   * @returns {Blob} - PDF文件blob
   */
  exportPdf: (templateData) => {
    const exportRequest = convertToExportRequest(templateData)
    return request.post('/export/pdf', exportRequest, {
      responseType: 'blob'
    }).then(res => res.data)
  },

  /**
   * 导出Word
   * @param {Object} templateData - loadFullTemplate返回的模板数据
   * @returns {Blob} - Word文件blob
   */
  exportWord: (templateData) => {
    const exportRequest = convertToExportRequest(templateData)
    return request.post('/export/word', exportRequest, {
      responseType: 'blob'
    }).then(res => res.data)
  },

  /**
   * 使用poi-tl模板导出Word
   * @param {Long} tplId - Word模板ID
   * @param {Object} data - 渲染数据
   * @returns {Blob} - Word文件blob
   */
  exportWordWithTemplate: (tplId, data) => {
    return request.post('/export/word/template', { tplId, data }, {
      responseType: 'blob'
    }).then(res => res.data)
  }
}

export default exportApi