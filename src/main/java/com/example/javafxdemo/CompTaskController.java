package com.example.javafxdemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import models.Task;

public class CompTaskController {
    private Task task;
    private boolean extended;

    @FXML
    private Button extendBtn;

    @FXML
    private Button prioBtn1;

    @FXML
    private Button prioBtn2;

    @FXML
    private Button prioBtn3;

    @FXML
    private TextField textField;

    @FXML
    private GridPane gridSubTask;

    public void setData(Task task){
        this.task = task;
        this.textField.setText(task.getTxt());
        this.extended = false;
        setPriorityBoxes();
        extendBtn.setOnAction(this::extend);
    }

    private void setPriorityBoxes(){
        if (task.getPriority() == 0){
            prioBtn1.setStyle("-fx-background-color: #09fc0d;");
            prioBtn2.setStyle("-fx-background-color: #ffffff;");
            prioBtn3.setStyle("-fx-background-color: #ffffff;");
        }
        else if (task.getPriority() == 1){
            prioBtn1.setStyle("-fx-background-color: #f7ff2a;");
            prioBtn2.setStyle("-fx-background-color: #f7ff2a;");
            prioBtn3.setStyle("-fx-background-color: #ffffff;");
        }
        else {
            prioBtn1.setStyle("-fx-background-color: #ff0000;");
            prioBtn2.setStyle("-fx-background-color: #ff0000;");
            prioBtn3.setStyle("-fx-background-color: #ff0000;");
        }
    }

    private void extend(ActionEvent action) {
        if (!extended){
            extendBtn.setRotate(180);
            try {
                FXMLLoader fxmlLoader2 = new FXMLLoader();
                fxmlLoader2.setLocation(getClass().getResource("compTaskDes.fxml"));
                AnchorPane anchorPane2 = fxmlLoader2.load();
                CompTaskDesController compTaskDesController = fxmlLoader2.getController();
                compTaskDesController.setData(task);
                gridSubTask.add(anchorPane2, 0, 0);
                for (int i=1;i<=task.getCompletedSubTasks().size();i++){
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("compSubTask.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    CompSubTaskController compSubTaskController = fxmlLoader.getController();
                    compSubTaskController.setData(task.getCompletedSubTasks().get(i-1));

                    gridSubTask.add(anchorPane, 0, i);
                    gridSubTask.setMargin(anchorPane, new Insets(0, 0, 4, 20));
                }
                extended = true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else {
            extendBtn.setRotate(0);
            if (gridSubTask.getChildren().size()>0)
                gridSubTask.getChildren().remove(0, gridSubTask.getChildren().size());
            extended = false;
        }
    }

}
