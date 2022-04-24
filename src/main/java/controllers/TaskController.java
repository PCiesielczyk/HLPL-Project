package controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.List;

import tasks.DatabaseQueries;
import tasks.TaskCreator;
import tasks.TasksFormatter;
import tasks.TasksList;

public class TaskController{

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private GridPane gridSubTask;

    @FXML
    private TextField textField;

    @FXML
    private CheckBox checkBoxTask;

    @FXML
    private Button prioBtn1;

    @FXML
    private Button prioBtn2;

    @FXML
    private Button prioBtn3;

    @FXML
    private Button extendBtn;


    private TaskCreator task;
    private GridPane grid;
    private GridPane compGrid;
    private ComboBox comboBox;
    private TasksList taskList;
    private List completedTaskList;
    private FadeTransition fadeTransition = new FadeTransition();
    private boolean extended = false;


    public void setData(TaskCreator task, GridPane grid, ComboBox comboBox, TasksList taskList, List completedTaskList) {
        this.task = task;
        textField.setText(task.getTitle());
//        priorityLabel.setText(String.valueOf(task.getPriority()));
//        dateLabel.setText(task.getDate() + "");
        this.grid = grid;
        this.compGrid = compGrid;
        this.comboBox = comboBox;
        this.taskList = taskList;
        this.completedTaskList = completedTaskList;
        checkBoxTask.setOnAction(this::deleteTask);
//        this.textField.setOnMouseClicked(e -> textField.selectAll());
        this.textField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                this.grid.requestFocus();
            }
            this.task.setTitle(textField.getText());
            try {
                TasksFormatter.updateTitle(taskList.getId(), task.getId(), textField.getText());
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        });
        setPriorityBoxes();
        prioBtn1.setOnAction(this::prioBtn1Clicked);
        prioBtn2.setOnAction(this::prioBtn2Clicked);
        prioBtn3.setOnAction(this::prioBtn3Clicked);
        extendBtn.setOnAction(this::extend);
    }

    public void deleteTask(ActionEvent event){
        makeFadeOut();
    }

    private void makeFadeOut(){
        fadeTransition.setDuration(Duration.millis(1000));
        fadeTransition.setNode(grid.getChildren().get(taskList.getTasks().indexOf(task)));
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
        fadeTransition.setOnFinished(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                completedTaskList.add(task);
                grid.getChildren().remove(taskList.getTasks().indexOf(task));
                taskList.getTasks().remove(task);
            }
        });
    }

    private void setPriorityBoxes(){
        if (task.getPriority() == 1){
            prioBtn1.setStyle("-fx-background-color: #09fc0d;");
            prioBtn2.setStyle("-fx-background-color: #ffffff;");
            prioBtn3.setStyle("-fx-background-color: #ffffff;");
        }
        else if (task.getPriority() == 2){
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

    private void prioBtn1Clicked(ActionEvent event) {
        prioBtn1.setStyle("-fx-background-color: #09fc0d;");
        prioBtn2.setStyle("-fx-background-color: #ffffff;");
        prioBtn3.setStyle("-fx-background-color: #ffffff;");
        this.task.setPriority(1);
        try {
            DatabaseQueries.changeDb(this.task.getId(), this.task.getPriority(), this.task.getTime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void prioBtn2Clicked(ActionEvent event) {
        prioBtn1.setStyle("-fx-background-color: #f7ff2a;");
        prioBtn2.setStyle("-fx-background-color: #f7ff2a;");
        prioBtn3.setStyle("-fx-background-color: #ffffff;");
        this.task.setPriority(2);
        try {
            DatabaseQueries.changeDb(this.task.getId(), this.task.getPriority(), this.task.getTime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void prioBtn3Clicked(ActionEvent event) {
        prioBtn1.setStyle("-fx-background-color: #ff0000;");
        prioBtn2.setStyle("-fx-background-color: #ff0000;");
        prioBtn3.setStyle("-fx-background-color: #ff0000;");
        this.task.setPriority(3);
        try {
            DatabaseQueries.changeDb(this.task.getId(), this.task.getPriority(), this.task.getTime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void extend(ActionEvent action){
        if (!extended){
            extendBtn.setRotate(180);
            try {
                FXMLLoader fxmlLoader2 = new FXMLLoader();
                fxmlLoader2.setLocation(getClass().getResource("../taskDes.fxml"));
                AnchorPane anchorPane2 = fxmlLoader2.load();
                TaskDesController taskDesController = fxmlLoader2.getController();
                taskDesController.setData(task, gridSubTask);
                gridSubTask.add(anchorPane2, 0, 0);
                for (int i=1;i<=task.getSubTasks().size();i++){
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("../subTask.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    SubTaskController subTaskController = fxmlLoader.getController();
                    subTaskController.setData(task.getSubTasks().get(i-1), task, gridSubTask);

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