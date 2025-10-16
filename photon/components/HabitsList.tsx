"use client"

import { Habit } from "@/types/habits"
import { useEffect, useState } from "react"

interface HabitsListProps {
  habits: Habit[]
  onHabitClick?: (habit: Habit) => void
}

export default function HabitsList({ habits, onHabitClick }: HabitsListProps) {
  const [isVisible, setIsVisible] = useState(false)

  useEffect(() => {
    // Trigger animation after component mounts
    const timer = setTimeout(() => setIsVisible(true), 100)
    return () => clearTimeout(timer)
  }, [])

  return (
    <div className="bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden">
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 px-4 py-4">
        <h3 className="text-white text-lg font-semibold">My Habits</h3>
        <p className="text-blue-100 text-sm">
          {habits.length} habit{habits.length !== 1 ? 's' : ''}
        </p>
      </div>

      {/* Habits List */}
      <div className="p-4 space-y-3 max-h-[400px] lg:max-h-[600px] overflow-y-auto">
        {habits.length === 0 ? (
          <div className="text-center text-gray-400 py-8 animate-fade-in">
            <svg 
              className="w-12 h-12 mx-auto mb-2 opacity-50" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" 
              />
            </svg>
            <p className="text-sm">No habits yet</p>
          </div>
        ) : (
          habits.map((habit, index) => (
            <div
              key={habit.id}
              onClick={() => onHabitClick?.(habit)}
              className={`
                group cursor-pointer hover:scale-[1.02] transition-all duration-300
                ${isVisible ? 'animate-slide-up-fade-in' : 'opacity-0 translate-y-4'}
              `}
              style={{
                animationDelay: `${index * 100}ms`,
                animationFillMode: 'both'
              }}
            >
              <div
                className="rounded-lg p-3 border-l-4 shadow-sm hover:shadow-md transition-all"
                style={{
                  backgroundColor: `${habit.color}15`,
                  borderLeftColor: habit.color,
                }}
              >
                {/* Habit Name */}
                <div className="flex items-center justify-between mb-2">
                  <h4 
                    className="font-semibold text-sm truncate flex-1 font-typewriter"
                    style={{ color: habit.color }}
                  >
                    {habit.name}
                  </h4>
                  <div 
                    className="w-3 h-3 rounded-full flex-shrink-0 ml-2"
                    style={{ backgroundColor: habit.color }}
                  />
                </div>

                {/* Days of Week */}
                <div className="flex items-center gap-1 text-xs text-gray-600 mb-2">
                  <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path 
                      strokeLinecap="round" 
                      strokeLinejoin="round" 
                      strokeWidth={2} 
                      d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" 
                    />
                  </svg>
                  <span className="truncate">{habit.daysOfWeek}</span>
                </div>

                {/* Streak Status with pulse animation - only show if streak > 0 */}
                {habit.streakStatus > 0 && (
                  <div className="flex items-center gap-1 text-xs text-gray-700">
                    <span className="inline-block animate-pulse-subtle">🔥</span>
                    <span className="font-medium">{habit.streakStatus}</span>
                  </div>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

