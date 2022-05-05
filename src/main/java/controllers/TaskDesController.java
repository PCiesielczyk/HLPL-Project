package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import operationResolver.OperationResolver;
import tasks.DatabaseQueries;
import tasks.SubTaskCreator;
import tasks.TaskCreator;
import tasks.TasksFormatter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public class TaskDesController {

    private TaskCreator task;
    private GridPane grid;
    private Label timeTxt;
    private OperationResolver operationResolver;

    @FXML
    private Button dateBtn;

    @FXML
    private TextArea textArea;

    @FXML
    private Button timeBtn;

    @FXML
    private Button addSubTaskBtn;

    public void setData(TaskCreator task, GridPane grid, Label timeTxt, OperationResolver operationResolver) {
        this.task = task;
        this.grid = grid;
        this.timeTxt = timeTxt;
        this.operationResolver = operationResolver;
        textArea.setText(task.getDetails());
        if (task.getLocalDateTime() != null) {

            timeBtn.setText(task.getTime());
            dateBtn.setText(TaskCreator.dateShow(task.getLocalDateTime()));
        }

        textArea.focusedProperty().addListener((obs, oldVal, newVal) -> {

            if (!newVal) {
                this.updateDescription();
            }
        });
        timeBtn.setOnAction(this::pickDateHour);
        dateBtn.setOnAction(this::pickDateHour);
        addSubTaskBtn.setOnAction(this::createSubTask);
    }

    private void updateDescription() {
        task.setDetails(textArea.getText());

        operationResolver.addRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    TasksFormatter.updateDetails(task.getId(), textArea.getText());
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void pickDateHour(ActionEvent action) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../dateHour.fxml"));
            DialogPane dialogPane = fxmlLoader.load();

            DateHourController dateHourController = fxmlLoader.getController();
            dateHourController.setData(task);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.get() == ButtonType.APPLY) {
                dateHourController.update();
                if (task.getLocalDateTime() != null) {
                    timeBtn.setText(task.getTime());
                    dateBtn.setText(TaskCreator.dateShow(task.getLocalDateTime()));
                    setTimeTxt();


                    operationResolver.addRunnable(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                TasksFormatter.updateDate(task.getId(), dateBtn.getText());

                                DatabaseQueries.changeDb(task.getId(), task.getPriority(), task.getTime());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

//                    TasksFormatter.updateDate(task.getId(), dateBtn.getText());
//
//                    DatabaseQueries.changeDb(task.getId(), task.getPriority(), task.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSubTask(ActionEvent action) {
        try {
            SubTaskCreator subTask = new SubTaskCreator();

            operationResolver.addRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        TasksFormatter.newEmptySubTask(task.getId());

                        subTask.setId(TasksFormatter.getLatestTask(TasksFormatter.getListIdByTaskId(task.getId())).getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            task.getSubTasks().add(subTask);

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../subTask.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();

            SubTaskController subTaskController = fxmlLoader.getController();
            subTaskController.setData(subTask, task, grid, operationResolver);

            grid.add(anchorPane, 0, grid.getRowCount());
            grid.setMargin(anchorPane, new Insets(0, 0, 4, 20));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTimeTxt(){
        String txt = TaskCreator.remainingTime(task.getLocalDateTime(), task.getTime());
        timeTxt.setText(txt);
        if (txt.equals("Time's up")){
            timeTxt.setStyle("-fx-text-fill: #ff0000");
        }
        else if (txt.length()>14){
            timeTxt.setStyle("-fx-text-fill: #bb4304");
        }
        else {
            timeTxt.setStyle("-fx-text-fill: #000000");
        }
    }

}