# User Management System Implementation Summary

## Overview

Implemented a complete user management system that automatically creates and updates user records in the PostgreSQL database after GitHub OAuth authentication.

## What Was Created

### Backend (Spring Boot) - `/GoalsManager/src/main/java/com/example/goalsmanager/`

#### 1. **User Entity** (`model/User.java`)

- JPA entity representing users in the database
- Fields:
  - `id` (Long) - Primary key, auto-generated
  - `githubId` (String) - GitHub user ID, unique, required
  - `username` (String) - GitHub username, unique, required
  - `email` (String) - User email, unique
  - `name` (String) - Display name
  - `avatarUrl` (String) - Profile picture URL
  - `bio` (String) - User biography
  - `location` (String) - User location
  - `createdAt` (LocalDateTime) - Auto-generated timestamp
  - `updatedAt` (LocalDateTime) - Auto-updated timestamp

#### 2. **User Repository** (`repository/UserRepository.java`)

- Spring Data JPA repository interface
- Custom query methods:
  - `findByGithubId(String githubId)`
  - `findByUsername(String username)`
  - `findByEmail(String email)`
  - `existsByGithubId(String githubId)`
  - `existsByUsername(String username)`
  - `existsByEmail(String email)`

#### 3. **User Service** (`service/UserService.java`)

- Business logic layer for user operations
- Key methods:
  - `createOrUpdateUser()` - Creates new user or updates existing by GitHub ID
  - `getUserById()` - Fetch user by database ID
  - `getUserByGithubId()` - Fetch user by GitHub ID
  - `getUserByUsername()` - Fetch user by username
  - `getUserByEmail()` - Fetch user by email
  - `getAllUsers()` - Fetch all users
  - `deleteUser()` - Delete user by ID
  - `existsByGithubId()` - Check if user exists

#### 4. **User Controller** (`controller/UserController.java`)

- REST API endpoints at `/api/users`:
  - `POST /api/users` - Create or update user
  - `GET /api/users` - Get all users
  - `GET /api/users/{id}` - Get user by ID
  - `GET /api/users/github/{githubId}` - Get user by GitHub ID
  - `GET /api/users/username/{username}` - Get user by username
  - `GET /api/users/email/{email}` - Get user by email
  - `GET /api/users/exists/github/{githubId}` - Check if user exists
  - `DELETE /api/users/{id}` - Delete user

#### 5. **DTOs** (`dto/`)

- `UserDTO.java` - Data transfer object for user responses
- `CreateUserRequest.java` - Request DTO for creating/updating users

### Frontend (Next.js) - `/photon/`

#### 1. **API Utility** (`lib/api.ts`)

- TypeScript utility functions for backend API calls
- Interfaces:
  - `User` - TypeScript type matching backend User entity
  - `CreateUserRequest` - Type for user creation requests
- Functions:
  - `createOrUpdateUser()` - Create/update user in backend
  - `getUserByGithubId()` - Fetch user by GitHub ID
  - `getUserById()` - Fetch user by database ID
  - `getAllUsers()` - Fetch all users
  - `checkUserExists()` - Check if user exists

#### 2. **NextAuth Configuration Update** (`app/api/auth/[...nextauth]/route.ts`)

- Added `signIn` callback that:
  - Triggers after successful GitHub authentication
  - Extracts user data from GitHub profile
  - Calls backend API to create/update user record
  - Gracefully handles errors (allows login even if backend fails)
- Added `githubId` to session and JWT token

#### 3. **Custom React Hook** (`hooks/useUser.ts`)

- `useUser()` hook for easy user data access
- Returns:
  - `user` - User data from backend database
  - `loading` - Loading state
  - `error` - Error message if any
  - `session` - NextAuth session data
  - `isAuthenticated` - Boolean authentication status

#### 4. **Enhanced Dashboard** (`app/dashboard/page.tsx`)

- Updated to use `useUser` hook
- Displays comprehensive user profile:
  - Profile picture
  - Name and username
  - Database ID
  - GitHub ID
  - Email
  - Member since date
  - Location (if available)
  - Bio (if available)
- Shows error messages if user data fails to load
- Maintains loading states

#### 5. **Type Definitions Update** (`types/next-auth.d.ts`)

- Extended NextAuth types to include `githubId`
- Added JWT token types

### Authentication & User Creation Flow

1. **User clicks "Sign in with GitHub"**
   - Redirected to GitHub for authorization

2. **GitHub authenticates and redirects back**
   - NextAuth.js receives OAuth callback at `/api/auth/callback/github`

3. **SignIn callback executes**
   - Extracts user data from GitHub profile
   - Calls `createOrUpdateUser()` function

4. **Backend processes request**
   - Checks if user exists by `githubId`
   - If exists: updates user information
   - If new: creates new user record
   - Returns user data with database ID

5. **User redirected to dashboard**
   - `useUser` hook fetches complete user data from backend
   - Dashboard displays user profile with database information

## Database Schema

The `users` table is automatically created by Hibernate with this structure:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    github_id VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    avatar_url VARCHAR(500),
    bio TEXT,
    location VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Testing the System

1. **Start the application**:

   ```bash
   docker-compose up --build
   ```

2. **Test API endpoints** (using curl or Postman):

   ```bash
   # Get all users
   curl http://localhost:8080/api/users

   # Get user by ID
   curl http://localhost:8080/api/users/1

   # Get user by GitHub ID
   curl http://localhost:8080/api/users/github/YOUR_GITHUB_ID
   ```
