/**
 * Notes API utility functions for communicating with the backend
 */

// For server-side calls (inside Docker), use the container name
// For client-side calls (browser), use localhost
const isServer = typeof window === 'undefined';
const SERVER_API_URL = process.env.API_URL || 'http://backend:8080/api';
const CLIENT_API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';
const API_BASE_URL = isServer ? SERVER_API_URL : CLIENT_API_URL;

export interface Note {
  id: number;
  content: string;
  goalId: number;
  createdAt: string;
}

export interface CreateNoteRequest {
  content: string;
  goalId: number;
}

export interface UpdateNoteRequest {
  content: string;
}

/**
 * Create a new note
 */
export async function createNote(noteData: CreateNoteRequest): Promise<Note> {
  const response = await fetch(`${API_BASE_URL}/notes`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(noteData),
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: response.statusText }));
    throw new Error(error.error || `Failed to create note: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get a note by ID
 */
export async function getNoteById(id: number): Promise<Note | null> {
  const response = await fetch(`${API_BASE_URL}/notes/${id}`);

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error(`Failed to get note: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Get all notes for a goal, ordered by creation date descending
 */
export async function getNotesByGoalId(goalId: number): Promise<Note[]> {
  const response = await fetch(`${API_BASE_URL}/notes/goal/${goalId}`);

  if (!response.ok) {
    throw new Error(`Failed to get notes: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Update a note
 */
export async function updateNote(id: number, noteData: UpdateNoteRequest): Promise<Note> {
  const response = await fetch(`${API_BASE_URL}/notes/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(noteData),
  });

  if (!response.ok) {
    throw new Error(`Failed to update note: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Delete a note
 */
export async function deleteNote(id: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/notes/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw new Error(`Failed to delete note: ${response.statusText}`);
  }
}

/**
 * Count notes for a goal
 */
export async function countNotesByGoalId(goalId: number): Promise<number> {
  const response = await fetch(`${API_BASE_URL}/notes/goal/${goalId}/count`);

  if (!response.ok) {
    throw new Error(`Failed to count notes: ${response.statusText}`);
  }

  const data = await response.json();
  return data.count;
}
