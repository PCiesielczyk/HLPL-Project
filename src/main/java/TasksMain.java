import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.sql.*;

public class TasksMain extends Application{
    private static final String APPLICATION_NAME = "Google Tasks API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(TasksScopes.TASKS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = TasksMain.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static final ArrayList<TaskCreator> tasksToShow = new ArrayList<>();

    public static void main(String... args) throws IOException, GeneralSecurityException, SQLException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        Tasks service = new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        /* Creating task
        Task task = new Task();
        task.setTitle("NOWY TASK");
        task.setDue("2022-04-12T23:20:50.52Z");

        Tasks.TasksOperations.Insert newOne = service.tasks().insert("@default", task);
        newOne.execute();*/


        /*TaskLists result = service.tasklists().list()
                .setMaxResults(10)
                .execute();
        List<TaskList> taskLists = result.getItems();*/

        List<Task> tasks = service.tasks()
                .list("@default")   //here goes TaskId
                .setShowCompleted(true)
                .setShowHidden(true)
                .setFields("items(id,title,notes,status,due,parent,position)")
                .execute()
                .getItems();

        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks");
        } else {
            for (Task tasksInList : tasks) {

                TaskCreator taskCreator = new TaskCreator(tasksInList.getId(), tasksInList.getTitle(),
                        TaskCreator.timeFormat(tasksInList.getDue()), tasksInList.getNotes(), tasksInList.getCompleted(),
                        tasksInList.getParent());

                tasksToShow.add(taskCreator);

                // insert query execution
               DatabaseQueries.insertTask(taskCreator.getId(), taskCreator.getPriority());
            }
            DatabaseQueries.loadPriorities();
        }
        /*//change 4 tasks' priority
        tasksToShow.stream()
                .limit(4)
                .forEach(taskCreator -> taskCreator.setPriority(2));
        //set priority in db
        for (TaskCreator taskCreator : tasksToShow) {
            System.out.println(taskCreator.getPriority());
            System.out.println(taskCreator.getId());
            statement.executeUpdate((DatabaseQueries.updateQuery(taskCreator.getId(), taskCreator.getPriority())));
        }*/

        for (TaskCreator task : tasksToShow) {
            if (task.getLocalDateTime() != null) {
                out.print(task.getTitle() + " - ");
                System.out.println(TaskCreator.remainingTime(task.getLocalDateTime()));
            }
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SceneBuilderTest.fxml"));
        Parent root =  loader.load();
        stage.setTitle("Testing one two");
        stage.setScene(new Scene(root));
        stage.show();
    }
}