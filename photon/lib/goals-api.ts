/**
 * Goals API utility functions for communicating with the backend
 */

// For server-side calls (inside Docker), use the container name
// For client-side calls (browser), use localhost
const isServer = typeof window === 'undefined';
const SERVER_API_URL = process.env.API_URL || 'http://backend:8080/api';
const CLIENT_API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';
const API_BASE_URL = isServer ? SERVER_API_URL : CLIENT_API_URL;

export type GoalStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'PAUSED' | 'CANCELLED';

export interface Goal {
  id: number;
  title: string;
  description: string | null;
  imageUrl: string | null;
  startDate: string; // ISO date string
  endDate: string; // ISO date string
  status: GoalStatus;
  notes: string | null;
  userId: number;
  username: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateGoalRequest {
  title: string;
  description?: string | null;
  imageUrl?: string | null;
  startDate: string; // ISO date string (YYYY-MM-DD)
  endDate: string; // ISO date string (YYYY-MM-DD)
  status?: GoalStatus;
  notes?: string | null;
  userId: number;
}

export interface UpdateGoalRequest {
  title?: string;
  description?: string | null;
  imageUrl?: string | null;
  startDate?: string;
  endDate?: string;
  status?: GoalStatus;
  notes?: string | null;
}

/**
 * Create a new goal
 */
export async function createGoal(goalData: CreateGoalRequest): Promise<Goal> {
  const response = await fetch(`${API_BASE_URL}/goals`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(goalData),
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: response.statusText }));
    throw new Error(error.error || `Failed to create goal: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get all goals for a user
 */
export async function getGoalsByUserId(userId: number): Promise<Goal[]> {
  const response = await fetch(`${API_BASE_URL}/goals/user/${userId}`);

  if (!response.ok) {
    throw new Error(`Failed to get goals: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get a goal by ID
 */
export async function getGoalById(id: number): Promise<Goal | null> {
  const response = await fetch(`${API_BASE_URL}/goals/${id}`);

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error(`Failed to get goal: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Update a goal
 */
export async function updateGoal(id: number, goalData: UpdateGoalRequest): Promise<Goal> {
  const response = await fetch(`${API_BASE_URL}/goals/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(goalData),
  });

  if (!response.ok) {
    throw new Error(`Failed to update goal: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Update goal status
 */
export async function updateGoalStatus(id: number, status: GoalStatus): Promise<Goal> {
  const response = await fetch(`${API_BASE_URL}/goals/${id}/status`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ status }),
  });

  if (!response.ok) {
    throw new Error(`Failed to update goal status: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Update goal notes
 */
export async function updateGoalNotes(id: number, notes: string): Promise<Goal> {
  const response = await fetch(`${API_BASE_URL}/goals/${id}/notes`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ notes }),
  });

  if (!response.ok) {
    throw new Error(`Failed to update goal notes: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Delete a goal
 */
export async function deleteGoal(id: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/goals/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw new Error(`Failed to delete goal: ${response.statusText}`);
  }
}

/**
 * Get active goals for a user
 */
export async function getActiveGoals(userId: number): Promise<Goal[]> {
  const response = await fetch(`${API_BASE_URL}/goals/user/${userId}/active`);

  if (!response.ok) {
    throw new Error(`Failed to get active goals: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get completed goals for a user
 */
export async function getCompletedGoals(userId: number): Promise<Goal[]> {
  const response = await fetch(`${API_BASE_URL}/goals/user/${userId}/completed`);

  if (!response.ok) {
    throw new Error(`Failed to get completed goals: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get overdue goals for a user
 */
export async function getOverdueGoals(userId: number): Promise<Goal[]> {
  const response = await fetch(`${API_BASE_URL}/goals/user/${userId}/overdue`);

  if (!response.ok) {
    throw new Error(`Failed to get overdue goals: ${response.statusText}`);
  }

  return response.json();
}

