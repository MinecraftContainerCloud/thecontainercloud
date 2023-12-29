package de.theccloud.thecontainercloud;

import de.theccloud.thecontainercloud.communication.web.WebCommunicationManager;
import de.theccloud.thecontainercloud.communication.DatabaseInteractionHandler;
import de.theccloud.thecontainercloud.communication.web.services.ServiceTable;
import de.theccloud.thecontainercloud.communication.web.tasks.TaskTable;
import de.theccloud.thecontainercloud.communication.web.template.TemplateTable;

public class Main {

    public static void main(String[] args) {

        DatabaseInteractionHandler databaseInteractionHandler = new DatabaseInteractionHandler();
        TaskTable taskTable = new TaskTable(databaseInteractionHandler);
        ServiceTable serviceTable = new ServiceTable(databaseInteractionHandler);
        TemplateTable templateTable = new TemplateTable(databaseInteractionHandler);

        new WebCommunicationManager(taskTable, serviceTable, templateTable);

    }

}
