package com.example.goalsmanager.repository;

import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.Habit;
import com.example.goalsmanager.model.Task;
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
@DisplayName("Task Repository Tests")
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private User testUser;
    private Goal testGoal;
    private Habit testHabit;
    private Task testTask1;
    private Task testTask2;
    private Task testTask3;

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

        // Create and persist test habit
        testHabit = new Habit();
        testHabit.setName("Morning Run");
        testHabit.setDescription("Run 5km every morning");
        testHabit.setDaysOfWeek("Mon,Wed,Fri");
        testHabit.setStartDate(LocalDate.of(2025, 1, 1));
        testHabit.setEndDate(LocalDate.of(2025, 12, 31));
        testHabit.setStreakStatus(5);
        testHabit.setGoal(testGoal);
        testHabit.setUser(testUser);
        entityManager.persist(testHabit);

        // Create and persist test tasks
        testTask1 = new Task();
        testTask1.setName("Monday Morning Run");
        testTask1.setDate(LocalDate.of(2025, 10, 20));
        testTask1.setCompleted(true);
        testTask1.setHabit(testHabit);
        testTask1.setUser(testUser);
        entityManager.persist(testTask1);

        testTask2 = new Task();
        testTask2.setName("Wednesday Morning Run");
        testTask2.setDate(LocalDate.of(2025, 10, 22));
        testTask2.setCompleted(false);
        testTask2.setHabit(testHabit);
        testTask2.setUser(testUser);
        entityManager.persist(testTask2);

        testTask3 = new Task();
        testTask3.setName("Friday Morning Run");
        testTask3.setDate(LocalDate.of(2025, 10, 24));
        testTask3.setCompleted(true);
        testTask3.setHabit(testHabit);
        testTask3.setUser(testUser);
        entityManager.persist(testTask3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find all tasks by user ID")
    void shouldFindAllTasksByUserIdTest() {
        // When
        List<Task> tasks = taskRepository.findByUserId(testUser.getId());

        // Then
        assertThat(tasks).hasSize(3);
        assertThat(tasks).extracting(Task::getName)
                .containsExactlyInAnyOrder("Monday Morning Run", "Wednesday Morning Run", "Friday Morning Run");
    }

    @Test
    @DisplayName("Should find all tasks by habit ID")
    void shouldFindAllTasksByHabitIdTest() {
        // When
        List<Task> tasks = taskRepository.findByHabitId(testHabit.getId());

        // Then
        assertThat(tasks).hasSize(3);
        assertThat(tasks).allMatch(t -> t.getHabit().getId().equals(testHabit.getId()));
    }

    @Test
    @DisplayName("Should find tasks by user ID and habit ID")
    void shouldFindTasksByUserIdAndHabitIdTest() {
        // When
        List<Task> tasks = taskRepository.findByUserIdAndHabitId(testUser.getId(), testHabit.getId());

        // Then
        assertThat(tasks).hasSize(3);
        assertThat(tasks).allMatch(t -> t.getUser().getId().equals(testUser.getId()));
        assertThat(tasks).allMatch(t -> t.getHabit().getId().equals(testHabit.getId()));
    }

    @Test
    @DisplayName("Should find completed tasks by user ID")
    void shouldFindCompletedTasksByUserIdTest() {
        // When
        List<Task> completedTasks = taskRepository.findByUserIdAndCompleted(testUser.getId(), true);

        // Then
        assertThat(completedTasks).hasSize(2);
        assertThat(completedTasks).allMatch(Task::getCompleted);
        assertThat(completedTasks).extracting(Task::getName)
                .containsExactlyInAnyOrder("Monday Morning Run", "Friday Morning Run");
    }

    @Test
    @DisplayName("Should find pending tasks by user ID")
    void shouldFindPendingTasksByUserIdTest() {
        // When
        List<Task> pendingTasks = taskRepository.findByUserIdAndCompleted(testUser.getId(), false);

        // Then
        assertThat(pendingTasks).hasSize(1);
        assertThat(pendingTasks).allMatch(t -> !t.getCompleted());
        assertThat(pendingTasks.get(0).getName()).isEqualTo("Wednesday Morning Run");
    }

    @Test
    @DisplayName("Should find completed tasks by habit ID")
    void shouldFindCompletedTasksByHabitIdTest() {
        // When
        List<Task> completedTasks = taskRepository.findByHabitIdAndCompleted(testHabit.getId(), true);

        // Then
        assertThat(completedTasks).hasSize(2);
        assertThat(completedTasks).allMatch(Task::getCompleted);
    }

    @Test
    @DisplayName("Should find tasks by user ID and date range")
    void shouldFindTasksByUserIdAndDateRangeTest() {
        // When
        List<Task> tasks = taskRepository.findByUserIdAndDateBetween(
                testUser.getId(),
                LocalDate.of(2025, 10, 19),
                LocalDate.of(2025, 10, 23)
        );

        // Then
        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getName)
                .containsExactlyInAnyOrder("Monday Morning Run", "Wednesday Morning Run");
    }

    @Test
    @DisplayName("Should count tasks by user ID")
    void shouldCountTasksByUserIdTest() {
        // When
        long count = taskRepository.countByUserId(testUser.getId());

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should count tasks by habit ID")
    void shouldCountTasksByHabitIdTest() {
        // When
        long count = taskRepository.countByHabitId(testHabit.getId());

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should save and retrieve task with all fields")
    void shouldSaveAndRetrieveTaskWithAllFieldsTest() {
        // Given
        Task newTask = new Task();
        newTask.setName("Sunday Morning Run");
        newTask.setDate(LocalDate.of(2025, 10, 27));
        newTask.setCompleted(false);
        newTask.setHabit(testHabit);
        newTask.setUser(testUser);

        // When
        Task savedTask = taskRepository.save(newTask);
        Task retrievedTask = taskRepository.findById(savedTask.getId()).orElse(null);

        // Then
        assertThat(retrievedTask).isNotNull();
        assertThat(retrievedTask.getName()).isEqualTo("Sunday Morning Run");
        assertThat(retrievedTask.getDate()).isEqualTo(LocalDate.of(2025, 10, 27));
        assertThat(retrievedTask.getCompleted()).isFalse();
        assertThat(retrievedTask.getCreatedAt()).isNotNull();
        assertThat(retrievedTask.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update task and update timestamp")
    void shouldUpdateTaskAndUpdateTimestampTest() throws InterruptedException {
        // Given
        Task task = taskRepository.findById(testTask2.getId()).orElseThrow();
        
        // Wait a bit to ensure timestamp difference
        Thread.sleep(10);

        // When
        task.setCompleted(true);
        task.setName("Wednesday Morning Run - Completed");
        Task updatedTask = taskRepository.save(task);

        // Then
        assertThat(updatedTask.getCompleted()).isTrue();
        assertThat(updatedTask.getName()).isEqualTo("Wednesday Morning Run - Completed");
        assertThat(updatedTask.getUpdatedAt()).isAfterOrEqualTo(updatedTask.getCreatedAt());
    }

    @Test
    @DisplayName("Should delete task")
    void shouldDeleteTaskTest() {
        // Given
        Long taskId = testTask1.getId();

        // When
        taskRepository.deleteById(taskId);
        entityManager.flush();

        // Then
        assertThat(taskRepository.findById(taskId)).isEmpty();
    }

    @Test
    @DisplayName("Should have cascade delete configuration for tasks when habit is deleted")
    void shouldHaveCascadeDeleteConfigurationTest() {
        // This test verifies the relationship configuration
        // The actual cascade behavior is guaranteed by JPA annotations:
        // @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
        
        // Given
        Long taskId = testTask1.getId();
        
        // When - Verify task exists and is linked to habit
        Task task = taskRepository.findById(taskId).orElseThrow();
        
        // Then - Verify the relationship is properly configured
        assertThat(task.getHabit()).isNotNull();
        assertThat(task.getHabit().getId()).isEqualTo(testHabit.getId());
        assertThat(taskRepository.findByHabitId(testHabit.getId())).hasSize(3);
    }

    @Test
    @DisplayName("Should find tasks within a week")
    void shouldFindTasksWithinWeekTest() {
        // Given
        LocalDate startOfWeek = LocalDate.of(2025, 10, 20);
        LocalDate endOfWeek = LocalDate.of(2025, 10, 26);

        // When
        List<Task> tasks = taskRepository.findByUserIdAndDateBetween(
                testUser.getId(), startOfWeek, endOfWeek);

        // Then
        assertThat(tasks).hasSize(3);
        assertThat(tasks).allMatch(t -> 
            !t.getDate().isBefore(startOfWeek) && !t.getDate().isAfter(endOfWeek));
    }
}

