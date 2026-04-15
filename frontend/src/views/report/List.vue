<template>
  <div class="report-list-container">
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <span class="title">报表模板管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建模板
          </el-button>
        </div>
      </template>

      <div class="search-form">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="模板名称">
            <el-input v-model="queryParams.tplName" placeholder="请输入模板名称" clearable />
          </el-form-item>
          <el-form-item label="模板类型">
            <el-select v-model="queryParams.tplType" placeholder="请选择" clearable>
              <el-option label="Luckysheet" value="luckysheet" />
              <el-option label="Word" value="word" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="tplName" label="模板名称" min-width="150" />
        <el-table-column prop="tplCode" label="模板编码" width="150" />
        <el-table-column prop="tplType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.tplType === 'luckysheet'" type="success">Luckysheet</el-tag>
            <el-tag v-else type="warning">Word</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sheetCount" label="Sheet数" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleDesign(row)">设计</el-button>
            <el-button type="primary" link @click="handlePreview(row)">预览</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="模板名称" prop="tplName">
          <el-input v-model="formData.tplName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板编码" prop="tplCode">
          <el-input v-model="formData.tplCode" placeholder="请输入模板编码" />
        </el-form-item>
        <el-form-item label="模板类型" prop="tplType">
          <el-select v-model="formData.tplType" placeholder="请选择模板类型">
            <el-option label="Luckysheet" value="luckysheet" />
            <el-option label="Word" value="word" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="2">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reportTplApi } from '@/api/report'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  tplName: '',
  tplType: '',
  pageNum: 1,
  pageSize: 10
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const formData = reactive({
  id: null,
  tplName: '',
  tplCode: '',
  tplType: 'luckysheet',
  status: 1
})

const formRules = {
  tplName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  tplCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
  tplType: [{ required: true, message: '请选择模板类型', trigger: 'change' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const data = await reportTplApi.list(queryParams)
    tableData.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  queryParams.tplName = ''
  queryParams.tplType = ''
  queryParams.pageNum = 1
  loadData()
}

// 分页
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  loadData()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  loadData()
}

// 新建
const handleCreate = () => {
  dialogTitle.value = '新建模板'
  Object.assign(formData, {
    id: null,
    tplName: '',
    tplCode: '',
    tplType: 'luckysheet',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑模板'
  Object.assign(formData, {
    id: row.id,
    tplName: row.tplName,
    tplCode: row.tplCode,
    tplType: row.tplType,
    status: row.status
  })
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await reportTplApi.update(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await reportTplApi.add(formData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('提交失败:', error)
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 设计
const handleDesign = (row) => {
  router.push(`/report/designer/${row.id}`)
}

// 预览
const handlePreview = (row) => {
  router.push(`/report/preview/${row.id}`)
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该模板吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await reportTplApi.delete(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.report-list-container {
  padding: 20px;
  height: 100%;
  overflow: auto;
  background: #f5f7fa;
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

.search-form {
  padding: 10px 0;
}

.table-card {
  /* height: calc(100% - 180px); */
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>