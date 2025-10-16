"use client"

import { useState, useEffect } from "react"
import { CreateHabitRequest, CreateTaskRequest } from "@/types/habits"
import { createHabit } from "@/lib/habits-api"
import { createTask } from "@/lib/tasks-api"
import { getGoalsByUserId, Goal } from "@/lib/goals-api"

interface CreateHabitModalProps {
  isOpen: boolean
  onClose: () => void
  userId: number
  onSuccess: () => void
}

const COLORS = [
  { value: "#3B82F6", name: "Blue" },
  { value: "#8B5CF6", name: "Purple" },
  { value: "#10B981", name: "Green" },
  { value: "#F59E0B", name: "Amber" },
  { value: "#EC4899", name: "Pink" },
  { value: "#EF4444", name: "Red" },
  { value: "#06B6D4", name: "Cyan" },
]

const DAYS = [
  { code: "Mon", full: "Monday" },
  { code: "Tue", full: "Tuesday" },
  { code: "Wed", full: "Wednesday" },
  { code: "Thu", full: "Thursday" },
  { code: "Fri", full: "Friday" },
  { code: "Sat", full: "Saturday" },
  { code: "Sun", full: "Sunday" },
]

interface AutoTask {
  date: string
  dayName: string
  name: string
}

export default function CreateHabitModal({ isOpen, onClose, userId, onSuccess }: CreateHabitModalProps) {
  // Form state
  const [name, setName] = useState("")
  const [description, setDescription] = useState("")
  const [color, setColor] = useState(COLORS[0].value)
  const [goalId, setGoalId] = useState<number | null>(null)
  const [selectedDays, setSelectedDays] = useState<string[]>([])
  const [startDate, setStartDate] = useState("")
  const [endDate, setEndDate] = useState("")
  
  // UI state
  const [goals, setGoals] = useState<Goal[]>([])
  const [autoTasks, setAutoTasks] = useState<AutoTask[]>([])
  const [loading, setLoading] = useState(false)
  const [errors, setErrors] = useState<Record<string, string>>({})

  // Fetch goals on mount
  useEffect(() => {
    if (isOpen && userId) {
      fetchGoals()
    }
  }, [isOpen, userId])

  // Generate auto tasks when days or dates change
  useEffect(() => {
    if (selectedDays.length > 0 && startDate) {
      generateAutoTasks()
    } else {
      setAutoTasks([])
    }
  }, [selectedDays, startDate, name])

  const fetchGoals = async () => {
    try {
      const userGoals = await getGoalsByUserId(userId)
      setGoals(userGoals)
      if (userGoals.length > 0 && !goalId) {
        setGoalId(userGoals[0].id)
      }
    } catch (error) {
      console.error("Failed to fetch goals:", error)
    }
  }

  const generateAutoTasks = () => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    
    // Get start of current week (Sunday)
    const startOfWeek = new Date(today)
    const dayOfWeek = startOfWeek.getDay()
    startOfWeek.setDate(startOfWeek.getDate() - dayOfWeek)
    
    // Generate tasks for this week
    const tasks: AutoTask[] = []
    for (let i = 0; i < 7; i++) {
      const date = new Date(startOfWeek)
      date.setDate(startOfWeek.getDate() + i)
      
      const dayCode = DAYS[i].code
      if (selectedDays.includes(dayCode)) {
        const dateStr = date.toISOString().split('T')[0]
        tasks.push({
          date: dateStr,
          dayName: DAYS[i].full,
          name: name ? `${name} - ${DAYS[i].full}` : `Task - ${DAYS[i].full}`
        })
      }
    }
    
    setAutoTasks(tasks)
  }

  const toggleDay = (dayCode: string) => {
    setSelectedDays(prev =>
      prev.includes(dayCode)
        ? prev.filter(d => d !== dayCode)
        : [...prev, dayCode]
    )
  }

  const updateTaskName = (index: number, newName: string) => {
    setAutoTasks(prev =>
      prev.map((task, i) => (i === index ? { ...task, name: newName } : task))
    )
  }

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {}

    if (!name.trim()) newErrors.name = "Name is required"
    if (!description.trim()) newErrors.description = "Description is required"
    if (!goalId) newErrors.goalId = "Goal is required"
    if (selectedDays.length === 0) newErrors.days = "Select at least one day"
    if (!startDate) newErrors.startDate = "Start date is required"
    if (!endDate) newErrors.endDate = "End date is required"
    if (startDate && endDate && new Date(endDate) <= new Date(startDate)) {
      newErrors.endDate = "End date must be after start date"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!validate() || !goalId) return

    setLoading(true)
    try {
      // Create habit
      const habitData: CreateHabitRequest = {
        name: name.trim(),
        description: description.trim(),
        daysOfWeek: selectedDays.join(", "),
        startDate,
        endDate,
        color,
        goalId,
        userId,
      }

      const createdHabit = await createHabit(habitData)

      // Create tasks
      const taskPromises = autoTasks.map(task => {
        const taskData: CreateTaskRequest = {
          name: task.name,
          date: task.date,
          habitId: createdHabit.id,
          userId,
        }
        return createTask(taskData)
      })

      await Promise.all(taskPromises)

      // Success!
      onSuccess()
      handleClose()
    } catch (error) {
      console.error("Failed to create habit:", error)
      setErrors({ submit: error instanceof Error ? error.message : "Failed to create habit" })
    } finally {
      setLoading(false)
    }
  }

  const handleClose = () => {
    // Reset form
    setName("")
    setDescription("")
    setColor(COLORS[0].value)
    setGoalId(null)
    setSelectedDays([])
    setStartDate("")
    setEndDate("")
    setAutoTasks([])
    setErrors({})
    onClose()
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-black/30 backdrop-blur-md animate-modal-backdrop"
        onClick={handleClose}
      />

      {/* Modal */}
      <div className="flex min-h-full items-center justify-center p-4">
        <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto animate-modal-content">
          {/* Header */}
          <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-purple-600 px-6 py-4 rounded-t-2xl">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-bold text-white">Create New Habit</h2>
              <button
                onClick={handleClose}
                className="text-white hover:bg-white hover:bg-opacity-20 rounded-lg p-2 transition"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="p-6 space-y-6">
            {/* Name */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Habit Name <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="e.g., Morning Exercise"
              />
              {errors.name && <p className="mt-1 text-sm text-red-500">{errors.name}</p>}
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Description <span className="text-red-500">*</span>
              </label>
              <textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={3}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                placeholder="Describe your habit..."
              />
              {errors.description && <p className="mt-1 text-sm text-red-500">{errors.description}</p>}
            </div>

            {/* Color Picker */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Color
              </label>
              <div className="flex gap-3">
                {COLORS.map((c) => (
                  <button
                    key={c.value}
                    type="button"
                    onClick={() => setColor(c.value)}
                    className={`w-10 h-10 rounded-full transition-all ${
                      color === c.value ? "ring-4 ring-offset-2 ring-gray-400 scale-110" : "hover:scale-105"
                    }`}
                    style={{ backgroundColor: c.value }}
                    title={c.name}
                  />
                ))}
              </div>
            </div>

            {/* Goal Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Associated Goal <span className="text-red-500">*</span>
              </label>
              <select
                value={goalId || ""}
                onChange={(e) => setGoalId(Number(e.target.value))}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">Select a goal...</option>
                {goals.map((goal) => (
                  <option key={goal.id} value={goal.id}>
                    {goal.title}
                  </option>
                ))}
              </select>
              {errors.goalId && <p className="mt-1 text-sm text-red-500">{errors.goalId}</p>}
            </div>

            {/* Days of Week */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Days of Week <span className="text-red-500">*</span>
              </label>
              <div className="grid grid-cols-7 gap-2">
                {DAYS.map((day) => (
                  <button
                    key={day.code}
                    type="button"
                    onClick={() => toggleDay(day.code)}
                    className={`px-3 py-2 rounded-lg font-medium text-sm transition-all ${
                      selectedDays.includes(day.code)
                        ? "bg-gradient-to-r from-blue-600 to-purple-600 text-white shadow-md"
                        : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                    }`}
                  >
                    {day.code}
                  </button>
                ))}
              </div>
              {errors.days && <p className="mt-1 text-sm text-red-500">{errors.days}</p>}
            </div>

            {/* Date Range */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Start Date <span className="text-red-500">*</span>
                </label>
                <input
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                {errors.startDate && <p className="mt-1 text-sm text-red-500">{errors.startDate}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  End Date <span className="text-red-500">*</span>
                </label>
                <input
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                {errors.endDate && <p className="mt-1 text-sm text-red-500">{errors.endDate}</p>}
              </div>
            </div>

            {/* Auto-generated Tasks */}
            {autoTasks.length > 0 && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tasks for This Week ({autoTasks.length})
                </label>
                <div className="space-y-2 max-h-48 overflow-y-auto p-3 bg-gray-50 rounded-lg">
                  {autoTasks.map((task, index) => (
                    <div key={index} className="flex items-center gap-2">
                      <span className="text-xs text-gray-500 w-24">{task.dayName}</span>
                      <input
                        type="text"
                        value={task.name}
                        onChange={(e) => updateTaskName(index, e.target.value)}
                        className="flex-1 px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      />
                    </div>
                  ))}
                </div>
                <p className="mt-2 text-xs text-gray-500">
                  These tasks will be created automatically based on your selected days
                </p>
              </div>
            )}

            {/* Error Message */}
            {errors.submit && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
                <p className="text-sm text-red-600">{errors.submit}</p>
              </div>
            )}

            {/* Actions */}
            <div className="flex gap-3 pt-4">
              <button
                type="button"
                onClick={handleClose}
                className="flex-1 px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition font-medium"
                disabled={loading}
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-lg hover:from-blue-700 hover:to-purple-700 transition font-medium shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? "Creating..." : "Create Habit"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

