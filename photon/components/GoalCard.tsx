import Image from "next/image"
import { Goal } from "@/lib/goals-api"

interface GoalCardProps {
  goal: Goal
  onClick?: () => void
}

export default function GoalCard({ goal, onClick }: GoalCardProps) {
  // Calculate progress based on dates
  const calculateProgress = () => {
    const start = new Date(goal.startDate).getTime()
    const end = new Date(goal.endDate).getTime()
    const now = Date.now()

    if (now < start) return 0
    if (now > end) return 100

    const total = end - start
    const elapsed = now - start
    return Math.round((elapsed / total) * 100)
  }

  const progress = calculateProgress()
  const daysRemaining = Math.ceil(
    (new Date(goal.endDate).getTime() - Date.now()) / (1000 * 60 * 60 * 24)
  )

  // Status badge colors
  const getStatusColor = (status: Goal['status']) => {
    const colors = {
      NOT_STARTED: 'bg-gray-100 text-gray-800 border-gray-300',
      IN_PROGRESS: 'bg-blue-100 text-blue-800 border-blue-300',
      COMPLETED: 'bg-green-100 text-green-800 border-green-300',
      PAUSED: 'bg-yellow-100 text-yellow-800 border-yellow-300',
      CANCELLED: 'bg-red-100 text-red-800 border-red-300',
    }
    return colors[status]
  }

  // Status display text
  const getStatusText = (status: Goal['status']) => {
    return status.replace('_', ' ')
  }

  // Progress bar color based on status and time
  const getProgressColor = () => {
    if (goal.status === 'COMPLETED') return 'bg-green-500'
    if (goal.status === 'CANCELLED') return 'bg-red-500'
    if (goal.status === 'PAUSED') return 'bg-yellow-500'
    if (daysRemaining < 0) return 'bg-red-500'
    if (daysRemaining < 7) return 'bg-orange-500'
    return 'bg-blue-500'
  }

  return (
    <div
      onClick={onClick}
      className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden cursor-pointer group border border-gray-200 hover:border-blue-300"
    >
      {/* Image Section */}
      {goal.imageUrl ? (
        <div className="relative w-full h-48 bg-gradient-to-br from-blue-50 to-purple-50 overflow-hidden">
          <Image
            src={goal.imageUrl}
            alt={goal.title}
            fill
            className="object-cover group-hover:scale-105 transition-transform duration-300"
            onError={(e) => {
              // Fallback if image fails to load
              e.currentTarget.style.display = 'none'
            }}
          />
          {/* Gradient overlay for better text readability */}
          <div className="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent" />
          
          {/* Status badge on image */}
          <div className="absolute top-3 right-3">
            <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${getStatusColor(goal.status)} backdrop-blur-sm`}>
              {getStatusText(goal.status)}
            </span>
          </div>
        </div>
      ) : (
        // No image - use gradient background with emoji
        <div className="relative w-full h-48 bg-gradient-to-br from-blue-100 via-purple-100 to-pink-100 flex items-center justify-center">
          <div className="text-6xl opacity-50">{goal.emoji || '⚡️'}</div>

          {/* Status badge */}
          <div className="absolute top-3 right-3">
            <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${getStatusColor(goal.status)}`}>
              {getStatusText(goal.status)}
            </span>
          </div>
        </div>
      )}

      {/* Content Section */}
      <div className="p-5">
        {/* Title */}
        <h3 className="text-xl font-bold text-gray-900 mb-2 line-clamp-2 group-hover:text-blue-600 transition-colors flex items-center gap-2">
          {goal.emoji && <span className="text-2xl">{goal.emoji}</span>}
          <span className="flex-1">{goal.title}</span>
        </h3>

        {/* Description */}
        {goal.description && (
          <p className="text-gray-600 text-sm mb-4 line-clamp-2">
            {goal.description}
          </p>
        )}

        {/* Progress Bar */}
        <div className="mb-4">
          <div className="flex justify-between items-center mb-2">
            <span className="text-xs font-medium text-gray-700">Progress</span>
            <span className="text-xs font-semibold text-gray-900">{progress}%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
            <div
              className={`h-full ${getProgressColor()} transition-all duration-500 rounded-full`}
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>

        {/* Dates and Stats */}
        <div className="grid grid-cols-2 gap-3 mb-4">
          {/* Start Date */}
          <div className="bg-gray-50 rounded-lg p-2">
            <p className="text-xs text-gray-600 mb-1">Start Date</p>
            <p className="text-sm font-semibold text-gray-900">
              {new Date(goal.startDate).toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric',
              })}
            </p>
          </div>

          {/* End Date */}
          <div className="bg-gray-50 rounded-lg p-2">
            <p className="text-xs text-gray-600 mb-1">Target Date</p>
            <p className="text-sm font-semibold text-gray-900">
              {new Date(goal.endDate).toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric',
              })}
            </p>
          </div>
        </div>

        {/* Days Remaining / Status Message */}
        <div className="flex items-center justify-between">
          {goal.status === 'COMPLETED' ? (
            <div className="flex items-center text-green-600">
              <svg className="w-5 h-5 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
              </svg>
              <span className="text-sm font-semibold">Completed!</span>
            </div>
          ) : goal.status === 'CANCELLED' ? (
            <div className="flex items-center text-red-600">
              <svg className="w-5 h-5 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
              <span className="text-sm font-semibold">Cancelled</span>
            </div>
          ) : daysRemaining < 0 ? (
            <div className="flex items-center text-red-600">
              <svg className="w-5 h-5 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
              <span className="text-sm font-semibold">Overdue</span>
            </div>
          ) : daysRemaining === 0 ? (
            <div className="flex items-center text-orange-600">
              <svg className="w-5 h-5 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clipRule="evenodd" />
              </svg>
              <span className="text-sm font-semibold">Due Today!</span>
            </div>
          ) : (
            <div className="flex items-center text-gray-700">
              <svg className="w-5 h-5 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clipRule="evenodd" />
              </svg>
              <span className="text-sm font-semibold">
                {daysRemaining} {daysRemaining === 1 ? 'day' : 'days'} left
              </span>
            </div>
          )}

          {/* View/Edit Arrow */}
          <div className="text-blue-600 group-hover:translate-x-1 transition-transform">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </div>

        {/* Notes Preview (if exists) */}
        {goal.notes && (
          <div className="mt-4 pt-4 border-t border-gray-100">
            <p className="text-xs text-gray-600 mb-1 flex items-center">
              <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
                <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clipRule="evenodd" />
              </svg>
              Latest Note
            </p>
            <p className="text-sm text-gray-700 line-clamp-2">{goal.notes}</p>
          </div>
        )}
      </div>
    </div>
  )
}

