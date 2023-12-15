package de.theccloud.thecontainercloud;

import de.theccloud.thecontainercloud.communication.web.WebCommunicationManager;
import de.theccloud.thecontainercloud.database.DatabaseInteractionHandler;
import de.theccloud.thecontainercloud.database.tasks.TaskTable;

public class Main {

    public static void main(String[] args) {

        DatabaseInteractionHandler databaseInteractionHandler = new DatabaseInteractionHandler();
        TaskTable taskTable = new TaskTable(databaseInteractionHandler);

        new WebCommunicationManager(taskTable);

    }

}
