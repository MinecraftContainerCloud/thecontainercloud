package de.theccloud.thecontainercloud.communication.web.services;

import de.theccloud.thecontainercloud.api.service.Service;
import de.theccloud.thecontainercloud.api.service.ServiceManager;
import de.theccloud.thecontainercloud.api.service.ServiceResult;
import de.theccloud.thecontainercloud.communication.web.services.impl.ServiceImpl;

public class ServiceManagerImpl implements ServiceManager {

    private final ServiceProcessHandler processHandler;

    public ServiceManagerImpl(ServiceProcessHandler processHandler) {
        this.processHandler = processHandler;
    }


    @Override
    public ServiceResult removeService() {
        return null;
    }

    @Override
    public ServiceResult createService(Service service) {
        return this.processHandler.createService(((ServiceImpl) service));
    }
}
