package de.theccloud.thecontainercloud.communication.web.tasks;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import de.theccloud.thecontainercloud.communication.DatabaseInteractionHandler;
import de.theccloud.thecontainercloud.communication.web.tasks.impl.TaskImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskTable {

    private final DatabaseInteractionHandler databaseInteractionHandler;

    public TaskTable(DatabaseInteractionHandler databaseInteractionHandler) {
        this.databaseInteractionHandler = databaseInteractionHandler;

        this.databaseInteractionHandler.getSession().execute(SchemaBuilder.createTable("cloud", "tasks").ifNotExists()
                .addPartitionKey("uid", DataType.uuid())
                .addColumn("running_services", DataType.cint())
                .addColumn("max_services", DataType.cint())
                .addColumn("min_services", DataType.cint())
                .addColumn("template", DataType.uuid())
        );

    }

    public List<TaskImpl> getAllTask() {

        ResultSetFuture resultSetFuture = this.databaseInteractionHandler.getSession()
                .executeAsync(QueryBuilder.select("uid", "running_services", "max_services", "min_services", "template").from("cloud", "tasks"));

        List<TaskImpl> list = new ArrayList<>();

        for (Row row : resultSetFuture.getUninterruptibly().all())
            list.add(this.fromRow(row));


        return list;
    }

    private TaskImpl fromRow(Row row) {
        int maxServices = row.getInt("max_services");
        int minServices = row.getInt("min_services");
        int runningServices = row.getInt("running_services");
        UUID template = row.getUUID("template");
        UUID uid = row.getUUID("uid");

        return new TaskImpl(maxServices, minServices, runningServices, template, uid);
    }

    public Optional<TaskImpl> getTaskByUid(UUID uid) {

        ResultSet resultSet = this.databaseInteractionHandler.getSession()
                .execute(QueryBuilder.select("uid", "running_services", "max_services", "min_services", "template").from("cloud", "tasks")
                        .where(QueryBuilder.eq("uid", uid)));

        Row row = resultSet.one();

        if (row == null)
            return Optional.empty();

        return Optional.of(this.fromRow(row));
    }

    public boolean existTask(UUID uid) {

        ResultSetFuture resultSetFuture = this.databaseInteractionHandler.getSession()
                .executeAsync(QueryBuilder.select("uid").from("cloud", "tasks")
                        .where(QueryBuilder.eq("uid", uid)));

        return resultSetFuture.getUninterruptibly().one() != null;
    }

    public void removeTaskIfExist(UUID uid) {
        if (this.existTask(uid)) {
            this.databaseInteractionHandler.getSession().execute(
                    QueryBuilder.delete().from("cloud", "tasks").where(QueryBuilder.eq("uid", uid))
            );
        }
    }

    public void saveTask(TaskImpl task) {

        this.removeTaskIfExist(task.getUid());

        this.databaseInteractionHandler.getSession().execute(
                QueryBuilder.insertInto("cloud", "tasks")
                        .values(List.of(
                                        "uid",
                                        "running_services",
                                        "max_services",
                                        "min_services",
                                        "template"
                                ),
                                List.of(
                                        task.getUid(),
                                        task.getRunningServiceCount(),
                                        task.getMaxServices(),
                                        task.getMinServices(),
                                        task.getTemplate()
                                ))
        );

    }

}
