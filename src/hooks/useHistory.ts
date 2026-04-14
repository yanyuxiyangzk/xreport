import { useState, useCallback, useRef } from 'react'

export function useHistory<T>(initial: T) {
  const [state, setState] = useState<T>(initial)
  const historyRef = useRef<T[]>([initial])
  const indexRef = useRef(0)

  const push = useCallback((newState: T) => {
    const currentIndex = indexRef.current
    const history = historyRef.current

    // 丢弃当前位置之后的历史（新分支）
    const newHistory = history.slice(0, currentIndex + 1)
    newHistory.push(newState)

    // 最多保留 50 步
    if (newHistory.length > 50) {
      newHistory.shift()
    } else {
      indexRef.current = newHistory.length - 1
    }

    historyRef.current = newHistory
    setState(newState)
  }, [])

  const undo = useCallback(() => {
    if (indexRef.current <= 0) return
    indexRef.current -= 1
    setState(historyRef.current[indexRef.current])
  }, [])

  const redo = useCallback(() => {
    if (indexRef.current >= historyRef.current.length - 1) return
    indexRef.current += 1
    setState(historyRef.current[indexRef.current])
  }, [])

  const canUndo = indexRef.current > 0
  const canRedo = indexRef.current < historyRef.current.length - 1

  return { state, setState, push, undo, redo, canUndo, canRedo }
}
