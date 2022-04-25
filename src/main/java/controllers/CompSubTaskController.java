package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import tasks.SubTaskCreator;

public class CompSubTaskController {

    private SubTaskCreator subTask;

    @FXML
    private TextField textField;

    public void setData(SubTaskCreator subTask){
        this.subTask = subTask;
        textField.setText(subTask.getTitle());
    }

}