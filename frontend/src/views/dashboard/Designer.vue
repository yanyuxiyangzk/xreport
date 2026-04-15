<template>
  <div class="dashboard-designer-container">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-divider direction="vertical" />
        <span class="tpl-name">{{ tplName }}</span>
      </div>
      <div class="toolbar-center">
        <el-button-group>
          <el-button @click="handleAddWidget('chart')">添加图表</el-button>
          <el-button @click="handleAddWidget('text')">添加文字</el-button>
          <el-button @click="handleAddWidget('table')">添加表格</el-button>
          <el-button @click="handleAddWidget('image')">添加图片</el-button>
        </el-button-group>
      </div>
      <div class="toolbar-right">
        <el-button @click="handlePreview">预览</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </div>
    </div>

    <!-- 设计区域 -->
    <div class="designer-main">
      <!-- 左侧组件面板 -->
      <div class="widget-panel">
        <div class="panel-title">组件库</div>
        <div class="widget-list">
          <div
            v-for="widget in widgetTypes"
            :key="widget.type"
            class="widget-item"
            draggable="true"
            @dragstart="onWidgetDragStart($event, widget)"
          >
            <el-icon :size="24"><component :is="widget.icon" /></el-icon>
            <span>{{ widget.name }}</span>
          </div>
        </div>

        <!-- 选中组件属性 -->
        <div v-if="selectedWidget" class="property-panel">
          <div class="panel-title">属性配置</div>
          <el-form label-width="80px" size="small">
            <el-form-item label="X位置">
              <el-input-number v-model="selectedWidget.x" :min="0" />
            </el-form-item>
            <el-form-item label="Y位置">
              <el-input-number v-model="selectedWidget.y" :min="0" />
            </el-form-item>
            <el-form-item label="宽度">
              <el-input-number v-model="selectedWidget.width" :min="50" />
            </el-form-item>
            <el-form-item label="高度">
              <el-input-number v-model="selectedWidget.height" :min="50" />
            </el-form-item>
            <el-form-item label="层级">
              <el-input-number v-model="selectedWidget.zIndex" :min="0" />
            </el-form-item>
            <el-divider />
            <el-form-item label="图表类型" v-if="selectedWidget.type === 'chart'">
              <el-select v-model="selectedWidget.chartType">
                <el-option label="折线图" value="line" />
                <el-option label="柱状图" value="bar" />
                <el-option label="饼图" value="pie" />
                <el-option label="仪表盘" value="gauge" />
              </el-select>
            </el-form-item>
            <el-form-item label="数据源" v-if="selectedWidget.type === 'chart'">
              <el-select v-model="selectedWidget.datasourceId" placeholder="请选择数据源">
                <el-option label="静态数据" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="文字内容" v-if="selectedWidget.type === 'text'">
              <el-input v-model="selectedWidget.content" type="textarea" />
            </el-form-item>
            <el-form-item label="字体大小" v-if="selectedWidget.type === 'text'">
              <el-input-number v-model="selectedWidget.fontSize" :min="12" :max="72" />
            </el-form-item>
            <el-form-item label="文字颜色" v-if="selectedWidget.type === 'text'">
              <el-color-picker v-model="selectedWidget.color" />
            </el-form-item>
          </el-form>
          <el-button type="danger" plain @click="handleDeleteWidget">删除组件</el-button>
        </div>
      </div>

      <!-- 中间画布 -->
      <div class="canvas-container" ref="canvasContainerRef">
        <div
          class="canvas"
          :style="{
            width: screenWidth + 'px',
            height: screenHeight + 'px',
            backgroundColor: bgColor,
            transform: `scale(${scale})`,
            transformOrigin: 'top left'
          }"
          @drop="onWidgetDrop"
          @dragover.prevent
        >
          <!-- 网格背景 -->
          <div class="grid-bg"></div>

          <!-- 组件列表 -->
          <div
            v-for="widget in widgets"
            :key="widget.id"
            class="widget"
            :class="{ selected: selectedWidget?.id === widget.id }"
            :style="{
              left: widget.x + 'px',
              top: widget.y + 'px',
              width: widget.width + 'px',
              height: widget.height + 'px',
              zIndex: widget.zIndex
            }"
            @click="selectWidget(widget)"
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
              <el-table :data="widget.tableData || []" border size="small">
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

            <!-- 选中边框 -->
            <div v-if="selectedWidget?.id === widget.id" class="selected-border"></div>
          </div>
        </div>
      </div>

      <!-- 右侧图层面板 -->
      <div class="layer-panel">
        <div class="panel-title">图层</div>
        <div class="layer-list">
          <div
            v-for="widget in widgets"
            :key="widget.id"
            class="layer-item"
            :class="{ selected: selectedWidget?.id === widget.id }"
            @click="selectWidget(widget)"
          >
            <el-icon><component :is="getWidgetIcon(widget.type)" /></el-icon>
            <span>{{ widget.name || widget.type }}</span>
            <el-icon class="delete-btn" @click.stop="handleDeleteWidget(widget)"><Delete /></el-icon>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { dashboardTplApi } from '@/api/dashboard'

const route = useRoute()
const router = useRouter()

// 模板信息
const tplId = ref(route.params.id)
const tplName = ref('')
const screenWidth = ref(1920)
const screenHeight = ref(1080)
const bgColor = ref('#0a0a0a')
const scale = ref(1)

// 画布引用
const canvasContainerRef = ref(null)

// 组件列表
const widgets = ref([])
const selectedWidget = ref(null)
const widgetIdMap = new Map()

// 组件类型定义
const widgetTypes = [
  { type: 'chart', name: '图表', icon: 'DataLine' },
  { type: 'text', name: '文字', icon: 'Edit' },
  { type: 'table', name: '表格', icon: 'Grid' },
  { type: 'image', name: '图片', icon: 'Picture' }
]

// 生成唯一ID
const generateId = () => Math.random().toString(36).substr(2, 9)

// 加载模板数据
const loadTemplate = async () => {
  if (!tplId.value) return
  try {
    const data = await dashboardTplApi.get(tplId.value)
    tplName.value = data.tplName
    screenWidth.value = data.screenWidth || 1920
    screenHeight.value = data.screenHeight || 1080
    bgColor.value = data.bgColor || '#0a0a0a'

    // 解析组件配置
    if (data.widgetConfig) {
      try {
        widgets.value = JSON.parse(data.widgetConfig)
        widgets.value.forEach(w => widgetIdMap.set(w.id, w))
      } catch (e) {
        console.error('解析组件配置失败:', e)
      }
    }
  } catch (error) {
    console.error('加载模板失败:', error)
  }
}

// 计算缩放比例
const calculateScale = () => {
  if (!canvasContainerRef.value) return
  const container = canvasContainerRef.value
  const containerWidth = container.clientWidth - 40
  const containerHeight = container.clientHeight - 40
  const scaleX = containerWidth / screenWidth.value
  const scaleY = containerHeight / screenHeight.value
  scale.value = Math.min(scaleX, scaleY, 1)
}

// 拖拽开始
const onWidgetDragStart = (event, widget) => {
  event.dataTransfer.setData('widgetType', widget.type)
}

// 放置组件
const onWidgetDrop = (event) => {
  const type = event.dataTransfer.getData('widgetType')
  if (!type) return

  const rect = event.currentTarget.getBoundingClientRect()
  const x = (event.clientX - rect.left) / scale.value
  const y = (event.clientY - rect.top) / scale.value

  const widget = createWidget(type, x, y)
  widgets.value.push(widget)
  widgetIdMap.set(widget.id, widget)
  selectWidget(widget)
}

// 创建组件
const createWidget = (type, x, y) => {
  const id = generateId()
  const baseWidget = {
    id,
    type,
    name: `${type}-${id}`,
    x: Math.round(x / 10) * 10,
    y: Math.round(y / 10) * 10,
    width: 400,
    height: 300,
    zIndex: widgets.value.length
  }

  if (type === 'chart') {
    return {
      ...baseWidget,
      chartType: 'line',
      datasourceId: null,
      chartConfig: {
        title: { text: '图表标题' },
        legend: { data: ['系列1', '系列2'] },
        xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五'] },
        yAxis: { type: 'value' },
        series: [
          { name: '系列1', type: 'line', data: [120, 200, 150, 80, 70] },
          { name: '系列2', type: 'line', data: [80, 100, 120, 90, 110] }
        ]
      }
    }
  }

  if (type === 'text') {
    return {
      ...baseWidget,
      content: '双击编辑文字',
      fontSize: 24,
      color: '#ffffff'
    }
  }

  if (type === 'table') {
    return {
      ...baseWidget,
      width: 600,
      height: 300,
      columns: [
        { prop: 'name', label: '名称' },
        { prop: 'value', label: '数值' }
      ],
      tableData: [
        { name: '项目1', value: 100 },
        { name: '项目2', value: 200 }
      ]
    }
  }

  if (type === 'image') {
    return {
      ...baseWidget,
      width: 400,
      height: 300,
      src: ''
    }
  }

  return baseWidget
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
  const option = {
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
      splitLine: { lineStyle: { color: '#333' } }
    },
    series: (config.series || []).map(s => ({
      ...s,
      itemStyle: { borderRadius: 4 }
    }))
  }

  return option
}

// 选择组件
const selectWidget = (widget) => {
  selectedWidget.value = widget
}

// 获取组件图标
const getWidgetIcon = (type) => {
  const iconMap = {
    chart: 'DataLine',
    text: 'Edit',
    table: 'Grid',
    image: 'Picture'
  }
  return iconMap[type] || 'Document'
}

// 添加组件
const handleAddWidget = (type) => {
  const widget = createWidget(type, 100, 100)
  widgets.value.push(widget)
  widgetIdMap.set(widget.id, widget)
  selectWidget(widget)
}

// 删除组件
const handleDeleteWidget = (widget) => {
  if (!widget) widget = selectedWidget.value
  if (!widget) return

  const index = widgets.value.findIndex(w => w.id === widget.id)
  if (index > -1) {
    widgets.value.splice(index, 1)
    widgetIdMap.delete(widget.id)
    if (selectedWidget.value?.id === widget.id) {
      selectedWidget.value = null
    }
  }
}

// 保存
const handleSave = async () => {
  try {
    await dashboardTplApi.update(tplId.value, {
      widgetConfig: JSON.stringify(widgets.value)
    })
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

// 预览
const handlePreview = () => {
  router.push(`/dashboard/preview/${tplId.value}`)
}

// 返回
const handleBack = () => {
  router.push('/dashboard/list')
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
</script>

<style scoped>
.dashboard-designer-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #1a1a2e;
  color: #fff;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #16213e;
  border-bottom: 1px solid #0f3460;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
}

.tpl-name {
  font-size: 16px;
  font-weight: 600;
}

.designer-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.widget-panel {
  width: 260px;
  background: #16213e;
  border-right: 1px solid #0f3460;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.panel-title {
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 600;
  border-bottom: 1px solid #0f3460;
}

.widget-list {
  padding: 12px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.widget-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  background: #1a1a2e;
  border-radius: 4px;
  cursor: move;
  transition: all 0.2s;
}

.widget-item:hover {
  background: #0f3460;
}

.widget-item span {
  margin-top: 4px;
  font-size: 12px;
}

.property-panel {
  padding: 12px;
  border-top: 1px solid #0f3460;
}

.property-panel .el-form-item {
  margin-bottom: 12px;
}

.canvas-container {
  flex: 1;
  overflow: auto;
  padding: 20px;
  background: #0a0a0a;
}

.canvas {
  position: relative;
  background: #0a0a0a;
  overflow: hidden;
}

.grid-bg {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  pointer-events: none;
}

.widget {
  position: absolute;
  cursor: move;
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
  background: rgba(255, 255, 255, 0.05);
}

.widget-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.selected-border {
  position: absolute;
  inset: -2px;
  border: 2px solid #409eff;
  pointer-events: none;
}

.layer-panel {
  width: 200px;
  background: #16213e;
  border-left: 1px solid #0f3460;
  display: flex;
  flex-direction: column;
}

.layer-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.layer-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  margin-bottom: 4px;
  background: #1a1a2e;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.layer-item:hover {
  background: #0f3460;
}

.layer-item.selected {
  background: #0f3460;
  border: 1px solid #409eff;
}

.layer-item span {
  flex: 1;
  margin-left: 8px;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.delete-btn {
  opacity: 0;
  transition: opacity 0.2s;
}

.layer-item:hover .delete-btn {
  opacity: 1;
}
</style>