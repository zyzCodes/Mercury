# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Mercury is a full-stack goal-tracking web application that helps users create, manage, and monitor their annual goals through habits and tasks. The application uses a modern tech stack with Spring Boot backend, Next.js frontend, and PostgreSQL database, all containerized with Docker.

## Development Commands

### Docker Environment
```bash
# Start all services (frontend, backend, database)
./scripts/start

# Stop all services (preserves data)
./scripts/stop

# Restart services (for config changes, no code rebuild)
./scripts/restart [backend|frontend|db]

# Rebuild backend/frontend after CODE CHANGES (rebuilds and applies DB schema updates)
./scripts/rebuild [backend|frontend|all]

# View logs
./scripts/logs [backend|frontend|db]

# Complete environment reset (WARNING: deletes all data)
./scripts/nuke_env
```

**When to use each script:**
- `./scripts/restart backend` - Quick restart for config/env changes (no rebuild)
- `./scripts/rebuild backend` - After Java code changes, new entity fields, or DB schema modifications
- `./scripts/rebuild frontend` - After adding npm packages or build config changes

### Backend (Spring Boot)
```bash
# Run tests
./scripts/test
# OR from GoalsManager directory:
cd GoalsManager && ./gradlew test

# Build without tests
cd GoalsManager && ./gradlew build -x test

# Clean build
cd GoalsManager && ./gradlew clean build

# View test reports
open GoalsManager/build/reports/tests/test/index.html
```

### Frontend (Next.js)
```bash
cd photon

# Run development server (without Docker)
npm run dev

# Build for production
npm run build

# Start production server
npm start

# Lint
npm run lint
```

## Architecture

### Tech Stack
- **Backend**: Spring Boot 3.5.5 with Java 21, Gradle build system
- **Frontend**: Next.js 15 with React 19, TypeScript, Tailwind CSS 4
- **Database**: PostgreSQL 16 (automatically managed by Hibernate JPA)
- **Authentication**: NextAuth.js with OAuth2 (GitHub, Google)
- **Containerization**: Docker Compose with 3 services (db, backend, frontend)

### Project Structure
```
Mercury/
├── GoalsManager/                              # Spring Boot Backend
│   └── src/main/java/com/example/goalsmanager/
│       ├── config/          # Security & CORS configuration
│       ├── controller/      # REST API endpoints
│       ├── dto/             # Data Transfer Objects for requests/responses
│       ├── model/           # JPA entities (User, Goal, Habit, Task, Note)
│       ├── repository/      # Spring Data JPA repositories
│       ├── service/         # Business logic layer
│       └── goalutils/       # Utilities (e.g., GoalStatus enum)
│
├── photon/                  # Next.js Frontend
│   ├── app/                 # Next.js App Router pages
│   │   ├── api/auth/        # NextAuth.js API routes
│   │   ├── dashboard/       # Main dashboard page
│   │   ├── goals/           # Goal detail pages
│   │   ├── tasks/           # Task management pages
│   │   └── login/           # Authentication page
│   ├── components/          # React components
│   ├── hooks/               # Custom React hooks (useUser)
│   ├── lib/                 # API client functions (api.ts, goals-api.ts, etc.)
│   └── types/               # TypeScript type definitions
│
├── scripts/                 # Development utility scripts
└── docker-compose.yml       # Docker orchestration
```

## Data Model & Relationships

The application has a hierarchical data model centered around users and their goals:

### Core Entities

1. **User** (Authentication entity)
   - Multi-provider support: GitHub, Google, JWT-ready
   - Fields: `provider`, `providerId`, `username`, `email`, `name`, `avatarUrl`, `bio`, `location`
   - Relationships: One-to-Many with Goal, Habit, Task, Note

2. **Goal** (Main tracking entity)
   - Fields: `title`, `description`, `imageUrl`, `startDate`, `endDate`, `status`, `notes`
   - Status enum: `NOT_STARTED`, `IN_PROGRESS`, `COMPLETED`, `PAUSED`, `CANCELLED`
   - Relationships:
     - Many-to-One with User
     - One-to-Many with Habit
     - One-to-Many with Note

3. **Habit** (Recurring activities for goals)
   - Fields: `name`, `description`, `daysOfWeek` (comma-separated: "Mon,Wed,Fri"), `startDate`, `endDate`, `streakStatus`
   - Relationships:
     - Many-to-One with User
     - Many-to-One with Goal
     - One-to-Many with Task

4. **Task** (Daily completions for habits)
   - Fields: `name`, `completed` (Boolean), `date` (LocalDate)
   - Relationships:
     - Many-to-One with User
     - Many-to-One with Habit
   - **Important**: When tasks are completed, they update the parent habit's streak status

5. **Note** (Progress journaling for goals)
   - Fields: `content`, `createdAt`
   - Relationships: Many-to-One with Goal and User

### Cascade Behavior
- Deleting a User cascades to all their Goals, Habits, Tasks, and Notes
- Deleting a Goal cascades to all its Habits and Notes
- Deleting a Habit cascades to all its Tasks
- All relationships use orphan removal where appropriate

## API Structure

All backend REST endpoints follow the pattern `/api/{resource}`:

### Authentication
- Backend: Spring Security with OAuth2 Client
- Frontend: NextAuth.js manages sessions
- User data synchronized between NextAuth session and backend database

### Resource Endpoints Pattern
Each entity (User, Goal, Habit, Task, Note) follows this pattern:
- `POST /api/{resource}` - Create
- `GET /api/{resource}` - Get all
- `GET /api/{resource}/{id}` - Get by ID
- `PUT /api/{resource}/{id}` - Update (partial updates supported)
- `DELETE /api/{resource}/{id}` - Delete
- `GET /api/{resource}/user/{userId}` - Get by user
- `GET /api/{resource}/exists/{id}` - Check existence
- `GET /api/{resource}/user/{userId}/count` - Count

### Special Endpoints
- Goals: `/api/goals/user/{userId}/active`, `/api/goals/user/{userId}/overdue`
- Habits: `/api/habits/goal/{goalId}`
- Tasks: `/api/tasks/{id}/toggle` (toggle completion), `/api/tasks/user/{userId}/week?startDate=&endDate=`
- Users: `/api/users/provider/{provider}/{providerId}` (multi-provider support)

## Key Implementation Patterns

### Backend Patterns
1. **DTOs for all API responses**: Never expose entities directly, always convert to DTOs
2. **Service layer validation**: Check entity existence before operations (e.g., verify User and Goal exist before creating Habit)
3. **Bidirectional relationships**: Use helper methods (`addGoal()`, `removeGoal()`) to maintain both sides
4. **Consistent error handling**: Return appropriate HTTP status codes with meaningful messages
5. **Transaction management**: Use `@Transactional` for operations that modify data
6. **Use `final` modifiers liberally**: Apply `final` to classes, fields, method parameters, and local variables where appropriate (see Java Code Style section below)

### Frontend Patterns
1. **API client functions**: All API calls are in `/lib/*-api.ts` files, never inline in components
2. **Type safety**: TypeScript interfaces for all entities match backend DTOs
3. **Custom hooks**: Use `useUser()` hook for authentication state
4. **Server components**: Default to Server Components, use Client Components only when needed (interactivity, hooks)
5. **Environment variables**: Use `NEXT_PUBLIC_*` for client-side, non-prefixed for server-side only

### Testing Patterns
1. **Repository tests**: Use `@DataJpaTest` with `TestEntityManager` for setup
2. **Service tests**: Use Mockito to mock repositories and dependencies
3. **Controller tests**: Integration tests with `MockMvc` and `@WebMvcTest`
4. **Test data**: Use H2 in-memory database for tests (configured in dependencies)

### Java Code Style

#### Use of `final` Modifier
Apply the `final` modifier liberally throughout the Java codebase to improve code quality, clarity, and immutability. Follow these guidelines:

**DO use `final` for:**
1. **Classes** that are not designed to be extended:
   - Utility classes
   - Most DTO classes (when they don't need to be extended)
   - **Note**: Do NOT use `final` on `@Configuration` classes - Spring needs to create CGLIB proxies

2. **Fields** that should be immutable after construction:
   - Constructor-injected dependencies in controllers and services
   - Constants (use with `static final`)
   - Any field that doesn't need to be reassigned

3. **Method parameters** - Add `final` to ALL method parameters in:
   - Controllers (e.g., `@PathVariable`, `@RequestBody`, `@RequestParam` parameters)
   - Services (all public and private method parameters)
   - Utility methods

4. **Local variables** that are not reassigned:
   - Variables initialized once and never modified
   - Variables in try-catch blocks (catch parameters)
   - Return value assignments that are returned immediately

**DO NOT use `final` for:**
1. **Spring `@Configuration` classes**:
   - Spring needs to create CGLIB proxies for configuration classes
   - Will cause runtime errors if marked `final`

2. **JPA Entity classes** (User, Goal, Habit, Task, Note):
   - Must remain non-final to allow JPA proxy generation
   - Entity fields with setters should remain mutable for framework compatibility

3. **Entity fields** that need setters:
   - JPA entities require mutable fields for ORM operations
   - Fields annotated with `@Column`, `@OneToMany`, `@ManyToOne`, etc.

4. **Repository interfaces**:
   - Keep interface method signatures clean
   - Spring Data JPA generates implementations

5. **DTO fields** (optional but recommended):
   - DTO fields typically have setters for framework compatibility
   - Constructor parameters in DTOs can remain non-final for simplicity

**Example:**
```java
// Configuration class - NOT marked final (Spring needs to proxy it)
@Configuration
public class SecurityConfig {

    // Method parameters marked final
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        // Local variable marked final
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        return http.build();
    }
}

// Controller with final injected dependency and parameters
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Injected dependency marked final
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody final CreateUserRequest request) {
        final UserDTO user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }
}

// Entity class - NOT marked final
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Not final - needs setter for JPA

    private String email;  // Not final - needs setter for JPA

    // Standard getters and setters...
}
```

**Benefits:**
- **Immutability**: Clear intent that values should not change
- **Thread safety**: Helps reason about concurrent access
- **Error prevention**: Prevents accidental reassignment bugs
- **Code clarity**: Signals design intent to other developers
- **Compiler optimization**: Enables potential optimizations

## Authentication Flow

1. User clicks sign-in with GitHub or Google on frontend
2. NextAuth.js redirects to OAuth provider
3. After authorization, NextAuth callback extracts provider info
4. Frontend calls backend `/api/users` to create/update user with `provider`, `providerId`
5. Backend stores user in PostgreSQL with provider information
6. NextAuth creates session with provider data
7. Frontend uses `useUser()` hook to fetch complete user data

**Important**: Always use `provider` + `providerId` for user identification, not provider-specific fields like `githubId` (kept for backward compatibility only).

## Environment Configuration

Required `.env` file in project root:
```env
# Database
POSTGRES_DB=mercury
POSTGRES_USER=mercury_user
POSTGRES_PASSWORD=your_secure_password

# NextAuth
NEXTAUTH_SECRET=your_random_secret_32_chars
NEXTAUTH_URL=http://localhost:3000

# OAuth Providers (at least one required)
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# API URLs
NEXT_PUBLIC_API_URL=http://localhost:8080/api
API_URL=http://backend:8080/api
```

## Common Development Workflows

### Adding a New Feature Endpoint
1. Create entity in `model/` (if needed)
2. Create repository interface in `repository/` with custom queries
3. Create DTOs in `dto/` (Response DTO, Create Request, Update Request)
4. Implement service in `service/` with business logic and validation
5. Create controller in `controller/` with REST endpoints
6. Add corresponding API functions in `photon/lib/*-api.ts`
7. Write tests for repository, service, and controller

### Modifying Entity Relationships
1. Update entity classes with JPA annotations
2. Add helper methods for bidirectional relationships
3. Update DTOs to include new fields
4. Update service layer to handle relationship management
5. **Rebuild backend**: `./scripts/rebuild backend` (Hibernate will auto-update schema on container startup)

### Frontend Component Development
1. Create API client functions in `/lib/*-api.ts` first
2. Define TypeScript interfaces matching backend DTOs
3. Use Server Components by default for data fetching
4. Only use "use client" directive when needed (useState, useEffect, event handlers)
5. Style with Tailwind CSS classes

## Important Notes

### Database Schema Management
- Hibernate auto-updates schema in development (no manual migrations needed)
- Database is persisted in Docker volume `postgres_data`
- Use `./scripts/nuke_env` to reset database (WARNING: destroys all data)

### Testing Before Commits
- Always run `./scripts/test` before committing backend changes
- Test reports available at `GoalsManager/build/reports/tests/test/index.html`
- Frontend linting: `cd photon && npm run lint`

### Working with Docker
- Frontend has hot reload enabled (changes reflect immediately)
- **Backend code changes**: Use `./scripts/rebuild backend` to rebuild and apply schema updates
- **Quick restart** (config only): Use `./scripts/restart backend`
- Database changes persist across restarts (stored in Docker volume)
- **Important**: Always use `./scripts/rebuild backend` after modifying entity classes or adding fields

### Current Development Branch
- Main branch: `main`
- Current feature branch: `habits` (habit and task functionality)

### Streak Updates
When a task is completed, the frontend updates the associated habit's streak status via the backend API. This is implemented in the task completion logic.

### Known Issues
Some pre-existing Goal tests have compilation errors related to Note relationships - these are unrelated to current Habit/Task implementation and should be addressed separately.
