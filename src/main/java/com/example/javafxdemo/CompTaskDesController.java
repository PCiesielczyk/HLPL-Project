package com.example.javafxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.Task;

public class CompTaskDesController {
    private Task task;

    @FXML
    private Label dateTxt;

    @FXML
    private TextArea textArea;

    @FXML
    private Label timeTxt;


    public void setData(Task task){
        this.task = task;
        textArea.setText(task.getDescription());
        if (task.getDate() != null)
            dateTxt.setText(task.getDate());
        if (task.getTime() != null)
            timeTxt.setText(task.getTime());
    }
}
