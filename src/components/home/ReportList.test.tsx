import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import ReportList from './ReportList'
import type { SavedReport } from '../../hooks/useReportList'
import type { ReportLayout } from '../../types'

const createMockReport = (overrides: Partial<SavedReport> = {}): SavedReport => ({
  id: 'report-1',
  title: 'Test Report',
  updatedAt: Date.now(),
  data: {
    widgets: [],
    settings: { title: 'Test', width: 800, height: 600 },
  },
  ...overrides,
})

const createMockLayout = (widgetCount: number): ReportLayout => ({
  widgets: Array(widgetCount).fill(null).map((_, i) => ({
    id: `widget-${i}`,
    type: 'table' as const,
    x: i * 10,
    y: i * 10,
    width: 100,
    height: 100,
    config: { columns: [], dataSource: 'mock' },
  })),
  settings: { title: 'Test', width: 800, height: 600 },
})

describe('ReportList', () => {
  it('should render empty state when no reports', () => {
    render(
      <ReportList
        reports={[]}
        onLoad={() => {}}
        onDelete={() => {}}
        onCreate={() => {}}
      />
    )

    expect(screen.getByText('还没有报表')).toBeInTheDocument()
    expect(screen.getByText('+ 新建报表')).toBeInTheDocument()
  })

  it('should render report cards when reports exist', () => {
    const reports = [
      createMockReport({ id: '1', title: 'Report A' }),
      createMockReport({ id: '2', title: 'Report B' }),
    ]

    render(
      <ReportList
        reports={reports}
        onLoad={() => {}}
        onDelete={() => {}}
        onCreate={() => {}}
      />
    )

    expect(screen.getByText('Report A')).toBeInTheDocument()
    expect(screen.getByText('Report B')).toBeInTheDocument()
  })

  it('should show widget count in card', () => {
    const reports = [
      createMockReport({
        id: '1',
        data: createMockLayout(3),
      }),
    ]

    render(
      <ReportList
        reports={reports}
        onLoad={() => {}}
        onDelete={() => {}}
        onCreate={() => {}}
      />
    )

    expect(screen.getByText(/3 个组件/)).toBeInTheDocument()
  })

  it('should show "空白报表" for report with no widgets', () => {
    const reports = [
      createMockReport({
        id: '1',
        data: createMockLayout(0),
      }),
    ]

    render(
      <ReportList
        reports={reports}
        onLoad={() => {}}
        onDelete={() => {}}
        onCreate={() => {}}
      />
    )

    expect(screen.getByText('空白报表')).toBeInTheDocument()
  })

  it('should call onCreate when "新建报表" button is clicked', () => {
    const onCreate = vi.fn()

    render(
      <ReportList
        reports={[]}
        onLoad={() => {}}
        onDelete={() => {}}
        onCreate={onCreate}
      />
    )

    fireEvent.click(screen.getByText('+ 新建报表'))
    expect(onCreate).toHaveBeenCalledTimes(1)
  })

  it('should call onLoad when "打开" button is clicked', () => {
    const onLoad = vi.fn()
    const reports = [createMockReport({ id: '1', title: 'Load Me' })]

    render(
      <ReportList
        reports={reports}
        onLoad={onLoad}
        onDelete={() => {}}
        onCreate={() => {}}
      />
    )

    fireEvent.click(screen.getByText('打开'))
    expect(onLoad).toHaveBeenCalledWith(reports[0])
  })

  it('should call onDelete when "删除" button is clicked', () => {
    const onDelete = vi.fn()
    const reports = [createMockReport({ id: '1', title: 'Delete Me' })]

    render(
      <ReportList
        reports={reports}
        onLoad={() => {}}
        onDelete={onDelete}
        onCreate={() => {}}
      />
    )

    vi.stubGlobal('confirm', () => true)
    fireEvent.click(screen.getByText('删除'))
    expect(onDelete).toHaveBeenCalledWith('1')
    vi.unstubAllGlobals()
  })

  it('should sort reports by updatedAt descending', () => {
    const now = Date.now()
    const reports = [
      createMockReport({ id: '1', title: 'Older', updatedAt: now - 1000 }),
      createMockReport({ id: '2', title: 'Newer', updatedAt: now }),
    ]

    render(
      <ReportList
        reports={reports}
        onLoad={() => {}}
        onDelete={() => {}}
        onCreate={() => {}}
      />
    )

    // Verify both reports are displayed
    expect(screen.getByText('Older')).toBeInTheDocument()
    expect(screen.getByText('Newer')).toBeInTheDocument()
  })
})
