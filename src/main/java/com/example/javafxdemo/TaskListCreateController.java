package com.example.javafxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import models.TaskList;

public class TaskListCreateController {

    TaskList taskList;

    @FXML
    private TextField textField;

    public void setData(TaskList taskList){
        this.taskList = taskList;
    }

    public void update(){
        taskList.setName(textField.getText());
    }
}
