package de.theccloud.thecontainercloud.communication.web;

import de.theccloud.thecontainercloud.communication.web.tasks.TaskHandler;
import de.theccloud.thecontainercloud.database.tasks.TaskTable;
import io.javalin.Javalin;

public class WebCommunicationManager {

    public WebCommunicationManager(TaskTable taskTable) {

        Javalin javalin = Javalin.create();

        javalin.get("/", context -> {
            context.json("OK");
        });

        TaskHandler taskHandler = new TaskHandler(taskTable);

        javalin.get("/tasks/", taskHandler::getTasks);
        javalin.get("/tasks/get/{id}", taskHandler::getTask);
        javalin.delete("/tasks/delete/{id}", taskHandler::deleteTask);
        javalin.post("/tasks/create/", taskHandler::createTask);

        javalin.start(8080);
    }
}
