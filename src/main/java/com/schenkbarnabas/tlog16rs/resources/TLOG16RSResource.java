package com.schenkbarnabas.tlog16rs.resources;

import com.avaje.ebean.Ebean;
import com.schenkbarnabas.tlog16rs.core.beans.*;
import com.schenkbarnabas.tlog16rs.entities.Task;
import com.schenkbarnabas.tlog16rs.entities.TimeLogger;
import com.schenkbarnabas.tlog16rs.entities.WorkDay;
import com.schenkbarnabas.tlog16rs.entities.WorkMonth;
import com.schenkbarnabas.tlog16rs.core.exceptions.*;
import com.schenkbarnabas.tlog16rs.resources.services.TLOG16RSService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Path("/timelogger")
public class TLOG16RSResource {

    private TLOG16RSService service = new TLOG16RSService();

    @Path("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkMonth> getJSONWorkMonths(){
        TimeLogger timeLogger = service.getTimeLogger();
        return timeLogger.getMonths();
    }

    @Path("/workmonths")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkMonth addNewWorkMonth(WorkMonthRB month){
        TimeLogger timeLogger = service.getTimeLogger();
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        try {
            service.addMonth(timeLogger, workMonth);
        } catch (NotNewMonthException | EmptyTimeFieldException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        return workMonth;
    }

    @Path("/workmonths/workdays")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay addNewWorkDay(WorkDayRB day){
        TimeLogger timeLogger = service.getTimeLogger();
        WorkDay workDay = null;
        try {
            workDay = new WorkDay(Math.round(day.getRequiredHours() * 60), LocalDate.of(day.getYear(), day.getMonth(), day.getDay()));
            service.addDay(timeLogger, workDay);

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
        TimeLogger timeLogger = service.getTimeLogger();
        Task task = null;
        try {
            task = new Task(startTaskRB.getTaskId());
            task.setStartTime(startTaskRB.getStartTime());
            task.setComment(startTaskRB.getComment());
            LocalDate localDate = LocalDate.of(startTaskRB.getYear(), startTaskRB.getMonth(), startTaskRB.getDay());
            service.startTask(timeLogger, task, localDate);
        } catch (InvalidTaskIdException | NoTaskIdException | NotExpectedTimeOrderException
                | NotSeparatedTimesException | FutureWorkException
                | WeekendNotEnabledException | EmptyTimeFieldException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
            log.info("asd");
        }
        return task;
    }

    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkDay> getJSONDaysOfWorkMonth(@PathParam(value = "year") String year, @PathParam(value = "month") String month){
        TimeLogger timeLogger = service.getTimeLogger();
        return service.getMonth(timeLogger, year, month).getDays();
    }

    @Path("/workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getJSONTasksOfWorkDay(@PathParam(value = "year") String year, @PathParam(value = "month") String month,@PathParam(value = "day") String day){
        TimeLogger timeLogger = service.getTimeLogger();
        List<Task> tasks = null;
        try {
             tasks = service.getDay(timeLogger, year, month, day).getTasks();
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
        TimeLogger timeLogger = service.getTimeLogger();
        Task task = null;
        try {
            task = new Task(finishingTaskRB.getTaskId(), finishingTaskRB.getStartTime(), finishingTaskRB.getEndTime(), "");
            task = service.finishTask(timeLogger, task, finishingTaskRB);
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
        TimeLogger timeLogger = service.getTimeLogger();
        Task task = null;
        try {
            Task tempTask = new Task(modifyTaskRB.getTaskId());
            tempTask.setStartTime(modifyTaskRB.getStartTime());
            tempTask.setEndTime(modifyTaskRB.getNewEndTime());
            tempTask.setComment(modifyTaskRB.getNewComment());
            task = service.modifyTask(timeLogger, tempTask, modifyTaskRB);
        } catch (InvalidTaskIdException | FutureWorkException | NotSeparatedTimesException |
                NegativeMinutesOfWorkException | NotExpectedTimeOrderException | NoTaskIdException | EmptyTimeFieldException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        return task;
    }

    @Path("/workmonths/workdays/tasks/delete")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task deleteTask(DeleteTaskRB deleteTaskRB){
        TimeLogger timeLogger = service.getTimeLogger();
        Task task = null;
        try {
            task = new Task(deleteTaskRB.getTaskId());
            task.setStartTime(deleteTaskRB.getStartTime());
            task = service.deleteTask(timeLogger, task, deleteTaskRB);
        } catch (InvalidTaskIdException | NegativeMinutesOfWorkException | FutureWorkException |
                NotExpectedTimeOrderException | NoTaskIdException | EmptyTimeFieldException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        return task;
    }

    @Path("/workmonths/deleteall")
    @PUT
    public void deleteAll(){
        TimeLogger timeLogger = service.getTimeLogger();
        Ebean.deleteAll(Ebean.find(TimeLogger.class).findList());
        timeLogger = new TimeLogger();
    }

}
