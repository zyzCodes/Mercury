"use client"

import { useMemo } from "react"
import TaskCard from "./TaskCard"
import { Task } from "@/types/habits"

interface WeeklyCalendarProps {
  /**
   * Optional starting date for the week. Defaults to current week.
   */
  startDate?: Date
  /**
   * Callback when a day is clicked
   */
  onDayClick?: (date: Date) => void
  /**
   * Tasks grouped by day
   */
  tasks?: Record<string, Task[]>
  /**
   * Callback when a task is clicked
   */
  onTaskClick?: (task: Task) => void
  /**
   * Callback when week navigation changes (returns the new week start date)
   */
  onWeekChange?: (startDate: Date) => void
}

interface DayInfo {
  date: Date
  dayName: string
  dayNameFull: string
  dayNumber: number
  isToday: boolean
  isWeekend: boolean
}

export default function WeeklyCalendar({
  startDate,
  onDayClick,
  tasks = {},
  onTaskClick,
  onWeekChange
}: WeeklyCalendarProps) {
  const weekDays = useMemo(() => {
    const days: DayInfo[] = []
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    
    // Get the start of the week (Sunday)
    const start = startDate ? new Date(startDate) : new Date()
    const dayOfWeek = start.getDay()
    start.setDate(start.getDate() - dayOfWeek) // Start from Sunday
    start.setHours(0, 0, 0, 0)

    // Generate 7 days (Sun - Sat)
    const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
    const dayNamesFull = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
    
    for (let i = 0; i < 7; i++) {
      const currentDate = new Date(start)
      currentDate.setDate(start.getDate() + i)
      
      const isToday = currentDate.getTime() === today.getTime()
      const isWeekend = i === 0 || i === 6 // Sunday or Saturday
      
      days.push({
        date: currentDate,
        dayName: dayNames[i],
        dayNameFull: dayNamesFull[i],
        dayNumber: currentDate.getDate(),
        isToday,
        isWeekend
      })
    }
    
    return days
  }, [startDate])

  const currentMonthYear = useMemo(() => {
    return weekDays[0].date.toLocaleDateString('en-US', { 
      month: 'long', 
      year: 'numeric' 
    })
  }, [weekDays])

  const handleDayClick = (day: DayInfo) => {
    if (onDayClick) {
      onDayClick(day.date)
    }
  }

  const getTasksForDay = (date: Date) => {
    const dateKey = date.toISOString().split('T')[0]
    return tasks[dateKey] || []
  }

  const handlePreviousWeek = () => {
    const currentStart = weekDays[0].date
    const newStart = new Date(currentStart)
    newStart.setDate(currentStart.getDate() - 7)
    onWeekChange?.(newStart)
  }

  const handleNextWeek = () => {
    const currentStart = weekDays[0].date
    const newStart = new Date(currentStart)
    newStart.setDate(currentStart.getDate() + 7)
    onWeekChange?.(newStart)
  }

  const handleToday = () => {
    const now = new Date()
    const dayOfWeek = now.getDay()
    const startOfWeek = new Date(now)
    startOfWeek.setDate(now.getDate() - dayOfWeek)
    startOfWeek.setHours(0, 0, 0, 0)
    onWeekChange?.(startOfWeek)
  }

  return (
    <div className="bg-white rounded-xl shadow-md overflow-hidden border border-gray-200">
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 px-4 sm:px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-white text-lg font-semibold">{currentMonthYear}</h3>
            <p className="text-blue-100 text-sm">
              Week of {weekDays[0].date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
            </p>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePreviousWeek}
              className="p-2 rounded-lg bg-white/10 hover:bg-white/20 transition text-white"
              aria-label="Previous week"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <button
              onClick={handleToday}
              className="hidden sm:block px-3 py-1.5 rounded-lg bg-white/10 hover:bg-white/20 transition text-white text-sm font-medium"
            >
              Today
            </button>
            <button
              onClick={handleNextWeek}
              className="p-2 rounded-lg bg-white/10 hover:bg-white/20 transition text-white"
              aria-label="Next week"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      {/* Weekly Columnar View */}
      <div className="overflow-x-auto">
        <div className="grid grid-cols-7 w-full min-w-[700px]">
          {weekDays.map((day, index) => (
            <div
              key={index}
              className={`
                border-r last:border-r-0
                ${day.isWeekend ? 'bg-gray-50' : 'bg-white'}
                ${index === 0 ? '' : 'border-l border-gray-200'}
              `}
            >
              {/* Day Header */}
              <div
                onClick={() => handleDayClick(day)}
                className={`
                  px-2 py-3 border-b-2 cursor-pointer transition-colors
                  ${day.isToday 
                    ? 'bg-blue-50 border-b-blue-500' 
                    : day.isWeekend
                      ? 'bg-gray-100 border-b-gray-300'
                      : 'bg-white border-b-gray-200 hover:bg-gray-50'
                  }
                `}
              >
                <div className="flex flex-col items-center">
                  <span className={`
                    text-xs font-semibold uppercase tracking-wide
                    ${day.isToday ? 'text-blue-600' : 'text-gray-600'}
                  `}>
                    {day.dayName}
                  </span>
                  <div className="flex items-center justify-center mt-1" style={{ minHeight: '40px' }}>
                    {day.isToday ? (
                      <div className={`
                        w-8 h-8 sm:w-10 sm:h-10 rounded-full 
                        bg-gradient-to-br from-red-500 to-pink-600
                        flex items-center justify-center
                        shadow-md
                      `}>
                        <span className="text-white text-base sm:text-lg font-bold">
                          {day.dayNumber}
                        </span>
                      </div>
                    ) : (
                      <span className={`
                        text-xl sm:text-2xl font-bold
                        ${day.isWeekend ? 'text-gray-600' : 'text-gray-900'}
                      `}>
                        {day.dayNumber}
                      </span>
                    )}
                  </div>
                </div>
              </div>

              {/* Tasks Column Area */}
              <div className="p-2 min-h-[400px] space-y-2">
                {getTasksForDay(day.date).length > 0 ? (
                  getTasksForDay(day.date).map((task) => (
                    <TaskCard
                      key={task.id}
                      name={task.name}
                      color={task.color}
                      completed={task.completed}
                      onClick={() => onTaskClick?.(task)}
                    />
                  ))
                ) : (
                  <div className="text-center text-gray-400 text-xs mt-4">
                    No tasks
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Footer Info */}
      <div className="px-4 sm:px-6 py-3 bg-gray-50 border-t border-gray-200">
        <div className="flex flex-wrap items-center justify-center gap-3 text-xs text-gray-600">
          <div className="flex items-center gap-1">
            <div className="w-3 h-3 rounded-full bg-gradient-to-br from-red-500 to-pink-600" />
            <span>Today</span>
          </div>
          <div className="flex items-center gap-1">
            <div className="w-3 h-3 rounded-full bg-gray-300" />
            <span>Weekend</span>
          </div>
        </div>
      </div>
    </div>
  )
}
