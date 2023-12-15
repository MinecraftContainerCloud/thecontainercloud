package de.theccloud.thecontainercloud.impl.template;

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
    public Path getTemplatePath() {
        try {
            return Path.of(new URI(path));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
