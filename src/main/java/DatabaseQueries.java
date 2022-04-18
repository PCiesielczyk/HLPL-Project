import java.sql.*;

public class DatabaseQueries {

    private static String insertQuery (String id, int priority) {
        return """
                        INSERT INTO "TasksDb" (id, priority)
                        VALUES ('%s' , '%o')
                        ON CONFLICT DO NOTHING;
                        """.formatted(id, priority);
    }

    private static String updateQuery (String id, int priority) {
        return """
                        UPDATE "TasksDb" SET priority = %o
                        WHERE id = '%s';
                        """.formatted(priority, id);
    }

    private static String selectQuery () {
        return """
                        SELECT id, priority FROM "TasksDb"
                        """;
    }

    public static void changePriority(String taskId, int priority) throws SQLException {

        // Database connection
        Connection connection = makeConnection();

        Statement statement = connection.createStatement();

        statement.executeUpdate((DatabaseQueries.updateQuery(taskId, priority)));
    }

    public static void loadPriorities () throws SQLException {

        Connection connection = makeConnection();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(selectQuery());
        while (rs.next()) {

            String id = rs.getString("id");
            int priority = rs.getInt("priority");

            boolean isPresent = TasksMain.tasksToShow.stream()
                    .anyMatch(task -> task.getId().equals(id));

            if (isPresent) {
                TasksMain.tasksToShow.stream()
                        .filter(task -> task.getId().equals(id))
                        .findFirst()
                        .get()
                        .setPriority(priority);
            }
        }
    }

    public static void insertTask(String taskId, int priority) throws SQLException {

        Connection connection = makeConnection();

        Statement statement = connection.createStatement();

        statement.executeUpdate(insertQuery(taskId, priority));
    }

    private static Connection makeConnection() {

        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "postgres");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        return connection;

    }

}
