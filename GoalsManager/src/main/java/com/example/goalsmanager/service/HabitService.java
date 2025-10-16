package com.example.goalsmanager.service;

import com.example.goalsmanager.dto.CreateHabitRequest;
import com.example.goalsmanager.dto.HabitDTO;
import com.example.goalsmanager.dto.UpdateHabitRequest;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.Habit;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.GoalRepository;
import com.example.goalsmanager.repository.HabitRepository;
import com.example.goalsmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HabitService {

    private final HabitRepository habitRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Autowired
    public HabitService(HabitRepository habitRepository,
                        GoalRepository goalRepository,
                        UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new habit
     */
    public HabitDTO createHabit(CreateHabitRequest request) {
        // Validate goal exists
        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + request.getGoalId()));

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Validate dates
        validateDates(request.getStartDate(), request.getEndDate());

        // Create habit
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setDaysOfWeek(request.getDaysOfWeek());
        habit.setStartDate(request.getStartDate());
        habit.setEndDate(request.getEndDate());
        habit.setStreakStatus(0);
        habit.setColor(request.getColor());
        habit.setGoal(goal);
        habit.setUser(user);

        Habit savedHabit = habitRepository.save(habit);
        return convertToDTO(savedHabit);
    }

    /**
     * Get habit by ID
     */
    @Transactional(readOnly = true)
    public HabitDTO getHabitById(Long id) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found with id: " + id));
        return convertToDTO(habit);
    }

    /**
     * Get all habits
     */
    @Transactional(readOnly = true)
    public List<HabitDTO> getAllHabits() {
        return habitRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all habits for a specific user
     */
    @Transactional(readOnly = true)
    public List<HabitDTO> getHabitsByUserId(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return habitRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all habits for a specific goal
     */
    @Transactional(readOnly = true)
    public List<HabitDTO> getHabitsByGoalId(Long goalId) {
        // Verify goal exists
        if (!goalRepository.existsById(goalId)) {
            throw new RuntimeException("Goal not found with id: " + goalId);
        }
        return habitRepository.findByGoalId(goalId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update habit
     */
    public HabitDTO updateHabit(Long id, UpdateHabitRequest request) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found with id: " + id));

        // Update only non-null fields
        if (request.getName() != null) {
            habit.setName(request.getName());
        }
        if (request.getDescription() != null) {
            habit.setDescription(request.getDescription());
        }
        if (request.getDaysOfWeek() != null) {
            habit.setDaysOfWeek(request.getDaysOfWeek());
        }
        if (request.getStartDate() != null) {
            habit.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            habit.setEndDate(request.getEndDate());
        }
        if (request.getStreakStatus() != null) {
            habit.setStreakStatus(request.getStreakStatus());
        }
        if (request.getColor() != null) {
            habit.setColor(request.getColor());
        }

        // Validate dates if both are present
        if (habit.getStartDate() != null && habit.getEndDate() != null) {
            validateDates(habit.getStartDate(), habit.getEndDate());
        }

        Habit updatedHabit = habitRepository.save(habit);
        return convertToDTO(updatedHabit);
    }

    /**
     * Delete habit
     */
    public void deleteHabit(Long id) {
        if (!habitRepository.existsById(id)) {
            throw new RuntimeException("Habit not found with id: " + id);
        }
        habitRepository.deleteById(id);
    }

    /**
     * Check if a habit exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return habitRepository.existsById(id);
    }

    /**
     * Count habits by user
     */
    @Transactional(readOnly = true)
    public long countHabitsByUserId(Long userId) {
        return habitRepository.countByUserId(userId);
    }

    /**
     * Count habits by goal
     */
    @Transactional(readOnly = true)
    public long countHabitsByGoalId(Long goalId) {
        return habitRepository.countByGoalId(goalId);
    }

    /**
     * Validate that end date is after start date
     */
    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new RuntimeException("End date must be after start date");
        }
    }

    /**
     * Convert Habit entity to HabitDTO
     */
    private HabitDTO convertToDTO(Habit habit) {
        return new HabitDTO(
                habit.getId(),
                habit.getName(),
                habit.getDescription(),
                habit.getDaysOfWeek(),
                habit.getStartDate(),
                habit.getEndDate(),
                habit.getStreakStatus(),
                habit.getColor(),
                habit.getGoal().getId(),
                habit.getGoal().getTitle(),
                habit.getUser().getId(),
                habit.getUser().getUsername(),
                habit.getCreatedAt(),
                habit.getUpdatedAt()
        );
    }
}

