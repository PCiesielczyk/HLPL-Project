import java.sql.*;

public class DatabaseQueries {

    public static String insertQuery (String id, String priority) {
        return """
                        INSERT INTO "TasksDb" (id, priority)
                        VALUES ('%s' , '%s');
                        """.formatted(id, priority);
    }

}
