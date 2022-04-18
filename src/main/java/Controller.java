import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ListView<TaskCreator> myListView;

    @FXML
    private ComboBox<Integer> myComboBox;
    Integer[] priorities = {1, 2, 3};


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        myListView.getItems().addAll(TasksMain.tasksToShow);
        myComboBox.getItems().addAll(priorities);

    }

    public void changePrio(ActionEvent event) throws SQLException {

        TaskCreator taskToChange = myListView.getSelectionModel().getSelectedItem();

        taskToChange.setPriority(myComboBox.getValue());
        DatabaseQueries.changePriority(taskToChange.getId(), myComboBox.getValue());

        myListView.refresh();

    }

}
