package com.example.javafxdemo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import models.SubTask;
import models.Task;

import java.util.Optional;

public class TaskDesController {

    Task task;
    GridPane grid;

    @FXML
    private Button dateBtn;

    @FXML
    private TextArea textArea;

    @FXML
    private Button timeBtn;

    @FXML
    private Button addSubTaskBtn;

    public void setData(Task task, GridPane grid){
        this.task = task;
        this.grid = grid;
        textArea.setText(task.getDescription());
        if (task.getTime() != null)
            timeBtn.setText(task.getTime());
        if (task.getDate() != null)
            dateBtn.setText(task.getDate());
        textArea.setOnKeyReleased(this::updateDescription);
        timeBtn.setOnAction(this::pickDateHour);
        dateBtn.setOnAction(this::pickDateHour);
        addSubTaskBtn.setOnAction(this::createSubTask);
    }

    private void updateDescription(KeyEvent keyEvent) {
        task.setDescription(textArea.getText());
    }

    public void pickDateHour(ActionEvent action){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("dateHour.fxml"));
            DialogPane dialogPane = fxmlLoader.load();

            DateHourController dateHourController = fxmlLoader.getController();
            dateHourController.setData(task);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.get() == ButtonType.APPLY){
                dateHourController.update();
                if (task.getTime() != null)
                    timeBtn.setText(task.getTime());
                if (task.getDate() != null)
                    dateBtn.setText(task.getDate());
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void createSubTask(ActionEvent action){
        try {
            SubTask subTask = new SubTask("");
            task.getSubTasks().add(subTask);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("subTask.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();

            SubTaskController subTaskController = fxmlLoader.getController();
            subTaskController.setData(subTask, task, grid);

            grid.add(anchorPane, 0, grid.getRowCount());
            grid.setMargin(anchorPane, new Insets(0, 0, 4, 20));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
