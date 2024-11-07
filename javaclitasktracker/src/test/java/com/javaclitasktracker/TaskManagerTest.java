package com.javaclitasktracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        // Set up a temporary file for testing
        Path tempFile = tempDir.resolve("tasksTests.json");
        Files.createFile(tempFile);
        System.setProperty("TASKS_FILE", tempFile.toString());

        taskManager = new TaskManager();
    }

    @Test
    void testAddTask() {
        taskManager.addTask("Test task");
        List<Task> tasks = taskManager.loadTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test task", tasks.get(0).getDescription());
    }

    @Test
    void testUpdateTask() {
        taskManager.addTask("Initial task");
        taskManager.updateTask(1, "Updated task");
        List<Task> tasks = taskManager.loadTasks();
        assertEquals("Updated task", tasks.get(0).getDescription());
    }

    @Test
    void testDeleteTask() {
        taskManager.addTask("Task to delete");
        taskManager.deleteTask(1);
        List<Task> tasks = taskManager.loadTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void testMarkTaskInProgress() {
        taskManager.addTask("Task in progress");
        taskManager.markTaskInProgress(1);
        List<Task> tasks = taskManager.loadTasks();
        assertEquals(TaskStatus.IN_PROGRESS, tasks.get(0).getStatus());
    }

    @Test
    void testMarkTaskDone() {
        taskManager.addTask("Task to complete");
        taskManager.markTaskDone(1);
        List<Task> tasks = taskManager.loadTasks();
        assertEquals(TaskStatus.DONE, tasks.get(0).getStatus());
    }

    @Test
    void testListTasks() {
        taskManager.addTask("Task 1");
        taskManager.addTask("Task 2");
        taskManager.markTaskDone(1);
        
        List<Task> doneTasks = taskManager.loadTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .toList();
        List<Task> todoTasks = taskManager.loadTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.TODO)
                .toList();
        
        assertEquals(1, doneTasks.size());
        assertEquals("Task 1", doneTasks.get(0).getDescription());
        
        assertEquals(1, todoTasks.size());
        assertEquals("Task 2", todoTasks.get(0).getDescription());
    }
} 