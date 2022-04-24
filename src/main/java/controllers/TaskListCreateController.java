package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import tasks.TasksList;

public class TaskListCreateController {

    TasksList taskList;

    @FXML
    private TextField textField;

    public void setData(TasksList taskList){
        this.taskList = taskList;
    }

    public void update(){
        taskList.setName(textField.getText());
    }
}