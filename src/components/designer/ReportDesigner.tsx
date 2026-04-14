import { useState, useCallback, useEffect, useRef } from 'react'
import type { ReportLayout, ReportWidget, TableConfig, ChartConfig, MetricCardConfig, ImageConfig, TextConfig, DividerConfig } from '../../types'
import { useHistory } from '../../hooks/useHistory'

interface Props {
  report: ReportLayout | null
  onChange: (report: ReportLayout) => void
}

export default function ReportDesigner({ report, onChange }: Props) {
  const [selectedWidget, setSelectedWidget] = useState<string | null>(null)
  const [selectedWidgets, setSelectedWidgets] = useState<string[]>([])
  const [draggedType, setDraggedType] = useState<string | null>(null)
  const [copiedWidget, setCopiedWidget] = useState<ReportWidget | null>(null)
  const [draggingId, setDraggingId] = useState<string | null>(null)
  const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 })
  const [resizingId, setResizingId] = useState<string | null>(null)
  const [resizeStart, setResizeStart] = useState({ x: 0, y: 0, w: 0, h: 0 })
  const [zoom, setZoom] = useState(100)
  const [snapEnabled, setSnapEnabled] = useState(true)
  const canvasRef = useRef<HTMLDivElement>(null)

  const SNAP_SIZE = 10

  const snap = (val: number) => snapEnabled ? Math.round(val / SNAP_SIZE) * SNAP_SIZE : val

  // History for undo/redo
  const { state: widgets, push: pushWidgets, undo, redo, canUndo, canRedo } = useHistory(report?.widgets || [])

  // Sync widgets changes to parent
  useEffect(() => {
    if (!report) return
    if (JSON.stringify(report.widgets) !== JSON.stringify(widgets)) {
      onChange({ ...report, widgets })
    }
  }, [widgets])

  // Keyboard shortcuts
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      const tag = (e.target as HTMLElement)?.tagName
      if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return

      if ((e.key === 'Delete' || e.key === 'Backspace') && selectedWidget) {
        e.preventDefault()
        handleDeleteWidget(selectedWidget)
      }

      if ((e.ctrlKey || e.metaKey) && e.key === 'z' && !e.shiftKey) {
        e.preventDefault()
        undo()
        setSelectedWidget(null)
      }

      if ((e.ctrlKey || e.metaKey) && (e.key === 'y' || (e.key === 'z' && e.shiftKey))) {
        e.preventDefault()
        redo()
        setSelectedWidget(null)
      }

      if ((e.ctrlKey || e.metaKey) && e.key === 'c' && selectedWidget) {
        e.preventDefault()
        const w = widgets.find(w => w.id === selectedWidget)
        if (w) setCopiedWidget({ ...w })
      }

      if ((e.ctrlKey || e.metaKey) && e.key === 'v' && copiedWidget) {
        e.preventDefault()
        handleDuplicate(copiedWidget)
      }

      if ((e.ctrlKey || e.metaKey) && e.key === 'a') {
        e.preventDefault()
        setSelectedWidgets(widgets.map(w => w.id))
        setSelectedWidget(null)
      }

      // Arrow key nudge
      if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(e.key) && selectedWidget) {
        e.preventDefault()
        const step = e.shiftKey ? 10 : 1
        const updated = widgets.map(w => {
          if (w.id !== selectedWidget) return w
          return {
            ...w,
            x: snap(Math.max(0, w.x + (e.key === 'ArrowLeft' ? -step : e.key === 'ArrowRight' ? step : 0))),
            y: snap(Math.max(0, w.y + (e.key === 'ArrowUp' ? -step : e.key === 'ArrowDown' ? step : 0))),
          }
        })
        pushWidgets(updated)
        return
      }

      if (e.key === 'Escape') {
        setSelectedWidget(null)
      }
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [selectedWidget, widgets, copiedWidget, snapEnabled])

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    if (!draggedType || !report) return

    const newWidget: ReportWidget = {
      id: `widget-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
      type: draggedType as ReportWidget['type'],
      x: snap(80 + Math.random() * 60),
      y: snap(60 + Math.random() * 60),
      width: 300,
      height: 220,
      config: getDefaultConfig(draggedType),
    }

    pushWidgets([...widgets, newWidget])
    setSelectedWidget(newWidget.id)
    setDraggedType(null)
  }, [draggedType, report, widgets, pushWidgets, snap])

  const getDefaultConfig = (type: string) => {
    switch (type) {
      case 'table':
        return {
          columns: [
            { field: 'name', title: '名称', width: 120 },
            { field: 'value', title: '数值', width: 100 },
          ],
          dataSource: 'mock',
        } as TableConfig
      case 'chart':
        return {
          chartType: 'bar' as const,
          xAxis: 'category',
          yAxis: ['value'],
          dataSource: 'mock',
        } as ChartConfig
      case 'metric-card':
        return {
          metric: 'total_users',
          label: '总用户数',
          value: 12345,
          unit: '人',
        } as MetricCardConfig
      case 'image':
        return {
          src: '',
          alt: '报表图片',
          fit: 'contain',
        } as ImageConfig
      case 'text':
        return {
          content: '双击编辑文本内容',
          fontSize: 14,
          fontWeight: 'normal',
          color: '#333333',
          align: 'left',
        } as TextConfig
      case 'divider':
        return {
          thickness: 1,
          color: '#e0e0e0',
          style: 'solid',
        } as DividerConfig
      default:
        return {} as TableConfig
    }
  }

  const handleDeleteWidget = useCallback((id: string) => {
    pushWidgets(widgets.filter(w => w.id !== id))
    setSelectedWidget(null)
  }, [widgets, pushWidgets])

  const handleDuplicate = useCallback((w: ReportWidget) => {
    const copy: ReportWidget = {
      ...w,
      id: `widget-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
      x: w.x + 20,
      y: w.y + 20,
      config: JSON.parse(JSON.stringify(w.config)),
    }
    pushWidgets([...widgets, copy])
    setSelectedWidget(copy.id)
  }, [widgets, pushWidgets])

  const handleBringForward = useCallback(() => {
    if (!selectedWidget) return
    const idx = widgets.findIndex(w => w.id === selectedWidget)
    if (idx >= widgets.length - 1) return
    const newWidgets = [...widgets]
    ;[newWidgets[idx], newWidgets[idx + 1]] = [newWidgets[idx + 1], newWidgets[idx]]
    pushWidgets(newWidgets)
  }, [selectedWidget, widgets, pushWidgets])

  const handleSendBackward = useCallback(() => {
    if (!selectedWidget) return
    const idx = widgets.findIndex(w => w.id === selectedWidget)
    if (idx <= 0) return
    const newWidgets = [...widgets]
    ;[newWidgets[idx], newWidgets[idx - 1]] = [newWidgets[idx - 1], newWidgets[idx]]
    pushWidgets(newWidgets)
  }, [selectedWidget, widgets, pushWidgets])

  const handleBringToFront = useCallback(() => {
    if (!selectedWidget) return
    const w = widgets.find(w => w.id === selectedWidget)
    if (!w) return
    pushWidgets([...widgets.filter(w => w.id !== selectedWidget), w])
  }, [selectedWidget, widgets, pushWidgets])

  const handleSendToBack = useCallback(() => {
    if (!selectedWidget) return
    const w = widgets.find(w => w.id === selectedWidget)
    if (!w) return
    pushWidgets([w, ...widgets.filter(w => w.id !== selectedWidget)])
  }, [selectedWidget, widgets, pushWidgets])

  // Canvas drag-and-drop for moving widgets
  const handleWidgetMouseDown = useCallback((e: React.MouseEvent, id: string) => {
    if ((e.target as HTMLElement).closest('.properties-panel')) return
    e.stopPropagation()

    // Shift+click multi-select
    if (e.shiftKey) {
      setSelectedWidgets(prev =>
        prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]
      )
      setSelectedWidget(id)
      return
    }

    setSelectedWidget(id)
    setSelectedWidgets([])
    const w = widgets.find(wg => wg.id === id)
    if (!w) return
    setDraggingId(id)
    const rect = canvasRef.current?.getBoundingClientRect()
    if (rect) {
      const scale = zoom / 100
      setDragOffset({
        x: (e.clientX - rect.left) / scale - w.x,
        y: (e.clientY - rect.top) / scale - w.y,
      })
    }
  }, [widgets, zoom])

  const handleResizeMouseDown = useCallback((e: React.MouseEvent, id: string) => {
    e.stopPropagation()
    const w = widgets.find(wg => wg.id === id)
    if (!w) return
    setResizingId(id)
    setResizeStart({ x: e.clientX, y: e.clientY, w: w.width, h: w.height })
  }, [widgets])

  const handleMouseMove = useCallback((e: React.MouseEvent) => {
    const scale = zoom / 100
    const rect = canvasRef.current?.getBoundingClientRect()
    if (!rect) return

    if (draggingId) {
      const canvasX = (e.clientX - rect.left) / scale
      const canvasY = (e.clientY - rect.top) / scale
      const newX = Math.max(0, canvasX - dragOffset.x)
      const newY = Math.max(0, canvasY - dragOffset.y)

      pushWidgets(widgets.map(w =>
        w.id === draggingId ? { ...w, x: snap(newX), y: snap(newY) } : w
      ))
    }

    if (resizingId) {
      const dx = (e.clientX - resizeStart.x) / scale
      const dy = (e.clientY - resizeStart.y) / scale
      const newW = Math.max(100, snap(resizeStart.w + dx))
      const newH = Math.max(80, snap(resizeStart.h + dy))
      pushWidgets(widgets.map(w =>
        w.id === resizingId ? { ...w, width: newW, height: newH } : w
      ))
    }
  }, [draggingId, resizingId, dragOffset, resizeStart, widgets, pushWidgets, zoom, snap])

  const handleMouseUp = useCallback(() => {
    setDraggingId(null)
    setResizingId(null)
  }, [])

  const handleCanvasClick = useCallback(() => {
    setSelectedWidget(null)
  }, [])

  const selected = widgets.find(w => w.id === selectedWidget)

  return (
    <div
      className="report-designer"
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
      onMouseLeave={handleMouseUp}
    >
      <aside className="widget-palette">
        <h3>组件库</h3>
        <div
          className="widget-item"
          draggable
          onDragStart={() => setDraggedType('table')}
        >
          📊 表格
        </div>
        <div
          className="widget-item"
          draggable
          onDragStart={() => setDraggedType('chart')}
        >
          📈 图表
        </div>
        <div
          className="widget-item"
          draggable
          onDragStart={() => setDraggedType('metric-card')}
        >
          📋 指标卡
        </div>
        <div
          className="widget-item"
          draggable
          onDragStart={() => setDraggedType('image')}
        >
          🖼 图片
        </div>
        <div
          className="widget-item"
          draggable
          onDragStart={() => setDraggedType('text')}
        >
          📝 文本
        </div>
        <div
          className="widget-item"
          draggable
          onDragStart={() => setDraggedType('divider')}
        >
          ➖ 分隔线
        </div>

        <div className="palette-divider" />

        <div className="toolbar-section">
          <h4>操作</h4>
          <button
            className="tool-btn"
            onClick={() => selectedWidget && handleDeleteWidget(selectedWidget)}
            disabled={!selectedWidget}
            title="删除 (Delete)"
          >
            🗑 删除
          </button>
          <button
            className="tool-btn"
            onClick={() => copiedWidget && handleDuplicate(copiedWidget)}
            disabled={!selectedWidget}
            title="复制 (Ctrl+C)"
          >
            📋 复制
          </button>
          <button
            className="tool-btn"
            onClick={() => copiedWidget && handleDuplicate(copiedWidget)}
            disabled={!copiedWidget}
            title="粘贴 (Ctrl+V)"
          >
            📄 粘贴
          </button>
        </div>

        <div className="toolbar-section">
          <h4>层级</h4>
          <button className="tool-btn" onClick={handleBringToFront} disabled={!selectedWidget} title="置顶">
            ⬆ 置顶
          </button>
          <button className="tool-btn" onClick={handleBringForward} disabled={!selectedWidget} title="上移">
            ↑ 上移
          </button>
          <button className="tool-btn" onClick={handleSendBackward} disabled={!selectedWidget} title="下移">
            ↓ 下移
          </button>
          <button className="tool-btn" onClick={handleSendToBack} disabled={!selectedWidget} title="置底">
            ⬇ 置底
          </button>
        </div>

        <div className="toolbar-section">
          <h4>历史</h4>
          <button className="tool-btn" onClick={undo} disabled={!canUndo} title="撤销 (Ctrl+Z)">
            ↩ 撤销
          </button>
          <button className="tool-btn" onClick={redo} disabled={!canRedo} title="重做 (Ctrl+Y)">
            ↪ 重做
          </button>
        </div>
      </aside>

      <div
        ref={canvasRef}
        className="canvas"
        onDrop={handleDrop}
        onDragOver={e => e.preventDefault()}
        onClick={handleCanvasClick}
        style={{ cursor: draggingId ? 'grabbing' : 'default' }}
      >
        <div className="canvas-toolbar">
          <button className="canvas-tool-btn" onClick={() => setZoom(z => Math.max(25, z - 25))} title="缩小">−</button>
          <span className="zoom-label">{zoom}%</span>
          <button className="canvas-tool-btn" onClick={() => setZoom(z => Math.min(200, z + 25))} title="放大">+</button>
          <button className="canvas-tool-btn" onClick={() => setZoom(100)} title="重置100%">⟳</button>
          <div className="toolbar-sep" />
          <button
            className={`canvas-tool-btn snap-btn ${snapEnabled ? 'active' : ''}`}
            onClick={() => setSnapEnabled(v => !v)}
            title={`网格对齐 ${snapEnabled ? '开' : '关'}`}
          >
            #
          </button>
        </div>
        <div className="canvas-inner" style={{ transform: `scale(${zoom / 100})`, transformOrigin: 'top left', width: '100%', height: '100%', position: 'relative' }}>
        {widgets.length === 0 ? (
          <div className="empty-canvas">
            从左侧拖拽组件到此处<br />
            <span className="shortcut-hint">Delete 删除 · Ctrl+Z 撤销 · Ctrl+C/V 复制粘贴 · 方向键移动</span>
          </div>
        ) : null}

        {widgets.map((widget) => (
          <div
            key={widget.id}
            className={`widget ${selectedWidget === widget.id ? 'selected' : ''} ${draggingId === widget.id ? 'dragging' : ''}`}
            style={{
              left: widget.x,
              top: widget.y,
              width: widget.width,
              height: widget.height,
              zIndex: widgets.findIndex(w => w.id === widget.id) + 1,
            }}
            onMouseDown={e => handleWidgetMouseDown(e, widget.id)}
          >
            <span className="widget-type">
              {widget.type === 'table' && '📊'}
              {widget.type === 'chart' && '📈'}
              {widget.type === 'metric-card' && '📋'}
              {' '}{widget.type === 'table' ? '表格' : widget.type === 'chart' ? '图表' : '指标卡'}
            </span>
            {widget.type === 'table' && (
              <span className="widget-info">
                {(widget.config as TableConfig)?.columns?.length || 0} 列
              </span>
            )}
            {widget.type === 'chart' && (
              <span className="widget-info">
                {(widget.config as ChartConfig)?.chartType || 'bar'}
              </span>
            )}
            {widget.type === 'metric-card' && (
              <span className="widget-info">
                {(widget.config as MetricCardConfig)?.label || ''}
              </span>
            )}
            {(selectedWidget === widget.id || selectedWidgets.includes(widget.id)) && (
              <div
                className="resize-handle"
                onMouseDown={e => handleResizeMouseDown(e, widget.id)}
              >⤡</div>
            )}
          </div>
        ))}
        </div>
      </div>

      <aside className="properties-panel">
        <h3>属性</h3>
        {selected ? (
          <div className="property-form">
            <div className="prop-section">
              <label>组件类型</label>
              <input type="text" value={selected.type === 'table' ? '表格' : selected.type === 'chart' ? '图表' : '指标卡'} disabled />
            </div>

            {selected.type === 'table' && (
              <TableProperties
                config={selected.config as TableConfig}
                onUpdate={(k, v) => {
                  const updated = widgets.map(w =>
                    w.id === selected.id ? { ...w, config: { ...w.config, [k]: v } } : w
                  )
                  pushWidgets(updated)
                }}
                onAddColumn={() => {
                  const cfg = selected.config as TableConfig
                  const cols = [...(cfg.columns || []), { field: `col${(cfg.columns || []).length}`, title: `列${(cfg.columns || []).length + 1}` }]
                  const updated = widgets.map(w => w.id === selected.id ? { ...w, config: { ...w.config, columns: cols } } : w)
                  pushWidgets(updated)
                }}
                onRemoveColumn={(i) => {
                  const cfg = selected.config as TableConfig
                  const cols = (cfg.columns || []).filter((_, idx) => idx !== i)
                  const updated = widgets.map(w => w.id === selected.id ? { ...w, config: { ...w.config, columns: cols } } : w)
                  pushWidgets(updated)
                }}
                onUpdateColumn={(i, f, v) => {
                  const cfg = selected.config as TableConfig
                  const cols = (cfg.columns || []).map((c, idx) => idx === i ? { ...c, [f]: v } : c)
                  const updated = widgets.map(w => w.id === selected.id ? { ...w, config: { ...w.config, columns: cols } } : w)
                  pushWidgets(updated)
                }}
              />
            )}

            {selected.type === 'chart' && (
              <ChartProperties
                config={selected.config as ChartConfig}
                onUpdate={(k, v) => {
                  const updated = widgets.map(w =>
                    w.id === selected.id ? { ...w, config: { ...w.config, [k]: v } } : w
                  )
                  pushWidgets(updated)
                }}
              />
            )}

            {selected.type === 'metric-card' && (
              <MetricCardProperties
                config={selected.config as MetricCardConfig}
                onUpdate={(k, v) => {
                  const updated = widgets.map(w =>
                    w.id === selected.id ? { ...w, config: { ...w.config, [k]: v } } : w
                  )
                  pushWidgets(updated)
                }}
              />
            )}

            {selected.type === 'image' && (
              <ImageProperties
                config={selected.config as ImageConfig}
                onUpdate={(k, v) => {
                  const updated = widgets.map(w =>
                    w.id === selected.id ? { ...w, config: { ...w.config, [k]: v } } : w
                  )
                  pushWidgets(updated)
                }}
              />
            )}

            {selected.type === 'text' && (
              <TextProperties
                config={selected.config as TextConfig}
                onUpdate={(k, v) => {
                  const updated = widgets.map(w =>
                    w.id === selected.id ? { ...w, config: { ...w.config, [k]: v } } : w
                  )
                  pushWidgets(updated)
                }}
              />
            )}

            {selected.type === 'divider' && (
              <DividerProperties
                config={selected.config as DividerConfig}
                onUpdate={(k, v) => {
                  const updated = widgets.map(w =>
                    w.id === selected.id ? { ...w, config: { ...w.config, [k]: v } } : w
                  )
                  pushWidgets(updated)
                }}
              />
            )}

            <div className="prop-section">
              <label>位置</label>
              <div className="prop-row">
                <span>X: <input type="number" value={Math.round(selected.x)} onChange={e => {
                  const updated = widgets.map(w => w.id === selected.id ? { ...w, x: Number(e.target.value) } : w)
                  pushWidgets(updated)
                }} /></span>
                <span>Y: <input type="number" value={Math.round(selected.y)} onChange={e => {
                  const updated = widgets.map(w => w.id === selected.id ? { ...w, y: Number(e.target.value) } : w)
                  pushWidgets(updated)
                }} /></span>
              </div>
            </div>

            <div className="prop-section">
              <label>尺寸</label>
              <div className="prop-row">
                <span>W: <input type="number" value={selected.width} onChange={e => {
                  const updated = widgets.map(w => w.id === selected.id ? { ...w, width: Number(e.target.value) } : w)
                  pushWidgets(updated)
                }} /></span>
                <span>H: <input type="number" value={selected.height} onChange={e => {
                  const updated = widgets.map(w => w.id === selected.id ? { ...w, height: Number(e.target.value) } : w)
                  pushWidgets(updated)
                }} /></span>
              </div>
            </div>

            <button className="btn-delete" onClick={() => handleDeleteWidget(selected.id)}>
              🗑 删除组件
            </button>
          </div>
        ) : (
          <p className="empty-hint">请选择组件</p>
        )}
      </aside>
    </div>
  )
}

function TableProperties({ config, onUpdate, onAddColumn, onRemoveColumn, onUpdateColumn }: {
  config: TableConfig
  onUpdate: (key: string, value: unknown) => void
  onAddColumn: () => void
  onRemoveColumn: (index: number) => void
  onUpdateColumn: (index: number, field: string, value: unknown) => void
}) {
  return (
    <>
      <div className="prop-section">
        <label>列配置</label>
        <div className="column-list">
          {config.columns?.map((col, i) => (
            <div key={i} className="column-row">
              <input
                placeholder="字段"
                value={col.field}
                onChange={e => onUpdateColumn(i, 'field', e.target.value)}
              />
              <input
                placeholder="标题"
                value={col.title}
                onChange={e => onUpdateColumn(i, 'title', e.target.value)}
              />
              <input
                placeholder="宽"
                type="number"
                value={col.width || ''}
                onChange={e => onUpdateColumn(i, 'width', Number(e.target.value))}
                style={{ width: 55 }}
              />
              <button className="btn-icon" onClick={() => onRemoveColumn(i)}>×</button>
            </div>
          ))}
        </div>
        <button className="btn-small" onClick={onAddColumn}>+ 添加列</button>
      </div>
      <div className="prop-section">
        <label>数据源</label>
        <select value={config.dataSource} onChange={e => onUpdate('dataSource', e.target.value)}>
          <option value="mock">模拟数据</option>
          <option value="api">API 接口</option>
        </select>
      </div>
    </>
  )
}

function ChartProperties({ config, onUpdate }: {
  config: ChartConfig
  onUpdate: (key: string, value: unknown) => void
}) {
  return (
    <>
      <div className="prop-section">
        <label>图表类型</label>
        <select value={config.chartType} onChange={e => onUpdate('chartType', e.target.value)}>
          <option value="bar">柱状图 (Bar)</option>
          <option value="line">折线图 (Line)</option>
          <option value="pie">饼图 (Pie)</option>
          <option value="scatter">散点图 (Scatter)</option>
        </select>
      </div>
      <div className="prop-section">
        <label>X 轴字段</label>
        <input type="text" value={config.xAxis} onChange={e => onUpdate('xAxis', e.target.value)} placeholder="例如: category" />
      </div>
      <div className="prop-section">
        <label>Y 轴字段</label>
        <input type="text" value={(config.yAxis || []).join(', ')} onChange={e => onUpdate('yAxis', e.target.value.split(',').map(s => s.trim()).filter(Boolean))} placeholder="例如: value,sales" />
      </div>
      <div className="prop-section">
        <label>数据源</label>
        <select value={config.dataSource} onChange={e => onUpdate('dataSource', e.target.value)}>
          <option value="mock">模拟数据</option>
          <option value="api">API 接口</option>
        </select>
      </div>
    </>
  )
}

function MetricCardProperties({ config, onUpdate }: {
  config: MetricCardConfig
  onUpdate: (key: string, value: unknown) => void
}) {
  return (
    <>
      <div className="prop-section">
        <label>指标名称</label>
        <input type="text" value={config.metric} onChange={e => onUpdate('metric', e.target.value)} placeholder="例如: total_users" />
      </div>
      <div className="prop-section">
        <label>显示标签</label>
        <input type="text" value={config.label} onChange={e => onUpdate('label', e.target.value)} placeholder="例如: 总用户数" />
      </div>
      <div className="prop-section">
        <label>数值</label>
        <input type="number" value={config.value} onChange={e => onUpdate('value', Number(e.target.value))} />
      </div>
      <div className="prop-section">
        <label>单位</label>
        <input type="text" value={config.unit || ''} onChange={e => onUpdate('unit', e.target.value)} placeholder="例如: 人、万元、%" />
      </div>
    </>
  )
}

function ImageProperties({ config, onUpdate }: {
  config: ImageConfig
  onUpdate: (key: string, value: unknown) => void
}) {
  return (
    <>
      <div className="prop-section">
        <label>图片地址</label>
        <input type="text" value={config.src} onChange={e => onUpdate('src', e.target.value)} placeholder="https://..." />
      </div>
      <div className="prop-section">
        <label>替代文本</label>
        <input type="text" value={config.alt || ''} onChange={e => onUpdate('alt', e.target.value)} placeholder="图片说明" />
      </div>
      <div className="prop-section">
        <label>填充方式</label>
        <select value={config.fit || 'contain'} onChange={e => onUpdate('fit', e.target.value)}>
          <option value="contain">适应 (contain)</option>
          <option value="cover">覆盖 (cover)</option>
          <option value="fill">拉伸 (fill)</option>
        </select>
      </div>
    </>
  )
}

function TextProperties({ config, onUpdate }: {
  config: TextConfig
  onUpdate: (key: string, value: unknown) => void
}) {
  return (
    <>
      <div className="prop-section">
        <label>文本内容</label>
        <textarea
          value={config.content}
          onChange={e => onUpdate('content', e.target.value)}
          placeholder="输入文本内容..."
          rows={3}
          style={{ width: '100%', padding: '8px', border: '1px solid #e0e0e0', borderRadius: '6px', fontSize: '13px', resize: 'vertical' }}
        />
      </div>
      <div className="prop-section">
        <label>字号</label>
        <input type="number" value={config.fontSize || 14} onChange={e => onUpdate('fontSize', Number(e.target.value))} min={10} max={72} />
      </div>
      <div className="prop-section">
        <label>字重</label>
        <select value={config.fontWeight || 'normal'} onChange={e => onUpdate('fontWeight', e.target.value)}>
          <option value="normal">正常</option>
          <option value="bold">粗体</option>
        </select>
      </div>
      <div className="prop-section">
        <label>文字颜色</label>
        <input type="color" value={config.color || '#333333'} onChange={e => onUpdate('color', e.target.value)} />
      </div>
      <div className="prop-section">
        <label>对齐</label>
        <select value={config.align || 'left'} onChange={e => onUpdate('align', e.target.value)}>
          <option value="left">居左</option>
          <option value="center">居中</option>
          <option value="right">居右</option>
        </select>
      </div>
    </>
  )
}

function DividerProperties({ config, onUpdate }: {
  config: DividerConfig
  onUpdate: (key: string, value: unknown) => void
}) {
  return (
    <>
      <div className="prop-section">
        <label>线条粗细 (px)</label>
        <input type="number" value={config.thickness || 1} onChange={e => onUpdate('thickness', Number(e.target.value))} min={1} max={10} />
      </div>
      <div className="prop-section">
        <label>线条颜色</label>
        <input type="color" value={config.color || '#e0e0e0'} onChange={e => onUpdate('color', e.target.value)} />
      </div>
      <div className="prop-section">
        <label>线条样式</label>
        <select value={config.style || 'solid'} onChange={e => onUpdate('style', e.target.value)}>
          <option value="solid">实线</option>
          <option value="dashed">虚线</option>
          <option value="dotted">点线</option>
        </select>
      </div>
    </>
  )
}
