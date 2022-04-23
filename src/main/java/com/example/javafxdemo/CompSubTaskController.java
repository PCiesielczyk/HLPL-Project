package com.example.javafxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import models.SubTask;

public class CompSubTaskController {

    private SubTask subTask;

    @FXML
    private TextField textField;

    public void setData(SubTask subTask){
        this.subTask = subTask;
        textField.setText(subTask.getTxt());
    }

}
