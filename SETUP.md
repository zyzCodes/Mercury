# Mercury Project Setup Guide

## Prerequisites

1. **Docker and Docker Compose** installed on your system
2. **GitHub OAuth App** created (see instructions below)

## GitHub OAuth Setup

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Click "New OAuth App"
3. Fill in the details:
   - **Application name**: Mercury Goal Tracker
   - **Homepage URL**: `http://localhost:3000`
   - **Authorization callback URL**: `http://localhost:3000/api/auth/callback/github`
4. Copy the **Client ID** and **Client Secret**

**Important**: The callback URL must be exactly `http://localhost:3000/api/auth/callback/github` for NextAuth.js to work properly.

## Environment Variables

Create a `.env` file in the root directory with the following variables:

```env
# Database Configuration
POSTGRES_DB=mercury
POSTGRES_USER=mercury_user
POSTGRES_PASSWORD=mercury_password

# GitHub OAuth Configuration
GITHUB_CLIENT_ID=your-github-client-id-here
GITHUB_CLIENT_SECRET=your-github-client-secret-here

# Google OAuth (Optional)
GOOGLE_CLIENT_ID=your-google-client-id-here
GOOGLE_CLIENT_SECRET=your-google-client-secret-here

# NextAuth Configuration
NEXTAUTH_SECRET=your-random-secret-key-here
NEXTAUTH_URL=http://localhost:3000
```

### Generating NEXTAUTH_SECRET

You can generate a secure random secret using:
```bash
openssl rand -base64 32
```

## Running the Application

1. **Start all services**:
   ```bash
   docker-compose up --build
   ```

2. **Access the application**:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Database: localhost:5432

## Authentication Flow

1. Visit http://localhost:3000
2. You'll be automatically redirected to the login page
3. Click "Sign in with GitHub"
4. Authorize the application on GitHub
5. **User account is automatically created in the database**
6. You'll be redirected to the dashboard with your profile information

## User Management System

The application automatically creates and updates user records in the PostgreSQL database after GitHub authentication:

### User Entity Fields
- `id` - Auto-generated database ID
- `githubId` - GitHub user ID (unique)
- `username` - GitHub username (unique)
- `email` - User email
- `name` - Display name
- `avatarUrl` - Profile picture URL
- `bio` - User biography
- `location` - User location
- `createdAt` - Account creation timestamp
- `updatedAt` - Last update timestamp

### API Endpoints

#### User Management
- `POST /api/users` - Create or update a user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by database ID
- `GET /api/users/github/{githubId}` - Get user by GitHub ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/exists/github/{githubId}` - Check if user exists
- `DELETE /api/users/{id}` - Delete a user

#### Authentication
- `GET /api/auth/user` - Get current user information
- `GET /api/auth/status` - Check authentication status

## Architecture

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.5.5 with Java 21
- **Database**: PostgreSQL 16
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security with OAuth2 Client

### Frontend (Next.js)
- **Framework**: Next.js 15 with React 19
- **Authentication**: NextAuth.js
- **Styling**: Tailwind CSS
- **Language**: TypeScript

### Database Schema
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
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

## Project Structure

```
Mercury/
├── GoalsManager/              # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/example/goalsmanager/
│   │       ├── config/        # Security & Web configuration
│   │       ├── controller/    # REST controllers
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── model/         # JPA entities
│   │       ├── repository/    # Database repositories
│   │       └── service/       # Business logic
│   └── build.gradle           # Dependencies
├── photon/                    # Next.js Frontend
│   ├── app/                   # Next.js app directory
│   │   ├── api/auth/          # NextAuth.js configuration
│   │   ├── dashboard/         # Dashboard page
│   │   └── login/             # Login page
│   ├── components/            # React components
│   ├── hooks/                 # Custom React hooks
│   ├── lib/                   # API utilities
│   └── types/                 # TypeScript type definitions
└── docker-compose.yml         # Docker orchestration
```

## Troubleshooting

### GitHub OAuth 404 Error
- Make sure your GitHub OAuth app callback URL is exactly: `http://localhost:3000/api/auth/callback/github`
- Verify that `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` in `.env` are correct
- The client ID should NOT be the placeholder `your_github_client_id_here`

### Database Connection Issues
- Ensure PostgreSQL container is running: `docker-compose ps`
- Check database logs: `docker-compose logs db`
- Verify database credentials in `.env` file

### Frontend Can't Connect to Backend
- Make sure backend is running on port 8080
- Check backend logs: `docker-compose logs backend`
- Verify `NEXT_PUBLIC_API_URL` is set correctly

### User Not Created in Database
- Check backend logs for errors during sign-in
- Verify database connection is working
- The backend should automatically create users on first login

### General Debugging
- Check all container statuses: `docker-compose ps`
- View logs for specific service: `docker-compose logs [service-name]`
- Restart all services: `docker-compose restart`
- Rebuild containers: `docker-compose up --build`

## Next Steps

After successful setup, you can:
1. View your user profile in the dashboard
2. Create goals and track progress (coming soon)
3. Manage your account settings (coming soon)
