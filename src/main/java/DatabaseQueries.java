import java.sql.*;

public class DatabaseQueries {

    public static String insertQuery (String id, int priority) {
        return """
                        INSERT INTO "TasksDb" (id, priority)
                        VALUES ('%s' , '%o');
                        """.formatted(id, priority);
    }

}
