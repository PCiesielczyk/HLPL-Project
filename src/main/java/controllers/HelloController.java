package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import tasks.TaskCreator;
import tasks.TasksFormatter;
import tasks.TasksList;
import tasks.TasksMain;

public class HelloController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private ComboBox<TasksList> comboBoxTop;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button addTaskBtn;

    @FXML
    private Button addTListBtn;

    @FXML
    private AnchorPane currentTasksPn;

    @FXML
    private AnchorPane completedTasksPn;

    @FXML
    private GridPane completedGridPane;

    @FXML
    private Button currTBtn;

    @FXML
    private Button compTBtn;

    @FXML
    private StackPane stackPane;

    List<TasksList> taskLists;
    List<TaskCreator> completedTaskList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskLists = new ArrayList<>();
        completedTaskList = new ArrayList<>();
        iterate();
        taskLists.addAll(TasksMain.tasksListsToShow);
        comboBoxTop.getItems().addAll(taskLists);
        comboBoxTop.setOnAction(this::getTaskList);
        addTaskBtn.setOnAction(this::createNewTask);
        addTListBtn.setOnAction(this::createTaskList);
        currTBtn.setOnAction(this::switchToCurrent);
        compTBtn.setOnAction(this::switchToCompleted);
    }

    private void loadTaskList() {
        try {
            for (int i = 0; i < comboBoxTop.getValue().getTasks().size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../task.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                TaskController taskController = fxmlLoader.getController();
                taskController.setData(comboBoxTop.getValue().getTasks().get(i), grid, comboBoxTop,
                        comboBoxTop.getValue(), completedTaskList);

                grid.add(anchorPane, 0, i);
                grid.setMargin(anchorPane, new Insets(4, 0, 0, 4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCompletedTaskList() {
        try {
            for (int i = 0; i < completedTaskList.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../compTask.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                CompTaskController compTaskController = fxmlLoader.getController();
                compTaskController.setData(completedTaskList.get(i));

                completedGridPane.add(anchorPane, 0, i);
                completedGridPane.setMargin(anchorPane, new Insets(4, 0, 0, 4));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTaskList(ActionEvent event) {
        if (grid.getChildren().size() > 0)
            grid.getChildren().remove(0, grid.getChildren().size());
        loadTaskList();
    }

    private void createNewTask(ActionEvent action) {
        if (!comboBoxTop.getSelectionModel().isEmpty()) {
            try {
                TaskCreator task = new TaskCreator();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TasksFormatter.newEmptyTask(comboBoxTop.getSelectionModel().getSelectedItem());
                            task.setId(TasksFormatter.getLatestTask(comboBoxTop.getSelectionModel().getSelectedItem().getId()).getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                comboBoxTop.getSelectionModel().getSelectedItem().getTasks().add(0, task);
                if (grid.getChildren().size() > 0) {
                    grid.getChildren().remove(0, grid.getChildren().size());
                }

                //TasksFormatter.sortByPriority(comboBoxTop.getSelectionModel().getSelectedItem());

                loadTaskList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createTaskList(ActionEvent event) {
        currentTasksPn.toFront();
        try {
            TasksList taskList = new TasksList();
            taskLists.add(taskList);

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../taskListCreate.fxml"));
            DialogPane dialogPane = fxmlLoader.load();

            TaskListCreateController taskListCreateController = fxmlLoader.getController();
            taskListCreateController.setData(taskList);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.get() == ButtonType.APPLY) {

                taskListCreateController.update();
                comboBoxTop.getItems().add(taskList);

                TasksFormatter.newEmptyTaskList(taskList.getName());
                taskList.setId(TasksFormatter.getLatestTaskList().getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchToCurrent(ActionEvent action) {
        currentTasksPn.toFront();
    }

    private void switchToCompleted(ActionEvent action) {
        completedTasksPn.toFront();
        if (completedGridPane.getChildren().size() > 0)
            completedGridPane.getChildren().remove(0, completedGridPane.getChildren().size());
        loadCompletedTaskList();
    }

    private void iterate() {

        for (TasksList tasksList : TasksMain.tasksListsToShow) {

            for (TaskCreator taskCreator : tasksList.getTasks()) {

                if (taskCreator.getComplete().equals("completed")) {

                    completedTaskList.add(taskCreator);
                }
            }
            tasksList.getTasks().removeAll(completedTaskList);
        }
    }
}