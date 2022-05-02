package tasks;

public class SubTaskCreator {

    private String id;
    private String title;
    private String parent;

    public SubTaskCreator(String id, String title, String parent) {
        this.id = id;
        this.title = title;
        this.parent = parent;
    }

    public SubTaskCreator() {
        this("", "", "");
    }

    public String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
