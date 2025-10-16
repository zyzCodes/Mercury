"use client"

import { useRouter } from "next/navigation"
import { useEffect, useState, useMemo } from "react"
import { useUser } from "@/hooks/useUser"
import Navbar from "@/components/Navbar"
import WeeklyCalendar from "@/components/WeeklyCalendar"
import HabitsList from "@/components/HabitsList"
import CreateHabitModal from "@/components/CreateHabitModal"
import { Habit, Task } from "@/types/habits"
import { getHabitsByUserId } from "@/lib/habits-api"
import { getTasksByUserId, toggleTaskCompletion } from "@/lib/tasks-api"

export default function TasksPage() {
  const { user, loading, isAuthenticated } = useUser()
  const router = useRouter()

  // Data state
  const [habits, setHabits] = useState<Habit[]>([])
  const [tasks, setTasks] = useState<Task[]>([])
  const [dataLoading, setDataLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  
  // UI state
  const [showCreateModal, setShowCreateModal] = useState(false)

  useEffect(() => {
    if (loading) return
    if (!isAuthenticated) router.push('/login')
  }, [isAuthenticated, loading, router])

  useEffect(() => {
    if (user?.id) {
      fetchData()
    }
  }, [user?.id])

  const fetchData = async () => {
    if (!user?.id) return
    
    setDataLoading(true)
    setError(null)
    try {
      const [habitsData, tasksData] = await Promise.all([
        getHabitsByUserId(user.id),
        getTasksByUserId(user.id),
      ])
      setHabits(habitsData)
      setTasks(tasksData)
    } catch (error) {
      console.error("Failed to fetch data:", error)
      const errorMessage = error instanceof Error ? error.message : "Failed to load data from backend"
      setError(errorMessage)
    } finally {
      setDataLoading(false)
    }
  }

  // Transform tasks into calendar format
  const tasksByDate = useMemo(() => {
    const grouped: Record<string, Task[]> = {}
    tasks.forEach(task => {
      const dateKey = task.date.split('T')[0] // Ensure date is in YYYY-MM-DD format
      if (!grouped[dateKey]) {
        grouped[dateKey] = []
      }
      grouped[dateKey].push(task)
    })
    return grouped
  }, [tasks])

  const handleDayClick = (date: Date) => {
    console.log('Selected date:', date)
    // TODO: Add task management functionality
  }

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
    } catch (error) {
      console.error('Failed to toggle task completion:', error)
      // Revert the optimistic update on error
      setTasks(prevTasks =>
        prevTasks.map(t =>
          t.id === task.id ? { ...t, completed: task.completed } : t
        )
      )
    }
  }

  const handleHabitClick = (habit: Habit) => {
    console.log('Habit clicked:', habit)
    // TODO: Add habit click functionality (edit/view details)
  }

  const handleCreateSuccess = () => {
    fetchData() // Refresh data after creating habit
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-gray-900"></div>
      </div>
    )
  }

  if (!isAuthenticated) {
    return null
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar variant="default" user={user || undefined} showNavLinks />

      {/* Main Content */}
      <main className="w-full py-6 px-4 sm:px-6 lg:px-8">
        <div className="py-6">
          {/* Page Header */}
          <div className="mb-8">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-3xl font-bold text-gray-900">Weekly Tasks</h2>
                <p className="text-gray-600 mt-1">
                  Plan and organize your recurring tasks throughout the week
                </p>
              </div>
              <button 
                onClick={() => setShowCreateModal(true)}
                disabled={dataLoading}
                className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-3 rounded-lg hover:from-blue-700 hover:to-purple-700 transition shadow-md hover:shadow-lg flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                </svg>
                New Habit
              </button>
            </div>
          </div>

          {/* Loading State */}
          {dataLoading && (
            <div className="flex items-center justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
          )}

          {/* Error State */}
          {error && !dataLoading && (
            <div className="max-w-2xl mx-auto">
              <div className="bg-red-50 border border-red-200 rounded-xl p-6">
                <div className="flex items-start gap-4">
                  <div className="flex-shrink-0">
                    <svg className="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-red-900 mb-2">
                      Failed to Load Data
                    </h3>
                    <p className="text-red-700 mb-4">{error}</p>
                    <div className="space-y-2 text-sm text-red-600">
                      <p>Common solutions:</p>
                      <ul className="list-disc list-inside space-y-1 ml-2">
                        <li>Make sure the backend server is running on port 8080</li>
                        <li>Run <code className="bg-red-100 px-2 py-0.5 rounded">./scripts/start</code> to start Docker containers</li>
                        <li>Check if <code className="bg-red-100 px-2 py-0.5 rounded">http://localhost:8080/api/habits/user/{user?.id}</code> is accessible</li>
                      </ul>
                    </div>
                    <button
                      onClick={fetchData}
                      className="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
                    >
                      Retry
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Layout with Habits Sidebar and Calendar */}
          {!dataLoading && !error && (
            <>
              <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
                {/* Habits List - Hidden on mobile, visible on large screens */}
                <div className="lg:col-span-3">
                  <HabitsList 
                    habits={habits} 
                    onHabitClick={handleHabitClick}
                  />
                </div>

                {/* Weekly Calendar */}
                <div className="lg:col-span-9">
                  <WeeklyCalendar 
                    onDayClick={handleDayClick}
                    tasks={tasksByDate}
                    onTaskClick={handleTaskClick}
                  />
                </div>
              </div>

              {/* Mobile Habits View - Shown only on small screens */}
              <div className="lg:hidden mt-6">
                <div className="bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden">
                  <div className="bg-gradient-to-r from-blue-600 to-purple-600 px-4 py-3">
                    <h3 className="text-white text-lg font-semibold">My Habits</h3>
                  </div>
                  <div className="p-4">
                    {habits.length === 0 ? (
                      <p className="text-center text-gray-400 py-4">No habits yet. Create one to get started!</p>
                    ) : (
                      <div className="flex flex-wrap gap-2">
                        {habits.map((habit) => (
                          <div
                            key={habit.id}
                            className="inline-flex items-center gap-2 px-3 py-2 rounded-full text-sm font-medium shadow-sm border"
                            style={{
                              backgroundColor: `${habit.color}20`,
                              borderColor: habit.color,
                              color: habit.color,
                            }}
                          >
                            <div 
                              className="w-2 h-2 rounded-full"
                              style={{ backgroundColor: habit.color }}
                            />
                            {habit.name}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </main>

      {/* Create Habit Modal */}
      {user?.id && (
        <CreateHabitModal
          isOpen={showCreateModal}
          onClose={() => setShowCreateModal(false)}
          userId={user.id}
          onSuccess={handleCreateSuccess}
        />
      )}
    </div>
  )
}
