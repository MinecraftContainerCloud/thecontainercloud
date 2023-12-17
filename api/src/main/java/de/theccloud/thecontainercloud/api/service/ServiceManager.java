package de.theccloud.thecontainercloud.api.service;

public interface ServiceManager {

    ServiceResult removeService();
    ServiceResult createService(Service service);

}
