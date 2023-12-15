package de.theccloud.thecontainercloud.impl.task;

import de.theccloud.thecontainercloud.api.tasks.Task;

import java.util.UUID;

public class TaskImpl implements Task {

    private final int runningServices;
    private final UUID template;
    private int maxServices;
    private int minServices;

    private UUID uid;

    public TaskImpl(int maxServices, int minServices, int runningServices, UUID template, UUID uid) {
        this.maxServices = maxServices;
        this.minServices = minServices;
        this.runningServices = runningServices;
        this.uid = uid;
        this.template = template;
    }

    @Override
    public UUID getUid() {
        return uid;
    }

    @Override
    public int getMaxServices() {
        return this.maxServices;
    }

    public void setMaxServices(int maxServices) {
        this.maxServices = maxServices;
    }

    @Override
    public int getMinServices() {
        return this.minServices;
    }

    public void setMinServices(int minServices) {
        this.minServices = minServices;
    }

    @Override
    public int getRunningServiceCount() {
        return runningServices;
    }

    @Override
    public UUID getTemplate() {
        return template;
    }
}
