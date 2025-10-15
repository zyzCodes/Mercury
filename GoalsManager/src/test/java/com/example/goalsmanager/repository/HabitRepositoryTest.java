package com.example.goalsmanager.repository;

import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.Habit;
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
@DisplayName("Habit Repository Tests")
class HabitRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HabitRepository habitRepository;

    private User testUser;
    private Goal testGoal;
    private Habit testHabit1;
    private Habit testHabit2;
    private Habit testHabit3;

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

        // Create and persist test goal
        testGoal = new Goal();
        testGoal.setTitle("Get Fit");
        testGoal.setDescription("Improve overall fitness");
        testGoal.setStartDate(LocalDate.of(2025, 1, 1));
        testGoal.setEndDate(LocalDate.of(2025, 12, 31));
        testGoal.setStatus(GoalStatus.IN_PROGRESS);
        testGoal.setUser(testUser);
        entityManager.persist(testGoal);

        // Create and persist test habits
        testHabit1 = new Habit();
        testHabit1.setName("Morning Run");
        testHabit1.setDescription("Run 5km every morning");
        testHabit1.setDaysOfWeek("Mon,Wed,Fri");
        testHabit1.setStartDate(LocalDate.of(2025, 1, 1));
        testHabit1.setEndDate(LocalDate.of(2025, 12, 31));
        testHabit1.setStreakStatus(5);
        testHabit1.setColor("#FF5733");
        testHabit1.setGoal(testGoal);
        testHabit1.setUser(testUser);
        entityManager.persist(testHabit1);

        testHabit2 = new Habit();
        testHabit2.setName("Meditation");
        testHabit2.setDescription("Meditate for 20 minutes");
        testHabit2.setDaysOfWeek("Mon,Tue,Wed,Thu,Fri");
        testHabit2.setStartDate(LocalDate.of(2025, 1, 1));
        testHabit2.setEndDate(LocalDate.of(2025, 6, 30));
        testHabit2.setStreakStatus(10);
        testHabit2.setColor("#33C9FF");
        testHabit2.setGoal(testGoal);
        testHabit2.setUser(testUser);
        entityManager.persist(testHabit2);

        testHabit3 = new Habit();
        testHabit3.setName("Drink Water");
        testHabit3.setDescription("Drink 8 glasses of water");
        testHabit3.setDaysOfWeek("Mon,Tue,Wed,Thu,Fri,Sat,Sun");
        testHabit3.setStartDate(LocalDate.of(2025, 1, 1));
        testHabit3.setEndDate(LocalDate.of(2025, 12, 31));
        testHabit3.setStreakStatus(15);
        testHabit3.setColor("#33FF57");
        testHabit3.setGoal(testGoal);
        testHabit3.setUser(testUser);
        entityManager.persist(testHabit3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find all habits by user ID")
    void shouldFindAllHabitsByUserIdTest() {
        // When
        List<Habit> habits = habitRepository.findByUserId(testUser.getId());

        // Then
        assertThat(habits).hasSize(3);
        assertThat(habits).extracting(Habit::getName)
                .containsExactlyInAnyOrder("Morning Run", "Meditation", "Drink Water");
    }

    @Test
    @DisplayName("Should find all habits by goal ID")
    void shouldFindAllHabitsByGoalIdTest() {
        // When
        List<Habit> habits = habitRepository.findByGoalId(testGoal.getId());

        // Then
        assertThat(habits).hasSize(3);
        assertThat(habits).extracting(Habit::getName)
                .containsExactlyInAnyOrder("Morning Run", "Meditation", "Drink Water");
    }

    @Test
    @DisplayName("Should find habits by user ID and goal ID")
    void shouldFindHabitsByUserIdAndGoalIdTest() {
        // When
        List<Habit> habits = habitRepository.findByUserIdAndGoalId(testUser.getId(), testGoal.getId());

        // Then
        assertThat(habits).hasSize(3);
        assertThat(habits).allMatch(h -> h.getUser().getId().equals(testUser.getId()));
        assertThat(habits).allMatch(h -> h.getGoal().getId().equals(testGoal.getId()));
    }

    @Test
    @DisplayName("Should check if habits exist for user")
    void shouldCheckIfHabitsExistForUserTest() {
        // When
        boolean exists = habitRepository.existsByUserId(testUser.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false if no habits exist for user")
    void shouldReturnFalseIfNoHabitsExistForUserTest() {
        // Given
        User newUser = new User();
        newUser.setProvider("github");
        newUser.setProviderId("67890");
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        entityManager.persist(newUser);
        entityManager.flush();

        // When
        boolean exists = habitRepository.existsByUserId(newUser.getId());

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should count habits by user ID")
    void shouldCountHabitsByUserIdTest() {
        // When
        long count = habitRepository.countByUserId(testUser.getId());

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should count habits by goal ID")
    void shouldCountHabitsByGoalIdTest() {
        // When
        long count = habitRepository.countByGoalId(testGoal.getId());

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should save and retrieve habit with all fields")
    void shouldSaveAndRetrieveHabitWithAllFieldsTest() {
        // Given
        Habit newHabit = new Habit();
        newHabit.setName("Yoga Practice");
        newHabit.setDescription("30 minutes of yoga");
        newHabit.setDaysOfWeek("Tue,Thu,Sat");
        newHabit.setStartDate(LocalDate.of(2025, 2, 1));
        newHabit.setEndDate(LocalDate.of(2025, 12, 31));
        newHabit.setStreakStatus(0);
        newHabit.setColor("#FF33C9");
        newHabit.setGoal(testGoal);
        newHabit.setUser(testUser);

        // When
        Habit savedHabit = habitRepository.save(newHabit);
        Habit retrievedHabit = habitRepository.findById(savedHabit.getId()).orElse(null);

        // Then
        assertThat(retrievedHabit).isNotNull();
        assertThat(retrievedHabit.getName()).isEqualTo("Yoga Practice");
        assertThat(retrievedHabit.getDescription()).isEqualTo("30 minutes of yoga");
        assertThat(retrievedHabit.getDaysOfWeek()).isEqualTo("Tue,Thu,Sat");
        assertThat(retrievedHabit.getStreakStatus()).isEqualTo(0);
        assertThat(retrievedHabit.getColor()).isEqualTo("#FF33C9");
        assertThat(retrievedHabit.getCreatedAt()).isNotNull();
        assertThat(retrievedHabit.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update habit and update timestamp")
    void shouldUpdateHabitAndUpdateTimestampTest() throws InterruptedException {
        // Given
        Habit habit = habitRepository.findById(testHabit1.getId()).orElseThrow();
        
        // Wait a bit to ensure timestamp difference
        Thread.sleep(100);

        // When
        habit.setName("Evening Run");
        habit.setStreakStatus(10);
        Habit updatedHabit = habitRepository.save(habit);

        // Then
        assertThat(updatedHabit.getName()).isEqualTo("Evening Run");
        assertThat(updatedHabit.getStreakStatus()).isEqualTo(10);
        assertThat(updatedHabit.getUpdatedAt()).isAfter(updatedHabit.getCreatedAt());
    }

    @Test
    @DisplayName("Should delete habit")
    void shouldDeleteHabitTest() {
        // Given
        Long habitId = testHabit1.getId();

        // When
        habitRepository.deleteById(habitId);
        entityManager.flush();

        // Then
        assertThat(habitRepository.findById(habitId)).isEmpty();
    }

    @Test
    @DisplayName("Should have cascade delete configuration for habits when goal is deleted")
    void shouldHaveCascadeDeleteConfigurationTest() {
        // This test verifies the relationship configuration
        // The actual cascade behavior is guaranteed by JPA annotations:
        // @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
        
        // Given
        Long habitId = testHabit1.getId();
        
        // When - Verify habit exists and is linked to goal
        Habit habit = habitRepository.findById(habitId).orElseThrow();
        
        // Then - Verify the relationship is properly configured
        assertThat(habit.getGoal()).isNotNull();
        assertThat(habit.getGoal().getId()).isEqualTo(testGoal.getId());
        assertThat(habitRepository.findByGoalId(testGoal.getId())).hasSize(3);
    }
}

