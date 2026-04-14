import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import ReportRenderer, { renderToHTML, renderToJSON } from './ReportRenderer'
import type { ReportLayout } from '../../types'

const createMockLayout = (widgets: ReportLayout['widgets'] = []): ReportLayout => ({
  widgets,
  settings: { title: 'Test Report', width: 800, height: 600 },
})

describe('ReportRenderer', () => {
  it('should render empty message when report is null', () => {
    render(<ReportRenderer report={null} />)
    expect(screen.getByText('请先设计报表')).toBeInTheDocument()
  })

  it('should render report title', () => {
    render(<ReportRenderer report={createMockLayout()} />)
    expect(screen.getByText('Test Report')).toBeInTheDocument()
  })

  it('should render empty widgets message', () => {
    render(<ReportRenderer report={createMockLayout([])} />)
    expect(screen.getByText('报表还没有添加组件')).toBeInTheDocument()
  })

  it('should render table widget', () => {
    const layout = createMockLayout([{
      id: 't1',
      type: 'table',
      x: 0,
      y: 0,
      width: 300,
      height: 200,
      config: {
        columns: [
          { field: 'name', title: '名称', width: 100 },
          { field: 'value', title: '数值', width: 100 },
        ],
        dataSource: 'mock',
      },
    }])

    render(<ReportRenderer report={layout} />)
    expect(document.querySelector('table')).toBeInTheDocument()
  })

  it('should render chart widget placeholder', () => {
    const layout = createMockLayout([{
      id: 'c1',
      type: 'chart',
      x: 0,
      y: 0,
      width: 300,
      height: 200,
      config: {
        chartType: 'bar',
        xAxis: 'category',
        yAxis: ['value'],
        dataSource: 'mock',
      },
    }])

    render(<ReportRenderer report={layout} />)
    const canvas = document.querySelector('canvas')
    expect(canvas).toBeInTheDocument()
  })

  it('should render metric card widget', () => {
    const layout = createMockLayout([{
      id: 'm1',
      type: 'metric-card',
      x: 0,
      y: 0,
      width: 200,
      height: 100,
      config: {
        metric: 'users',
        label: '总用户数',
        value: 12345,
        unit: '人',
      },
    }])

    render(<ReportRenderer report={layout} />)
    expect(screen.getByText('总用户数')).toBeInTheDocument()
    expect(screen.getByText('12,345')).toBeInTheDocument()
    expect(screen.getByText('人')).toBeInTheDocument()
  })

  it('should render image widget with placeholder', () => {
    const layout = createMockLayout([{
      id: 'i1',
      type: 'image',
      x: 0,
      y: 0,
      width: 200,
      height: 150,
      config: {
        src: '',
        alt: 'test',
        fit: 'contain',
      },
    }])

    render(<ReportRenderer report={layout} />)
    expect(screen.getByText('📷 点击设置图片地址')).toBeInTheDocument()
  })

  it('should render text widget', () => {
    const layout = createMockLayout([{
      id: 'tx1',
      type: 'text',
      x: 0,
      y: 0,
      width: 200,
      height: 100,
      config: {
        content: 'Hello World',
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333',
        align: 'left',
      },
    }])

    render(<ReportRenderer report={layout} />)
    expect(screen.getByText('Hello World')).toBeInTheDocument()
  })

  it('should render divider widget', () => {
    const layout = createMockLayout([{
      id: 'd1',
      type: 'divider',
      x: 0,
      y: 0,
      width: 200,
      height: 10,
      config: {
        thickness: 2,
        color: '#ccc',
        style: 'solid',
      },
    }])

    render(<ReportRenderer report={layout} />)
    const divider = document.querySelector('.divider-renderer')
    expect(divider).toBeInTheDocument()
  })

  it('should render unknown widget type gracefully', () => {
    const layout = createMockLayout([{
      id: 'u1',
      type: 'unknown' as any,
      x: 0,
      y: 0,
      width: 100,
      height: 100,
      config: {},
    }])

    render(<ReportRenderer report={layout} />)
    expect(screen.getByText('未知组件类型')).toBeInTheDocument()
  })
})

describe('renderToHTML', () => {
  it('should generate HTML string', () => {
    const layout = createMockLayout([{
      id: 't1',
      type: 'table',
      x: 0,
      y: 0,
      width: 300,
      height: 200,
      config: {
        columns: [
          { field: 'name', title: '名称', width: 100 },
        ],
        dataSource: 'mock',
      },
    }])

    const html = renderToHTML(layout)
    expect(html).toContain('<!DOCTYPE html>')
    expect(html).toContain('Test Report')
    expect(html).toContain('<table>')
  })
})

describe('renderToJSON', () => {
  it('should generate JSON string', () => {
    const layout = createMockLayout()
    const json = renderToJSON(layout)
    expect(() => JSON.parse(json)).not.toThrow()
    const parsed = JSON.parse(json)
    expect(parsed.settings.title).toBe('Test Report')
  })
})
