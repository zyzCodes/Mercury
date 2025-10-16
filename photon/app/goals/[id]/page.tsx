"use client"

import { useState, useEffect } from "react"
import { useRouter, useParams } from "next/navigation"
import { useUser } from "@/hooks/useUser"
import {
  getGoalById,
  updateGoal,
  deleteGoal,
  updateGoalStatus,
  type Goal,
  type GoalStatus,
  type UpdateGoalRequest
} from "@/lib/goals-api"
import Navbar from "@/components/Navbar"
import NotesList from "@/components/NotesList"
import Image from "next/image"

export default function GoalDetailPage() {
  const { loading: userLoading, isAuthenticated } = useUser()
  const router = useRouter()
  const params = useParams()
  const goalId = params.id as string

  const [goal, setGoal] = useState<Goal | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [isEditing, setIsEditing] = useState(false)
  const [isSaving, setIsSaving] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  // Edit form state
  const [editForm, setEditForm] = useState({
    title: "",
    description: "",
    imageUrl: "",
    startDate: "",
    endDate: "",
    status: "NOT_STARTED" as GoalStatus,
  })

  useEffect(() => {
    if (!userLoading && !isAuthenticated) {
      router.push('/login')
    }
  }, [isAuthenticated, userLoading, router])

  // Fetch goal
  useEffect(() => {
    if (goalId) {
      setLoading(true)
      getGoalById(Number(goalId))
        .then((data) => {
          if (!data) {
            setError("Goal not found")
            return
          }
          setGoal(data)
          setEditForm({
            title: data.title,
            description: data.description || "",
            imageUrl: data.imageUrl || "",
            startDate: data.startDate,
            endDate: data.endDate,
            status: data.status,
          })
        })
        .catch((err) => setError(err.message))
        .finally(() => setLoading(false))
    }
  }, [goalId])

  const handleSave = async () => {
    if (!goal) return

    setIsSaving(true)
    setError(null)

    try {
      const updateData: UpdateGoalRequest = {
        title: editForm.title,
        description: editForm.description || null,
        imageUrl: editForm.imageUrl || null,
        startDate: editForm.startDate,
        endDate: editForm.endDate,
        status: editForm.status,
      }

      const updatedGoal = await updateGoal(goal.id, updateData)
      setGoal(updatedGoal)
      setIsEditing(false)
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update goal")
    } finally {
      setIsSaving(false)
    }
  }

  const handleStatusChange = async (newStatus: GoalStatus) => {
    if (!goal) return

    try {
      const updatedGoal = await updateGoalStatus(goal.id, newStatus)
      setGoal(updatedGoal)
      setEditForm({ ...editForm, status: newStatus })
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update status")
    }
  }

  const handleDelete = async () => {
    if (!goal) return

    try {
      await deleteGoal(goal.id)
      router.push('/dashboard')
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to delete goal")
      setShowDeleteConfirm(false)
    }
  }

  const calculateProgress = () => {
    if (!goal) return 0
    const start = new Date(goal.startDate).getTime()
    const end = new Date(goal.endDate).getTime()
    const now = Date.now()

    if (now < start) return 0
    if (now > end) return 100

    const total = end - start
    const elapsed = now - start
    return Math.round((elapsed / total) * 100)
  }

  const getStatusColor = (status: GoalStatus) => {
    const colors = {
      NOT_STARTED: 'bg-gray-100 text-gray-800 border-gray-300',
      IN_PROGRESS: 'bg-blue-100 text-blue-800 border-blue-300',
      COMPLETED: 'bg-green-100 text-green-800 border-green-300',
      PAUSED: 'bg-yellow-100 text-yellow-800 border-yellow-300',
      CANCELLED: 'bg-red-100 text-red-800 border-red-300',
    }
    return colors[status]
  }

  if (userLoading || loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (error && !goal) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-xl shadow-lg p-8 max-w-md w-full text-center">
          <div className="text-6xl mb-4">😕</div>
          <h2 className="text-2xl font-bold text-gray-900 mb-2">Goal Not Found</h2>
          <p className="text-gray-600 mb-6">{error}</p>
          <button
            onClick={() => router.push('/dashboard')}
            className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition"
          >
            Back to Dashboard
          </button>
        </div>
      </div>
    )
  }

  if (!goal) return null

  const progress = calculateProgress()
  const daysRemaining = Math.ceil(
    (new Date(goal.endDate).getTime() - Date.now()) / (1000 * 60 * 60 * 24)
  )

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar
        variant="detail"
        onBack={() => router.push('/dashboard')}
        backLabel="Back to Dashboard"
        customActions={
          !isEditing ? (
            <>
              <button
                onClick={() => setIsEditing(true)}
                className="flex items-center gap-2 px-4 py-2 text-blue-600 hover:bg-blue-50 rounded-lg transition"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
                <span className="hidden sm:inline">Edit</span>
              </button>
              <button
                onClick={() => setShowDeleteConfirm(true)}
                className="flex items-center gap-2 px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg transition"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                <span className="hidden sm:inline">Delete</span>
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => {
                  setIsEditing(false)
                  setEditForm({
                    title: goal.title,
                    description: goal.description || "",
                    imageUrl: goal.imageUrl || "",
                    startDate: goal.startDate,
                    endDate: goal.endDate,
                    status: goal.status,
                  })
                }}
                className="px-4 py-2 text-gray-700 hover:bg-gray-100 rounded-lg transition"
              >
                Cancel
              </button>
              <button
                onClick={handleSave}
                disabled={isSaving}
                className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition disabled:opacity-50"
              >
                {isSaving ? 'Saving...' : 'Save Changes'}
              </button>
            </>
          )
        }
      />

      {/* Main Content */}
      <main className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            {error}
          </div>
        )}

        <div className="grid lg:grid-cols-3 gap-6">
          {/* Left Column - Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Hero Image/Card */}
            <div className="bg-white rounded-xl shadow-lg overflow-hidden">
              {(isEditing ? editForm.imageUrl : goal.imageUrl) ? (
                <div className="relative w-full h-80">
                  <Image
                    src={isEditing ? editForm.imageUrl : goal.imageUrl!}
                    alt={isEditing ? editForm.title : goal.title}
                    fill
                    className="object-cover"
                  />
                </div>
              ) : (
                <div className="w-full h-80 bg-gradient-to-br from-blue-100 via-purple-100 to-pink-100 flex items-center justify-center">
                  <div className="text-9xl opacity-50">🎯</div>
                </div>
              )}

              <div className="p-8">
                {isEditing ? (
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Title</label>
                      <input
                        type="text"
                        value={editForm.title}
                        onChange={(e) => setEditForm({ ...editForm, title: e.target.value })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        maxLength={100}
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Description</label>
                      <textarea
                        value={editForm.description}
                        onChange={(e) => setEditForm({ ...editForm, description: e.target.value })}
                        rows={4}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none"
                        maxLength={500}
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Image URL</label>
                      <input
                        type="url"
                        value={editForm.imageUrl}
                        onChange={(e) => setEditForm({ ...editForm, imageUrl: e.target.value })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="https://..."
                      />
                    </div>
                  </div>
                ) : (
                  <>
                    <h1 className="text-4xl font-bold text-gray-900 mb-4">{goal.title}</h1>
                    {goal.description && (
                      <p className="text-lg text-gray-700 leading-relaxed">{goal.description}</p>
                    )}
                  </>
                )}
              </div>
            </div>

            {/* Notes Section */}
            <div className="bg-white rounded-xl shadow-lg p-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-6 flex items-center">
                <svg className="w-6 h-6 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
                  <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clipRule="evenodd" />
                </svg>
                Progress Notes & Journal
              </h2>

              <NotesList goalId={goal.id} />
            </div>
          </div>

          {/* Right Column - Status & Details */}
          <div className="space-y-6">
            {/* Status Card */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Status</h3>
              
              {!isEditing ? (
                <div className="space-y-3">
                  <div className={`px-4 py-3 rounded-lg border-2 ${getStatusColor(goal.status)} text-center font-semibold`}>
                    {goal.status.replace('_', ' ')}
                  </div>
                  
                  <div className="grid grid-cols-2 gap-2">
                    {(['NOT_STARTED', 'IN_PROGRESS', 'PAUSED', 'COMPLETED', 'CANCELLED'] as GoalStatus[]).map((status) => (
                      status !== goal.status && (
                        <button
                          key={status}
                          onClick={() => handleStatusChange(status)}
                          className="px-3 py-2 text-sm border-2 border-gray-300 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition"
                        >
                          {status.replace('_', ' ')}
                        </button>
                      )
                    ))}
                  </div>
                </div>
              ) : (
                <select
                  value={editForm.status}
                  onChange={(e) => setEditForm({ ...editForm, status: e.target.value as GoalStatus })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="NOT_STARTED">Not Started</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="PAUSED">Paused</option>
                  <option value="COMPLETED">Completed</option>
                  <option value="CANCELLED">Cancelled</option>
                </select>
              )}
            </div>

            {/* Progress Card */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Progress</h3>
              
              <div className="space-y-4">
                <div>
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-sm text-gray-600">Time Progress</span>
                    <span className="text-2xl font-bold text-gray-900">{progress}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                    <div
                      className="h-full bg-gradient-to-r from-blue-500 to-purple-500 transition-all duration-500"
                      style={{ width: `${progress}%` }}
                    />
                  </div>
                </div>

                <div className="pt-4 border-t border-gray-200">
                  {goal.status === 'COMPLETED' ? (
                    <div className="text-center text-green-600 font-semibold flex items-center justify-center">
                      <svg className="w-6 h-6 mr-2" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                      </svg>
                      Goal Completed! 🎉
                    </div>
                  ) : daysRemaining < 0 ? (
                    <div className="text-center text-red-600 font-semibold">
                      Overdue by {Math.abs(daysRemaining)} days
                    </div>
                  ) : (
                    <div className="text-center text-gray-700 font-semibold">
                      {daysRemaining} {daysRemaining === 1 ? 'day' : 'days'} remaining
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Dates Card */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Timeline</h3>
              
              {isEditing ? (
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Start Date</label>
                    <input
                      type="date"
                      value={editForm.startDate}
                      onChange={(e) => setEditForm({ ...editForm, startDate: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">End Date</label>
                    <input
                      type="date"
                      value={editForm.endDate}
                      onChange={(e) => setEditForm({ ...editForm, endDate: e.target.value })}
                      min={editForm.startDate}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                </div>
              ) : (
                <div className="space-y-4">
                  <div>
                    <p className="text-sm text-gray-600 mb-1">Start Date</p>
                    <p className="text-lg font-semibold text-gray-900">
                      {new Date(goal.startDate).toLocaleDateString('en-US', {
                        month: 'long',
                        day: 'numeric',
                        year: 'numeric',
                      })}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 mb-1">Target Date</p>
                    <p className="text-lg font-semibold text-gray-900">
                      {new Date(goal.endDate).toLocaleDateString('en-US', {
                        month: 'long',
                        day: 'numeric',
                        year: 'numeric',
                      })}
                    </p>
                  </div>
                  <div className="pt-4 border-t border-gray-200">
                    <p className="text-sm text-gray-600 mb-1">Duration</p>
                    <p className="text-lg font-semibold text-gray-900">
                      {Math.ceil((new Date(goal.endDate).getTime() - new Date(goal.startDate).getTime()) / (1000 * 60 * 60 * 24))} days
                    </p>
                  </div>
                </div>
              )}
            </div>

            {/* Metadata Card */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Info</h3>
              <div className="space-y-3 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Created</span>
                  <span className="font-medium text-gray-900">
                    {new Date(goal.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Last Updated</span>
                  <span className="font-medium text-gray-900">
                    {new Date(goal.updatedAt).toLocaleDateString()}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Owner</span>
                  <span className="font-medium text-gray-900">{goal.username}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Delete Confirmation Modal */}
      {showDeleteConfirm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl shadow-2xl p-8 max-w-md w-full">
            <div className="text-center">
              <div className="text-6xl mb-4">⚠️</div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">Delete Goal?</h3>
              <p className="text-gray-600 mb-6">
                Are you sure you want to delete &quot;{goal.title}&quot;? This action cannot be undone.
              </p>
              <div className="flex gap-3 justify-center">
                <button
                  onClick={() => setShowDeleteConfirm(false)}
                  className="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition"
                >
                  Cancel
                </button>
                <button
                  onClick={handleDelete}
                  className="px-6 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
                >
                  Delete Goal
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

