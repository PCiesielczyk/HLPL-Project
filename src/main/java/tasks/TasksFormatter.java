package tasks;

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

import javax.swing.text.DateFormatter;
import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksFormatter {

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

    public static Tasks getService() throws IOException, GeneralSecurityException{

        // Build a new authorized API client service.

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void newEmptyTask(TasksList tasksList) throws GeneralSecurityException, IOException {

        Task task = new Task();

        Tasks.TasksOperations.Insert newOne = getService().tasks().insert(tasksList.getId(), task);
        newOne.execute();
    }

    public static void updateTitle(String taskListId, String taskId, String title) throws GeneralSecurityException, IOException {

        Task taskToUpdate = getNativeTaskById(taskId, getTasksFromList(taskListId));

        taskToUpdate.setTitle(title);

        Tasks.TasksOperations.Update titleUpdate = getService().tasks().update(taskListId, taskId, taskToUpdate);
        titleUpdate.execute();
    }

    public static TaskCreator getTaskById(String id, ArrayList<TaskCreator> taskList) {
        return taskList.stream()
                .filter(taskCreator -> taskCreator.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static Task getNativeTaskById(String id, List<Task> taskList) {
        return taskList.stream()
                .filter(taskCreator -> taskCreator.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private static List<Task> getTasksFromList(String listId) throws GeneralSecurityException, IOException {

        return getService().tasks()
                .list(listId)   //here goes TaskId
                .setShowCompleted(true)
                .setShowHidden(true)
                .setFields("items(id,title,notes,status,due,parent,position)")
                .execute()
                .getItems();
    }
}
