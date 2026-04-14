import { describe, it, expect, beforeEach } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useReportList } from './useReportList'
import type { ReportLayout } from '../types'

const createMockLayout = (widgets: number = 0): ReportLayout => ({
  widgets: Array(widgets).fill(null).map((_, i) => ({
    id: `widget-${i}`,
    type: 'table' as const,
    x: i * 10,
    y: i * 10,
    width: 100,
    height: 100,
    config: { columns: [], dataSource: 'mock' },
  })),
  settings: { title: 'Test Report', width: 800, height: 600 },
})

describe('useReportList', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('should initialize with empty reports', () => {
    const { result } = renderHook(() => useReportList())
    expect(result.current.reports).toEqual([])
  })

  it('should create a new report', () => {
    const { result } = renderHook(() => useReportList())

    act(() => {
      const report = result.current.createReport('My Report')
      expect(report.title).toBe('My Report')
      expect(report.data.widgets).toEqual([])
    })

    expect(result.current.reports.length).toBe(1)
  })

  it('should create report with default title', () => {
    const { result } = renderHook(() => useReportList())

    act(() => {
      result.current.createReport()
    })

    expect(result.current.reports[0].title).toBe('未命名报表')
  })

  it('should save a report', () => {
    const { result } = renderHook(() => useReportList())

    let createdReport
    act(() => {
      createdReport = result.current.createReport('Original Title')
    })

    act(() => {
      result.current.saveReport(
        createdReport!.id,
        'Updated Title',
        createMockLayout(2)
      )
    })

    const saved = result.current.reports.find(r => r.id === createdReport!.id)
    expect(saved?.title).toBe('Updated Title')
    expect(saved?.data.widgets.length).toBe(2)
  })

  it('should update existing report instead of creating new one', () => {
    const { result } = renderHook(() => useReportList())

    act(() => {
      result.current.createReport('First')
    })

    const firstReport = result.current.reports[0]
    const initialLength = result.current.reports.length

    act(() => {
      result.current.saveReport(firstReport.id, 'Updated', createMockLayout())
    })

    expect(result.current.reports.length).toBe(initialLength)
  })

  it('should delete a report', () => {
    const { result } = renderHook(() => useReportList())

    let createdReport
    act(() => {
      createdReport = result.current.createReport('To Delete')
    })

    const reportId = createdReport!.id
    expect(result.current.reports.length).toBe(1)

    act(() => {
      result.current.deleteReport(reportId)
    })

    expect(result.current.reports.length).toBe(0)
    expect(result.current.reports.find(r => r.id === reportId)).toBeUndefined()
  })

  it('should persist reports to localStorage', () => {
    const { result } = renderHook(() => useReportList())

    act(() => {
      result.current.createReport('Persisted Report')
    })

    const stored = localStorage.getItem('xreport_reports')
    expect(stored).toBeTruthy()
    const parsed = JSON.parse(stored!)
    expect(parsed.length).toBe(1)
    expect(parsed[0].title).toBe('Persisted Report')
  })

  it('should load reports from localStorage on init', () => {
    const existingReports = [{
      id: 'existing-1',
      title: 'Loaded Report',
      updatedAt: Date.now(),
      data: createMockLayout(3),
    }]

    localStorage.setItem('xreport_reports', JSON.stringify(existingReports))

    const { result } = renderHook(() => useReportList())
    expect(result.current.reports.length).toBe(1)
    expect(result.current.reports[0].title).toBe('Loaded Report')
  })
})
