package de.theccloud.thecontainercloud.communication.web.template;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import de.theccloud.thecontainercloud.communication.DatabaseInteractionHandler;
import de.theccloud.thecontainercloud.communication.web.template.impl.TemplateImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TemplateTable {

    private final DatabaseInteractionHandler databaseInteractionHandler;

    public TemplateTable(DatabaseInteractionHandler databaseInteractionHandler) {
        this.databaseInteractionHandler = databaseInteractionHandler;

        this.databaseInteractionHandler.getSession().execute(SchemaBuilder.createTable("cloud", "templates").ifNotExists()
                .addPartitionKey("uid", DataType.uuid())
                .addColumn("name", DataType.varchar())
                .addColumn("path", DataType.varchar())
        );
    }

    public List<TemplateImpl> getAllTemplates() {

        ResultSetFuture resultSetFuture = this.databaseInteractionHandler.getSession()
                .executeAsync(QueryBuilder.select("uid", "name", "path").from("cloud", "templates"));

        List<TemplateImpl> list = new ArrayList<>();

        for (Row row : resultSetFuture.getUninterruptibly().all()) {
            if (row == null)
                continue;
            list.add(this.fromRow(row));
        }


        return list;
    }

    private TemplateImpl fromRow(Row row) {
        String name = row.getString("name");
        String path = row.getString("path");
        UUID uid = row.getUUID("uid");

        return new TemplateImpl(path, name, uid);
    }

    public Optional<TemplateImpl> getTemplateByUid(UUID uid) {

        ResultSet resultSet = this.databaseInteractionHandler.getSession()
                .execute(QueryBuilder.select("uid", "name", "path").from("cloud", "templates")
                        .where(QueryBuilder.eq("uid", uid)));

        Row row = resultSet.one();

        if (row == null)
            return Optional.empty();

        return Optional.of(this.fromRow(row));
    }

    public boolean existTemplate(UUID uid) {

        ResultSetFuture resultSetFuture = this.databaseInteractionHandler.getSession()
                .executeAsync(QueryBuilder.select("uid").from("cloud", "templates")
                        .where(QueryBuilder.eq("uid", uid)));

        return resultSetFuture.getUninterruptibly().one() != null;
    }

    public void removeTemplateIfExist(UUID uid) {
        if (this.existTemplate(uid)) {
            this.databaseInteractionHandler.getSession().execute(
                    QueryBuilder.delete().from("cloud", "templates").where(QueryBuilder.eq("uid", uid))
            );
        }
    }

    public void saveTemplate(TemplateImpl template) {

        this.removeTemplateIfExist(template.getUid());

        this.databaseInteractionHandler.getSession().execute(
                QueryBuilder.insertInto("cloud", "templates")
                        .values(List.of(
                                        "uid",
                                        "path",
                                        "name"
                                ),
                                List.of(
                                        template.getUid(),
                                        template.getTemplatePath(),
                                        template.getTemplateName()
                                ))
        );

    }


}
