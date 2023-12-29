package de.theccloud.thecontainercloud.communication.web.template;

import java.util.UUID;

public record TemplateResponseBody(UUID uid, String path, String name) {
}
