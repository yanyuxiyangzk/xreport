<template>
  <div class="datasource-list-container">
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <span class="title">数据源管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建数据源
          </el-button>
        </div>
      </template>

      <div class="search-form">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="数据源名称">
            <el-input v-model="queryParams.datasourceName" placeholder="请输入数据源名称" clearable />
          </el-form-item>
          <el-form-item label="数据源类型">
            <el-select v-model="queryParams.datasourceType" placeholder="请选择" clearable>
              <el-option label="JDBC" value="jdbc" />
              <el-option label="API" value="api" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="请选择" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="停用" :value="2" />
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
        <el-table-column prop="datasourceName" label="数据源名称" min-width="150" />
        <el-table-column prop="datasourceType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.datasourceType === 'jdbc'" type="success">JDBC</el-tag>
            <el-tag v-else type="warning">API</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jdbcUrl" label="连接信息" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.datasourceType === 'jdbc' ? row.jdbcUrl : row.apiUrl }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleTest(row)">测试</el-button>
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
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-form-item label="数据源名称" prop="datasourceName">
          <el-input v-model="formData.datasourceName" placeholder="请输入数据源名称" />
        </el-form-item>
        <el-form-item label="数据源类型" prop="datasourceType">
          <el-select v-model="formData.datasourceType" placeholder="请选择数据源类型" @change="handleTypeChange">
            <el-option label="JDBC" value="jdbc" />
            <el-option label="API" value="api" />
          </el-select>
        </el-form-item>

        <!-- JDBC配置 -->
        <template v-if="formData.datasourceType === 'jdbc'">
          <el-form-item label="JDBC URL" prop="jdbcUrl">
            <el-input v-model="formData.jdbcUrl" placeholder="jdbc:mysql://localhost:3306/dbname" />
          </el-form-item>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="formData.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="formData.password" placeholder="请输入密码" show-password />
          </el-form-item>
        </template>

        <!-- API配置 -->
        <template v-else>
          <el-form-item label="API URL" prop="apiUrl">
            <el-input v-model="formData.apiUrl" placeholder="https://api.example.com/data" />
          </el-form-item>
          <el-form-item label="请求方法" prop="apiMethod">
            <el-select v-model="formData.apiMethod" placeholder="请选择">
              <el-option label="GET" value="GET" />
              <el-option label="POST" value="POST" />
              <el-option label="PUT" value="PUT" />
              <el-option label="DELETE" value="DELETE" />
            </el-select>
          </el-form-item>
          <el-form-item label="请求头(JSON)">
            <el-input v-model="formData.apiHeaders" placeholder='{"Content-Type": "application/json"}' type="textarea" :rows="2" />
          </el-form-item>
          <el-form-item label="请求体">
            <el-input v-model="formData.apiBody" placeholder="请求体内容" type="textarea" :rows="3" />
          </el-form-item>
        </template>

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

    <!-- 测试连接对话框 -->
    <el-dialog
      v-model="testDialogVisible"
      title="测试连接"
      width="500px"
    >
      <el-form ref="testFormRef" :model="testFormData" label-width="100px">
        <template v-if="testFormData.datasourceType === 'jdbc'">
          <el-form-item label="JDBC URL">
            <el-input v-model="testFormData.jdbcUrl" disabled />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="testFormData.username" disabled />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="testFormData.password" disabled />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="API URL">
            <el-input v-model="testFormData.apiUrl" disabled />
          </el-form-item>
          <el-form-item label="请求方法">
            <el-input v-model="testFormData.apiMethod" disabled />
          </el-form-item>
        </template>
        <el-form-item label="测试结果">
          <el-tag v-if="testLoading" type="info">测试中...</el-tag>
          <el-tag v-else-if="testResult === 'success'" type="success">连接成功</el-tag>
          <el-tag v-else-if="testResult === 'fail'" type="danger">连接失败</el-tag>
          <el-tag v-else type="info">未测试</el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="testDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleTestConnection" :loading="testLoading">开始测试</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { datasourceApi } from '@/api/datasource'

// 查询参数
const queryParams = reactive({
  datasourceName: '',
  datasourceType: '',
  status: null,
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
  datasourceName: '',
  datasourceType: 'jdbc',
  jdbcUrl: '',
  username: '',
  password: '',
  apiUrl: '',
  apiMethod: 'GET',
  apiHeaders: '',
  apiBody: '',
  status: 1
})

const formRules = {
  datasourceName: [{ required: true, message: '请输入数据源名称', trigger: 'blur' }],
  datasourceType: [{ required: true, message: '请选择数据源类型', trigger: 'change' }],
  jdbcUrl: [{ required: true, message: '请输入JDBC URL', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  apiUrl: [{ required: true, message: '请输入API URL', trigger: 'blur' }],
  apiMethod: [{ required: true, message: '请选择请求方法', trigger: 'change' }]
}

// 测试对话框
const testDialogVisible = ref(false)
const testFormRef = ref(null)
const testFormData = reactive({
  datasourceType: 'jdbc',
  jdbcUrl: '',
  username: '',
  password: '',
  apiUrl: '',
  apiMethod: 'GET'
})
const testLoading = ref(false)
const testResult = ref('')

// 类型切换
const handleTypeChange = () => {
  // 重置相关字段
  formData.jdbcUrl = ''
  formData.username = ''
  formData.password = ''
  formData.apiUrl = ''
  formData.apiMethod = 'GET'
  formData.apiHeaders = ''
  formData.apiBody = ''
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const data = await datasourceApi.list(queryParams)
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
  queryParams.datasourceName = ''
  queryParams.datasourceType = ''
  queryParams.status = null
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
  dialogTitle.value = '新建数据源'
  Object.assign(formData, {
    id: null,
    datasourceName: '',
    datasourceType: 'jdbc',
    jdbcUrl: '',
    username: '',
    password: '',
    apiUrl: '',
    apiMethod: 'GET',
    apiHeaders: '',
    apiBody: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑数据源'
  Object.assign(formData, {
    id: row.id,
    datasourceName: row.datasourceName,
    datasourceType: row.datasourceType,
    jdbcUrl: row.jdbcUrl || '',
    username: row.username || '',
    password: row.password || '',
    apiUrl: row.apiUrl || '',
    apiMethod: row.apiMethod || 'GET',
    apiHeaders: row.apiHeaders || '',
    apiBody: row.apiBody || '',
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
          await datasourceApi.update(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await datasourceApi.add(formData)
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

// 测试
const handleTest = (row) => {
  testResult.value = ''
  Object.assign(testFormData, {
    datasourceType: row.datasourceType,
    jdbcUrl: row.jdbcUrl || '',
    username: row.username || '',
    password: row.password || '',
    apiUrl: row.apiUrl || '',
    apiMethod: row.apiMethod || 'GET'
  })
  testDialogVisible.value = true
}

// 执行测试
const handleTestConnection = async () => {
  testLoading.value = true
  testResult.value = ''
  try {
    if (testFormData.datasourceType === 'jdbc') {
      await datasourceApi.testJdbc({
        jdbcUrl: testFormData.jdbcUrl,
        username: testFormData.username,
        password: testFormData.password
      })
      testResult.value = 'success'
      ElMessage.success('JDBC连接成功')
    } else {
      await datasourceApi.testApi({
        apiUrl: testFormData.apiUrl,
        method: testFormData.apiMethod
      })
      testResult.value = 'success'
      ElMessage.success('API连接成功')
    }
  } catch (error) {
    testResult.value = 'fail'
    console.error('测试失败:', error)
  } finally {
    testLoading.value = false
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该数据源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await datasourceApi.delete(row.id)
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
.datasource-list-container {
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

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>