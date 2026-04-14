import { useState } from 'react'
import type { ReportSettings } from '../../types'

interface Props {
  settings: ReportSettings
  onChange: (settings: ReportSettings) => void
  onClose: () => void
}

export default function ReportSettingsModal({ settings, onChange, onClose }: Props) {
  const [form, setForm] = useState({ ...settings })

  const handleSave = () => {
    onChange({ ...form, title: form.title.trim() || '未命名报表' })
    onClose()
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h3>报表设置</h3>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          <div className="form-group">
            <label>报表标题</label>
            <input
              type="text"
              value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
              placeholder="输入报表标题"
              autoFocus
            />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>画布宽度 (px)</label>
              <input
                type="number"
                value={form.width}
                onChange={e => setForm(f => ({ ...f, width: Number(e.target.value) }))}
                min={400}
                max={2000}
              />
            </div>
            <div className="form-group">
              <label>画布高度 (px)</label>
              <input
                type="number"
                value={form.height}
                onChange={e => setForm(f => ({ ...f, height: Number(e.target.value) }))}
                min={300}
                max={2000}
              />
            </div>
          </div>
        </div>
        <div className="modal-footer">
          <button className="btn-cancel" onClick={onClose}>取消</button>
          <button className="btn-save" onClick={handleSave}>保存</button>
        </div>
      </div>
    </div>
  )
}
