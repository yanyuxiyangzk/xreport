<template>
  <div class="share-view-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <el-result icon="error" title="访问失败" :sub-title="errorMessage">
        <template #extra>
          <el-button type="primary" @click="goHome">返回首页</el-button>
        </template>
      </el-result>
    </div>

    <!-- 报表分享 -->
    <div v-else-if="shareType === 'report'" class="report-view">
      <ReportPreview v-if="content" :report-id="targetId" />
    </div>

    <!-- 大屏分享 -->
    <div v-else-if="shareType === 'dashboard'" class="dashboard-view">
      <DashboardPreview v-if="content" :dashboard-id="targetId" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { shareApi } from '@/api/share'
import ReportPreview from '@/views/report/Preview.vue'
import DashboardPreview from '@/views/dashboard/Preview.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const error = ref(false)
const errorMessage = ref('')
const shareType = ref('')
const targetId = ref(null)
const content = ref(null)

const loadShare = async () => {
  const shareToken = route.params.shareToken
  if (!shareToken) {
    error.value = true
    errorMessage.value = '分享链接无效'
    loading.value = false
    return
  }

  try {
    const data = await shareApi.access(shareToken)
    shareType.value = data.shareType
    targetId.value = data.targetId
    content.value = data.content
    loading.value = false
  } catch (err) {
    error.value = true
    errorMessage.value = err.message || '无法访问该分享内容'
    loading.value = false
  }
}

const goHome = () => {
  router.push('/')
}

onMounted(() => {
  loadShare()
})
</script>

<style scoped>
.share-view-container {
  width: 100%;
  height: 100%;
  overflow: auto;
  background: #1a1a2e;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  color: #fff;
  font-size: 18px;
}

.loading-container .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.report-view,
.dashboard-view {
  width: 100%;
  height: 100%;
}
</style>