package com.schenkbarnabas.tlog16rs.core.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by bschenk on 7/17/17.
 */
public class WeekendNotEnabledRestException extends WebApplicationException {
    public WeekendNotEnabledRestException(String message) {
        super(Response.status(403)
                .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
