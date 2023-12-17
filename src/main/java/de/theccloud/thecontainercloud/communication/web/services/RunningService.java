package de.theccloud.thecontainercloud.communication.web.services;

import de.theccloud.thecontainercloud.communication.web.services.impl.ServiceImpl;

public record RunningService(Process process, ServiceImpl service) {
}
