<template>
  <div class="report-designer-container">
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <el-button @click="handleBack">
              <el-icon><Back /></el-icon>
              返回
            </el-button>
            <span class="title">{{ isEdit ? '编辑模板' : '新建模板' }}</span>
          </div>
          <div class="right">
            <el-button @click="handleSaveTemplate">保存模板</el-button>
            <el-button type="primary" @click="handlePreview">预览</el-button>
          </div>
        </div>
      </template>

      <div class="template-info">
        <el-form :inline="true" :model="templateInfo">
          <el-form-item label="模板名称">
            <el-input v-model="templateInfo.tplName" placeholder="请输入模板名称" />
          </el-form-item>
          <el-form-item label="模板编码">
            <el-input v-model="templateInfo.tplCode" placeholder="请输入模板编码" />
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="designer-card" ref="designerCardRef">
      <div id="luckysheet" class="luckysheet-container"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import LuckySheet from 'luckysheet'
import { reportTplApi } from '@/api/report'

const router = useRouter()
const route = useRoute()
const designerCardRef = ref(null)

const templateId = ref(route.params.id || null)
const isEdit = ref(!!templateId.value)

const templateInfo = reactive({
  tplName: '',
  tplCode: '',
  tplType: 'luckysheet'
})

let luckysheetInstance = null

// 初始化Luckysheet
const initLuckysheet = (data) => {
  const options = {
    container: 'luckysheet',
    lang: 'zh',
    showinfobar: false,
    showtoolbar: true,
    showtoolbarConfig: {
      undoRedo: true,
      paintFormat: true,
      currencyFormat: true,
      percentageFormat: true,
      precision: true,
      color: true,
      fontSize: true,
      fontFamily: true,
      fontBold: true,
      fontItalic: true,
      textColor: true,
      backgroundColor: true,
      alignment: true,
      textWrap: true,
      border: true,
      mergeCell: true,
      align: true,
      tableType: true
    },
    showstaticsBar: false,
    sheetBottomConfig: false,
    sheetRightConfig: false,
    sheetTabsConfig: {
      show: true,
      row: 'auto',
      col: 'auto'
    },
    allowEdit: true,
    enableAddRow: true,
    enableAddCol: true,
    enableEditCell: true,
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

// 保存模板
const handleSaveTemplate = async () => {
  if (!templateInfo.tplName) {
    ElMessage.warning('请输入模板名称')
    return
  }
  if (!templateInfo.tplCode) {
    ElMessage.warning('请输入模板编码')
    return
  }

  try {
    // 获取Luckysheet数据
    const sheetData = luckysheetInstance.getAllSheets()

    const templateData = {
      id: templateId.value,
      tplName: templateInfo.tplName,
      tplCode: templateInfo.tplCode,
      tplType: 'luckysheet',
      sheets: sheetData.map((sheet, index) => ({
        id: sheet.id,
        sheetName: sheet.name,
        sheetIndex: index,
        sheetConfig: JSON.stringify(sheet.config || {}),
        cells: sheet.celldata || []
      }))
    }

    await reportTplApi.saveFullTemplate(templateData)
    ElMessage.success('保存成功')

    if (!templateId.value) {
      // 如果是新建的，获取返回的ID并更新路由
      const list = await reportTplApi.list({ tplCode: templateInfo.tplCode })
      if (list.list && list.list.length > 0) {
        templateId.value = list.list[0].id
        isEdit.value = true
        router.replace(`/report/designer/${templateId.value}`)
      }
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

// 预览
const handlePreview = () => {
  if (!templateId.value) {
    ElMessage.warning('请先保存模板')
    return
  }
  router.push(`/report/preview/${templateId.value}`)
}

// 返回
const handleBack = () => {
  router.push('/report/list')
}

// 加载模板数据
const loadTemplate = async () => {
  if (!templateId.value) {
    // 新建模板，初始化空白Luckysheet
    await nextTick()
    initLuckysheet()
    return
  }

  try {
    const data = await reportTplApi.loadFullTemplate(templateId.value)
    templateInfo.tplName = data.tplName
    templateInfo.tplCode = data.tplCode
    templateInfo.tplType = data.tplType || 'luckysheet'

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
    await nextTick()
    initLuckysheet()
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
.report-designer-container {
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

.template-info {
  padding: 10px 0;
}

.designer-card {
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