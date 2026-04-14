import { describe, it, expect, beforeEach } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useHistory } from './useHistory'

describe('useHistory', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('should initialize with initial state', () => {
    const { result } = renderHook(() => useHistory(['initial']))
    expect(result.current.state).toEqual(['initial'])
    expect(result.current.canUndo).toBe(false)
    expect(result.current.canRedo).toBe(false)
  })

  it('should push new state and enable undo', () => {
    const { result } = renderHook(() => useHistory(['initial']))

    act(() => {
      result.current.push(['second'])
    })

    expect(result.current.state).toEqual(['second'])
    expect(result.current.canUndo).toBe(true)
    expect(result.current.canRedo).toBe(false)
  })

  it('should undo to previous state', () => {
    const { result } = renderHook(() => useHistory(['initial']))

    act(() => {
      result.current.push(['second'])
    })
    expect(result.current.state).toEqual(['second'])

    act(() => {
      result.current.undo()
    })
    expect(result.current.state).toEqual(['initial'])
    expect(result.current.canUndo).toBe(false)
    expect(result.current.canRedo).toBe(true)
  })

  it('should redo to next state', () => {
    const { result } = renderHook(() => useHistory(['initial']))

    act(() => {
      result.current.push(['second'])
    })
    act(() => {
      result.current.undo()
    })
    expect(result.current.state).toEqual(['initial'])

    act(() => {
      result.current.redo()
    })
    expect(result.current.state).toEqual(['second'])
    expect(result.current.canRedo).toBe(false)
  })

  it('should discard future history when pushing new state', () => {
    const { result } = renderHook(() => useHistory(['initial']))

    act(() => {
      result.current.push(['second'])
    })
    act(() => {
      result.current.undo()
    })
    expect(result.current.canRedo).toBe(true)

    act(() => {
      result.current.push(['third'])
    })
    expect(result.current.state).toEqual(['third'])
    expect(result.current.canRedo).toBe(false)
  })

  it('should limit history to 50 steps', () => {
    const { result } = renderHook(() => useHistory(['step0']))

    for (let i = 1; i <= 55; i++) {
      act(() => {
        result.current.push([`step${i}`])
      })
    }

    const historyLength = result.current.state
    expect(historyLength).toEqual(['step55'])
  })

  it('should not undo when at beginning', () => {
    const { result } = renderHook(() => useHistory(['initial']))

    act(() => {
      result.current.undo()
    })

    expect(result.current.state).toEqual(['initial'])
    expect(result.current.canUndo).toBe(false)
  })

  it('should not redo when at end', () => {
    const { result } = renderHook(() => useHistory(['initial']))

    act(() => {
      result.current.push(['second'])
    })

    act(() => {
      result.current.redo()
    })

    expect(result.current.state).toEqual(['second'])
    expect(result.current.canRedo).toBe(false)
  })
})
