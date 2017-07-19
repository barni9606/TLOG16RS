package com.schenkbarnabas.tlog16rs.resources;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schenkbarnabas.tlog16rs.core.beans.*;
import com.schenkbarnabas.tlog16rs.core.exceptions.rest.RestException;
import com.schenkbarnabas.tlog16rs.entities.Task;
import com.schenkbarnabas.tlog16rs.entities.TimeLogger;
import com.schenkbarnabas.tlog16rs.entities.WorkDay;
import com.schenkbarnabas.tlog16rs.entities.WorkMonth;
import com.schenkbarnabas.tlog16rs.core.exceptions.*;
import com.schenkbarnabas.tlog16rs.resources.services.TLOG16RSService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Provider
@Path("/timelogger")
public class TLOG16RSResource implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext cres) throws IOException {
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");
        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");
    }

    private TLOG16RSService service = new TLOG16RSService();
    private ObjectMapper mapper = new ObjectMapper();

    @Path("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSONWorkMonths(){
        TimeLogger timeLogger = service.getTimeLogger();
        String data = null;
        try {
            data = mapper.writeValueAsString(timeLogger.getMonths());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewWorkMonth(WorkMonthRB month){
        TimeLogger timeLogger = service.getTimeLogger();
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        String data = null;
        try {
            service.addMonth(timeLogger, workMonth);
            data = mapper.writeValueAsString(workMonth);
        } catch (NotNewMonthException | EmptyTimeFieldException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage() + " HTTP status: " + e.getStatus() + " HTTP message: " + e.getHttpMessage());
            throw new RestException(e.getStatus(), e.getHttpMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths/workdays")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewWorkDay(WorkDayRB day){
        TimeLogger timeLogger = service.getTimeLogger();
        WorkDay workDay = null;
        String data = null;
        try {
            workDay = new WorkDay(Math.round(day.getRequiredHours() * 60), LocalDate.of(day.getYear(), day.getMonth(), day.getDay()));
            service.addDay(timeLogger, workDay, false);
            data = mapper.writeValueAsString(workDay);

        } catch (NegativeMinutesOfWorkException | NotNewDateException | NotTheSameMonthException | FutureWorkException | WeekendNotEnabledException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage() + " HTTP status: " + e.getStatus() + " HTTP message: " + e.getHttpMessage());
            throw new RestException(e.getStatus(), e.getHttpMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths/workdays/weekend")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewWorkDayWeekend(WorkDayRB day){
        TimeLogger timeLogger = service.getTimeLogger();
        WorkDay workDay = null;
        String data = null;
        try {
            workDay = new WorkDay(Math.round(day.getRequiredHours() * 60), LocalDate.of(day.getYear(), day.getMonth(), day.getDay()));
            service.addDay(timeLogger, workDay, true);
            data = mapper.writeValueAsString(workDay);

        } catch (NegativeMinutesOfWorkException | NotNewDateException | NotTheSameMonthException | FutureWorkException | WeekendNotEnabledException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage() + " HTTP status: " + e.getStatus() + " HTTP message: " + e.getHttpMessage());
            throw new RestException(e.getStatus(), e.getHttpMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths/workdays/tasks/start")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response startNewTask(StartTaskRB startTaskRB) throws EmptyTimeFieldException, NotExpectedTimeOrderException,
            NoTaskIdException, InvalidTaskIdException {
        TimeLogger timeLogger = service.getTimeLogger();
        Task task = null;
        String data = null;
        try {
            task = new Task(startTaskRB.getTaskId());
            task.setStartTime(startTaskRB.getStartTime());
            task.setComment(startTaskRB.getComment());
            LocalDate localDate = LocalDate.of(startTaskRB.getYear(), startTaskRB.getMonth(), startTaskRB.getDay());
            service.startTask(timeLogger, task, localDate);
            data = mapper.writeValueAsString(task);
        } catch (InvalidTaskIdException | NoTaskIdException | NotExpectedTimeOrderException
                | NotSeparatedTimesException | FutureWorkException
                | WeekendNotEnabledException | EmptyTimeFieldException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage() + " HTTP status: " + e.getStatus() + " HTTP message: " + e.getHttpMessage());
            throw new RestException(e.getStatus(), e.getHttpMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSONDaysOfWorkMonth(@PathParam(value = "year") String year, @PathParam(value = "month") String month){
        TimeLogger timeLogger = service.getTimeLogger();
        String data = null;
        try {
            data = mapper.writeValueAsString(service.getMonth(timeLogger, year, month).getDays());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSONTasksOfWorkDay(@PathParam(value = "year") String year, @PathParam(value = "month") String month,@PathParam(value = "day") String day){
        TimeLogger timeLogger = service.getTimeLogger();
        List<Task> tasks = null;
        String data = null;
        try {
            tasks = service.getDay(timeLogger, year, month, day).getTasks();
            data = mapper.writeValueAsString(tasks);
        } catch (NegativeMinutesOfWorkException | FutureWorkException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage() + " HTTP status: " + e.getStatus() + " HTTP message: " + e.getHttpMessage());
            throw new RestException(e.getStatus(), e.getHttpMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths/workdays/tasks/finish")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response finishTask(FinishingTaskRB finishingTaskRB){
        TimeLogger timeLogger = service.getTimeLogger();
        Task task = null;
        String data = null;
        try {
            task = new Task(finishingTaskRB.getTaskId(), finishingTaskRB.getStartTime(), finishingTaskRB.getEndTime(), "");
            task = service.finishTask(timeLogger, task, finishingTaskRB);
            data = mapper.writeValueAsString(task);
        } catch (NotExpectedTimeOrderException | FutureWorkException | NotSeparatedTimesException |
                NegativeMinutesOfWorkException | EmptyTimeFieldException | NoTaskIdException | InvalidTaskIdException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage() + " HTTP status: " + e.getStatus() + " HTTP message: " + e.getHttpMessage());
            throw new RestException(e.getStatus(), e.getHttpMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths/workdays/tasks/modify")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyTask(ModifyTaskRB modifyTaskRB){
        TimeLogger timeLogger = service.getTimeLogger();
        Task task = null;
        String data = null;
        try {
            Task tempTask = new Task(modifyTaskRB.getTaskId());
            tempTask.setStartTime(modifyTaskRB.getStartTime());
            tempTask.setEndTime(modifyTaskRB.getNewEndTime());
            tempTask.setComment(modifyTaskRB.getNewComment());
            task = service.modifyTask(timeLogger, tempTask, modifyTaskRB);
            data = mapper.writeValueAsString(task);
        } catch (InvalidTaskIdException | FutureWorkException | NotSeparatedTimesException |
                NegativeMinutesOfWorkException | NotExpectedTimeOrderException | NoTaskIdException | EmptyTimeFieldException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage() + " HTTP status: " + e.getStatus() + " HTTP message: " + e.getHttpMessage());
            throw new RestException(e.getStatus(), e.getHttpMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths/workdays/tasks/delete")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTask(DeleteTaskRB deleteTaskRB){
        TimeLogger timeLogger = service.getTimeLogger();
        Task task = null;
        String data = null;
        try {
            task = new Task(deleteTaskRB.getTaskId());
            task.setStartTime(deleteTaskRB.getStartTime());
            task = service.deleteTask(timeLogger, task, deleteTaskRB);
            data = mapper.writeValueAsString(task);
        } catch (InvalidTaskIdException | NegativeMinutesOfWorkException | FutureWorkException |
                NotExpectedTimeOrderException | NoTaskIdException | EmptyTimeFieldException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage() + " HTTP status: " + e.getStatus() + " HTTP message: " + e.getHttpMessage());
            throw new RestException(e.getStatus(), e.getHttpMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getClass().toString() + ": " + e.getMessage());
        }
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @Path("/workmonths/deleteall")
    @PUT
    public Response deleteAll(){
        Ebean.deleteAll(Ebean.find(TimeLogger.class).findList());
        return Response.noContent().build();
    }

}
