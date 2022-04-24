package tasks;

import java.util.ArrayList;
import java.util.List;

public class TasksList {

    private String id;
    private String name;
    private List<TaskCreator> tasks;

    public TasksList(String id, String name){
        this.id = id;
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public TasksList(){
        this.tasks = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskCreator> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskCreator> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return name;
    }
}
