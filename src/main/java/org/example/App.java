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

        try{
           connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

           PreparedStatement addNewTask;
           PreparedStatement deleteTask;
           PreparedStatement getAllTasks = connection.prepareStatement("SELECT * FROM task ORDER BY priority DESC");

           while(true) {
               System.out.println("Decide what you want to do:");
               System.out.println("\t 1 = Show tasks");
               System.out.println("\t 2 = Add new task");
               System.out.println("\t 3 = Update task's state");
               System.out.println("\t 4 = Delete task by id");
               System.out.println("\t 5 = Exit app");

                decision = scanner.nextInt();

               switch(decision) {
                   case 1:
                       ResultSet resultSet = getAllTasks.executeQuery();

                       while(resultSet.next()) {
                           System.out.printf("#%d %s \n", resultSet.getInt(1),
                                   capitalize(resultSet.getString(2)));
                           if(resultSet.getString(3) == null) {
                               System.out.println("No description");
                           } else {
                               System.out.printf("Description:\t%s\n", capitalize(resultSet.getString(3)));
                           }
                           System.out.printf("Priority:\t%d\n", resultSet.getInt(4));
                           System.out.println("----------------");
                       }
                       break;
                   case 2:
                       System.out.print("Enter a name for your task\n: ");
                       scanner.nextLine();
                       String name = scanner.nextLine();
                       System.out.print("Enter a description for your task. Leave blank if you don't want to have one\n: ");
                       String description = scanner.nextLine();
                       System.out.print("Enter a priority for your task\n: ");
                       String priority = scanner.nextLine();
                       System.out.print("Enter a state for your task\n: ");
                       String state = scanner.nextLine();

                       if(description.isBlank()) {
                           addNewTask = connection.prepareStatement(
                                   "INSERT INTO task (name, description, priority, state)" +
                                           String.format(" VALUES ('%s', null, %s, '%s')", name, priority, state));
                       } else {
                           addNewTask = connection.prepareStatement(
                                   "INSERT INTO task (name, description, priority, state)" +
                                           String.format(" VALUES ('%s', '%s', %s, '%s')", name, description, priority, state));
                       }

                       addNewTask.execute();
                       break;
                   case 3:
                       break;
                   case 4:
                       System.out.print("Enter id of a task you want to delete\n: ");
                       int id = scanner.nextInt();

                       deleteTask = connection.prepareStatement(String.format("DELETE FROM task WHERE id=%d", id));

                       deleteTask.execute();
                       break;
                   case 5:
                       System.exit(0);
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
