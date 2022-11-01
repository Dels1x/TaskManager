package org.example;

import java.sql.*;
import java.util.Scanner;

public class App {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args ) {

        Connection connection;
        Scanner scanner = new Scanner(System.in);
        int decision;
        int id;
        String name;
        String description;
        String priority;
        String state;

        try{
           connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

           PreparedStatement addNewTask;
           PreparedStatement updateTask;
           PreparedStatement deleteTask;
           PreparedStatement getAllCompletedTasks = connection.prepareStatement(
                   "SELECT * FROM task WHERE " +
                           "state = 'Completed' OR state = 'completed' OR state = 'COMPLETED'" +
                           "OR state = 'Finished' OR state = 'finished' OR state = 'FINISHED' ORDER BY priority");

            PreparedStatement getAllUncompletedTasks = connection.prepareStatement(
                    "SELECT * FROM task WHERE " +
                            "state != 'Completed' AND state != 'completed' AND state != 'COMPLETED'" +
                            "AND state != 'Finished' AND state != 'finished' AND state != 'FINISHED' ORDER BY priority");

           while(true) {
               System.out.println("Decide what you want to do:");
               System.out.println("\t 1 = Show completed tasks");
               System.out.println("\t 2 = Show uncompleted tasks");
               System.out.println("\t 3 = Add new task");
               System.out.println("\t 5 = Update task");
               System.out.println("\t 6 = Delete task by id");
               System.out.println("\t 7 = Exit app");

                decision = scanner.nextInt();

               switch (decision) {
                   case 1 -> {
                       ResultSet resultSet = getAllCompletedTasks.executeQuery();
                       while (resultSet.next()) {
                           System.out.printf("#%d\t\t\t\t%s \n", resultSet.getInt(1),
                                   capitalize(resultSet.getString(2)));
                           if (resultSet.getString(3) == null) {
                               System.out.println("Description:\tNo description");
                           } else {
                               System.out.printf("Description:\t%s\n", capitalize(resultSet.getString(3)));
                           }
                           System.out.printf("Priority:\t\t%d\n", resultSet.getInt(4));
                           System.out.println("State:\t\t\tCompleted");
                           System.out.println("----------------");
                       }
                   }
                   case 2 -> {
                       ResultSet resultSet = getAllUncompletedTasks.executeQuery();
                       while (resultSet.next()) {
                           System.out.printf("#%d\t\t\t\t%s \n", resultSet.getInt(1),
                                   capitalize(resultSet.getString(2)));
                           if (resultSet.getString(3) == null) {
                               System.out.println("Description:\tNo description");
                           } else {
                               System.out.printf("Description:\t%s\n", capitalize(resultSet.getString(3)));
                           }
                           System.out.printf("Priority:\t\t%d\n", resultSet.getInt(4));
                           System.out.printf("State:\t\t\t%s\n", resultSet.getString(5));
                           System.out.println("----------------");
                       }
                   }
                   case 3 -> {
                       while (true) {
                           System.out.print("Enter a name for your task\n: ");
                           scanner.nextLine();
                           name = scanner.nextLine();

                           if (name.isBlank()) {
                               System.out.println("Name can't be blank!");
                               continue;
                           }

                           if (name.length() > 90) {
                               name = name.substring(0, 90);
                           }

                           break;
                       }
                       System.out.print("Enter a description for your task. Leave blank if you prefer not to have one\n: ");
                       description = scanner.nextLine();
                       if (description.length() > 255) {
                           description = description.substring(0, 255);
                       }
                       while (true) {
                           System.out.print("Enter a priority for your task\n: ");
                           priority = scanner.nextLine();

                           if (priority.isBlank()) {
                               System.out.println("Priority can't be blank!");
                               continue;
                           }

                           if (!priority.matches("[0-9]+")) {
                               System.out.println("Priority must be a number");
                               continue;
                           }
                           break;
                       }
                       while (true) {
                           System.out.print("Enter a state for your task\n: ");
                           state = scanner.nextLine();

                           if (state.isBlank()) {
                               System.out.println("State can't be blank!");
                               continue;
                           }

                           if (state.length() > 30) {
                               state = state.substring(0, 30);
                           }

                           break;
                       }
                       if (description.isBlank()) {
                           addNewTask = connection.prepareStatement(
                                   "INSERT INTO task (name, description, priority, state)" +
                                           String.format(" VALUES ('%s', null, %s, '%s')", name, priority, state));
                       } else {
                           addNewTask = connection.prepareStatement(
                                   "INSERT INTO task (name, description, priority, state)" +
                                           String.format(" VALUES ('%s', '%s', %s, '%s')", name, description, priority, state));
                       }
                       addNewTask.execute();
                   }
                   case 4 -> {
                       System.out.print("Enter ID of task you want to update\n: ");
                       id = scanner.nextInt();
                       System.out.println("Enter what you want to update: ");
                       System.out.println("\t 1. Name");
                       System.out.println("\t 2. Description");
                       System.out.println("\t 3. Priority");
                       System.out.print("\t 4. State\n: ");
                       decision = scanner.nextInt();
                       while (true) {
                           switch (decision) {
                               case 1:
                                   while (true) {
                                       System.out.print("Enter a new name for a task\n: ");
                                       scanner.nextLine();
                                       name = scanner.nextLine();

                                       if (name.isBlank()) {
                                           System.out.println("Name can't be blank!");
                                           continue;
                                       }

                                       if (name.length() > 90) {
                                           name = name.substring(0, 90);
                                       }

                                       updateTask = connection.prepareStatement(
                                               String.format("UPDATE task SET name = '%s' WHERE id = %d", name, id));

                                       updateTask.execute();

                                       System.out.println("Name of the task successfully was set to " + name + ".");
                                       break;
                                   }
                                   break;
                               case 2:
                                   System.out.print("Enter a new description for a task\n: ");
                                   scanner.nextLine();
                                   description = scanner.nextLine();

                                   if (description.length() > 255) {
                                       description = description.substring(0, 255);
                                   }

                                   updateTask = connection.prepareStatement(
                                           String.format("UPDATE task SET description = '%s' WHERE id = %d", description, id));

                                   updateTask.execute();

                                   System.out.println("Description of the task successfully was set to " + description + ".");
                                   break;
                               case 3:
                                   while (true) {
                                       System.out.print("Enter a new priority for a task\n: ");
                                       scanner.nextLine();
                                       priority = scanner.nextLine();

                                       if (priority.isBlank()) {
                                           System.out.println("Priority can't be blank!");
                                           continue;
                                       }

                                       if (!priority.matches("[0-9]+")) {
                                           System.out.println("Priority must be a number");
                                           continue;
                                       }

                                       updateTask = connection.prepareStatement(
                                               String.format("UPDATE task SET priority = '%s' WHERE id = %d", priority, id));

                                       updateTask.execute();

                                       System.out.println("Priority of the task successfully was set to " + priority + ".");
                                       break;
                                   }
                                   break;
                               case 4:
                                   while (true) {
                                       System.out.print("Enter a new state for a task\n: ");
                                       scanner.nextLine();
                                       state = scanner.nextLine();

                                       if (state.isBlank()) {
                                           System.out.println("State can't be blank!");
                                           continue;
                                       }

                                       if (state.length() > 30) {
                                           state = state.substring(0, 30);
                                       }

                                       updateTask = connection.prepareStatement(
                                               String.format("UPDATE task SET state = '%s' WHERE id = %d", state, id));

                                       updateTask.execute();

                                       System.out.println("State of the task successfully was set to " + state + ".");
                                       break;
                                   }
                                   break;
                               default:
                                   System.out.println("Wrong!");
                                   continue;
                           }
                           break;
                       }
                   }
                   case 5 -> {
                       System.out.print("Enter ID of a task you want to delete\n: ");
                       id = scanner.nextInt();
                       deleteTask = connection.prepareStatement(String.format("DELETE FROM task WHERE id=%d", id));
                       deleteTask.execute();
                   }
                   case 6 -> System.exit(0);
               }
           }
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);

    }

}
