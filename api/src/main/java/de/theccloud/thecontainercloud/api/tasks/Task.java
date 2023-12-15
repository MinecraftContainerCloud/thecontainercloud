package de.theccloud.thecontainercloud.api.tasks;

import java.util.UUID;

public interface Task {

    UUID getUid();

    /**
     * @return max services per task
     */
    int getMaxServices();

    /**
     * @return min services per task
     */
    int getMinServices();

    int getRunningServiceCount();

    /**
     * @return a list containing all templates sorted by priority
     */
    UUID getTemplate();


}
