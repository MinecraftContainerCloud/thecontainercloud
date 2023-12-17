package de.theccloud.thecontainercloud.communication.web.services;

import com.google.gson.Gson;
import de.theccloud.thecontainercloud.communication.web.services.impl.ServiceImpl;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;

import java.util.Optional;
import java.util.UUID;

public class ServiceHandler {

    private final ServiceTable serviceTable;

    public ServiceHandler(ServiceTable serviceTable) {
        this.serviceTable = serviceTable;
    }

    public void createService(Context ctx) {

        ServiceCreateBody body = new JavalinGson(new Gson()).fromJsonStream(ctx.bodyInputStream(), ServiceCreateBody.class);

        ServiceImpl service = new ServiceImpl(UUID.randomUUID(), body.taskId(), body.playerCount());
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
}
