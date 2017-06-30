package com.schenkbarnabas.tlog16rs.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.schenkbarnabas.tlog16rs.core.beans.*;
import com.schenkbarnabas.tlog16rs.core.exceptions.*;
import io.dropwizard.jackson.Jackson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
            e.printStackTrace();
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

        } catch (NegativeMinutesOfWorkException | WeekendNotEnabledException | NotNewDateException | NotTheSameMonthException | FutureWorkException e) {
            e.printStackTrace();
        }
        return workDay;
    }

    @Path("/workmonths/workdays/tasks/start")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task startNewTask(StartTaskRB startTaskRB) throws EmptyTimeFieldException, NotExpectedTimeOrderException, NoTaskIdException, InvalidTaskIdException {
        Task task = null;
        try {
            task = new Task(startTaskRB.getTaskId());
            task.setStartTime(startTaskRB.getStartTime());
            task.setComment(startTaskRB.getComment());
            LocalDate localDate = LocalDate.of(startTaskRB.getYear(), startTaskRB.getMonth(), startTaskRB.getDay());
            timeLogger.startTask(task, localDate);
        } catch (InvalidTaskIdException | NoTaskIdException | NotExpectedTimeOrderException
                | NotSeparatedTimesException | FutureWorkException | WeekendNotEnabledException | EmptyTimeFieldException e) {
            e.printStackTrace();
        }
        return task;
    }

    
}
