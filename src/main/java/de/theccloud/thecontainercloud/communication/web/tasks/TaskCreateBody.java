package de.theccloud.thecontainercloud.communication.web.tasks;

import java.util.UUID;

public record TaskCreateBody(int minServices, int maxServices, UUID template) {
}
