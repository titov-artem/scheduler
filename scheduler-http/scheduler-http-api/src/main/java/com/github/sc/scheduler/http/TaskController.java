package com.github.sc.scheduler.http;

import com.github.sc.scheduler.http.dto.TaskForm;
import com.github.sc.scheduler.http.dto.TaskView;

import javax.ws.rs.*;
import java.util.List;

/**
 * Controller for operating with tasks in scheduler
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
@Path("/task")
public interface TaskController extends JsonAPI {

    @GET
    List<TaskView> get();

    @POST
    TaskView create(TaskForm taskForm);

    @GET
    @Path("/{id}")
    TaskView get(@PathParam("id") String taskId);

    @DELETE
    @Path("/{id}")
    void delete(String taskId);
}
