import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.json.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String in = sc.nextLine();

            if (in.isEmpty()) System.out.println("String is empty");
            else if (in.startsWith("add")) {
                add(in);
            } else if (in.startsWith("list")) {
                display(in);
            } else if (in.startsWith("update")) {
                update(in);
            } else if (in.startsWith("mark")) {
                mark(in);
            } else if (in.startsWith("delete")) {
                delete(in);
            } else
                System.out.println("Unknown command");
        }
    }

    private static void add(String in) {
        String regex = "add\\s+\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(in);

        if (matcher.find()) {
            String description = matcher.group(1);

            JsonArray tasks = loadTasks();

            if (tasks.size() == 0) {
                Task task = new Task(description, 0);
                addTask(task);
            } else {
                int newId = 0;
                for (JsonValue jsonTask : tasks) {
                    int currentId = jsonTask.asJsonObject().getInt("id");
                    newId = Math.max(newId, currentId);
                }

                Task task = new Task(description, newId + 1);
                addTask(task);
            }
        } else
            System.out.println("Incorrect input.");
    }

    private static void addTask(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JsonArray tasks = loadTasks();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder(tasks);
        JsonObject newTask = Json.createObjectBuilder()
                .add("id", task.id)
                .add("description", task.description)
                .add("status", task.status)
                .add("createdAt", (task.createdAt).format(formatter))
                .add("updatedAt", (task.updatedAt).format(formatter))
                .build();

        arrayBuilder.add(newTask);
        saveTasks(arrayBuilder.build());
        System.out.printf("Task added successfully (ID: %d)", task.id);
        System.out.println();
    }

    private static void update(String in) {
        String regex = "update\\s+(\\d+)\\s+\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(in);

        if (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            String description = matcher.group(2);

            updateTask(id, description);
        } else
            System.out.println("Incorrect input.");
    }

    private static void updateTask(int id, String newDesc) {
        JsonArray tasks = loadTasks();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        boolean taskFound = false;
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.getJsonObject(i).getInt("id") == id) {
                taskFound = true;
                arrayBuilder.add(Json.createObjectBuilder()
                        .add("id", tasks.getJsonObject(i).getInt("id"))
                        .add("description", newDesc)
                        .add("status", tasks.getJsonObject(i).getString("status"))
                        .add("createdAt", tasks.getJsonObject(i).getString("createdAt"))
                        .add("updatedAt", (LocalDateTime.now()).format(formatter))
                        .build());
            } else {
                arrayBuilder.add(tasks.getJsonObject(i));
            }
        }

        if (!taskFound) {
            System.out.println("Task with the given ID not found.");
        }

        saveTasks(arrayBuilder.build());
        System.out.println("Task updated");
    }

    private static void saveTasks(JsonArray tasks) {
        try (OutputStream output = new FileOutputStream("tasks.json");
             Writer writer = new OutputStreamWriter(output, "UTF-8");
             JsonWriter jsonWriter = Json.createWriter(writer)) {
            jsonWriter.writeArray(tasks);
        } catch (Exception e) {
            System.out.println("Error while saving tasks: " + e.getMessage());
        }
    }

    private static JsonArray loadTasks() {
        File file = new File("tasks.json");
        if (!file.exists() || file.length() == 0) return Json.createArrayBuilder().build();
        try (InputStream inputStream = new FileInputStream(file);
             JsonReader jsonReader = Json.createReader(inputStream)) {
            return jsonReader.readArray();
        } catch (IOException | JsonException e) {
            System.out.println("Error while loading tasks: " + e.getMessage());
            return Json.createArrayBuilder().build();
        }
    }

    private static void mark(String in) {
        String regex = "mark-([a-zA-Z-]+)\\s+(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(in);

        if (matcher.find()) {
            String status = matcher.group(1);
            int id = Integer.parseInt(matcher.group(2));

            updateTaskMark(id, status);
        } else
            System.out.println("Incorrect input");
    }

    private static void updateTaskMark(int id, String newStatus) {
        Set<String> validStatuses = new HashSet<>(Arrays.asList("todo", "in-progress", "done"));
        if (!validStatuses.contains(newStatus)) {
            System.out.println("Wrong mark");
            return;
        }

        boolean taskFound = false;
        JsonArray tasks = loadTasks();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.getJsonObject(i).getInt("id") == id) {
                taskFound = true;
                arrayBuilder.add(Json.createObjectBuilder()
                        .add("id", tasks.getJsonObject(i).getInt("id"))
                        .add("description", tasks.getJsonObject(i).getString("description"))
                        .add("status", newStatus)
                        .add("createdAt", tasks.getJsonObject(i).getString("createdAt"))
                        .add("updatedAt", (LocalDateTime.now()).format(formatter))
                        .build());
            } else {
                arrayBuilder.add(tasks.getJsonObject(i));
            }
        }
        if (!taskFound) {
            System.out.println("Task with the given ID not found.");
        }

        saveTasks(arrayBuilder.build());
        System.out.println("Task updated");
    }

    private static void display(String in) {
        String regex = "list(\\s+(done|in-progress|todo))?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(in);

        JsonArray tasks = loadTasks();
        if (tasks.size() == 0) {
            System.out.println("No tasks available.");
            return;
        }

        int counter = 0;
        //list done or list in-progress or list todo
        if (matcher.find()) {
            String neededStatus = matcher.group(2);
            if (neededStatus != null) {
                System.out.println("Filtering tasks with status: " + neededStatus);
                for (int i = 0; i < tasks.size(); i++) {
                    JsonObject obj = tasks.getJsonObject(i);
                    if (obj.getString("status").equals(neededStatus)) {
                        counter++;
                        System.out.printf("ID: %d, Description: %s, Status: %s, Created At: %s, Updated At: %s\n",
                                obj.getInt("id"),
                                obj.getString("description"),
                                obj.getString("status"),
                                obj.getString("createdAt"),
                                obj.getString("updatedAt"));
                    }
                }
                if (counter == 0) {
                    System.out.printf("No tasks with status %s available.\n", neededStatus);
                }
            } else {
                for (JsonValue task : tasks) {
                    JsonObject obj = task.asJsonObject();
                    System.out.printf("ID: %d, Description: %s, Status: %s, Created At: %s, Updated At: %s\n",
                            obj.getInt("id"),
                            obj.getString("description"),
                            obj.getString("status"),
                            obj.getString("createdAt"),
                            obj.getString("updatedAt"));
                }
            }
        } else
            System.out.println("Incorrect input format or no status provided.");
    }

    private static void delete(String in) {
        try {
            int id = Integer.parseInt(in.substring(7));
            deleteTask(id);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format");
        }
    }

    private static void deleteTask(int id) {
        boolean taskFound = false;
        JsonArray tasks = loadTasks();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.getJsonObject(i).getInt("id") != id) {
                arrayBuilder.add(tasks.getJsonObject(i));
            } else taskFound = true;
        }

        if (!taskFound) {
            System.out.println("Task with the given ID not found.");
        }

        JsonArray updatedTasks = arrayBuilder.build();
        saveTasks(updatedTasks);
        System.out.printf("Task %d removed", id);
        System.out.println();
    }
}