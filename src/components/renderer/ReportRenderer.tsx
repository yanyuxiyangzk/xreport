import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js'
import { Bar, Line, Pie, Scatter } from 'react-chartjs-2'
import type { ReportLayout, TableConfig, ChartConfig, MetricCardConfig, ImageConfig, TextConfig, DividerConfig } from '../../types'

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
)

interface Props {
  report: ReportLayout | null
}

// 模拟数据源
const MOCK_DATA: Record<string, { labels: string[]; values: number[] }> = {
  default: {
    labels: ['一月', '二月', '三月', '四月', '五月', '六月'],
    values: [65, 59, 80, 81, 56, 55],
  },
  sales: {
    labels: ['北京', '上海', '广州', '深圳', '成都', '杭州'],
    values: [120, 90, 75, 110, 65, 85],
  },
  users: {
    labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    values: [230, 340, 280, 420, 380, 520, 490],
  },
}

function getMockData(source: string) {
  return MOCK_DATA[source] || MOCK_DATA.default
}

function getChartColors() {
  return [
    'rgba(54, 162, 235, 0.7)',
    'rgba(255, 99, 132, 0.7)',
    'rgba(75, 192, 192, 0.7)',
    'rgba(255, 206, 86, 0.7)',
    'rgba(153, 102, 255, 0.7)',
    'rgba(255, 159, 64, 0.7)',
  ]
}

export default function ReportRenderer({ report }: Props) {
  if (!report) {
    return <div className="renderer-empty">请先设计报表</div>
  }

  return (
    <div className="report-renderer">
      <div className="report-preview">
        <h2 className="report-title">{report.settings.title || '未命名报表'}</h2>
        <div className="widgets-container" style={{ position: 'relative', minHeight: 400 }}>
          {report.widgets.map((widget) => (
            <div
              key={widget.id}
              className="rendered-widget"
              style={{
                position: 'absolute',
                left: widget.x,
                top: widget.y,
                width: Math.max(widget.width, 200),
                height: Math.max(widget.height, 150),
              }}
            >
              {renderWidget(widget)}
            </div>
          ))}
          {report.widgets.length === 0 && (
            <div className="empty-widgets">报表还没有添加组件</div>
          )}
        </div>
      </div>
    </div>
  )
}

function renderWidget(widget: ReportLayout['widgets'][0]) {
  switch (widget.type) {
    case 'table':
      return <TableRenderer config={widget.config as TableConfig} />
    case 'chart':
      return <ChartRenderer config={widget.config as ChartConfig} />
    case 'metric-card':
      return <MetricCardRenderer config={widget.config as MetricCardConfig} />
    case 'image':
      return <ImageRenderer config={widget.config as ImageConfig} />
    case 'text':
      return <TextRenderer config={widget.config as TextConfig} />
    case 'divider':
      return <DividerRenderer config={widget.config as DividerConfig} />
    default:
      return <div className="unknown-widget">未知组件类型</div>
  }
}

function TableRenderer({ config }: { config: TableConfig }) {
  const mockData = getMockData(config.dataSource || 'default')

  return (
    <div className="table-renderer">
      <table>
        <thead>
          <tr>
            {config.columns?.map((col, i) => (
              <th key={i} style={{ width: col.width }}>{col.title}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {mockData.labels.map((label, rowIdx) => (
            <tr key={rowIdx}>
              {config.columns?.map((col, colIdx) => (
                <td key={colIdx}>
                  {col.field === config.columns?.[0]?.field
                    ? label
                    : mockData.values[rowIdx] + (colIdx > 0 ? colIdx * 10 : 0)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function ChartRenderer({ config }: { config: ChartConfig }) {
  const mock = getMockData(config.dataSource || 'default')

  const data = {
    labels: mock.labels,
    datasets: (config.yAxis || ['value']).map((yField, i) => ({
      label: yField,
      data: mock.values.map(v => v + i * 15),
      backgroundColor: getChartColors()[i % getChartColors().length],
      borderColor: getChartColors()[i % getChartColors().length].replace('0.7', '1'),
      borderWidth: 1,
    })),
  }

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top' as const },
    },
  }

  const chartKey = `${config.chartType}-${config.xAxis}-${(config.yAxis || []).join('-')}`

  switch (config.chartType) {
    case 'bar':
      return (
        <div style={{ height: '100%', minHeight: 150 }}>
          <Bar key={chartKey} data={data} options={options} />
        </div>
      )
    case 'line':
      return (
        <div style={{ height: '100%', minHeight: 150 }}>
          <Line key={chartKey} data={data} options={options} />
        </div>
      )
    case 'pie':
      return (
        <div style={{ height: '100%', minHeight: 150 }}>
          <Pie
            key={chartKey}
            data={{
              labels: mock.labels,
              datasets: [{
                data: mock.values,
                backgroundColor: getChartColors(),
                borderWidth: 0,
              }],
            }}
            options={{ responsive: true, maintainAspectRatio: false }}
          />
        </div>
      )
    case 'scatter':
      return (
        <div style={{ height: '100%', minHeight: 150 }}>
          <Scatter
            key={chartKey}
            data={{
              datasets: [{
                label: config.yAxis?.[0] || 'Y',
                data: mock.labels.map((_label, i) => ({
                  x: i,
                  y: mock.values[i],
                })),
                backgroundColor: 'rgba(54, 162, 235, 0.7)',
              }],
            }}
            options={{
              responsive: true,
              maintainAspectRatio: false,
              plugins: { legend: { display: false } },
            }}
          />
        </div>
      )
    default:
      return <div className="chart-placeholder">不支持的图表类型</div>
  }
}

function MetricCardRenderer({ config }: { config: MetricCardConfig }) {
  return (
    <div className="metric-card-renderer">
      <div className="metric-label">{config.label || '指标'}</div>
      <div className="metric-value">
        {config.value.toLocaleString()}
        {config.unit && <span className="metric-unit">{config.unit}</span>}
      </div>
      <div className="metric-name">{config.metric}</div>
    </div>
  )
}

function ImageRenderer({ config }: { config: ImageConfig }) {
  return (
    <div className="image-renderer">
      {config.src ? (
        <img
          src={config.src}
          alt={config.alt || ''}
          style={{
            width: '100%',
            height: '100%',
            objectFit: config.fit || 'contain',
          }}
        />
      ) : (
        <div className="image-placeholder">📷 点击设置图片地址</div>
      )}
    </div>
  )
}

function TextRenderer({ config }: { config: TextConfig }) {
  return (
    <div className="text-renderer" style={{
      fontSize: config.fontSize || 14,
      fontWeight: config.fontWeight || 'normal',
      color: config.color || '#333',
      textAlign: config.align || 'left',
      padding: '8px',
      lineHeight: 1.5,
    }}>
      {config.content || '点击输入文本'}
    </div>
  )
}

function DividerRenderer({ config }: { config: DividerConfig }) {
  return (
    <div className="divider-renderer" style={{
      height: config.thickness || 1,
      backgroundColor: config.color || '#e0e0e0',
      borderTop: config.style === 'dashed'
        ? `1px dashed ${config.color || '#e0e0e0'}`
        : config.style === 'dotted'
        ? `1px dotted ${config.color || '#e0e0e0'}`
        : 'none',
      width: '100%',
    }} />
  )
}

export function renderToHTML(report: ReportLayout): string {
  return `<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${report.settings.title || '报表'}</title>
  <style>
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; padding: 20px; }
    h1 { color: #333; }
    table { border-collapse: collapse; width: 100%; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f5f5f5; }
  </style>
</head>
<body>
  <h1>${report.settings.title || '报表'}</h1>
  ${report.widgets.map(w => {
    if (w.type === 'table') {
      const cfg = w.config as TableConfig
      return `<table><thead><tr>${cfg.columns?.map(c => `<th>${c.title}</th>`).join('')}</tr></thead></table>`
    }
    return `<div>${w.type} widget</div>`
  }).join('')}
</body>
</html>`
}

export function renderToJSON(report: ReportLayout): string {
  return JSON.stringify(report, null, 2)
}
