"use client"

import { useRouter } from "next/navigation"
import { useEffect, useState, useMemo } from "react"
import { useUser } from "@/hooks/useUser"
import { getGoalsByUserId, type Goal } from "@/lib/goals-api"
import { getHabitsByUserId } from "@/lib/habits-api"
import { getTasksByDateRange, toggleTaskCompletion } from "@/lib/tasks-api"
import { Habit, Task } from "@/types/habits"
import Navbar from "@/components/Navbar"
import GoalCard from "@/components/GoalCard"
import WeeklyCalendar from "@/components/WeeklyCalendar"
import HabitsList from "@/components/HabitsList"

export default function DashboardPage() {
  const { user, loading, error, isAuthenticated } = useUser()
  const router = useRouter()
  const [goals, setGoals] = useState<Goal[]>([])
  const [goalsLoading, setGoalsLoading] = useState(true)
  const [goalsError, setGoalsError] = useState<string | null>(null)
  const [habits, setHabits] = useState<Habit[]>([])
  const [habitsLoading, setHabitsLoading] = useState(true)
  const [tasks, setTasks] = useState<Task[]>([])
  const [tasksLoading, setTasksLoading] = useState(true)
  const [currentWeekStart, setCurrentWeekStart] = useState<Date>(() => {
    const now = new Date()
    const dayOfWeek = now.getDay()
    const startOfWeek = new Date(now)
    startOfWeek.setDate(now.getDate() - dayOfWeek)
    startOfWeek.setHours(0, 0, 0, 0)
    return startOfWeek
  })

  useEffect(() => {
    if (loading) return // Still loading
    if (!isAuthenticated) router.push('/login') // Not authenticated
  }, [isAuthenticated, loading, router])

  // Fetch user's goals
  useEffect(() => {
    if (user) {
      setGoalsLoading(true)
      getGoalsByUserId(user.id)
        .then(setGoals)
        .catch((err) => setGoalsError(err.message))
        .finally(() => setGoalsLoading(false))
    }
  }, [user])

  // Fetch user's habits
  useEffect(() => {
    if (user) {
      setHabitsLoading(true)
      getHabitsByUserId(user.id)
        .then(setHabits)
        .catch((err) => console.error('Failed to load habits:', err))
        .finally(() => setHabitsLoading(false))
    }
  }, [user])

  // Fetch current week's tasks
  useEffect(() => {
    if (user) {
      setTasksLoading(true)
      const endOfWeek = new Date(currentWeekStart)
      endOfWeek.setDate(currentWeekStart.getDate() + 6)
      endOfWeek.setHours(23, 59, 59, 999)

      const startDateStr = currentWeekStart.toISOString().split('T')[0]
      const endDateStr = endOfWeek.toISOString().split('T')[0]

      getTasksByDateRange(user.id, startDateStr, endDateStr)
        .then(setTasks)
        .catch((err) => console.error('Failed to load tasks:', err))
        .finally(() => setTasksLoading(false))
    }
  }, [user, currentWeekStart])

  // Group tasks by date for the calendar
  const tasksByDate = useMemo(() => {
    const grouped: Record<string, Task[]> = {}
    tasks.forEach(task => {
      const dateKey = task.date
      if (!grouped[dateKey]) {
        grouped[dateKey] = []
      }
      grouped[dateKey].push(task)
    })
    return grouped
  }, [tasks])

  // Handle task click/toggle
  const handleTaskClick = async (task: Task) => {
    try {
      // Optimistically update the UI
      setTasks(prevTasks =>
        prevTasks.map(t =>
          t.id === task.id ? { ...t, completed: !t.completed } : t
        )
      )

      // Call API to toggle completion (this also updates the streak on backend)
      await toggleTaskCompletion(task.id)

      // Refetch habits to get updated streaks
      if (user?.id) {
        const updatedHabits = await getHabitsByUserId(user.id)
        setHabits(updatedHabits)
      }
    } catch (err) {
      console.error('Failed to toggle task:', err)
      // Revert the optimistic update on error
      setTasks(prevTasks =>
        prevTasks.map(t =>
          t.id === task.id ? { ...t, completed: task.completed } : t
        )
      )
    }
  }

  // Handle habit click
  const handleHabitClick = (habit: Habit) => {
    router.push(`/goals/${habit.goalId}`)
  }

  // Handle week change
  const handleWeekChange = (newWeekStart: Date) => {
    setCurrentWeekStart(newWeekStart)
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-gray-900"></div>
      </div>
    )
  }

  if (!isAuthenticated) {
    return null // Will redirect to login
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar variant="default" user={user || undefined} />

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          {error && (
            <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              Error loading user data: {error}
            </div>
          )}

          {/* Weekly Tasks Calendar & Habits Section */}
          <div className="mb-6">
            <div className="mb-4">
              <h2 className="text-2xl font-bold text-gray-900">Your Week</h2>
              <p className="text-gray-600 text-sm mt-1">Track your daily tasks and build lasting habits</p>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
            {/* Weekly Calendar - takes 3 columns on large screens */}
            <div className="lg:col-span-3">
              {tasksLoading ? (
                <div className="bg-white rounded-xl shadow-md border border-gray-200 flex justify-center items-center py-12">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                </div>
              ) : (
                <WeeklyCalendar
                  startDate={currentWeekStart}
                  tasks={tasksByDate}
                  onTaskClick={handleTaskClick}
                  onDayClick={(date) => router.push(`/tasks?date=${date.toISOString().split('T')[0]}`)}
                  onWeekChange={handleWeekChange}
                />
              )}
            </div>

            {/* Habits List - takes 1 column on large screens */}
            <div className="lg:col-span-1">
              {habitsLoading ? (
                <div className="bg-white rounded-xl shadow-md border border-gray-200 flex justify-center items-center py-12">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                </div>
              ) : (
                <HabitsList
                  habits={habits}
                  onHabitClick={handleHabitClick}
                />
              )}
            </div>
            </div>
          </div>

          {/* Goals Section */}
          <div className="bg-white shadow rounded-lg p-6">
            <div className="flex justify-between items-center mb-6">
              <div>
                <h3 className="text-2xl font-bold text-gray-900">Your Goals</h3>
                <p className="text-gray-600 mt-1">
                  {goals.length === 0 
                    ? "Start tracking your goals today" 
                    : `${goals.length} ${goals.length === 1 ? 'goal' : 'goals'} in progress`}
                </p>
              </div>
              <div className="flex gap-3">
                <button 
                  onClick={() => router.push('/tasks')}
                  className="bg-white text-blue-600 border-2 border-blue-600 px-6 py-3 rounded-lg hover:bg-blue-50 transition shadow-sm hover:shadow-md flex items-center gap-2"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  Weekly Tasks
                </button>
                <button 
                  onClick={() => router.push('/new')}
                  className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-3 rounded-lg hover:from-blue-700 hover:to-purple-700 transition shadow-md hover:shadow-lg flex items-center gap-2"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                  </svg>
                  New Goal
                </button>
              </div>
            </div>

            {goalsError && (
              <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
                Error loading goals: {goalsError}
              </div>
            )}

            {goalsLoading ? (
              <div className="flex justify-center items-center py-12">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
              </div>
            ) : goals.length === 0 ? (
              <div className="text-center py-12">
                <div className="text-6xl mb-4">🎯</div>
                <h4 className="text-xl font-semibold text-gray-900 mb-2">No goals yet</h4>
                <p className="text-gray-600 mb-6">
                  Create your first goal and start your journey to success!
                </p>
                <button 
                  onClick={() => router.push('/new')}
                  className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition inline-flex items-center gap-2"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                  </svg>
                  Create Your First Goal
                </button>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {goals.map((goal) => (
                  <GoalCard 
                    key={goal.id} 
                    goal={goal}
                    onClick={() => router.push(`/goals/${goal.id}`)}
                  />
                ))}
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  )
}
