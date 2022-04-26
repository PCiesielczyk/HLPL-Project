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
import com.google.api.services.tasks.model.TaskLists;

import javax.swing.text.DateFormatter;
import java.io.*;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    public static void newEmptyTask(TasksList tasksList) throws GeneralSecurityException, IOException, SQLException {

        Task task = new Task();

        Tasks.TasksOperations.Insert newOne = getService().tasks().insert(tasksList.getId(), task);
        newOne.execute();

        Task newTask = getLatestTask(tasksList.getId());
        DatabaseQueries.insertTask(newTask.getId(), 1, "");
    }

    public static void newEmptyTaskList(String taskListName) throws GeneralSecurityException, IOException {

        TaskList taskList = new TaskList();
        taskList.setTitle(taskListName);

        Tasks.Tasklists.Insert newOne = getService().tasklists().insert(taskList);
        newOne.execute();
    }

    public static void updateTitle(String taskListId, String taskId, String title) throws GeneralSecurityException, IOException {

        Task taskToUpdate = getNativeTaskById(taskId, taskListId);

        taskToUpdate.setTitle(title);

        Tasks.TasksOperations.Update titleUpdate = getService().tasks().update(taskListId, taskId, taskToUpdate);
        titleUpdate.execute();
    }

    public static void updateDetails(String taskId, String details) throws GeneralSecurityException, IOException {

        String taskListId = getListIdByTaskId(taskId);
        Task taskToUpdate = getNativeTaskById(taskId, taskListId);

        taskToUpdate.setNotes(details);

        Tasks.TasksOperations.Update detailsUpdate = getService().tasks().update(taskListId, taskId, taskToUpdate);
        detailsUpdate.execute();
    }

    public static void completeTask(String taskId) throws GeneralSecurityException, IOException {

        String taskListId = getListIdByTaskId(taskId);
        Task taskToUpdate = getNativeTaskById(taskId, taskListId);

        taskToUpdate.setStatus("completed");

        Tasks.TasksOperations.Update completeUpdate = getService().tasks().update(taskListId, taskId, taskToUpdate);
        completeUpdate.execute();

    }

    public static void newEmptySubTask(String taskId) throws GeneralSecurityException, IOException {

        String taskListId = getListIdByTaskId(taskId);
        Task task = new Task();

        Tasks.TasksOperations.Insert newOne = getService().tasks().insert(taskListId, task);
        newOne.execute();

        Task latestTask = getLatestTask(taskListId);

        Tasks.TasksOperations.Move move = getService().tasks().move(taskListId, latestTask.getId());
        move.setParent(taskId);
        move.execute();
    }

    public static void updateSubTask(String subTaskId, String title) throws GeneralSecurityException, IOException {

        String taskListId = getListIdByTaskId(subTaskId);
        Task taskToUpdate = getNativeTaskById(subTaskId, taskListId);

        taskToUpdate.setTitle(title);

        Tasks.TasksOperations.Update subTaskUpdate = getService().tasks().update(taskListId, subTaskId, taskToUpdate);
        subTaskUpdate.execute();
    }

    public static void updateDate(String taskId, String dateBeforeFormat) throws GeneralSecurityException, IOException, ParseException {

        String taskListId = getListIdByTaskId(taskId);
        Task taskToUpdate = getNativeTaskById(taskId, taskListId);

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ");
        String dateAfterFormat = myFormat.format(fromUser.parse(dateBeforeFormat));

        taskToUpdate.setDue(dateAfterFormat);

        Tasks.TasksOperations.Update dateUpdate = getService().tasks().update(taskListId, taskId, taskToUpdate);
        dateUpdate.execute();
    }

    public static TaskCreator getTaskById(String id, ArrayList<TaskCreator> taskList) {
        return taskList.stream()
                .filter(taskCreator -> taskCreator.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static Task getNativeTaskById(String id, String taskListId) throws GeneralSecurityException, IOException {

        List<Task> taskList = getService().tasks()
                .list(taskListId)   //here goes TaskId
                .setShowCompleted(true)
                .setShowHidden(true)
                .setFields("items(id,title,notes,status,due,parent,position)")
                .execute()
                .getItems();

        return taskList.stream()
                .filter(taskCreator -> taskCreator.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static String getListIdByTaskId(String taskId) throws GeneralSecurityException, IOException {

        TaskLists result = getService().tasklists().list()
                .setMaxResults(10)
                .execute();

        List<TaskList> taskLists = result.getItems();

        for (TaskList taskList : taskLists) {

            List<Task> tasks = getService().tasks()
                    .list(taskList.getId())   //here goes TaskId
                    .setShowCompleted(true)
                    .setShowHidden(true)
                    .setFields("items(id,title,notes,status,due,parent,position)")
                    .execute()
                    .getItems();

            for (Task task : tasks) {
                if (task.getId().equals(taskId)) {
                    return taskList.getId();
                }
            }
        }
        return null;
    }

    public static Task getLatestTask(String listId) throws GeneralSecurityException, IOException {

        List<Task> tasks = getService().tasks()
                .list(listId)   //here goes TaskId
                .setShowCompleted(true)
                .setShowHidden(true)
                .setFields("items(id,title,notes,status,due,parent,position)")
                .setMaxResults(1)
                .execute()
                .getItems();

        return tasks.get(0);
    }

    public static TaskList getLatestTaskList() throws GeneralSecurityException, IOException {

        TaskLists result = getService().tasklists().list()
                .setMaxResults(10)
                .execute();

        List<TaskList> taskLists = result.getItems();

        return taskLists.get(taskLists.size() - 1);
    }
}
