public class TaskCreator {

    private String id;
    private String title;
    private String localDateTime;
    private String details;

    public TaskCreator(String id, String title, String localDateTime, String details) {
        this.id = id;
        this.title = title;
        this.localDateTime = localDateTime;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
