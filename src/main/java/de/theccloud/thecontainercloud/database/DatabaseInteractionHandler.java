package de.theccloud.thecontainercloud.database;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;

import java.util.concurrent.ExecutionException;

public class DatabaseInteractionHandler {

    private final Cluster cluster;

    private Session session;

    public DatabaseInteractionHandler() {
        PoolingOptions poolingOptions = new PoolingOptions();

        poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 4);
        poolingOptions.setCoreConnectionsPerHost(HostDistance.REMOTE, 4);
        poolingOptions.setMaxConnectionsPerHost(HostDistance.LOCAL, 4);
        poolingOptions.setMaxConnectionsPerHost(HostDistance.REMOTE, 4);

        poolingOptions.setHeartbeatIntervalSeconds(20);

        poolingOptions.setMaxRequestsPerConnection(HostDistance.LOCAL, 1000);
        poolingOptions.setMaxRequestsPerConnection(HostDistance.REMOTE, 1000);

        this.cluster = Cluster.builder()
                .addContactPoint("127.0.0.1")
                .withPoolingOptions(poolingOptions)
                .build();

        this.connect();
        this.createKeySpaceIfRequired();
    }

    private void connect() {

        try {
            this.session = this.cluster.connectAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    private void createKeySpaceIfRequired() {
        // TODO - keyspace name changeable
        this.getSession().executeAsync("CREATE KEYSPACE [IF NOT EXISTS] cloud WITH REPLICATION= {'class': 'SimpleStrategy', 'replication_factor': 1};");
    }

    public Session getSession() {

        while (this.session == null) {
            this.connect();
        }

        return session;
    }
}
