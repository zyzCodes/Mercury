# Mercury Scripts

This directory contains convenient scripts for managing your Mercury development environment.

## Quick Reference

| Script | Description |
|--------|-------------|
| `./scripts/start` | Start all Docker containers |
| `./scripts/stop` | Stop all Docker containers (preserves data) |
| `./scripts/restart` | Restart all or specific containers |
| `./scripts/logs` | View container logs |
| `./scripts/test` | Run GoalsManager test suite |
| `./scripts/nuke_env` | ⚠️ **Destroy everything and start fresh** |

## Script Details

### 🚀 `start`
Start all Mercury containers (database, backend, frontend).

```bash
./scripts/start
```

**What it does:**
- Starts all containers in detached mode
- Shows you where services are available

**Services will be available at:**
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Database: localhost:5432

---

### 🛑 `stop`
Stop all running containers without deleting any data.

```bash
./scripts/stop
```

**What it does:**
- Stops all containers gracefully
- Preserves all database data and volumes
- Quick restart with `./scripts/start`

---

### 🔄 `restart`
Restart all containers or a specific service.

```bash
# Restart everything
./scripts/restart

# Restart just the backend
./scripts/restart backend

# Restart just the frontend
./scripts/restart frontend

# Restart just the database
./scripts/restart db
```

**What it does:**
- Restarts containers without rebuilding
- Preserves all data
- Useful when you've made code changes or need to refresh a service

---

### 📋 `logs`
View logs from your containers.

```bash
# View logs from all containers
./scripts/logs

# View logs from a specific container
./scripts/logs backend
./scripts/logs frontend
./scripts/logs db
```

**What it does:**
- Shows real-time logs (press Ctrl+C to exit)
- Useful for debugging and monitoring

---

### 🧪 `test`
Run the GoalsManager test suite.

```bash
./scripts/test
```

**What it does:**
- Runs all JUnit tests for the backend
- Generates HTML test reports
- Shows test results in the terminal

**After running:**
- View detailed test reports at: `GoalsManager/build/reports/tests/test/index.html`

---

### 💥 `nuke_env` (USE WITH CAUTION!)
Completely destroy and rebuild your environment.

```bash
./scripts/nuke_env
```

**⚠️ WARNING: This is a destructive operation!**

**What it does:**
1. Stops all containers
2. Removes all containers
3. **Deletes all volumes (including ALL database data)**
4. Removes all images
5. Rebuilds and starts fresh containers

**When to use:**
- When you have corrupted data
- When you want a completely clean slate
- When docker-compose configuration has changed significantly
- When you need to test a fresh installation

**Safety features:**
- Requires explicit "yes" confirmation
- Shows clear warning before execution
- Cannot be accidentally run

---

## Common Workflows

### Daily Development
```bash
# Morning: Start your environment
./scripts/start

# View what's happening
./scripts/logs backend

# Made changes to backend? Restart it
./scripts/restart backend

# End of day: Stop everything
./scripts/stop
```

### Running Tests
```bash
# Run tests before committing
./scripts/test

# Open test report in browser
open GoalsManager/build/reports/tests/test/index.html
```

### Debugging Issues
```bash
# Check logs for errors
./scripts/logs

# If things are really broken, restart
./scripts/restart

# Last resort: nuclear option
./scripts/nuke_env
```

### Fresh Start
```bash
# Nuclear option - start completely fresh
./scripts/nuke_env
# Type 'yes' when prompted
```

---

## Tips

- All scripts can be run from anywhere in the project (they auto-detect the project root)
- Scripts are safe to run multiple times
- Use `./scripts/logs` to debug issues
- Always run `./scripts/test` before pushing code
- The `nuke_env` script requires explicit confirmation to prevent accidents

---

## Troubleshooting

**Problem: Script won't run**
```bash
# Make sure scripts are executable
chmod +x scripts/*
```

**Problem: Port already in use**
```bash
# Stop containers and try again
./scripts/stop
./scripts/start
```

**Problem: Database connection issues**
```bash
# Restart just the database
./scripts/restart db
```

**Problem: Nothing works**
```bash
# Nuclear option
./scripts/nuke_env
```

