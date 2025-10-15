"use client"

import { useMemo } from "react"

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
   * Tasks grouped by day (for future implementation)
   */
  tasks?: Record<string, Array<{ id: string; title: string; color?: string }>>
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
  tasks = {}
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

  return (
    <div className="bg-white rounded-xl shadow-md overflow-hidden border border-gray-200">
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 px-4 sm:px-6 py-4">
        <h3 className="text-white text-lg font-semibold">{currentMonthYear}</h3>
        <p className="text-blue-100 text-sm">
          Week of {weekDays[0].date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
        </p>
      </div>

      {/* Weekly Columnar View */}
      <div className="overflow-x-auto">
        <div className="min-w-full inline-flex">
          {weekDays.map((day, index) => (
            <div
              key={index}
              className={`
                flex-1 min-w-[100px] xs:min-w-[120px] sm:min-w-[140px] border-r last:border-r-0
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
                    <div
                      key={task.id}
                      className={`
                        p-2 rounded-lg text-sm cursor-pointer
                        transition-all hover:shadow-md
                        ${task.color || 'bg-blue-100 text-blue-800 border border-blue-200'}
                      `}
                    >
                      {task.title}
                    </div>
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
        <div className="flex flex-wrap items-center justify-between gap-2 text-xs text-gray-600">
          <div className="flex items-center gap-2">
            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd" />
            </svg>
            <span className="hidden sm:inline">Click any day to add tasks</span>
            <span className="sm:hidden">Tap day to add tasks</span>
          </div>
          
          <div className="flex items-center gap-3">
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
    </div>
  )
}
