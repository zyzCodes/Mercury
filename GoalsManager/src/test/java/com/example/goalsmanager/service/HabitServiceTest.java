package com.example.goalsmanager.service;

import com.example.goalsmanager.dto.CreateHabitRequest;
import com.example.goalsmanager.dto.HabitDTO;
import com.example.goalsmanager.dto.UpdateHabitRequest;
import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.Habit;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.GoalRepository;
import com.example.goalsmanager.repository.HabitRepository;
import com.example.goalsmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Habit Service Tests")
class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HabitService habitService;

    private User testUser;
    private Goal testGoal;
    private Habit testHabit;
    private CreateHabitRequest createRequest;

    @BeforeEach
    void setUp() {
        // Set up test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setProvider("github");
        testUser.setProviderId("12345");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        // Set up test goal
        testGoal = new Goal();
        testGoal.setId(1L);
        testGoal.setTitle("Get Fit");
        testGoal.setDescription("Improve overall fitness");
        testGoal.setStartDate(LocalDate.of(2025, 1, 1));
        testGoal.setEndDate(LocalDate.of(2025, 12, 31));
        testGoal.setStatus(GoalStatus.IN_PROGRESS);
        testGoal.setUser(testUser);

        // Set up test habit
        testHabit = new Habit();
        testHabit.setId(1L);
        testHabit.setName("Morning Run");
        testHabit.setDescription("Run 5km every morning");
        testHabit.setDaysOfWeek("Mon,Wed,Fri");
        testHabit.setStartDate(LocalDate.of(2025, 1, 1));
        testHabit.setEndDate(LocalDate.of(2025, 12, 31));
        testHabit.setStreakStatus(0);
        testHabit.setColor("#FF5733");
        testHabit.setGoal(testGoal);
        testHabit.setUser(testUser);
        testHabit.setCreatedAt(LocalDateTime.now());
        testHabit.setUpdatedAt(LocalDateTime.now());

        // Set up create request
        createRequest = new CreateHabitRequest();
        createRequest.setName("Morning Run");
        createRequest.setDescription("Run 5km every morning");
        createRequest.setDaysOfWeek("Mon,Wed,Fri");
        createRequest.setStartDate(LocalDate.of(2025, 1, 1));
        createRequest.setEndDate(LocalDate.of(2025, 12, 31));
        createRequest.setColor("#FF5733");
        createRequest.setGoalId(1L);
        createRequest.setUserId(1L);
    }

    @Test
    @DisplayName("Should create habit successfully")
    void shouldCreateHabitSuccessfully() {
        // Given
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        // When
        HabitDTO result = habitService.createHabit(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Morning Run");
        assertThat(result.getDescription()).isEqualTo("Run 5km every morning");
        assertThat(result.getDaysOfWeek()).isEqualTo("Mon,Wed,Fri");
        assertThat(result.getGoalId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        
        verify(goalRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    @DisplayName("Should throw exception when creating habit for non-existent goal")
    void shouldThrowExceptionWhenGoalNotFound() {
        // Given
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> habitService.createHabit(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Goal not found");
        
        verify(goalRepository, times(1)).findById(1L);
        verify(habitRepository, never()).save(any(Habit.class));
    }

    @Test
    @DisplayName("Should throw exception when creating habit for non-existent user")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> habitService.createHabit(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(goalRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(habitRepository, never()).save(any(Habit.class));
    }

    @Test
    @DisplayName("Should throw exception when end date is before start date")
    void shouldThrowExceptionWhenInvalidDates() {
        // Given
        createRequest.setStartDate(LocalDate.of(2025, 12, 31));
        createRequest.setEndDate(LocalDate.of(2025, 1, 1));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> habitService.createHabit(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("End date must be after start date");
        
        verify(habitRepository, never()).save(any(Habit.class));
    }

    @Test
    @DisplayName("Should get habit by ID successfully")
    void shouldGetHabitByIdSuccessfully() {
        // Given
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));

        // When
        HabitDTO result = habitService.getHabitById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Morning Run");
        
        verify(habitRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when habit not found by ID")
    void shouldThrowExceptionWhenHabitNotFoundById() {
        // Given
        when(habitRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> habitService.getHabitById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Habit not found");
        
        verify(habitRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all habits for user")
    void shouldGetAllHabitsForUser() {
        // Given
        Habit habit2 = new Habit();
        habit2.setId(2L);
        habit2.setName("Meditation");
        habit2.setDescription("Meditate for 20 minutes");
        habit2.setDaysOfWeek("Mon,Tue,Wed,Thu,Fri");
        habit2.setStartDate(LocalDate.of(2025, 1, 1));
        habit2.setEndDate(LocalDate.of(2025, 12, 31));
        habit2.setStreakStatus(10);
        habit2.setColor("#33C9FF");
        habit2.setGoal(testGoal);
        habit2.setUser(testUser);
        habit2.setCreatedAt(LocalDateTime.now());
        habit2.setUpdatedAt(LocalDateTime.now());

        when(userRepository.existsById(1L)).thenReturn(true);
        when(habitRepository.findByUserId(1L)).thenReturn(Arrays.asList(testHabit, habit2));

        // When
        List<HabitDTO> results = habitService.getHabitsByUserId(1L);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("Morning Run");
        assertThat(results.get(1).getName()).isEqualTo("Meditation");
        
        verify(userRepository, times(1)).existsById(1L);
        verify(habitRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting habits for non-existent user")
    void shouldThrowExceptionWhenGettingHabitsForNonExistentUser() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> habitService.getHabitsByUserId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(userRepository, times(1)).existsById(999L);
        verify(habitRepository, never()).findByUserId(anyLong());
    }

    @Test
    @DisplayName("Should get all habits for goal")
    void shouldGetAllHabitsForGoal() {
        // Given
        when(goalRepository.existsById(1L)).thenReturn(true);
        when(habitRepository.findByGoalId(1L)).thenReturn(Arrays.asList(testHabit));

        // When
        List<HabitDTO> results = habitService.getHabitsByGoalId(1L);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Morning Run");
        
        verify(goalRepository, times(1)).existsById(1L);
        verify(habitRepository, times(1)).findByGoalId(1L);
    }

    @Test
    @DisplayName("Should update habit successfully")
    void shouldUpdateHabitSuccessfully() {
        // Given
        UpdateHabitRequest updateRequest = new UpdateHabitRequest();
        updateRequest.setName("Evening Run");
        updateRequest.setStreakStatus(10);
        updateRequest.setDaysOfWeek("Mon,Wed,Fri,Sun");

        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        // When
        HabitDTO result = habitService.updateHabit(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(habitRepository, times(1)).findById(1L);
        verify(habitRepository, times(1)).save(testHabit);
        assertThat(testHabit.getName()).isEqualTo("Evening Run");
        assertThat(testHabit.getStreakStatus()).isEqualTo(10);
        assertThat(testHabit.getDaysOfWeek()).isEqualTo("Mon,Wed,Fri,Sun");
    }

    @Test
    @DisplayName("Should delete habit successfully")
    void shouldDeleteHabitSuccessfully() {
        // Given
        when(habitRepository.existsById(1L)).thenReturn(true);
        doNothing().when(habitRepository).deleteById(1L);

        // When
        habitService.deleteHabit(1L);

        // Then
        verify(habitRepository, times(1)).existsById(1L);
        verify(habitRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent habit")
    void shouldThrowExceptionWhenDeletingNonExistentHabit() {
        // Given
        when(habitRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> habitService.deleteHabit(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Habit not found");
        
        verify(habitRepository, times(1)).existsById(999L);
        verify(habitRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should count habits by user ID")
    void shouldCountHabitsByUserId() {
        // Given
        when(habitRepository.countByUserId(1L)).thenReturn(5L);

        // When
        long count = habitService.countHabitsByUserId(1L);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(habitRepository, times(1)).countByUserId(1L);
    }

    @Test
    @DisplayName("Should count habits by goal ID")
    void shouldCountHabitsByGoalId() {
        // Given
        when(habitRepository.countByGoalId(1L)).thenReturn(3L);

        // When
        long count = habitService.countHabitsByGoalId(1L);

        // Then
        assertThat(count).isEqualTo(3L);
        verify(habitRepository, times(1)).countByGoalId(1L);
    }

    @Test
    @DisplayName("Should check if habit exists")
    void shouldCheckIfHabitExists() {
        // Given
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(habitRepository.existsById(999L)).thenReturn(false);

        // When
        boolean exists = habitService.existsById(1L);
        boolean notExists = habitService.existsById(999L);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        
        verify(habitRepository, times(1)).existsById(1L);
        verify(habitRepository, times(1)).existsById(999L);
    }
}

