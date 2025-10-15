/**
 * Tasks API utility functions for communicating with the backend
 */

import { Task, CreateTaskRequest, UpdateTaskRequest } from '@/types/habits'

// For server-side calls (inside Docker), use the container name
// For client-side calls (browser), use localhost
const isServer = typeof window === 'undefined'
const SERVER_API_URL = process.env.API_URL || 'http://backend:8080/api'
const CLIENT_API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api'
const API_BASE_URL = isServer ? SERVER_API_URL : CLIENT_API_URL

/**
 * Create a new task
 */
export async function createTask(taskData: CreateTaskRequest): Promise<Task> {
  const response = await fetch(`${API_BASE_URL}/tasks`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(taskData),
  })

  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: response.statusText }))
    throw new Error(error.error || `Failed to create task: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Get all tasks
 */
export async function getAllTasks(): Promise<Task[]> {
  const response = await fetch(`${API_BASE_URL}/tasks`)

  if (!response.ok) {
    throw new Error(`Failed to get tasks: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Get a task by ID
 */
export async function getTaskById(id: number): Promise<Task | null> {
  const response = await fetch(`${API_BASE_URL}/tasks/${id}`)

  if (response.status === 404) {
    return null
  }

  if (!response.ok) {
    throw new Error(`Failed to get task: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Get all tasks for a user
 */
export async function getTasksByUserId(userId: number): Promise<Task[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/tasks/user/${userId}`)

    if (!response.ok) {
      const errorText = await response.text().catch(() => response.statusText)
      console.error(`Failed to get tasks for user ${userId}:`, {
        status: response.status,
        statusText: response.statusText,
        error: errorText,
        url: `${API_BASE_URL}/tasks/user/${userId}`
      })
      throw new Error(`Failed to get tasks (${response.status}): ${errorText}`)
    }

    return response.json()
  } catch (error) {
    if (error instanceof TypeError && error.message.includes('fetch')) {
      throw new Error('Cannot connect to backend. Is the backend server running?')
    }
    throw error
  }
}

/**
 * Get all tasks for a habit
 */
export async function getTasksByHabitId(habitId: number): Promise<Task[]> {
  const response = await fetch(`${API_BASE_URL}/tasks/habit/${habitId}`)

  if (!response.ok) {
    throw new Error(`Failed to get tasks: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Get tasks for a user within a date range
 */
export async function getTasksByDateRange(
  userId: number,
  startDate: string,
  endDate: string
): Promise<Task[]> {
  const response = await fetch(
    `${API_BASE_URL}/tasks/user/${userId}/week?startDate=${startDate}&endDate=${endDate}`
  )

  if (!response.ok) {
    throw new Error(`Failed to get tasks: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Update a task
 */
export async function updateTask(id: number, taskData: UpdateTaskRequest): Promise<Task> {
  const response = await fetch(`${API_BASE_URL}/tasks/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(taskData),
  })

  if (!response.ok) {
    throw new Error(`Failed to update task: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Toggle task completion status
 */
export async function toggleTaskCompletion(id: number): Promise<Task> {
  const response = await fetch(`${API_BASE_URL}/tasks/${id}/toggle`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    throw new Error(`Failed to toggle task completion: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Delete a task
 */
export async function deleteTask(id: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/tasks/${id}`, {
    method: 'DELETE',
  })

  if (!response.ok) {
    throw new Error(`Failed to delete task: ${response.statusText}`)
  }
}

/**
 * Get completed tasks for a user
 */
export async function getCompletedTasks(userId: number): Promise<Task[]> {
  const response = await fetch(`${API_BASE_URL}/tasks/user/${userId}/completed`)

  if (!response.ok) {
    throw new Error(`Failed to get completed tasks: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Get pending tasks for a user
 */
export async function getPendingTasks(userId: number): Promise<Task[]> {
  const response = await fetch(`${API_BASE_URL}/tasks/user/${userId}/pending`)

  if (!response.ok) {
    throw new Error(`Failed to get pending tasks: ${response.statusText}`)
  }

  return response.json()
}

