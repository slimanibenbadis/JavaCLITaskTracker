package com.javaclitasktracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TaskManager {
    private List<Task> tasks;
    private static final String TASKS_FILE = "tasks.json";

    public TaskManager() {
        tasks = new ArrayList<>();
        loadTasks();
    }

    protected List<Task> loadTasks() {
        List<Task> loadedTasks = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(TASKS_FILE))) {
                return loadedTasks;
            }
            String content = new String(Files.readAllBytes(Paths.get(TASKS_FILE)));
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTask = jsonArray.getJSONObject(i);
                Task task = new Task(
                    jsonTask.getInt("id"),
                    jsonTask.getString("description")
                );
                task.setStatus(TaskStatus.valueOf(jsonTask.getString("status")));
                task.setCreatedAt(LocalDateTime.parse(jsonTask.getString("createdAt")));
                task.setUpdatedAt(LocalDateTime.parse(jsonTask.getString("updatedAt")));
                loadedTasks.add(task);
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
        return loadedTasks;
    }

    private void saveTasks() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Task task : tasks) {
                JSONObject jsonTask = new JSONObject();
                jsonTask.put("id", task.getId());
                jsonTask.put("description", task.getDescription());
                jsonTask.put("status", task.getStatus().toString());
                jsonTask.put("createdAt", task.getCreatedAt().toString());
                jsonTask.put("updatedAt", task.getUpdatedAt().toString());
                jsonArray.put(jsonTask);
            }
            Files.write(Paths.get(TASKS_FILE), jsonArray.toString(2).getBytes());
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    public void addTask(String description) {
        int newId = tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).getId() + 1;
        Task task = new Task(newId, description);
        tasks.add(task);
        saveTasks();
        System.out.println("Task added successfully (ID: " + newId + ")");
    }

    public void updateTask(int id, String description) {
        Task task = findTask(id);
        if (task != null) {
            task.setDescription(description);
            saveTasks();
            System.out.println("Task updated successfully");
        } else {
            System.out.println("Task not found");
        }
    }

    public void deleteTask(int id) {
        boolean removed = tasks.removeIf(task -> task.getId() == id);
        if (removed) {
            saveTasks();
            System.out.println("Task deleted successfully");
        } else {
            System.out.println("Task not found");
        }
    }

    public void markTaskInProgress(int id) {
        updateTaskStatus(id, TaskStatus.IN_PROGRESS);
    }

    public void markTaskDone(int id) {
        updateTaskStatus(id, TaskStatus.DONE);
    }

    private void updateTaskStatus(int id, TaskStatus status) {
        Task task = findTask(id);
        if (task != null) {
            task.setStatus(status);
            saveTasks();
            System.out.println("Task marked as " + status.toString().toLowerCase());
        } else {
            System.out.println("Task not found");
        }
    }

    private Task findTask(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void listTasks(String filter) {
        List<Task> filteredTasks = switch (filter.toLowerCase()) {
            case "done" -> tasks.stream()
                    .filter(task -> task.getStatus() == TaskStatus.DONE)
                    .toList();
            case "todo" -> tasks.stream()
                    .filter(task -> task.getStatus() == TaskStatus.TODO)
                    .toList();
            case "in-progress" -> tasks.stream()
                    .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
                    .toList();
            default -> tasks;
        };

        if (filteredTasks.isEmpty()) {
            System.out.println("No tasks found");
            return;
        }

        for (Task task : filteredTasks) {
            System.out.printf("ID: %d | Status: %s | Description: %s%n",
                    task.getId(), task.getStatus(), task.getDescription());
        }
    }
}
