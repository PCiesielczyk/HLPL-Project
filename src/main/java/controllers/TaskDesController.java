package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import tasks.DatabaseQueries;
import tasks.SubTaskCreator;
import tasks.TaskCreator;
import tasks.TasksFormatter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class TaskDesController {

    TaskCreator task;
    GridPane grid;

    @FXML
    private Button dateBtn;

    @FXML
    private TextArea textArea;

    @FXML
    private Button timeBtn;

    @FXML
    private Button addSubTaskBtn;

    public void setData(TaskCreator task, GridPane grid) {
        this.task = task;
        this.grid = grid;
        textArea.setText(task.getDetails());
        if (task.getLocalDateTime() != null) {

            timeBtn.setText(task.getTime());
            dateBtn.setText(TaskCreator.dateShow(task.getLocalDateTime()));
        }

        textArea.setOnKeyReleased(this::updateDescription);
        timeBtn.setOnAction(this::pickDateHour);
        dateBtn.setOnAction(this::pickDateHour);
        addSubTaskBtn.setOnAction(this::createSubTask);
    }

    private void updateDescription(KeyEvent keyEvent) {
        task.setDetails(textArea.getText());

        try {
            TasksFormatter.updateDetails(task.getId(), textArea.getText());
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
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

                    TasksFormatter.updateDate(task.getId(), dateBtn.getText());

                    DatabaseQueries.changeDb(task.getId(), task.getPriority(), task.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSubTask(ActionEvent action) {
        try {

            TasksFormatter.newEmptySubTask(task.getId());

            SubTaskCreator subTask = new SubTaskCreator();
            subTask.setId(TasksFormatter.getLatestId(TasksFormatter.getListIdByTaskId(task.getId())));

            task.getSubTasks().add(subTask);

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../subTask.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();

            SubTaskController subTaskController = fxmlLoader.getController();
            subTaskController.setData(subTask, task, grid);

            grid.add(anchorPane, 0, grid.getRowCount());
            grid.setMargin(anchorPane, new Insets(0, 0, 4, 20));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}