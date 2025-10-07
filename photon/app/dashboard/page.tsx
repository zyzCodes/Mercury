"use client"

import { signOut } from "next-auth/react"
import { useRouter } from "next/navigation"
import { useEffect } from "react"
import Image from "next/image"
import { useUser } from "@/hooks/useUser"

export default function DashboardPage() {
  const { user, loading, error, session, isAuthenticated } = useUser()
  const router = useRouter()

  useEffect(() => {
    if (loading) return // Still loading
    if (!isAuthenticated) router.push('/login') // Not authenticated
  }, [isAuthenticated, loading, router])

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

          <div className="bg-white shadow rounded-lg p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              Ready to get started?
            </h3>
            <p className="text-gray-600 mb-4">
              Your user account has been created in the database. You can now start creating goals and tracking your progress.
            </p>
            <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition">
              Create Your First Goal
            </button>
          </div>
        </div>
      </main>
    </div>
  )
}
