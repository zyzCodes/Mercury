"use client"

import { signOut } from "next-auth/react"
import { useRouter } from "next/navigation"
import { useEffect } from "react"
import { useUser } from "@/hooks/useUser"
import WeeklyCalendar from "@/components/WeeklyCalendar"

export default function TasksPage() {
  const { user, loading, isAuthenticated } = useUser()
  const router = useRouter()

  useEffect(() => {
    if (loading) return
    if (!isAuthenticated) router.push('/login')
  }, [isAuthenticated, loading, router])

  const handleDayClick = (date: Date) => {
    console.log('Selected date:', date)
    // TODO: Add task management functionality
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
      {/* Navigation */}
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center space-x-8">
              <h1 className="text-xl font-semibold text-gray-900">Mercury</h1>
              <div className="hidden md:flex space-x-4">
                <button
                  onClick={() => router.push('/dashboard')}
                  className="text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition"
                >
                  Dashboard
                </button>
                <button
                  onClick={() => router.push('/tasks')}
                  className="text-blue-600 border-b-2 border-blue-600 px-3 py-2 text-sm font-medium"
                >
                  Tasks
                </button>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">
                {user?.name || user?.username}
              </span>
              <button
                onClick={() => signOut({ callbackUrl: '/login' })}
                className="bg-gray-900 text-white px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-800"
              >
                Sign Out
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
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
                onClick={() => router.push('/new')}
                className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-3 rounded-lg hover:from-blue-700 hover:to-purple-700 transition shadow-md hover:shadow-lg flex items-center gap-2"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                </svg>
                New Task
              </button>
            </div>
          </div>

          {/* Weekly Calendar */}
          <WeeklyCalendar onDayClick={handleDayClick} />
        </div>
      </main>
    </div>
  )
}
