package de.theccloud.thecontainercloud.communication.web.template;

import de.theccloud.thecontainercloud.communication.web.template.impl.TemplateImpl;
import io.javalin.http.Context;

import java.util.UUID;

public class TemplateHandler {

    private final TemplateTable templateTable;

    public TemplateHandler(TemplateTable templateTable) {
        this.templateTable = templateTable;
    }

    public void createTemplate(Context ctx) {
        CreateTemplateBody body = ctx.bodyAsClass(CreateTemplateBody.class);

        TemplateImpl template = new TemplateImpl(body.path(), body.name(), UUID.randomUUID());
        this.templateTable.saveTemplate(template);

        ctx.json(new TemplateResponseBody(template.getUid(), template.getTemplatePath(), template.getTemplateName()));

    }
}
