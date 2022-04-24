package controllers;

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
import tasks.TaskCreator;

public class SubTaskController {

    @FXML
    private CheckBox checkBoxSubTask;

    @FXML
    private TextField textField;

    private String subTask;
    private TaskCreator task;
    private GridPane grid;

    public void setData(String subTask, TaskCreator task, GridPane grid){
        this.subTask = subTask;
        this.task = task;
        this.grid = grid;
        textField.setText(subTask);
        textField.setOnKeyReleased(this::updateTitle);
    }

    private void updateTitle(KeyEvent key){

        subTask = textField.getText();

    }

    public void deleteSubTask(){
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
            }
        });
    }

}