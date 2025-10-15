package com.example.goalsmanager.repository;

import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Goal Repository Tests")
class GoalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GoalRepository goalRepository;

    private User testUser;
    private Goal testGoal1;
    private Goal testGoal2;
    private Goal testGoal3;

    @BeforeEach
    void setUp() {
        // Create and persist test user
        testUser = new User();
        testUser.setProvider("github");
        testUser.setProviderId("12345");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        entityManager.persist(testUser);

        // Create and persist test goals
        testGoal1 = new Goal();
        testGoal1.setTitle("Learn Spring Boot");
        testGoal1.setDescription("Master Spring Boot framework");
        testGoal1.setStartDate(LocalDate.of(2025, 1, 1));
        testGoal1.setEndDate(LocalDate.of(2025, 12, 31));
        testGoal1.setStatus(GoalStatus.IN_PROGRESS);
        testGoal1.setUser(testUser);
        entityManager.persist(testGoal1);

        testGoal2 = new Goal();
        testGoal2.setTitle("Learn React");
        testGoal2.setDescription("Master React framework");
        testGoal2.setStartDate(LocalDate.of(2025, 1, 1));
        testGoal2.setEndDate(LocalDate.of(2025, 6, 30));
        testGoal2.setStatus(GoalStatus.COMPLETED);
        testGoal2.setUser(testUser);
        entityManager.persist(testGoal2);

        testGoal3 = new Goal();
        testGoal3.setTitle("Learn Docker");
        testGoal3.setDescription("Master containerization");
        testGoal3.setStartDate(LocalDate.of(2025, 7, 1));
        testGoal3.setEndDate(LocalDate.of(2025, 12, 31));
        testGoal3.setStatus(GoalStatus.NOT_STARTED);
        testGoal3.setUser(testUser);
        entityManager.persist(testGoal3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find all goals by user ID")
    void shouldFindAllGoalsByUserIdTest() {
        // When
        List<Goal> goals = goalRepository.findByUserId(testUser.getId());

        // Then
        assertThat(goals).hasSize(3);
        assertThat(goals).extracting(Goal::getTitle)
                .containsExactlyInAnyOrder("Learn Spring Boot", "Learn React", "Learn Docker");
    }

    @Test
    @DisplayName("Should find goals by user ID and status")
    void shouldFindGoalsByUserIdAndStatusTest() {
        // When
        List<Goal> completedGoals = goalRepository.findByUserIdAndStatus(
                testUser.getId(), GoalStatus.COMPLETED);

        // Then
        assertThat(completedGoals).hasSize(1);
        assertThat(completedGoals.get(0).getTitle()).isEqualTo("Learn React");
        assertThat(completedGoals.get(0).getStatus()).isEqualTo(GoalStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should find goals by status")
    void shouldFindGoalsByStatusTest() {
        // When
        List<Goal> inProgressGoals = goalRepository.findByStatus(GoalStatus.IN_PROGRESS);

        // Then
        assertThat(inProgressGoals).hasSize(1);
        assertThat(inProgressGoals.get(0).getTitle()).isEqualTo("Learn Spring Boot");
    }

    @Test
    @DisplayName("Should find goals ending before a specific date")
    void shouldFindGoalsEndingBeforeDateTest() {
        // When
        List<Goal> goals = goalRepository.findByEndDateBefore(LocalDate.of(2025, 7, 1));

        // Then
        assertThat(goals).hasSize(1);
        assertThat(goals.get(0).getTitle()).isEqualTo("Learn React");
    }

    @Test
    @DisplayName("Should find goals ending after a specific date")
    void shouldFindGoalsEndingAfterDateTest() {
        // When
        List<Goal> goals = goalRepository.findByEndDateAfter(LocalDate.of(2025, 11, 30));

        // Then
        assertThat(goals).hasSize(2);
        assertThat(goals).extracting(Goal::getTitle)
                .containsExactlyInAnyOrder("Learn Spring Boot", "Learn Docker");
    }

    @Test
    @DisplayName("Should check if goals exist for user")
    void shouldCheckIfGoalsExistForUserTest() {
        // When
        boolean exists = goalRepository.existsByUserId(testUser.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false if no goals exist for user")
    void shouldReturnFalseIfNoGoalsExistForUserTest() {
        // Given
        User newUser = new User();
        newUser.setProvider("github");
        newUser.setProviderId("67890");
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        entityManager.persist(newUser);
        entityManager.flush();

        // When
        boolean exists = goalRepository.existsByUserId(newUser.getId());

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should count goals by user ID")
    void shouldCountGoalsByUserIdTest() {
        // When
        long count = goalRepository.countByUserId(testUser.getId());

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should count goals by user ID and status")
    void shouldCountGoalsByUserIdAndStatusTest() {
        // When
        long completedCount = goalRepository.countByUserIdAndStatus(
                testUser.getId(), GoalStatus.COMPLETED);
        long inProgressCount = goalRepository.countByUserIdAndStatus(
                testUser.getId(), GoalStatus.IN_PROGRESS);

        // Then
        assertThat(completedCount).isEqualTo(1);
        assertThat(inProgressCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should save and retrieve goal with all fields")
    void shouldSaveAndRetrieveGoalWithAllFieldsTest() {
        // Given
        Goal newGoal = new Goal();
        newGoal.setTitle("Complete Project");
        newGoal.setDescription("Finish the Mercury project");
        newGoal.setImageUrl("https://example.com/image.jpg");
        newGoal.setStartDate(LocalDate.of(2025, 1, 1));
        newGoal.setEndDate(LocalDate.of(2025, 3, 31));
        newGoal.setStatus(GoalStatus.IN_PROGRESS);
        newGoal.setUser(testUser);

        // When
        Goal savedGoal = goalRepository.save(newGoal);
        Goal retrievedGoal = goalRepository.findById(savedGoal.getId()).orElse(null);

        // Then
        assertThat(retrievedGoal).isNotNull();
        assertThat(retrievedGoal.getTitle()).isEqualTo("Complete Project");
        assertThat(retrievedGoal.getDescription()).isEqualTo("Finish the Mercury project");
        assertThat(retrievedGoal.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(retrievedGoal.getStatus()).isEqualTo(GoalStatus.IN_PROGRESS);
        assertThat(retrievedGoal.getCreatedAt()).isNotNull();
        assertThat(retrievedGoal.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update goal and update timestamp")
    void shouldUpdateGoalAndUpdateTimestampTest() throws InterruptedException {
        // Given
        Goal goal = goalRepository.findById(testGoal1.getId()).orElseThrow();
        
        // Wait a bit to ensure timestamp difference
        Thread.sleep(100);

        // When
        goal.setTitle("Updated Title");
        goal.setStatus(GoalStatus.COMPLETED);
        Goal updatedGoal = goalRepository.save(goal);

        // Then
        assertThat(updatedGoal.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedGoal.getStatus()).isEqualTo(GoalStatus.COMPLETED);
        assertThat(updatedGoal.getUpdatedAt()).isAfter(updatedGoal.getCreatedAt());
    }

    @Test
    @DisplayName("Should delete goal")
    void shouldDeleteGoalTest() {
        // Given
        Long goalId = testGoal1.getId();

        // When
        goalRepository.deleteById(goalId);
        entityManager.flush();

        // Then
        assertThat(goalRepository.findById(goalId)).isEmpty();
    }

    @Test
    @DisplayName("Should find goals by user within date range")
    void shouldFindGoalsByUserWithinDateRangeTest() {
        // When
        List<Goal> goals = goalRepository.findByUserIdAndStartDateBetween(
                testUser.getId(),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 30)
        );

        // Then
        assertThat(goals).hasSize(2);
        assertThat(goals).extracting(Goal::getTitle)
                .containsExactlyInAnyOrder("Learn Spring Boot", "Learn React");
    }
}

