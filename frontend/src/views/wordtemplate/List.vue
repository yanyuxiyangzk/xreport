<template>
  <div class="word-template-container">
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <span class="title">Word模板管理</span>
          <el-button type="primary" @click="handleUploadDialog">
            <el-icon><Upload /></el-icon>
            上传模板
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索模板名称"
          style="width: 300px"
          clearable
          @clear="loadTemplates"
          @keyup.enter="loadTemplates"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="loadTemplates">搜索</el-button>
        <el-button @click="searchKeyword = ''; loadTemplates()">重置</el-button>
      </div>

      <!-- 模板列表 -->
      <el-table :data="filteredTemplates" style="width: 100%; margin-top: 16px" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="tplName" label="模板名称" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleRender(row)">
              <el-icon><Document /></el-icon>
              渲染
            </el-button>
            <el-button type="primary" link @click="handleDownload(row)">
              <el-icon><Download /></el-icon>
              下载
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传Word模板" width="500px">
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="模板名称" required>
          <el-input v-model="uploadForm.tplName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板描述">
          <el-input v-model="uploadForm.description" type="textarea" :rows="3" placeholder="请输入模板描述" />
        </el-form-item>
        <el-form-item label="模板文件" required>
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :accept="'.docx'"
            :file-list="fileList"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传.docx文件，且不超过10MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploading">上传</el-button>
      </template>
    </el-dialog>

    <!-- 渲染对话框 -->
    <el-dialog v-model="renderDialogVisible" title="渲染Word模板" width="600px">
      <el-form :model="renderForm" label-width="100px">
        <el-form-item label="模板名称">
          <span>{{ currentTemplate?.tplName }}</span>
        </el-form-item>
        <el-form-item label="渲染数据">
          <el-input
            v-model="renderForm.dataJson"
            type="textarea"
            :rows="10"
            placeholder='请输入JSON格式的渲染数据，如: {"name": "张三", "age": 25}'
          />
        </el-form-item>
        <el-form-item>
          <el-link type="primary" :underline="false" @click="showDataHelp = !showDataHelp">
            {{ showDataHelp ? '收起' : '查看' }}数据格式说明
          </el-link>
        </el-form-item>
        <el-form-item v-if="showDataHelp">
          <div class="data-help">
            <p><strong>poi-tl标签格式：</strong></p>
            <ul>
              <li><code v-pre>{{name}}</code> - 文本标签，渲染为文本</li>
              <li><code v-pre>{{#list}}...{{/list}}</code> - 循环标签，用于列表渲染</li>
              <li><code v-pre>{{@image}}</code> - 图片标签</li>
              <li><code v-pre>{{$table}}</code> - 表格标签</li>
            </ul>
            <p><strong>示例数据：</strong></p>
            <pre v-pre>{"name": "张三", "department": "技术部", "items": [{"name": "项目A", "score": 90}]}</pre>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRenderConfirm" :loading="rendering">渲染并下载</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Download, Delete, Document, Search } from '@element-plus/icons-vue'
import { wordTemplateApi } from '@/api/wordTemplate'

const loading = ref(false)
const templates = ref([])
const searchKeyword = ref('')

// 上传相关
const uploadDialogVisible = ref(false)
const uploadForm = ref({ tplName: '', description: '' })
const uploading = ref(false)
const fileList = ref([])
const uploadRef = ref(null)

// 渲染相关
const renderDialogVisible = ref(false)
const currentTemplate = ref(null)
const renderForm = ref({ dataJson: '{}' })
const rendering = ref(false)
const showDataHelp = ref(false)

const filteredTemplates = computed(() => {
  if (!searchKeyword.value) return templates.value
  return templates.value.filter(t =>
    t.tplName.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const loadTemplates = async () => {
  loading.value = true
  try {
    templates.value = await wordTemplateApi.list()
  } catch (error) {
    console.error('加载模板列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleUploadDialog = () => {
  uploadForm.value = { tplName: '', description: '' }
  fileList.value = []
  uploadDialogVisible.value = true
}

const handleFileChange = (file) => {
  fileList.value = [file]
}

const handleFileRemove = () => {
  fileList.value = []
}

const handleUpload = async () => {
  if (!uploadForm.value.tplName) {
    ElMessage.error('请输入模板名称')
    return
  }
  if (fileList.value.length === 0) {
    ElMessage.error('请选择模板文件')
    return
  }

  uploading.value = true
  try {
    const file = fileList.value[0].raw
    await wordTemplateApi.upload(file, uploadForm.value.tplName, uploadForm.value.description)
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    loadTemplates()
  } catch (error) {
    console.error('上传失败:', error)
  } finally {
    uploading.value = false
  }
}

const handleRender = (row) => {
  currentTemplate.value = row
  renderForm.value.dataJson = JSON.stringify({ name: '示例名称', value: '示例值' }, null, 2)
  renderDialogVisible.value = true
}

const handleRenderConfirm = async () => {
  let data
  try {
    data = JSON.parse(renderForm.value.dataJson)
  } catch (e) {
    ElMessage.error('JSON格式不正确')
    return
  }

  rendering.value = true
  try {
    const blob = await wordTemplateApi.render(currentTemplate.value.id, data)
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${currentTemplate.value.tplName}_渲染结果.docx`)
    document.body.appendChild(link)
    link.click()
    link.parentNode.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('渲染成功')
    renderDialogVisible.value = false
  } catch (error) {
    console.error('渲染失败:', error)
  } finally {
    rendering.value = false
  }
}

const handleDownload = async (row) => {
  try {
    const blob = await wordTemplateApi.download(row.id)
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${row.tplName}.docx`)
    document.body.appendChild(link)
    link.click()
    link.parentNode.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载失败:', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除模板"${row.tplName}"吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await wordTemplateApi.delete(row.id)
    ElMessage.success('删除成功')
    loadTemplates()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadTemplates()
})
</script>

<style scoped>
.word-template-container {
  padding: 16px;
}

.header-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.search-bar {
  display: flex;
  gap: 8px;
  align-items: center;
}

.data-help {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  font-size: 13px;
}

.data-help ul {
  margin: 8px 0;
  padding-left: 20px;
}

.data-help li {
  margin: 4px 0;
}

.data-help pre {
  background: #fff;
  padding: 8px;
  border-radius: 4px;
  overflow-x: auto;
}
</style>