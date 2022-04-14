public class DatabaseQueries {

    public static String insertQuery (String id, int priority) {
        return """
                        INSERT INTO "TasksDb" (id, priority)
                        VALUES ('%s' , '%o')
                        ON CONFLICT DO NOTHING;
                        """.formatted(id, priority);
    }

    public static String updateQuery (String id, int priority) {
        return """
                        UPDATE "TasksDb" SET priority = %o
                        WHERE id = '%s';
                        """.formatted(priority, id);
    }

    public static String selectQuery () {
        return """
                        SELECT id, priority FROM "TasksDb"
                        """;
    }

}
