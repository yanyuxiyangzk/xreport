import { useState } from 'react'
import { jsPDF } from 'jspdf'
import * as XLSX from 'xlsx'
import type { ReportLayout, ExportFormat, TableConfig, ChartConfig, MetricCardConfig } from '../../types'

interface Props {
  report: ReportLayout | null
}

export default function ReportExporter({ report }: Props) {
  const [format, setFormat] = useState<ExportFormat>('pdf')
  const [exporting, setExporting] = useState(false)
  const [status, setStatus] = useState<string>('')

  if (!report) {
    return <div className="exporter-empty">请先设计报表</div>
  }

  const handleExport = async () => {
    setExporting(true)
    setStatus('')
    try {
      switch (format) {
        case 'pdf':
          await exportPDF(report)
          setStatus('PDF 导出成功！')
          break
        case 'excel':
          await exportExcel(report)
          setStatus('Excel 导出成功！')
          break
        case 'csv':
          await exportCSV(report)
          setStatus('CSV 导出成功！')
          break
      }
    } catch (err) {
      setStatus(`导出失败: ${err instanceof Error ? err.message : '未知错误'}`)
    } finally {
      setExporting(false)
    }
  }

  return (
    <div className="report-exporter">
      <h3>导出报表</h3>
      <div className="export-options">
        <label className={format === 'pdf' ? 'selected' : ''}>
          <input
            type="radio"
            name="format"
            value="pdf"
            checked={format === 'pdf'}
            onChange={() => setFormat('pdf')}
          />
          📄 PDF
        </label>
        <label className={format === 'excel' ? 'selected' : ''}>
          <input
            type="radio"
            name="format"
            value="excel"
            checked={format === 'excel'}
            onChange={() => setFormat('excel')}
          />
          📊 Excel (.xlsx)
        </label>
        <label className={format === 'csv' ? 'selected' : ''}>
          <input
            type="radio"
            name="format"
            value="csv"
            checked={format === 'csv'}
            onChange={() => setFormat('csv')}
          />
          📋 CSV
        </label>
      </div>
      <button
        className="btn-export"
        onClick={handleExport}
        disabled={exporting}
      >
        {exporting ? '导出中...' : `导出为 ${format.toUpperCase()}`}
      </button>
      {status && <div className="export-status">{status}</div>}
    </div>
  )
}

async function exportPDF(report: ReportLayout): Promise<void> {
  const doc = new jsPDF()

  // 标题
  doc.setFontSize(20)
  doc.setTextColor(33, 33, 33)
  doc.text(report.settings.title || '报表', 20, 20)

  // 分隔线
  doc.setDrawColor(200, 200, 200)
  doc.line(20, 25, 190, 25)

  let yPos = 35
  const pageHeight = doc.internal.pageSize.height

  for (const widget of report.widgets) {
    // 检查是否需要新页面
    if (yPos > pageHeight - 40) {
      doc.addPage()
      yPos = 20
    }

    switch (widget.type) {
      case 'metric-card': {
        const cfg = widget.config as MetricCardConfig
        doc.setFontSize(12)
        doc.setTextColor(100, 100, 100)
        doc.text(cfg.label || '指标', 20, yPos)
        yPos += 8
        doc.setFontSize(24)
        doc.setTextColor(33, 33, 33)
        doc.text(`${cfg.value.toLocaleString()} ${cfg.unit || ''}`, 20, yPos)
        yPos += 15
        break
      }

      case 'table': {
        const cfg = widget.config as TableConfig
        const cols = cfg.columns || []
        const colWidth = Math.min(160 / cols.length, 40)

        // 表头
        doc.setFillColor(245, 245, 245)
        doc.rect(20, yPos, cols.length * colWidth, 8, 'F')
        doc.setFontSize(10)
        doc.setTextColor(33, 33, 33)
        cols.forEach((col, i) => {
          doc.text(String(col.title).substring(0, 10), 22 + i * colWidth, yPos + 6)
        })
        yPos += 8

        // 数据行（模拟 3 行）
        const mockValues = [65, 59, 80]
        doc.setFontSize(9)
        for (let row = 0; row < 3; row++) {
          if (yPos > pageHeight - 15) {
            doc.addPage()
            yPos = 20
          }
          cols.forEach((_col, i) => {
            doc.setTextColor(66, 66, 66)
            const val = i === 0 ? `行${row + 1}` : String(mockValues[row % mockValues.length] + i * 10)
            doc.text(val.substring(0, 10), 22 + i * colWidth, yPos + 5)
          })
          yPos += 7
        }
        yPos += 8
        break
      }

      case 'chart': {
        const cfg = widget.config as ChartConfig
        if (yPos > pageHeight - 50) {
          doc.addPage()
          yPos = 20
        }
        doc.setFontSize(11)
        doc.setTextColor(66, 66, 66)
        doc.text(`图表类型: ${cfg.chartType}  |  X轴: ${cfg.xAxis}  |  Y轴: ${cfg.yAxis.join(', ')}`, 20, yPos)
        yPos += 10

        // 简单柱状图示意
        const chartData = [65, 59, 80, 81, 56, 55]
        const barWidth = 4
        const maxVal = Math.max(...chartData)
        chartData.forEach((val, i) => {
          const barH = (val / maxVal) * 30
          doc.setFillColor(54, 162, 235)
          doc.rect(25 + i * (barWidth + 3), yPos + 30 - barH, barWidth, barH, 'F')
        })
        yPos += 45
        break
      }
    }
  }

  // 页脚
  doc.setFontSize(8)
  doc.setTextColor(150, 150, 150)
  doc.text(
    `导出时间: ${new Date().toLocaleString('zh-CN')}`,
    20,
    doc.internal.pageSize.height - 10
  )

  const filename = `${report.settings.title || 'report'}_${Date.now()}.pdf`
  doc.save(filename)
}

async function exportExcel(report: ReportLayout): Promise<void> {
  const wb = XLSX.utils.book_new()

  // 创建一个汇总 sheet
  const summaryData: (string | number)[][] = [
    [report.settings.title || '报表', '', ''],
    ['', '', ''],
    ['组件类型', '配置', '位置'],
  ]

  report.widgets.forEach((w, _i) => {
    if (w.type === 'table') {
      const cfg = w.config as TableConfig
      summaryData.push([
        '表格',
        `列数: ${cfg.columns?.length || 0}`,
        `(${Math.round(w.x)}, ${Math.round(w.y)})`,
      ])
    } else if (w.type === 'chart') {
      const cfg = w.config as ChartConfig
      summaryData.push([
        `图表(${cfg.chartType})`,
        `${cfg.xAxis} vs ${cfg.yAxis.join(', ')}`,
        `(${Math.round(w.x)}, ${Math.round(w.y)})`,
      ])
    } else if (w.type === 'metric-card') {
      const cfg = w.config as MetricCardConfig
      summaryData.push([
        '指标卡',
        `${cfg.label}: ${cfg.value}${cfg.unit || ''}`,
        `(${Math.round(w.x)}, ${Math.round(w.y)})`,
      ])
    }
  })

  const summaryWs = XLSX.utils.aoa_to_sheet(summaryData)
  XLSX.utils.book_append_sheet(wb, summaryWs, '报表概览')

  // 为每个表格组件创建单独的 sheet
  const tableWidgets = report.widgets.filter(w => w.type === 'table')
  const MOCK_DATA = ['一月', '二月', '三月', '四月', '五月', '六月']

  tableWidgets.forEach((w, idx) => {
    const cfg = w.config as TableConfig
    const cols = cfg.columns || []

    // 表头 + 数据
    const sheetData: (string | number)[][] = [
      cols.map(c => c.title),
      ...MOCK_DATA.map((label, _rowIdx) =>
        cols.map((_col, colIdx) =>
          colIdx === 0 ? label : 65 + Math.floor(Math.random() * 30) + colIdx * 10
        )
      ),
    ]

    const ws = XLSX.utils.aoa_to_sheet(sheetData)
    XLSX.utils.book_append_sheet(wb, ws, `表格${idx + 1}`)
  })

  const filename = `${report.settings.title || 'report'}_${Date.now()}.xlsx`
  XLSX.writeFile(wb, filename)
}

async function exportCSV(report: ReportLayout): Promise<void> {
  const tableWidgets = report.widgets.filter(w => w.type === 'table')
  if (tableWidgets.length === 0) {
    throw new Error('报表中没有表格组件')
  }

  let csvContent = '\uFEFF' // BOM for UTF-8

  for (const widget of tableWidgets) {
    const cfg = widget.config as TableConfig
    const cols = cfg.columns || []

    // 表头
    csvContent += cols.map(c => `"${c.title}"`).join(',') + '\r\n'

    // 模拟数据
    const mockLabels = ['一月', '二月', '三月', '四月', '五月', '六月']
    const mockValues = [65, 59, 80, 81, 56, 55]

    mockLabels.forEach((label, rowIdx) => {
      const row = cols.map((_col, colIdx) => {
        if (colIdx === 0) return `"${label}"`
        return String(mockValues[rowIdx] + colIdx * 10)
      })
      csvContent += row.join(',') + '\r\n'
    })

    csvContent += '\r\n'
  }

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${report.settings.title || 'report'}_${Date.now()}.csv`
  a.click()
  URL.revokeObjectURL(url)
}
