package tasks;

import com.google.api.client.util.DateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskCreator {

    static final int PRIORITY = 1;
    private String id;
    private String title;
    private LocalDateTime localDateTime;
    private String time;
    private String details;
    private String complete;
    private String parent;
    private int priority;
    private List<String> subTasks;
    private List<String> completedSubTasks;

    public TaskCreator(String id, String title, LocalDateTime localDateTime, String time,
                       String details, String complete, String parent, int priority) {
        this.id = id;
        this.title = title;
        this.localDateTime = localDateTime;
        this.time = time;
        this.details = details;
        this.complete = complete;
        this.parent = parent;
        this.priority = priority;
        this.subTasks = new ArrayList<>();
        this.completedSubTasks = new ArrayList<>();
    }

    public TaskCreator(String id, String title, LocalDateTime localDateTime, String time,
                       String details, String complete, String parent) {
        this(id, title, localDateTime, time, details, complete, parent, PRIORITY);
    }

    public TaskCreator() {
        this("", "", null, "", "", "", "", PRIORITY);
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

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<String> subTasks) {
        this.subTasks = subTasks;
    }

    public List<String> getCompletedSubTasks() {
        return completedSubTasks;
    }

    public void setCompletedSubTasks(List<String> completedSubTasks) {
        this.completedSubTasks = completedSubTasks;
    }

    public String toString() {
        return getTitle();
    }

    public static LocalDateTime timeFormat(String dateString) {

        if (dateString == null) {
            return null;
        }
        dateString = dateString.substring(0, 19).replace("T", " ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter);
    }

    public static String timeShow (LocalDateTime localDateTime) {

        if (localDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return localDateTime.format(formatter);
        } else {
            return null;
        }
    }

    public static String hourShow (LocalDateTime localDateTime) {

        if (localDateTime != null) {
            if (localDateTime.getMinute() < 10) {
                return localDateTime.getHour() + ":0" + localDateTime.getMinute();
            } else {
                return localDateTime.getHour() + ":" + localDateTime.getMinute();
            }
        } else {
            return null;
        }
    }

    public static String dateShow (LocalDateTime localDateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return localDateTime.format(formatter);
    }

    public static String remainingTime (LocalDateTime localDateTime) {

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(localDateTime)) {
            return "Time's up";
        } else {
            long days = now.until(localDateTime, ChronoUnit.DAYS);
            now = now.plusDays(days);

            long hours = now.until(localDateTime, ChronoUnit.HOURS);
            now = now.plusHours(hours);

            long minutes = now.until(localDateTime, ChronoUnit.MINUTES);

            return String.format("Remains: %o day(s), %o hour(s) and %o minute(s)", days, hours, minutes);
        }
    }
}
