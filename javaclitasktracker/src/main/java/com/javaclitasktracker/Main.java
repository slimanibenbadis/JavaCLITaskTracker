package com.javaclitasktracker;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to Task Tracker CLI!");
            printUsage();

            while (true) {
                System.out.print("\nEnter command (or 'help' for usage, 'exit' to quit): ");
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                
                if (input.isEmpty()) {
                    continue;
                }

                if (input.equalsIgnoreCase("help")) {
                    printUsage();
                    continue;
                }

                // Split input into command and arguments
                String[] argsArray = input.split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (int i = 0; i < argsArray.length; i++) {
                    argsArray[i] = argsArray[i].replaceAll("\"", "");
                }

                if (argsArray.length == 0) {
                    continue;
                }

                String command = argsArray[0];

                try {
                    switch (command) {
                        case "add" -> {
                            if (argsArray.length < 2) throw new IllegalArgumentException("Description required");
                            String description = String.join(" ", Arrays.copyOfRange(argsArray, 1, argsArray.length));
                            taskManager.addTask(description);
                        }
                        case "update" -> {
                            if (argsArray.length < 3) throw new IllegalArgumentException("ID and description required");
                            int id = Integer.parseInt(argsArray[1]);
                            String description = String.join(" ", Arrays.copyOfRange(argsArray, 2, argsArray.length));
                            taskManager.updateTask(id, description);
                        }
                        case "delete" -> {
                            if (argsArray.length < 2) throw new IllegalArgumentException("ID required");
                            int id = Integer.parseInt(argsArray[1]);
                            taskManager.deleteTask(id);
                        }
                        case "mark-in-progress" -> {
                            if (argsArray.length < 2) throw new IllegalArgumentException("ID required");
                            int id = Integer.parseInt(argsArray[1]);
                            taskManager.markTaskInProgress(id);
                        }
                        case "mark-done" -> {
                            if (argsArray.length < 2) throw new IllegalArgumentException("ID required");
                            int id = Integer.parseInt(argsArray[1]);
                            taskManager.markTaskDone(id);
                        }
                        case "list" -> {
                            String filter = argsArray.length > 1 ? argsArray[1].toLowerCase() : "all";
                            if (!isValidFilter(filter)) {
                                throw new IllegalArgumentException("Invalid filter. Use: all, done, todo, or in-progress");
                            }
                            taskManager.listTasks(filter);
                        }
                        default -> {
                            System.err.println("Unknown command: " + command);
                            printUsage();
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid ID format. Please provide a valid number.");
                } catch (IllegalArgumentException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
    }

    private static boolean isValidFilter(String filter) {
        return filter.equals("all") || 
               filter.equals("done") || 
               filter.equals("todo") || 
               filter.equals("in-progress");
    }

    private static void printUsage() {
        System.out.println("""
            
            Task Tracker CLI - Available Commands:
            --------------------------------
              add <description>          Add a new task
              update <id> <description>  Update an existing task
              delete <id>               Delete a task
              mark-in-progress <id>     Mark a task as in progress
              mark-done <id>            Mark a task as done
              list [filter]             List tasks (filter: all|done|todo|in-progress)
              help                      Show this usage information
              exit                      Exit the application

            Examples:
              add "Buy groceries"
              update 1 "Buy groceries and cook dinner"
              mark-done 1
              list done
            --------------------------------
            """);
    }
}
