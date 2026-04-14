// Report configuration types

export type WidgetType = 'table' | 'chart' | 'metric-card' | 'image' | 'text' | 'divider'

export interface ImageConfig {
  src: string
  alt?: string
  fit?: 'cover' | 'contain' | 'fill'
}

export interface TextConfig {
  content: string
  fontSize?: number
  fontWeight?: 'normal' | 'bold'
  color?: string
  align?: 'left' | 'center' | 'right'
}

export interface DividerConfig {
  thickness?: number
  color?: string
  style?: 'solid' | 'dashed' | 'dotted'
}

export interface ReportWidget {
  id: string
  type: WidgetType
  x: number
  y: number
  width: number
  height: number
  config: TableConfig | ChartConfig | MetricCardConfig | ImageConfig | TextConfig | DividerConfig
}

export interface TableConfig {
  columns: Column[]
  dataSource: string
}

export interface Column {
  field: string
  title: string
  width?: number
}

export interface ChartConfig {
  chartType: 'line' | 'bar' | 'pie' | 'scatter'
  xAxis: string
  yAxis: string[]
  dataSource: string
}

export interface MetricCardConfig {
  metric: string
  label: string
  value: number
  unit?: string
}

export interface ReportLayout {
  widgets: ReportWidget[]
  settings: ReportSettings
}

export interface ReportSettings {
  title: string
  width: number
  height: number
}

export interface RenderOptions {
  format: 'html' | 'json'
}

export type ExportFormat = 'pdf' | 'excel' | 'csv'