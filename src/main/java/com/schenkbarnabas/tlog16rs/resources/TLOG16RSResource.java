package com.schenkbarnabas.tlog16rs.resources;

import com.schenkbarnabas.tlog16rs.core.beans.TimeLogger;
import com.schenkbarnabas.tlog16rs.core.beans.WorkMonth;
import com.schenkbarnabas.tlog16rs.core.beans.WorkMonthRB;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotNewMonthException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
    
}
