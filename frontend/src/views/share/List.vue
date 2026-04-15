<template>
  <div class="share-list-container">
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <span class="title">分享管理</span>
        </div>
      </template>

      <div class="search-form">
        <el-form :inline="true">
          <el-form-item>
            <el-button type="primary" @click="loadData">刷新</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="shareToken" label="分享Token" width="200" show-overflow-tooltip />
        <el-table-column prop="shareType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.shareType === 'report'" type="primary">报表</el-tag>
            <el-tag v-else-if="row.shareType === 'dashboard'" type="success">大屏</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetName" label="资源名称" min-width="150" />
        <el-table-column prop="accessCount" label="访问次数" width="100" />
        <el-table-column prop="accessLimit" label="访问限制" width="100">
          <template #default="{ row }">
            <span v-if="row.accessLimit === 0">不限</span>
            <span v-else>{{ row.accessCount }}/{{ row.accessLimit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="180">
          <template #default="{ row }">
            <span v-if="row.expireTime">{{ row.expireTime }}</span>
            <span v-else>永不过期</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleCopyLink(row)">复制链接</el-button>
            <el-button type="primary" link @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { shareApi } from '@/api/share'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const data = await shareApi.list(queryParams)
    tableData.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
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

// 复制链接
const handleCopyLink = (row) => {
  const link = `${window.location.origin}/#/share/${row.shareToken}`
  navigator.clipboard.writeText(link).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  ElMessageBox.confirm(`确认${action}该分享链接吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await shareApi.updateStatus(row.shareToken, newStatus)
      ElMessage.success(`${action}成功`)
      loadData()
    } catch (error) {
      console.error(`${action}失败:`, error)
    }
  }).catch(() => {})
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该分享链接吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await shareApi.delete(row.shareToken)
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
.share-list-container {
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