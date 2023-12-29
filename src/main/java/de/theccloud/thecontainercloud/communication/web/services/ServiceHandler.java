package de.theccloud.thecontainercloud.communication.web.services;

import com.google.gson.Gson;
import de.theccloud.thecontainercloud.communication.web.services.impl.ServiceImpl;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import io.javalin.websocket.WsConfig;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ServiceHandler {

    private final ServiceTable serviceTable;
    private final ServiceProcessHandler processHandler;
    private final Map<UUID, Session> consoleMonitoring = new HashMap<>();

    public ServiceHandler(ServiceTable serviceTable, ServiceProcessHandler processHandler) {
        this.serviceTable = serviceTable;
        this.processHandler = processHandler;
    }

    public void createService(Context ctx) {

        ServiceCreateBody body = new JavalinGson(new Gson()).fromJsonStream(ctx.bodyInputStream(), ServiceCreateBody.class);

        ServiceImpl service = new ServiceImpl(UUID.randomUUID(), body.taskId(), 0);
        this.serviceTable.saveService(service);

        ctx.json(service);
    }

    public void getServices(Context ctx) {

        ctx.json(this.serviceTable.getServices());

    }

    public void getServiceById(Context ctx) {

        UUID id = UUID.fromString(ctx.pathParam("id"));
        Optional<ServiceImpl> serviceById = this.serviceTable.getServiceById(id);

        if (serviceById.isEmpty()) {
            ctx.json("");
            return;
        }

        ctx.json(serviceById.get());
    }

    public void deleteService(Context ctx) {
        UUID id = UUID.fromString(ctx.pathParam("id"));
        this.serviceTable.removeService(id);
        ctx.json("deleted");
    }


    public void sendLiveConsoleData(UUID fromService) {

        try {
            this.consoleMonitoring.get(fromService).getRemote().sendString(this.processHandler.getConsoleOutput(fromService));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void liveConsole(WsConfig wsConfig) {

        wsConfig.onConnect(ctx -> {

            UUID id = UUID.fromString(ctx.pathParam("id"));
            this.consoleMonitoring.put(id, ctx.session);

            this.sendLiveConsoleData(id);

        });

        wsConfig.onClose(ctx -> {

            UUID id = UUID.fromString(ctx.pathParam("id"));
            this.consoleMonitoring.remove(id);

        });

    }

    public void startService(Context ctx) {
        UUID id = UUID.fromString(ctx.pathParam("id"));

        Optional<ServiceImpl> serviceById = this.serviceTable.getServiceById(id);

        if (serviceById.isEmpty()) {
            try {
                ctx.res().sendError(404, "ServiceId not found");
            } catch (IOException e) {
                e.fillInStackTrace();
            }
            return;
        }

        try {
            ctx.json(this.processHandler.createService(serviceById.get()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopService(Context ctx) {
        UUID id = UUID.fromString(ctx.pathParam("id"));

        Optional<ServiceImpl> serviceById = this.serviceTable.getServiceById(id);

        if (serviceById.isEmpty()) {
            try {
                ctx.res().sendError(404, "ServiceId not found");
            } catch (IOException e) {
                e.fillInStackTrace();
            }
            return;
        }

        ctx.json(this.processHandler.removeService(serviceById.get()));

    }
}
