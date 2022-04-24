package controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import tasks.TaskCreator;


public class CompTaskDesController {
    private TaskCreator task;

    @FXML
    private Label dateTxt;

    @FXML
    private TextArea textArea;

    @FXML
    private Label timeTxt;


    public void setData(TaskCreator task){
        this.task = task;
        textArea.setText(task.getDetails());
        if (task.getLocalDateTime() != null) {

            dateTxt.setText(TaskCreator.dateShow(task.getLocalDateTime()));
            timeTxt.setText(task.getTime());
        }

    }
}
