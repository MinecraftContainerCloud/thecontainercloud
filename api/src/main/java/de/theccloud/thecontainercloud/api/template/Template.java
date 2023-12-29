package de.theccloud.thecontainercloud.api.template;

import java.nio.file.Path;
import java.util.UUID;

public interface Template {

    UUID getUid();

    String getTemplateName();

    /**
     * @return path to root folder from template
     */
    String getTemplatePath();

}
