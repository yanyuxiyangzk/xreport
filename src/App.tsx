import { useState, useCallback, useEffect } from 'react'
import ReportDesigner from './components/designer/ReportDesigner'
import ReportRenderer from './components/renderer/ReportRenderer'
import ReportExporter from './components/exporter/ReportExporter'
import ReportSettingsModal from './components/designer/ReportSettingsModal'
import ReportList from './components/home/ReportList'
import { useReportList, type SavedReport } from './hooks/useReportList'
import type { ReportLayout } from './types'

type View = 'home' | 'design' | 'preview' | 'export'

function App() {
  const { reports, saveReport, deleteReport, createReport } = useReportList()
  const [currentReportId, setCurrentReportId] = useState<string | null>(null)
  const [view, setView] = useState<View>('home')
  const [report, setReport] = useState<ReportLayout | null>(null)
  const [showSettings, setShowSettings] = useState(false)
  const [saveStatus, setSaveStatus] = useState<'saved' | 'saving' | 'unsaved'>('saved')
  const [reportTitle, setReportTitle] = useState('')

  // Auto-save on report change
  useEffect(() => {
    if (!currentReportId || !report) return
    setSaveStatus('unsaved')
    const timer = setTimeout(() => {
      setSaveStatus('saving')
      saveReport(currentReportId, reportTitle || report.settings.title, report)
      setSaveStatus('saved')
    }, 800)
    return () => clearTimeout(timer)
  }, [report, currentReportId, reportTitle, saveReport])

  const handleLoadReport = useCallback((saved: SavedReport) => {
    setReport(saved.data)
    setReportTitle(saved.title)
    setCurrentReportId(saved.id)
    setView('design')
  }, [])

  const handleNewReport = useCallback(() => {
    const created = createReport()
    setReport(created.data)
    setReportTitle(created.title)
    setCurrentReportId(created.id)
    setView('design')
  }, [createReport])

  const handleBackHome = useCallback(() => {
    if (report && currentReportId) {
      saveReport(currentReportId, reportTitle || report.settings.title, report)
    }
    setCurrentReportId(null)
    setReport(null)
    setReportTitle('')
    setView('home')
  }, [report, currentReportId, reportTitle, saveReport])

  const handleSettingsChange = useCallback((settings: ReportLayout['settings']) => {
    if (!report) return
    const updated = { ...report, settings }
    setReport(updated)
    setReportTitle(settings.title)
  }, [report])

  const saveStatusText = {
    saved: '✓ 已保存',
    saving: '保存中...',
    unsaved: '● 未保存',
  }[saveStatus]

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-left">
          {view !== 'home' && (
            <button className="btn-home" onClick={handleBackHome} title="返回列表">
              ←
            </button>
          )}
          <h1>XReport 报表设计器</h1>
        </div>

        {view !== 'home' && (
          <>
            <button
              className="report-title-btn"
              onClick={() => setShowSettings(true)}
              title="点击修改报表设置"
            >
              {reportTitle || report?.settings.title || '未命名报表'}
            </button>

            <nav className="header-nav">
              <button
                onClick={() => setView('design')}
                className={view === 'design' ? 'active' : ''}
              >
                设计
              </button>
              <button
                onClick={() => setView('preview')}
                className={view === 'preview' ? 'active' : ''}
              >
                预览
              </button>
              <button
                onClick={() => setView('export')}
                className={view === 'export' ? 'active' : ''}
              >
                导出
              </button>
            </nav>

            <span className={`save-status ${saveStatus}`}>{saveStatusText}</span>
          </>
        )}
      </header>

      <main>
        {view === 'home' && (
          <ReportList
            reports={reports}
            onLoad={handleLoadReport}
            onDelete={deleteReport}
            onCreate={handleNewReport}
          />
        )}

        {view === 'design' && report && (
          <ReportDesigner
            report={report}
            onChange={setReport}
          />
        )}

        {view === 'preview' && (
          <ReportRenderer report={report} />
        )}

        {view === 'export' && (
          <ReportExporter report={report} />
        )}
      </main>

      {showSettings && report && (
        <ReportSettingsModal
          settings={report.settings}
          onChange={handleSettingsChange}
          onClose={() => setShowSettings(false)}
        />
      )}
    </div>
  )
}

export default App
