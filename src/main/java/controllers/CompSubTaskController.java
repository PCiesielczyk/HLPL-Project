package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CompSubTaskController {

    private String subTask;

    @FXML
    private TextField textField;

    public void setData(String subTask){
        this.subTask = subTask;
        textField.setText(subTask);
    }

}