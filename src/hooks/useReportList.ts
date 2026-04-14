import { useState, useCallback, useEffect } from 'react'
import type { ReportLayout } from '../types'

export interface SavedReport {
  id: string
  title: string
  updatedAt: number
  data: ReportLayout
}

const STORAGE_KEY = 'xreport_reports'

function loadReports(): SavedReport[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

function saveReports(reports: SavedReport[]) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(reports))
}

export function useReportList() {
  const [reports, setReports] = useState<SavedReport[]>(() => loadReports())

  useEffect(() => {
    saveReports(reports)
  }, [reports])

  const saveReport = useCallback((id: string, title: string, data: ReportLayout) => {
    setReports(prev => {
      const existing = prev.findIndex(r => r.id === id)
      if (existing >= 0) {
        const updated = [...prev]
        updated[existing] = { ...updated[existing], title, updatedAt: Date.now(), data }
        return updated
      } else {
        return [...prev, { id, title, updatedAt: Date.now(), data }]
      }
    })
  }, [])

  const deleteReport = useCallback((id: string) => {
    setReports(prev => prev.filter(r => r.id !== id))
  }, [])

  const createReport = useCallback((title = '未命名报表'): SavedReport => {
    const report: SavedReport = {
      id: `report-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
      title,
      updatedAt: Date.now(),
      data: {
        widgets: [],
        settings: { title, width: 800, height: 600 },
      },
    }
    setReports(prev => [...prev, report])
    return report
  }, [])

  return { reports, saveReport, deleteReport, createReport }
}
