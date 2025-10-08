# Goals Management System Implementation

## Overview

Complete implementation of the Goals management system that allows users to create, track, and manage their goals. Each user can have 0 or multiple goals, but each goal must belong to exactly one user.

## Database Schema

### Goals Table

The `goals` table is automatically created by Hibernate with this structure:

```sql
CREATE TABLE goals (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Relationship

- **User to Goal**: One-to-Many (One user can have multiple goals)
- **Goal to User**: Many-to-One (Each goal belongs to exactly one user)
- Cascade: When a user is deleted, all their goals are automatically deleted (orphan removal)

## What Was Created

### Backend (Spring Boot) - `/GoalsManager/src/main/java/com/example/goalsmanager/`

#### 1. **Goal Status Enum** (`goalutils/GoalStatus.java`)

Defines the possible states of a goal:
- `NOT_STARTED` - Goal has been created but not started
- `IN_PROGRESS` - Goal is actively being worked on
- `COMPLETED` - Goal has been completed
- `PAUSED` - Goal is temporarily paused
- `CANCELLED` - Goal has been cancelled

#### 2. **Goal Entity** (`model/Goal.java`)

JPA entity representing goals in the database.

**Fields:**
- `id` (Long) - Primary key, auto-generated
- `title` (String) - Goal title, required
- `description` (String) - Detailed description
- `imageUrl` (String) - Optional image URL
- `startDate` (LocalDate) - When the goal starts, required
- `endDate` (LocalDate) - Target completion date, required
- `status` (GoalStatus) - Current status, defaults to NOT_STARTED
- `notes` (String) - Progress notes and journaling
- `user` (User) - Reference to the user who owns this goal, required
- `createdAt` (LocalDateTime) - Auto-generated timestamp
- `updatedAt` (LocalDateTime) - Auto-updated timestamp

**Relationships:**
- `@ManyToOne` relationship with User (lazy loaded)

#### 3. **Updated User Entity** (`model/User.java`)

Added bidirectional relationship to goals:
- `@OneToMany` relationship with Goal
- Cascade ALL operations and orphan removal enabled
- Helper methods: `addGoal()` and `removeGoal()`

#### 4. **Goal Repository** (`repository/GoalRepository.java`)

Spring Data JPA repository interface with custom query methods:
- `findByUserId(Long userId)` - Get all goals for a user
- `findByUserIdAndStatus(Long userId, GoalStatus status)` - Filter by status
- `findByStatus(GoalStatus status)` - Find goals by status
- `findByEndDateBefore(LocalDate date)` - Find goals ending before a date
- `findByEndDateAfter(LocalDate date)` - Find goals ending after a date
- `findByUserIdAndStartDateBetween(...)` - Date range queries
- `existsByUserId(Long userId)` - Check if user has goals
- `countByUserId(Long userId)` - Count user's goals
- `countByUserIdAndStatus(...)` - Count goals by status

#### 5. **DTOs** (`dto/`)

**GoalDTO.java** - Data transfer object for goal responses
- Includes all goal fields plus username
- Used for API responses

**CreateGoalRequest.java** - Request DTO for creating goals
- Required fields: title, startDate, endDate, userId
- Optional fields: description, imageUrl, status, notes
- Includes validation annotations

**UpdateGoalRequest.java** - Request DTO for updating goals
- All fields optional (partial updates supported)
- Only provided fields will be updated

#### 6. **Goal Service** (`service/GoalService.java`)

Business logic layer with comprehensive methods:

**CRUD Operations:**
- `createGoal()` - Create a new goal with validation
- `getGoalById()` - Fetch goal by ID
- `getAllGoals()` - Get all goals in the system
- `updateGoal()` - Update goal (partial updates supported)
- `deleteGoal()` - Delete a goal

**User-Specific Queries:**
- `getGoalsByUserId()` - Get all goals for a user
- `getGoalsByUserIdAndStatus()` - Filter by status
- `getActiveGoalsByUserId()` - Get in-progress/not-started goals
- `getCompletedGoalsByUserId()` - Get completed goals
- `getOverdueGoalsByUserId()` - Get goals past end date and not completed

**Specialized Operations:**
- `updateGoalStatus()` - Update only the status
- `updateGoalNotes()` - Add/update progress notes
- `countGoalsByUserId()` - Count user's goals
- `countGoalsByUserIdAndStatus()` - Count by status
- `existsById()` - Check if goal exists

**Business Logic:**
- Date validation (end date must be after start date)
- User existence verification
- Automatic DTO conversion
- Transaction management

#### 7. **Goal Controller** (`controller/GoalController.java`)

REST API endpoints at `/api/goals`:

**Goal Management:**
- `POST /api/goals` - Create a new goal
- `GET /api/goals` - Get all goals
- `GET /api/goals/{id}` - Get goal by ID
- `PUT /api/goals/{id}` - Update a goal
- `DELETE /api/goals/{id}` - Delete a goal

**User-Specific Endpoints:**
- `GET /api/goals/user/{userId}` - Get all goals for a user
- `GET /api/goals/user/{userId}/active` - Get active goals
- `GET /api/goals/user/{userId}/completed` - Get completed goals
- `GET /api/goals/user/{userId}/overdue` - Get overdue goals
- `GET /api/goals/user/{userId}/status/{status}` - Filter by status
- `GET /api/goals/user/{userId}/count` - Count user's goals
- `GET /api/goals/user/{userId}/count/{status}` - Count by status

**Status and Notes:**
- `PATCH /api/goals/{id}/status` - Update goal status
- `PATCH /api/goals/{id}/notes` - Update goal notes

**Utility Endpoints:**
- `GET /api/goals/status/{status}` - Get all goals by status
- `GET /api/goals/exists/{id}` - Check if goal exists

**Features:**
- Comprehensive error handling
- Input validation using Jakarta Validation
- CORS enabled for frontend integration
- Consistent JSON response format

## API Usage Examples

### Create a Goal

```bash
curl -X POST http://localhost:8080/api/goals \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Spring Boot",
    "description": "Master Spring Boot framework and build REST APIs",
    "startDate": "2025-01-01",
    "endDate": "2025-12-31",
    "userId": 1
  }'
```

### Get All Goals for a User

```bash
curl http://localhost:8080/api/goals/user/1
```

### Update Goal Status

```bash
curl -X PATCH http://localhost:8080/api/goals/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "IN_PROGRESS"}'
```

### Add Progress Notes

```bash
curl -X PATCH http://localhost:8080/api/goals/1/notes \
  -H "Content-Type: application/json" \
  -d '{"notes": "Made great progress today! Completed chapter 5."}'
```

### Update a Goal

```bash
curl -X PUT http://localhost:8080/api/goals/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Master Spring Boot",
    "description": "Updated description",
    "status": "IN_PROGRESS"
  }'
```

### Get Active Goals

```bash
curl http://localhost:8080/api/goals/user/1/active
```

### Delete a Goal

```bash
curl -X DELETE http://localhost:8080/api/goals/1
```

## Testing the System

1. **Start the application**:
   ```bash
   docker-compose up --build
   ```

2. **Create a user** (if you haven't already):
   - Sign in with GitHub at `http://localhost:3000`
   - This will create a user in the database

3. **Create a goal using the API**:
   ```bash
   curl -X POST http://localhost:8080/api/goals \
     -H "Content-Type: application/json" \
     -d '{
       "title": "Complete Project Mercury",
       "description": "Build a full-stack goal tracking application",
       "startDate": "2025-01-01",
       "endDate": "2025-06-30",
       "userId": 1
     }'
   ```

4. **View goals**:
   ```bash
   # Get all goals for user 1
   curl http://localhost:8080/api/goals/user/1
   
   # Get active goals
   curl http://localhost:8080/api/goals/user/1/active
   
   # Get goal count
   curl http://localhost:8080/api/goals/user/1/count
   ```

Example TypeScript interface:

```typescript
interface Goal {
  id: number;
  title: string;
  description?: string;
  imageUrl?: string;
  startDate: string;
  endDate: string;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'PAUSED' | 'CANCELLED';
  notes?: string;
  userId: number;
  username: string;
  createdAt: string;
  updatedAt: string;
}
```
