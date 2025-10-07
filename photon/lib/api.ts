/**
 * API utility functions for communicating with the backend
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export interface User {
  id: number;
  provider: string;  // 'github', 'google', 'jwt', etc.
  providerId: string;
  githubId: string | null;  // Legacy field
  username: string;
  email: string;
  name: string | null;
  avatarUrl: string | null;
  bio: string | null;
  location: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  provider: string;
  providerId: string;
  githubId?: string;  // Legacy field
  username: string;
  email: string;
  name?: string | null;
  avatarUrl?: string | null;
  bio?: string | null;
  location?: string | null;
}

/**
 * Create or update a user in the backend
 */
export async function createOrUpdateUser(userData: CreateUserRequest): Promise<User> {
  const response = await fetch(`${API_BASE_URL}/users`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData),
  });

  if (!response.ok) {
    throw new Error(`Failed to create/update user: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get a user by their provider and provider ID
 */
export async function getUserByProvider(provider: string, providerId: string): Promise<User | null> {
  const response = await fetch(`${API_BASE_URL}/users/provider/${provider}/${providerId}`);

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error(`Failed to get user: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get a user by their GitHub ID (legacy support)
 */
export async function getUserByGithubId(githubId: string): Promise<User | null> {
  const response = await fetch(`${API_BASE_URL}/users/github/${githubId}`);

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error(`Failed to get user: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get a user by their ID
 */
export async function getUserById(id: number): Promise<User | null> {
  const response = await fetch(`${API_BASE_URL}/users/${id}`);

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error(`Failed to get user: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get all users
 */
export async function getAllUsers(): Promise<User[]> {
  const response = await fetch(`${API_BASE_URL}/users`);

  if (!response.ok) {
    throw new Error(`Failed to get users: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get all users by authentication provider
 */
export async function getUsersByProvider(provider: string): Promise<User[]> {
  const response = await fetch(`${API_BASE_URL}/users/provider/${provider}`);

  if (!response.ok) {
    throw new Error(`Failed to get users: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Check if a user exists by provider and provider ID
 */
export async function checkUserExistsByProvider(provider: string, providerId: string): Promise<boolean> {
  const response = await fetch(`${API_BASE_URL}/users/exists/provider/${provider}/${providerId}`);

  if (!response.ok) {
    throw new Error(`Failed to check user existence: ${response.statusText}`);
  }

  const data = await response.json();
  return data.exists;
}

/**
 * Check if a user exists by GitHub ID (legacy support)
 */
export async function checkUserExists(githubId: string): Promise<boolean> {
  const response = await fetch(`${API_BASE_URL}/users/exists/github/${githubId}`);

  if (!response.ok) {
    throw new Error(`Failed to check user existence: ${response.statusText}`);
  }

  const data = await response.json();
  return data.exists;
}
