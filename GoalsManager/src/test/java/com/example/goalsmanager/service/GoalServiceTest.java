package com.example.goalsmanager.service;

import com.example.goalsmanager.dto.CreateGoalRequest;
import com.example.goalsmanager.dto.GoalDTO;
import com.example.goalsmanager.dto.UpdateGoalRequest;
import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.GoalRepository;
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
@DisplayName("Goal Service Tests")
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalService goalService;

    private User testUser;
    private Goal testGoal;
    private CreateGoalRequest createRequest;

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
        testGoal.setTitle("Learn Spring Boot");
        testGoal.setDescription("Master Spring Boot framework");
        testGoal.setStartDate(LocalDate.of(2025, 1, 1));
        testGoal.setEndDate(LocalDate.of(2025, 12, 31));
        testGoal.setStatus(GoalStatus.NOT_STARTED);
        testGoal.setUser(testUser);
        testGoal.setCreatedAt(LocalDateTime.now());
        testGoal.setUpdatedAt(LocalDateTime.now());

        // Set up create request
        createRequest = new CreateGoalRequest();
        createRequest.setTitle("Learn Spring Boot");
        createRequest.setDescription("Master Spring Boot framework");
        createRequest.setStartDate(LocalDate.of(2025, 1, 1));
        createRequest.setEndDate(LocalDate.of(2025, 12, 31));
        createRequest.setUserId(1L);
    }

    @Test
    @DisplayName("Should create goal successfully")
    void shouldCreateGoalSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // When
        GoalDTO result = goalService.createGoal(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Learn Spring Boot");
        assertThat(result.getDescription()).isEqualTo("Master Spring Boot framework");
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(GoalStatus.NOT_STARTED);
        
        verify(userRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should throw exception when creating goal for non-existent user")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> goalService.createGoal(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(userRepository, times(1)).findById(1L);
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should throw exception when end date is before start date")
    void shouldThrowExceptionWhenInvalidDates() {
        // Given
        createRequest.setStartDate(LocalDate.of(2025, 12, 31));
        createRequest.setEndDate(LocalDate.of(2025, 1, 1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> goalService.createGoal(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("End date must be after start date");
        
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should get goal by ID successfully")
    void shouldGetGoalByIdSuccessfully() {
        // Given
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        // When
        GoalDTO result = goalService.getGoalById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Learn Spring Boot");
        
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when goal not found by ID")
    void shouldThrowExceptionWhenGoalNotFoundById() {
        // Given
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> goalService.getGoalById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Goal not found");
        
        verify(goalRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all goals for user")
    void shouldGetAllGoalsForUser() {
        // Given
        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Learn React");
        goal2.setDescription("Master React framework");
        goal2.setStartDate(LocalDate.of(2025, 1, 1));
        goal2.setEndDate(LocalDate.of(2025, 12, 31));
        goal2.setStatus(GoalStatus.IN_PROGRESS);
        goal2.setUser(testUser);
        goal2.setCreatedAt(LocalDateTime.now());
        goal2.setUpdatedAt(LocalDateTime.now());

        when(userRepository.existsById(1L)).thenReturn(true);
        when(goalRepository.findByUserId(1L)).thenReturn(Arrays.asList(testGoal, goal2));

        // When
        List<GoalDTO> results = goalService.getGoalsByUserId(1L);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTitle()).isEqualTo("Learn Spring Boot");
        assertThat(results.get(1).getTitle()).isEqualTo("Learn React");
        
        verify(userRepository, times(1)).existsById(1L);
        verify(goalRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting goals for non-existent user")
    void shouldThrowExceptionWhenGettingGoalsForNonExistentUser() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> goalService.getGoalsByUserId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(userRepository, times(1)).existsById(999L);
        verify(goalRepository, never()).findByUserId(anyLong());
    }

    @Test
    @DisplayName("Should update goal successfully")
    void shouldUpdateGoalSuccessfully() {
        // Given
        UpdateGoalRequest updateRequest = new UpdateGoalRequest();
        updateRequest.setTitle("Learn Spring Boot Advanced");
        updateRequest.setStatus(GoalStatus.IN_PROGRESS);
        updateRequest.setNotes("Making good progress");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // When
        GoalDTO result = goalService.updateGoal(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(testGoal);
        assertThat(testGoal.getTitle()).isEqualTo("Learn Spring Boot Advanced");
        assertThat(testGoal.getStatus()).isEqualTo(GoalStatus.IN_PROGRESS);
        assertThat(testGoal.getNotes()).isEqualTo("Making good progress");
    }

    @Test
    @DisplayName("Should update goal status successfully")
    void shouldUpdateGoalStatusSuccessfully() {
        // Given
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // When
        GoalDTO result = goalService.updateGoalStatus(1L, GoalStatus.COMPLETED);

        // Then
        assertThat(result).isNotNull();
        assertThat(testGoal.getStatus()).isEqualTo(GoalStatus.COMPLETED);
        
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(testGoal);
    }

    @Test
    @DisplayName("Should update goal notes successfully")
    void shouldUpdateGoalNotesSuccessfully() {
        // Given
        String notes = "Completed chapter 5 today. Great progress!";
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // When
        GoalDTO result = goalService.updateGoalNotes(1L, notes);

        // Then
        assertThat(result).isNotNull();
        assertThat(testGoal.getNotes()).isEqualTo(notes);
        
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(testGoal);
    }

    @Test
    @DisplayName("Should delete goal successfully")
    void shouldDeleteGoalSuccessfully() {
        // Given
        when(goalRepository.existsById(1L)).thenReturn(true);
        doNothing().when(goalRepository).deleteById(1L);

        // When
        goalService.deleteGoal(1L);

        // Then
        verify(goalRepository, times(1)).existsById(1L);
        verify(goalRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent goal")
    void shouldThrowExceptionWhenDeletingNonExistentGoal() {
        // Given
        when(goalRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> goalService.deleteGoal(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Goal not found");
        
        verify(goalRepository, times(1)).existsById(999L);
        verify(goalRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should count goals by user ID")
    void shouldCountGoalsByUserId() {
        // Given
        when(goalRepository.countByUserId(1L)).thenReturn(5L);

        // When
        long count = goalService.countGoalsByUserId(1L);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(goalRepository, times(1)).countByUserId(1L);
    }

    @Test
    @DisplayName("Should get active goals for user")
    void shouldGetActiveGoalsForUser() {
        // Given
        testGoal.setStatus(GoalStatus.IN_PROGRESS);
        testGoal.setEndDate(LocalDate.now().plusMonths(6));
        
        when(goalRepository.findByUserId(1L)).thenReturn(Arrays.asList(testGoal));

        // When
        List<GoalDTO> results = goalService.getActiveGoalsByUserId(1L);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(GoalStatus.IN_PROGRESS);
        
        verify(goalRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should get overdue goals for user")
    void shouldGetOverdueGoalsForUser() {
        // Given
        testGoal.setStatus(GoalStatus.IN_PROGRESS);
        testGoal.setEndDate(LocalDate.now().minusDays(10));
        
        when(goalRepository.findByUserId(1L)).thenReturn(Arrays.asList(testGoal));

        // When
        List<GoalDTO> results = goalService.getOverdueGoalsByUserId(1L);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEndDate()).isBefore(LocalDate.now());
        
        verify(goalRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should get completed goals for user")
    void shouldGetCompletedGoalsForUser() {
        // Given
        testGoal.setStatus(GoalStatus.COMPLETED);
        
        when(goalRepository.findByUserIdAndStatus(1L, GoalStatus.COMPLETED))
                .thenReturn(Arrays.asList(testGoal));

        // When
        List<GoalDTO> results = goalService.getCompletedGoalsByUserId(1L);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(GoalStatus.COMPLETED);
        
        verify(goalRepository, times(1)).findByUserIdAndStatus(1L, GoalStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should check if goal exists")
    void shouldCheckIfGoalExists() {
        // Given
        when(goalRepository.existsById(1L)).thenReturn(true);
        when(goalRepository.existsById(999L)).thenReturn(false);

        // When
        boolean exists = goalService.existsById(1L);
        boolean notExists = goalService.existsById(999L);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        
        verify(goalRepository, times(1)).existsById(1L);
        verify(goalRepository, times(1)).existsById(999L);
    }
}

