package models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Task{
    private String txt;
    private int priority;
    private String date, time, description;
    private List<SubTask> subTasks;
    private List<SubTask> completedSubTasks;

    public Task(String txt, int priority, String date, String time, String description){
        this.txt = txt;
        this.priority = priority;
        this.date = date;
        this.time = time;
        this.subTasks = new ArrayList<>();
        this.completedSubTasks = new ArrayList<>();
        this.description = description;
    }

    public Task(){
        this.subTasks = new ArrayList<>();
        this.completedSubTasks = new ArrayList<>();
        this.priority = 0;
        this.description = "";
        this.txt = "";
    }


    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SubTask> getCompletedSubTasks() {
        return completedSubTasks;
    }

    public void setCompletedSubTasks(List<SubTask> completedSubTasks) {
        this.completedSubTasks = completedSubTasks;
    }
}
