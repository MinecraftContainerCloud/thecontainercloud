package de.theccloud.thecontainercloud.api.service;

public interface ServiceManager {

    ServiceResult startService();
    ServiceResult stopService();
    ServiceResult removeService();
    ServiceResult createService();

}
