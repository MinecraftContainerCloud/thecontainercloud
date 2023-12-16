package de.theccloud.thecontainercloud.communication.web.services;

import de.theccloud.thecontainercloud.database.service.ServiceTable;
import de.theccloud.thecontainercloud.impl.service.ServiceImpl;
import io.javalin.http.Context;

import java.util.Optional;
import java.util.UUID;

public class ServiceHandler {

    public ServiceHandler(ServiceTable serviceTable) {
        this.serviceTable = serviceTable;
    }

    private final ServiceTable serviceTable;

    public void createService(Context ctx) {

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
