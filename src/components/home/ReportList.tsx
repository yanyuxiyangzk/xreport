import type { SavedReport } from '../../hooks/useReportList'

interface Props {
  reports: SavedReport[]
  onLoad: (report: SavedReport) => void
  onDelete: (id: string) => void
  onCreate: () => void
}

export default function ReportList({ reports, onLoad, onDelete, onCreate }: Props) {
  const sorted = [...reports].sort((a, b) => b.updatedAt - a.updatedAt)

  const formatDate = (ts: number) => {
    const d = new Date(ts)
    return d.toLocaleString('zh-CN', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  return (
    <div className="report-list">
      <div className="list-header">
        <h2>我的报表</h2>
        <button className="btn-create" onClick={onCreate}>
          + 新建报表
        </button>
      </div>

      {sorted.length === 0 ? (
        <div className="list-empty">
          <div className="empty-icon">📊</div>
          <p>还没有报表</p>
          <button className="btn-create" onClick={onCreate}>创建第一个报表</button>
        </div>
      ) : (
        <div className="list-grid">
          {sorted.map(report => (
            <div key={report.id} className="report-card">
              <div className="card-preview">
                <div className="preview-placeholder">
                  {report.data.widgets.length === 0
                    ? '空白报表'
                    : `${report.data.widgets.length} 个组件`}
                </div>
              </div>
              <div className="card-body">
                <h4 className="card-title">{report.title || '未命名报表'}</h4>
                <p className="card-meta">
                  {formatDate(report.updatedAt)} · {report.data.widgets.length} 组件
                </p>
              </div>
              <div className="card-actions">
                <button className="btn-open" onClick={() => onLoad(report)}>
                  打开
                </button>
                <button
                  className="btn-delete-card"
                  onClick={() => {
                    if (confirm(`确定删除"${report.title || '未命名报表'}"吗？`)) {
                      onDelete(report.id)
                    }
                  }}
                >
                  删除
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
