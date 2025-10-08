"use client"

import { signOut } from "next-auth/react"
import { useRouter } from "next/navigation"
import { useEffect, useState } from "react"
import Image from "next/image"
import { useUser } from "@/hooks/useUser"
import { getGoalsByUserId, type Goal } from "@/lib/goals-api"
import GoalCard from "@/components/GoalCard"

export default function DashboardPage() {
  const { user, loading, error, session, isAuthenticated } = useUser()
  const router = useRouter()
  const [goals, setGoals] = useState<Goal[]>([])
  const [goalsLoading, setGoalsLoading] = useState(true)
  const [goalsError, setGoalsError] = useState<string | null>(null)

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
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900">Mercury</h1>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">
                Welcome, {user?.name || user?.username || session?.user?.email}
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

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          {error && (
            <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              Error loading user data: {error}
            </div>
          )}
          
          <div className="bg-white shadow rounded-lg p-6 mb-6">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
              Welcome to Mercury! 🚀
            </h2>
            
            {user ? (
              <div className="space-y-4">
                <div className="flex items-center space-x-4">
                  {user.avatarUrl && (
                    <Image 
                      src={user.avatarUrl} 
                      alt="Profile" 
                      width={80}
                      height={80}
                      className="w-20 h-20 rounded-full border-2 border-gray-200"
                    />
                  )}
                  <div>
                    <h3 className="text-xl font-semibold text-gray-900">
                      {user.name || user.username}
                    </h3>
                    <p className="text-gray-600">@{user.username}</p>
                  </div>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6">
                  <div className="bg-gray-50 p-4 rounded">
                    <p className="text-sm text-gray-600 font-medium">Database ID</p>
                    <p className="text-lg text-gray-900">{user.id}</p>
                  </div>
                  <div className="bg-gray-50 p-4 rounded">
                    <p className="text-sm text-gray-600 font-medium">Auth Provider</p>
                    <p className="text-lg text-gray-900 capitalize">{user.provider}</p>
                  </div>
                  <div className="bg-gray-50 p-4 rounded">
                    <p className="text-sm text-gray-600 font-medium">Provider ID</p>
                    <p className="text-lg text-gray-900">{user.providerId}</p>
                  </div>
                  <div className="bg-gray-50 p-4 rounded">
                    <p className="text-sm text-gray-600 font-medium">Email</p>
                    <p className="text-lg text-gray-900">{user.email || 'Not provided'}</p>
                  </div>
                  <div className="bg-gray-50 p-4 rounded">
                    <p className="text-sm text-gray-600 font-medium">Member Since</p>
                    <p className="text-lg text-gray-900">
                      {new Date(user.createdAt).toLocaleDateString()}
                    </p>
                  </div>
                  {user.location && (
                    <div className="bg-gray-50 p-4 rounded">
                      <p className="text-sm text-gray-600 font-medium">Location</p>
                      <p className="text-lg text-gray-900">{user.location}</p>
                    </div>
                  )}
                  {user.bio && (
                    <div className="bg-gray-50 p-4 rounded md:col-span-2">
                      <p className="text-sm text-gray-600 font-medium">Bio</p>
                      <p className="text-lg text-gray-900">{user.bio}</p>
                    </div>
                  )}
                </div>
              </div>
            ) : (
              <p className="text-gray-600">
                You&apos;re successfully logged in and ready to start tracking your goals.
              </p>
            )}
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
