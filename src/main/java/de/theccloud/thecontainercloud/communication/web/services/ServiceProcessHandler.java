package de.theccloud.thecontainercloud.communication.web.services;

import de.theccloud.thecontainercloud.api.service.ServiceResult;
import de.theccloud.thecontainercloud.communication.web.services.impl.ServiceImpl;
import de.theccloud.thecontainercloud.communication.web.tasks.TaskTable;
import de.theccloud.thecontainercloud.communication.web.tasks.impl.TaskImpl;
import de.theccloud.thecontainercloud.communication.web.template.TemplateTable;
import de.theccloud.thecontainercloud.communication.web.template.impl.TemplateImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class ServiceProcessHandler {

    private final Path rootPath = new File("C:\\Users\\shorty\\Desktop\\Cloud").toPath();
    private final TaskTable taskTable;
    private final TemplateTable templateTable;
    private final Map<UUID, List<RunningService>> runningService = new HashMap<>();

    public ServiceProcessHandler(TaskTable taskTable, TemplateTable templateTable) {
        this.taskTable = taskTable;
        this.templateTable = templateTable;
    }

    public ServiceResult removeService(ServiceImpl service) {

        if (!this.containsService(service.getUid()))
            return ServiceResult.ALREADY_REMOVED;

        Optional<RunningService> optionalRunningService = this.runningService.get(service.getTask()).stream()
                .filter(runningService1 -> runningService1.service().getUid() == service.getUid()).findFirst();

        if (optionalRunningService.isEmpty())
            return ServiceResult.ALREADY_REMOVED;

        RunningService runningService = optionalRunningService.get();

        runningService.process().destroy();

        this.runningService.get(service.getTask()).removeIf(runningService1 -> runningService1.service().getUid() == service.getUid());

        return ServiceResult.REMOVED;
    }

    public String getConsoleOutput(UUID uid) throws IOException {

        Optional<ServiceImpl> serviceImplFromUid = this.getServiceImplFromUid(uid);
        if (serviceImplFromUid.isEmpty())
            return null;

        Optional<RunningService> optionalRunningService = this.runningService.get(serviceImplFromUid.get().getTask()).stream()
                .filter(runningService1 -> runningService1.service().getUid() == uid).findFirst();

        if (optionalRunningService.isEmpty())
            return null;

        RunningService runningService = optionalRunningService.get();

        InputStream inputStream = runningService.process().getInputStream();


        byte b[] = new byte[inputStream.available()];
        inputStream.read(b, 0, b.length);

        return new String(b);
    }

    private boolean containsService(UUID uid) {
        return this.getServiceImplFromUid(uid).isPresent();
    }

    /**
     * starts a server from the ServiceImpl
     *
     * @param service service that should be started
     * @return ServiceResult that represents the case of the creation of the service
     */
    public ServiceResult createService(ServiceImpl service) throws IOException {


        File file = new File(rootPath.toFile(), service.getUid().toString());

        Optional<TaskImpl> optionalTask = this.taskTable.getTaskByUid(service.getTask());

        if (optionalTask.isEmpty()) {
            System.out.println("Task not found");
            return ServiceResult.ERROR;
        }

        TaskImpl task = optionalTask.get();

        Optional<TemplateImpl> optionalTemplate = this.templateTable.getTemplateByUid(task.getTemplate());

        if (optionalTemplate.isEmpty()) {
            System.out.println("Template not found");
            return ServiceResult.FAILED;
        }

        TemplateImpl template = optionalTemplate.get();

        //this.prepareDirectory(Path.of(template.getTemplatePath()), file.toPath());

        Process exec = Runtime.getRuntime().exec("java -DcloudServiceId=" + service.getUid().toString() + " -jar " + file.toPath().toString() + "/server.jar");

        //ProcessBuilder processBuilder = new ProcessBuilder("java -DcloudServiceId=" + service.getUid().toString() + " -jar server.jar");
        //processBuilder.directory(file);


        this.addRunningService(exec, task, service);

        return ServiceResult.STARTING;
    }

    protected Optional<ServiceImpl> getServiceImplFromUid(UUID uid) {
        Set<Map.Entry<UUID, List<RunningService>>> entries = this.runningService.entrySet();

        Optional<Stream<ServiceImpl>> first = entries.stream()
                .map(Map.Entry::getValue)
                .map(runningServices -> runningServices.stream().map(RunningService::service)
                        .filter(service -> service.getUid() == uid)).findFirst();

        if (first.isEmpty())
            return Optional.empty();

        Stream<ServiceImpl> serviceStream = first.get();
        return serviceStream.findFirst();
    }

    public Optional<Process> getProcessToAssociatedServiceUid(UUID uid) {

        Optional<ServiceImpl> serviceImplFromUid = this.getServiceImplFromUid(uid);

        if (serviceImplFromUid.isEmpty())
            return Optional.empty();

        ServiceImpl service = serviceImplFromUid.get();

        Optional<RunningService> optionalRunningService = this.runningService.get(service.getTask()).stream()
                .filter(runningService1 -> runningService1.service().getUid() == uid).findFirst();

        return optionalRunningService.map(RunningService::process);
    }

    private void addRunningService(Process process, TaskImpl task, ServiceImpl service) {

        if (this.runningService.containsKey(task.getUid())) {


            this.runningService.get(task.getUid()).add(new RunningService(process, service));

            return;
        }

        List<RunningService> list = new ArrayList<>();

        list.add(new RunningService(process, service));

        this.runningService.put(task.getUid(), list);

    }

    private void prepareDirectory(Path templateFolder, Path destinationFolder) {

        if (!destinationFolder.toFile().exists())
            destinationFolder.toFile().mkdirs();

        // TODO - won't work correctly

        try {
            Files.copy(templateFolder, destinationFolder, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
