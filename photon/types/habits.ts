export interface Habit {
  id: number
  name: string
  description: string
  color: string
  daysOfWeek: string
  startDate: string
  endDate: string
  streakStatus: number
  goalId: number
  goalTitle: string
  userId: number
  username: string
  createdAt: string
  updatedAt: string
}

export interface Task {
  id: number
  name: string
  completed: boolean
  date: string
  habitId: number
  habitName: string
  color: string
  userId: number
  username: string
  createdAt: string
  updatedAt: string
}

export interface CreateHabitRequest {
  name: string
  description: string
  daysOfWeek: string
  startDate: string
  endDate: string
  color: string
  goalId: number
  userId: number
}

export interface UpdateHabitRequest {
  name?: string
  description?: string
  daysOfWeek?: string
  startDate?: string
  endDate?: string
  streakStatus?: number
  color?: string
}

export interface CreateTaskRequest {
  name: string
  date: string
  habitId: number
  userId: number
}

export interface UpdateTaskRequest {
  name?: string
  completed?: boolean
  date?: string
}

export interface AIHabitRecommendation {
  name: string
  description: string
  daysOfWeek: string[]
  rationale: string
  accepted: boolean // UI state for user accept/deny
}

export interface AIRecommendationResponse {
  reasoning: string
  habits: Omit<AIHabitRecommendation, 'accepted'>[]
}

