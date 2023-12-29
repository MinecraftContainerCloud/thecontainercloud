package de.theccloud.thecontainercloud.communication.web.template.impl;

import de.theccloud.thecontainercloud.api.template.Template;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.UUID;

public class TemplateImpl implements Template {

    private final String path;
    private final String name;
    private final UUID uid;

    public TemplateImpl(String path, String name, UUID uid) {
        this.path = path;
        this.name = name;
        this.uid = uid;
    }

    @Override
    public UUID getUid() {
        return uid;
    }

    @Override
    public String getTemplateName() {
        return name;
    }


    @Override
    public String getTemplatePath() {
        return path;
    }
}
