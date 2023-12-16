package de.theccloud.thecontainercloud.database.service;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import de.theccloud.thecontainercloud.database.DatabaseInteractionHandler;
import de.theccloud.thecontainercloud.impl.service.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServiceTable {

    private final DatabaseInteractionHandler databaseInteractionHandler;

    public ServiceTable(DatabaseInteractionHandler databaseInteractionHandler) {
        this.databaseInteractionHandler = databaseInteractionHandler;

        this.databaseInteractionHandler.getSession().executeAsync(SchemaBuilder.createTable("cloud", "services").ifNotExists()
                .addPartitionKey("uid", DataType.uuid())
                .addColumn("task_id", DataType.uuid())
                .addColumn("player_count", DataType.cint())
        );

    }

    public List<ServiceImpl> getServices() {

        ResultSetFuture resultSetFuture = this.databaseInteractionHandler.getSession()
                .executeAsync(QueryBuilder.select().from("cloud", "services"));

        List<ServiceImpl> list = new ArrayList<>();

        resultSetFuture.getUninterruptibly().all().forEach(row -> list.add(this.fromRow(row)));

        return list;
    }

    public Optional<ServiceImpl> getServiceById(UUID uid) {

        if (!this.existService(uid))
            return Optional.empty();

        Row one = this.databaseInteractionHandler.getSession()
                .execute(QueryBuilder.select("uid", "task_id", "player_count").from("cloud", "services")
                        .where(QueryBuilder.eq("uid", uid))).one();


        return Optional.of(this.fromRow(one));
    }

    private ServiceImpl fromRow(Row row) {

        UUID uid = row.getUUID("uid");
        UUID taskId = row.getUUID("task_id");
        int playerCount = row.getInt("player_count");

        return new ServiceImpl(uid, taskId, playerCount);
    }

    public boolean existService(UUID uid) {

        ResultSetFuture resultSetFuture = this.databaseInteractionHandler.getSession()
                .executeAsync(QueryBuilder.select("uid").from("cloud", "services")
                        .where(QueryBuilder.eq("uid", uid)));

        return resultSetFuture.getUninterruptibly().one() != null;
    }

    public void removeService(UUID uid) {
        this.databaseInteractionHandler.getSession().executeAsync(QueryBuilder.delete().from("cloud", "services")
                .where(QueryBuilder.eq("uid", uid)));
    }

    public void saveService(ServiceImpl service) {

        this.removeService(service.getUid());

        this.databaseInteractionHandler.getSession().executeAsync(QueryBuilder.insertInto("cloud", "services")
                .values(
                        List.of("uid", "task_id", "player_count"),
                        List.of(service.getUid(), service.getTask(), service.getPlayerCount())
                ));
    }


}
