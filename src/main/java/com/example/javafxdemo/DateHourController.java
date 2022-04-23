package com.example.javafxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import models.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHourController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox hourComBox;

    private Task task;
    public void setData(Task task){
        this.task = task;
        if (task.getDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.parse(task.getDate(), formatter);
            datePicker.setValue(localDate);
            hourComBox.getItems().add(task.getTime());
            hourComBox.getSelectionModel().select(0);
        }
        fillHourComBox();
    }
    private void fillHourComBox(){
        for (int i=0;i<10;i++){
            hourComBox.getItems().add("0" + i + ":" + "00");
            hourComBox.getItems().add("0" + i + ":" + "30");
        }
        for (int i=10;i<24;i++){
            hourComBox.getItems().add(i + ":" + "00");
            hourComBox.getItems().add(i + ":" + "30");
        }
    }

    public void update(){
        LocalDate localDate = datePicker.getValue();
        if (datePicker.getValue() != null)
            task.setDate(localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (!hourComBox.getSelectionModel().isEmpty())
            task.setTime(hourComBox.getSelectionModel().getSelectedItem().toString());
    }



}
