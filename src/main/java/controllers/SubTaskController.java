package controllers;

import com.google.api.services.tasks.model.Task;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import tasks.DatabaseQueries;
import tasks.SubTaskCreator;
import tasks.TaskCreator;
import tasks.TasksFormatter;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SubTaskController {

    @FXML
    private CheckBox checkBoxSubTask;

    @FXML
    private TextField textField;

    private SubTaskCreator subTask;
    private TaskCreator task;
    private GridPane grid;

    public void setData(SubTaskCreator subTask, TaskCreator task, GridPane grid){
        this.subTask = subTask;
        this.task = task;
        this.grid = grid;
        textField.setText(subTask.getTitle());

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {

            if (!newVal) {
                this.updateTitle();
            }
        });
    }

    private void updateTitle(){

        subTask.setTitle(textField.getText());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TasksFormatter.updateSubTask(subTask.getId(), textField.getText());
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void deleteSubTask() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(1000));
        fadeTransition.setNode(grid.getChildren().get(task.getSubTasks().indexOf(subTask) + 1));
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        fadeTransition.setOnFinished(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                grid.getChildren().remove(task.getSubTasks().indexOf(subTask) + 1);
                task.getCompletedSubTasks().add(subTask);
                task.getSubTasks().remove(subTask);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TasksFormatter.deleteSubTask(subTask.getId());
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

}