package tasks;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;

import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.stage.WindowEvent;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class TasksMain extends Application{

    public static ArrayList<TasksList> tasksListsToShow = new ArrayList<>();

    public static void main(String... args) throws IOException, GeneralSecurityException, SQLException {


        Tasks service = TasksFormatter.getService();

        TaskLists result = service.tasklists().list()
                .setMaxResults(10)
                .execute();
        List<TaskList> taskLists = result.getItems();

        for (TaskList taskListsToGet : taskLists) {

            ArrayList<TaskCreator> tasksInThisList = new ArrayList<>();
            List<Task> tasks = service.tasks()
                    .list(taskListsToGet.getId())   //here goes TaskId
                    .setShowCompleted(true)
                    .setShowHidden(true)
                    .setFields("items(id,title,notes,status,due,parent,position)")
                    .execute()
                    .getItems();

            if (tasks == null || tasks.isEmpty()) {
                System.out.println("No tasks");
            } else {

                ArrayList<Task> children = new ArrayList<>();

                for (Task tasksInList : tasks) {

                    if (tasksInList.getParent() != null) {

                        children.add(tasksInList);

                    } else {
                        TaskCreator taskCreator = new TaskCreator(tasksInList.getId(), tasksInList.getTitle(),
                                TaskCreator.timeFormat(tasksInList.getDue()), TaskCreator.hourShow(TaskCreator.timeFormat(tasksInList.getDue())),
                                tasksInList.getNotes(), tasksInList.getCompleted(), tasksInList.getParent());

                        tasksInThisList.add(taskCreator);
                        DatabaseQueries.insertTask(taskCreator.getId(), taskCreator.getPriority(), taskCreator.getTime());
                    }
                }

                for (Task childrenTasks : children) {

                    TaskCreator parent = TasksFormatter.getTaskById(childrenTasks.getParent(), tasksInThisList);

                    if (parent != null) {
                        List<String> subTasks = parent.getSubTasks();
                        subTasks.add(childrenTasks.getTitle());
                        parent.setSubTasks(subTasks);
                    }
                }
                DatabaseQueries.loadValues(tasksInThisList);

                TasksList tasksList = new TasksList(taskListsToGet.getId(), taskListsToGet.getTitle());
                tasksList.setTasks(tasksInThisList);

                tasksListsToShow.add(tasksList);
            }
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        String css = this.getClass().getResource("../style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
//        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}