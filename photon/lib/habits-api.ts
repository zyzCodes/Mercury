/**
 * Habits API utility functions for communicating with the backend
 */

import { Habit, CreateHabitRequest, UpdateHabitRequest } from '@/types/habits'

// For server-side calls (inside Docker), use the container name
// For client-side calls (browser), use localhost
const isServer = typeof window === 'undefined'
const SERVER_API_URL = process.env.API_URL || 'http://backend:8080/api'
const CLIENT_API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api'
const API_BASE_URL = isServer ? SERVER_API_URL : CLIENT_API_URL

/**
 * Create a new habit
 */
export async function createHabit(habitData: CreateHabitRequest): Promise<Habit> {
  const response = await fetch(`${API_BASE_URL}/habits`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(habitData),
  })

  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: response.statusText }))
    throw new Error(error.error || `Failed to create habit: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Get all habits
 */
export async function getAllHabits(): Promise<Habit[]> {
  const response = await fetch(`${API_BASE_URL}/habits`)

  if (!response.ok) {
    throw new Error(`Failed to get habits: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Get a habit by ID
 */
export async function getHabitById(id: number): Promise<Habit | null> {
  const response = await fetch(`${API_BASE_URL}/habits/${id}`)

  if (response.status === 404) {
    return null
  }

  if (!response.ok) {
    throw new Error(`Failed to get habit: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Get all habits for a user
 */
export async function getHabitsByUserId(userId: number): Promise<Habit[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/habits/user/${userId}`)

    if (!response.ok) {
      const errorText = await response.text().catch(() => response.statusText)
      console.error(`Failed to get habits for user ${userId}:`, {
        status: response.status,
        statusText: response.statusText,
        error: errorText,
        url: `${API_BASE_URL}/habits/user/${userId}`
      })
      throw new Error(`Failed to get habits (${response.status}): ${errorText}`)
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
 * Get all habits for a goal
 */
export async function getHabitsByGoalId(goalId: number): Promise<Habit[]> {
  const response = await fetch(`${API_BASE_URL}/habits/goal/${goalId}`)

  if (!response.ok) {
    throw new Error(`Failed to get habits: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Update a habit
 */
export async function updateHabit(id: number, habitData: UpdateHabitRequest): Promise<Habit> {
  const response = await fetch(`${API_BASE_URL}/habits/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(habitData),
  })

  if (!response.ok) {
    throw new Error(`Failed to update habit: ${response.statusText}`)
  }

  return response.json()
}

/**
 * Delete a habit
 */
export async function deleteHabit(id: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/habits/${id}`, {
    method: 'DELETE',
  })

  if (!response.ok) {
    throw new Error(`Failed to delete habit: ${response.statusText}`)
  }
}

/**
 * Get count of habits for a user
 */
export async function countHabitsByUserId(userId: number): Promise<number> {
  const response = await fetch(`${API_BASE_URL}/habits/user/${userId}/count`)

  if (!response.ok) {
    throw new Error(`Failed to count habits: ${response.statusText}`)
  }

  const data = await response.json()
  return data.count
}

