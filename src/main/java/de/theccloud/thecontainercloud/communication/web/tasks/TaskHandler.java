package de.theccloud.thecontainercloud.communication.web.tasks;

import com.google.gson.Gson;
import de.theccloud.thecontainercloud.database.tasks.TaskTable;
import de.theccloud.thecontainercloud.impl.task.TaskImpl;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;

import java.util.UUID;

public class TaskHandler {

    private final TaskTable taskTable;

    public TaskHandler(TaskTable taskTable) {
        this.taskTable = taskTable;
    }

    public void getTasks(Context ctx) {

        ctx.json(this.taskTable.getAllTask());

    }

    public void getTask(Context ctx) {

        UUID id = UUID.fromString(ctx.pathParam("id"));

        TaskImpl taskByUid = this.taskTable.getTaskByUid(id);

        ctx.json(taskByUid);

    }

    public void deleteTask(Context ctx) {

        UUID id = UUID.fromString(ctx.pathParam("id"));
        this.taskTable.removeTaskIfExist(id);

        ctx.json("deleted!");
    }

    public void createTask(Context ctx) {

        TaskCreateBody body = new JavalinGson(new Gson()).fromJsonStream(ctx.bodyInputStream(), TaskCreateBody.class);

        TaskImpl task = new TaskImpl(body.maxServices(), body.minServices(), 0, body.template(), UUID.randomUUID());

        this.taskTable.saveTask(task);

        ctx.json(task);

    }
}
