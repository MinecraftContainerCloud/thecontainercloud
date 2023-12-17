package de.theccloud.thecontainercloud.communication.web.services.impl;

import de.theccloud.thecontainercloud.api.service.Service;

import java.util.UUID;

public class ServiceImpl implements Service {

    private final UUID uid;
    private final UUID taskId;
    private int playerCount;

    public ServiceImpl(UUID uid, UUID taskId, int playerCount) {
        this.uid = uid;
        this.taskId = taskId;
        this.playerCount = playerCount;
    }

    @Override
    public UUID getUid() {
        return this.uid;
    }

    @Override
    public UUID getTask() {
        return this.taskId;
    }

    @Override
    public int getPlayerCount() {
        return this.playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
