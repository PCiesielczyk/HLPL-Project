package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import tasks.TaskCreator;

public class DateHourController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox hourComBox;

    private TaskCreator task;
    public void setData(TaskCreator task){
        this.task = task;
        if (task.getLocalDateTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate localDate = LocalDate.parse(TaskCreator.dateShow(task.getLocalDateTime()), formatter);
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
        if (datePicker.getValue() != null) {
            task.setLocalDateTime(localDate.atTime(0, 0));
        }
        if (!hourComBox.getSelectionModel().isEmpty()) {
            task.setTime(hourComBox.getSelectionModel().getSelectedItem().toString());
        }
    }

}