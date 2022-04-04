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
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksQuickstart extends Application{
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
        InputStream in = TasksQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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

    private static final ArrayList<TaskCreator> tasksToShow = new ArrayList<TaskCreator>();

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Tasks service = new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        /* Creating task
        Task task = new Task();
        task.setTitle("NOWY TASK");
        task.setDue("2022-04-12T23:20:50.52Z");

        Tasks.TasksOperations.Insert newOne = service.tasks().insert("@default", task);
        newOne.execute();*/

        // Print the first 10 task lists.
        TaskLists result = service.tasklists().list()
                .setMaxResults(10)
                .execute();
        List<TaskList> taskLists = result.getItems();

        List<Task> tasks = service.tasks()
                .list("@default")   //here goes TaskId
                .setFields("items(title,notes,status,due)")
                .execute()
                .getItems();

        if (taskLists == null || taskLists.isEmpty()) {
            System.out.println("Brak list zadań");
        } else {
            System.out.println("Listy zadań: ");
            for (TaskList tasklist : taskLists) {
                System.out.println("------" + tasklist.getTitle() + "-------------");
            }
        }

        if (tasks == null || tasks.isEmpty()) {
            System.out.println("Brak zadań");
        } else {
            System.out.println("Zadania: ");
            for (Task tasksInTasks : tasks) {
                PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
                out.println(tasksInTasks.getTitle());

                TaskCreator taskCreator = new TaskCreator(tasksInTasks.getTitle(),
                        tasksInTasks.getDue(), tasksInTasks.getNotes());
                tasksToShow.add(taskCreator);
            }
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Group root = new Group();
        Scene scene = new Scene(root, 600, 600);
        int x = 50;
        int y = 50;

        for (TaskCreator taskCreator : tasksToShow) {
            Text text = new Text();
            text.setText(taskCreator.getTitle() + " | " + taskCreator.getDetails() + " | " + taskCreator.getLocalDateTime());
            text.setX(x);
            text.setY(y);
            root.getChildren().add(text);
            y += 50;
        }

        stage.setScene(scene);
        stage.show();
    }
}