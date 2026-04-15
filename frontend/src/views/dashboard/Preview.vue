<template>
  <div class="dashboard-preview-container" :style="{ backgroundColor: bgColor }">
    <!-- 工具栏 -->
    <div class="preview-toolbar">
      <el-button @click="handleBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <span class="title">{{ tplName }}</span>
      <el-button @click="handleFullscreen">全屏</el-button>
    </div>

    <!-- 大屏内容 -->
    <div
      class="preview-content"
      :style="{
        width: screenWidth + 'px',
        height: screenHeight + 'px',
        transform: `scale(${scale})`,
        transformOrigin: 'top left'
      }"
    >
      <div
        v-for="widget in widgets"
        :key="widget.id"
        class="widget"
        :style="{
          left: widget.x + 'px',
          top: widget.y + 'px',
          width: widget.width + 'px',
          height: widget.height + 'px',
          zIndex: widget.zIndex
        }"
      >
        <!-- 图表组件 -->
        <div v-if="widget.type === 'chart'" class="widget-chart">
          <ECharts :option="getChartOption(widget)" autoresize />
        </div>

        <!-- 文字组件 -->
        <div
          v-else-if="widget.type === 'text'"
          class="widget-text"
          :style="{
            fontSize: widget.fontSize + 'px',
            color: widget.color
          }"
        >
          {{ widget.content }}
        </div>

        <!-- 表格组件 -->
        <div v-else-if="widget.type === 'table'" class="widget-table">
          <el-table :data="widget.tableData || []" border size="small" height="100%">
            <el-table-column
              v-for="col in widget.columns"
              :key="col.prop"
              :prop="col.prop"
              :label="col.label"
            />
          </el-table>
        </div>

        <!-- 图片组件 -->
        <div v-else-if="widget.type === 'image'" class="widget-image">
          <img v-if="widget.src" :src="widget.src" />
          <el-icon v-else :size="32"><Picture /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ECharts from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart, GaugeChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  GaugeChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const route = useRoute()
const router = useRouter()

// 模板信息
const tplId = ref(route.params.id)
const tplName = ref('')
const screenWidth = ref(1920)
const screenHeight = ref(1080)
const bgColor = ref('#0a0a0a')
const scale = ref(1)
const widgets = ref([])

// 全屏元素引用
let containerRef = null

// 加载模板数据
const loadTemplate = async () => {
  if (!tplId.value) return
  try {
    const data = await dashboardTplApi.get(tplId.value)
    tplName.value = data.tplName
    screenWidth.value = data.screenWidth || 1920
    screenHeight.value = data.screenHeight || 1080
    bgColor.value = data.bgColor || '#0a0a0a'

    if (data.widgetConfig) {
      try {
        widgets.value = JSON.parse(data.widgetConfig)
      } catch (e) {
        console.error('解析组件配置失败:', e)
      }
    }
  } catch (error) {
    console.error('加载模板失败:', error)
  }
}

// 获取图表配置
const getChartOption = (widget) => {
  if (widget.type !== 'chart' || !widget.chartConfig) {
    return {
      title: { text: '图表', textStyle: { color: '#fff' } },
      xAxis: { type: 'category', data: ['暂无数据'] },
      yAxis: { type: 'value' },
      series: [{ data: [] }]
    }
  }

  const config = widget.chartConfig
  return {
    backgroundColor: 'transparent',
    title: {
      text: config.title?.text || '',
      textStyle: { color: '#fff', fontSize: 14 }
    },
    tooltip: { trigger: 'axis' },
    legend: {
      data: config.legend?.data || [],
      textStyle: { color: '#fff' }
    },
    xAxis: {
      type: config.xAxis?.type || 'category',
      data: config.xAxis?.data || [],
      axisLine: { lineStyle: { color: '#fff' } },
      axisLabel: { color: '#fff' }
    },
    yAxis: {
      type: config.yAxis?.type || 'value',
      axisLine: { lineStyle: { color: '#fff' } },
      axisLabel: { color: '#fff' },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
    },
    series: (config.series || []).map(s => ({
      ...s,
      itemStyle: { borderRadius: 4 }
    }))
  }
}

// 计算缩放比例
const calculateScale = () => {
  const container = document.querySelector('.preview-content')
  if (!container) return

  const containerWidth = window.innerWidth
  const containerHeight = window.innerHeight - 50 // 减去工具栏高度
  const scaleX = containerWidth / screenWidth.value
  const scaleY = containerHeight / screenHeight.value
  scale.value = Math.min(scaleX, scaleY, 1)
}

// 全屏
const handleFullscreen = () => {
  const elem = document.querySelector('.preview-content')
  if (elem.requestFullscreen) {
    elem.requestFullscreen()
  } else if (elem.webkitRequestFullscreen) {
    elem.webkitRequestFullscreen()
  } else if (elem.msRequestFullscreen) {
    elem.msRequestFullscreen()
  }
}

// 返回
const handleBack = () => {
  router.push(`/dashboard/designer/${tplId.value}`)
}

// 监听窗口变化
const handleResize = () => {
  calculateScale()
}

onMounted(() => {
  loadTemplate()
  window.addEventListener('resize', handleResize)
  setTimeout(calculateScale, 100)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard-preview-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.preview-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.preview-content {
  position: relative;
  flex: 1;
  overflow: hidden;
}

.widget {
  position: absolute;
}

.widget-chart {
  width: 100%;
  height: 100%;
}

.widget-chart > * {
  width: 100% !important;
  height: 100% !important;
}

.widget-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  word-break: break-word;
  padding: 10px;
}

.widget-table {
  width: 100%;
  height: 100%;
  overflow: auto;
  background: rgba(0, 0, 0, 0.3);
  padding: 8px;
}

.widget-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.widget-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}
</style>