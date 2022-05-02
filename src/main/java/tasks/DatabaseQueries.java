package tasks;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DatabaseQueries {

    private static String insertQuery (String id, int priority, String time) {
        return """
                        INSERT IGNORE INTO TasksDb (id, priority, time)
                        VALUES ('%s' , %o, '%s');
                        """.formatted(id, priority, time);
    }

    private static String updateQuery (String id, int priority, String time) {
        return """
                        UPDATE TasksDb
                        SET priority = %o, time = '%s'
                        WHERE id = '%s';
                        """.formatted(priority, time, id);
    }

    private static String selectQuery () {
        return """
                        SELECT id, priority, time FROM TasksDb
                        """;
    }

    public static void changeDb(String taskId, int priority, String time) throws SQLException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = makeConnection();

                    Statement statement = connection.createStatement();

                    statement.executeUpdate((DatabaseQueries.updateQuery(taskId, priority, time)));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        // Database connection
    }

    public static void loadValues (ArrayList<TaskCreator> tasksInThisList) throws SQLException {

        Connection connection = makeConnection();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(selectQuery());
        while (rs.next()) {

            String id = rs.getString("id");
            int priority = rs.getInt("priority");
            String time = rs.getString("time");

            boolean isPresent = tasksInThisList.stream()
                    .anyMatch(task -> task.getId().equals(id));

            if (isPresent) {
                tasksInThisList.stream()
                        .filter(task -> task.getId().equals(id))
                        .findFirst()
                        .get()
                        .setPriority(priority);

                tasksInThisList.stream()
                        .filter(task -> task.getId().equals(id))
                        .findFirst()
                        .get()
                        .setTime(time);
            }
        }
    }

    public static void insertTask(String taskId, int priority, String time) throws SQLException {

        Connection connection = makeConnection();

        Statement statement = connection.createStatement();

        statement.executeUpdate(insertQuery(taskId, priority, time));
    }

    private static Connection makeConnection() {

        Connection connection = null;
        ResourceBundle reader;

        try {

            reader = ResourceBundle.getBundle("dbconfig");
            String url = reader.getString("db.url");
            String username = reader.getString("db.username");
            String password = reader.getString("db.password");

            connection = DriverManager
                    .getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        return connection;

    }

}
