package com.schenkbarnabas.tlog16rs.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.schenkbarnabas.tlog16rs.core.beans.*;
import com.schenkbarnabas.tlog16rs.core.exceptions.*;
import io.dropwizard.jackson.Jackson;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Path("/timelogger")
public class TLOG16RSResource {
    private TimeLogger timeLogger = new TimeLogger();
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "Hello world!";
    }

    @Path("/{name}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getNamedGreeting(@PathParam(value = "name") String name) {
        return "Hello " + name;
    }

    @Path("/query_param")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getNamedStringWithParam(@DefaultValue("world") @QueryParam("name") String name) {
        return "Hello " + name;
    }

    @Path("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkMonth> getJSONWorkMonths(){
        return timeLogger.getMonths();
    }

    @Path("/workmonths")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkMonth addNewWorkMonth(WorkMonthRB month){
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        try {
            timeLogger.addMonth(workMonth);
        } catch (NotNewMonthException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        return workMonth;
    }

    @Path("/workmonths/workdays")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay addNewWorkDay(WorkDayRB day){
        WorkDay workDay = null;
        try {
            workDay = new WorkDay(day.getRequiredHours(), LocalDate.of(day.getYear(), day.getMonth(), day.getDay()));
            timeLogger.addDay(workDay);

        } catch (NegativeMinutesOfWorkException | WeekendNotEnabledException | NotNewDateException
                | NotTheSameMonthException | FutureWorkException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        return workDay;
    }

    @Path("/workmonths/workdays/tasks/start")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task startNewTask(StartTaskRB startTaskRB) throws EmptyTimeFieldException, NotExpectedTimeOrderException,
            NoTaskIdException, InvalidTaskIdException {
        Task task = null;
        try {
            task = new Task(startTaskRB.getTaskId());
            task.setStartTime(startTaskRB.getStartTime());
            task.setComment(startTaskRB.getComment());
            LocalDate localDate = LocalDate.of(startTaskRB.getYear(), startTaskRB.getMonth(), startTaskRB.getDay());
            timeLogger.startTask(task, localDate);
        } catch (InvalidTaskIdException | NoTaskIdException | NotExpectedTimeOrderException
                | NotSeparatedTimesException | FutureWorkException
                | WeekendNotEnabledException | EmptyTimeFieldException e) {
            e.printStackTrace();
            log.error(e.getClass().toString() + ": " +  e.getMessage());
            log.info("asd");
        }
        return task;
    }

    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkDay> getJSONDaysOfWorkMonth(@PathParam(value = "year") String year, @PathParam(value = "month") String month){
        return timeLogger.getMonth(year, month).getDays();
    }

    @Path("/workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getJSONTasksOfWorkDay(@PathParam(value = "year") String year, @PathParam(value = "month") String month,@PathParam(value = "day") String day){
        List<Task> tasks = null;
        try {
             tasks = timeLogger.getDay(year, month, day).getTasks();
        } catch (NegativeMinutesOfWorkException | FutureWorkException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        return tasks;
    }

    @Path("/workmonths/workdays/tasks/finish")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task finishTask(FinishingTaskRB finishingTaskRB){
        Task task = null;
        try {
            task = new Task(finishingTaskRB.getTaskId(), finishingTaskRB.getStartTime(), finishingTaskRB.getEndTime(), "");
            timeLogger.finishTask(task, finishingTaskRB);
        } catch (NotExpectedTimeOrderException | FutureWorkException | NotSeparatedTimesException |
                NegativeMinutesOfWorkException | EmptyTimeFieldException | NoTaskIdException | InvalidTaskIdException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        return task;
    }

    @Path("/workmonths/workdays/tasks/modify")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task modifyTask(ModifyTaskRB modifyTaskRB){
        Task task = null;
        try {
            Task tempTask = new Task(modifyTaskRB.getTaskId());
            tempTask.setStartTime(modifyTaskRB.getStartTime());
            task = timeLogger.modifyTask(tempTask, modifyTaskRB);
        } catch (InvalidTaskIdException | FutureWorkException | NotSeparatedTimesException |
                NegativeMinutesOfWorkException | NotExpectedTimeOrderException | NoTaskIdException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        return task;
    }

    @Path("/workmonths/workdays/tasks/delete")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task deleteTask(DeleteTaskRB deleteTaskRB){
        Task task = null;
        try {
            task = new Task(deleteTaskRB.getTaskId());
            task.setStartTime(deleteTaskRB.getStartTime());
            timeLogger.deleteTask(task, deleteTaskRB);
        } catch (InvalidTaskIdException | NegativeMinutesOfWorkException | FutureWorkException |
                NotExpectedTimeOrderException | NoTaskIdException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        return task;
    }

}
