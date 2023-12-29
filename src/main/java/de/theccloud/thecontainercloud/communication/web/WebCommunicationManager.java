package de.theccloud.thecontainercloud.communication.web;

import de.theccloud.thecontainercloud.communication.web.services.ServiceHandler;
import de.theccloud.thecontainercloud.communication.web.services.ServiceProcessHandler;
import de.theccloud.thecontainercloud.communication.web.tasks.TaskHandler;
import de.theccloud.thecontainercloud.communication.web.services.ServiceTable;
import de.theccloud.thecontainercloud.communication.web.tasks.TaskTable;
import de.theccloud.thecontainercloud.communication.web.template.TemplateHandler;
import de.theccloud.thecontainercloud.communication.web.template.TemplateTable;
import io.javalin.Javalin;

public class WebCommunicationManager {

    public WebCommunicationManager(TaskTable taskTable, ServiceTable serviceTable, TemplateTable templateTable) {

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

        ServiceProcessHandler processHandler = new ServiceProcessHandler(taskTable, templateTable);
        ServiceHandler serviceHandler = new ServiceHandler(serviceTable, processHandler);

        javalin.post("/services/create/", serviceHandler::createService);
        javalin.get("/services/", serviceHandler::getServices);
        javalin.get("/services/get/{id}", serviceHandler::getServiceById);

        javalin.put("/services/start/{id}", serviceHandler::startService);
        javalin.put("/services/stop/{id}", serviceHandler::stopService);

        javalin.delete("/services/delete/", serviceHandler::deleteService);
        javalin.ws("/services/{id}/console", serviceHandler::liveConsole);

        TemplateHandler templateHandler = new TemplateHandler(templateTable);
        javalin.post("/templates/create", templateHandler::createTemplate);

        javalin.start(8080);
    }
}
