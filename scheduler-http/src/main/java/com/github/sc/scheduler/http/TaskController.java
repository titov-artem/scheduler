package com.github.sc.scheduler.http;

import com.github.sc.scheduler.http.dto.TaskForm;
import com.github.sc.scheduler.http.dto.TaskView;

import javax.ws.rs.*;
import java.util.List;

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
