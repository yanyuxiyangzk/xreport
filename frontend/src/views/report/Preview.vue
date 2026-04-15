<template>
  <div class="report-preview-container">
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <el-button @click="handleBack">
              <el-icon><Back /></el-icon>
              返回
            </el-button>
            <span class="title">{{ templateName }} - 预览</span>
          </div>
          <div class="right">
            <el-dropdown @command="handleExportCommand" trigger="click">
              <el-button type="primary">
                导出 <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="excel">
                    <el-icon><Document /></el-icon> 导出Excel
                  </el-dropdown-item>
                  <el-dropdown-item command="pdf">
                    <el-icon><Document /></el-icon> 导出PDF
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>
    </el-card>

    <el-card class="preview-card">
      <div id="luckysheet-preview" class="luckysheet-container"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, ArrowDown } from '@element-plus/icons-vue'
import LuckySheet from 'luckysheet'
import { reportTplApi } from '@/api/report'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'

const router = useRouter()
const route = useRoute()

const templateId = ref(route.params.id)
const templateName = ref('')
let luckysheetInstance = null

// 加载模板数据
const loadTemplate = async () => {
  if (!templateId.value) {
    ElMessage.error('模板ID不能为空')
    return
  }

  try {
    const data = await reportTplApi.loadFullTemplate(templateId.value)
    templateName.value = data.tplName

    // 转换数据为Luckysheet格式
    const sheets = data.sheets.map(sheet => ({
      id: sheet.id,
      name: sheet.sheetName,
      color: '#5a9cf8',
      index: sheet.sheetIndex,
      status: sheet.sheetIndex === 0 ? 1 : 0,
      celldata: sheet.cells || [],
      config: sheet.sheetConfig ? JSON.parse(sheet.sheetConfig) : {
        merge: [],
        rowlen: {},
        columnlen: {}
      }
    }))

    await nextTick()
    initLuckysheet(sheets.length > 0 ? sheets : null)
  } catch (error) {
    console.error('加载模板失败:', error)
    ElMessage.error('加载模板失败')
  }
}

// 初始化Luckysheet（只读模式）
const initLuckysheet = (data) => {
  const options = {
    container: 'luckysheet-preview',
    lang: 'zh',
    showinfobar: false,
    showtoolbar: false,
    showstaticsBar: false,
    sheetBottomConfig: false,
    sheetRightConfig: false,
    sheetTabsConfig: {
      show: true,
      row: 'auto',
      col: 'auto'
    },
    allowEdit: false,  // 只读模式
    enableAddRow: false,
    enableAddCol: false,
    enableEditCell: false,
    data: data || [{
      name: 'Sheet1',
      color: '#5a9cf8',
      index: 0,
      status: 1,
      celldata: [],
      config: {
        merge: [],
        rowlen: {},
        columnlen: {}
      }
    }]
  }

  luckysheetInstance = LuckySheet.createInstance(options)
}

// 返回
const handleBack = () => {
  router.push('/report/list')
}

// 导出命令处理
const handleExportCommand = (command) => {
  if (command === 'excel') {
    handleExportExcel()
  } else if (command === 'pdf') {
    handleExportPDF()
  }
}

// 导出Excel - 通过服务端API
const handleExportExcel = async () => {
  if (!templateId.value) {
    ElMessage.error('模板ID不能为空')
    return
  }

  try {
    ElMessage.info('正在导出Excel...')

    // 获取完整的模板数据并发送到服务端
    const templateData = await reportTplApi.loadFullTemplate(templateId.value)
    const blob = await reportTplApi.exportExcelPost(templateData)

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${templateName.value || 'report'}.xlsx`)
    document.body.appendChild(link)
    link.click()
    link.parentNode.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('Excel导出成功')
  } catch (error) {
    console.error('导出Excel失败:', error)
    ElMessage.error('导出Excel失败')
  }
}

// 导出PDF - 使用html2canvas + jsPDF
const handleExportPDF = async () => {
  const previewContainer = document.getElementById('luckysheet-preview')
  if (!previewContainer) {
    ElMessage.error('预览容器不存在')
    return
  }

  try {
    ElMessage.info('正在导出PDF...')

    // 使用html2canvas截图
    const canvas = await html2canvas(previewContainer, {
      scale: 2,
      useCORS: true,
      logging: false,
      backgroundColor: '#ffffff'
    })

    // 创建PDF
    const imgData = canvas.toDataURL('image/png')
    const pdf = new jsPDF({
      orientation: canvas.width > canvas.height ? 'landscape' : 'portrait',
      unit: 'px',
      format: [canvas.width, canvas.height]
    })

    pdf.addImage(imgData, 'PNG', 0, 0, canvas.width, canvas.height)
    pdf.save(`${templateName.value || 'report'}.pdf`)

    ElMessage.success('PDF导出成功')
  } catch (error) {
    console.error('导出PDF失败:', error)
    ElMessage.error('导出PDF失败')
  }
}

onMounted(() => {
  loadTemplate()
})

onBeforeUnmount(() => {
  if (luckysheetInstance) {
    luckysheetInstance.destroy()
    luckysheetInstance = null
  }
})
</script>

<style scoped>
.report-preview-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f7fa;
}

.header-card {
  flex-shrink: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.preview-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.luckysheet-container {
  width: 100%;
  height: 100%;
  min-height: 500px;
}
</style>