package de.theccloud.thecontainercloud.communication.web;

import de.theccloud.thecontainercloud.communication.web.services.ServiceHandler;
import de.theccloud.thecontainercloud.communication.web.tasks.TaskHandler;
import de.theccloud.thecontainercloud.database.service.ServiceTable;
import de.theccloud.thecontainercloud.database.tasks.TaskTable;
import io.javalin.Javalin;

public class WebCommunicationManager {

    public WebCommunicationManager(TaskTable taskTable, ServiceTable serviceTable) {

        Javalin javalin = Javalin.create();

        javalin.get("/", context -> {
            context.json("OK");
        });

        TaskHandler taskHandler = new TaskHandler(taskTable);

        javalin.get("/tasks/", taskHandler::getTasks);
        javalin.get("/tasks/get/{id}", taskHandler::getTask);
        javalin.delete("/tasks/delete/{id}", taskHandler::deleteTask);
        javalin.post("/tasks/create/", taskHandler::createTask);

        javalin.ws("/tasks/updates/", taskHandler::updates);

        ServiceHandler serviceHandler = new ServiceHandler(serviceTable);

        javalin.post("/services/create/", serviceHandler::createService);
        javalin.get("/services/", serviceHandler::getServices);
        javalin.get("/services/get/{0}", serviceHandler::getServiceById);
        javalin.delete("/services/create/", serviceHandler::deleteService);

        javalin.start(8080);
    }
}
