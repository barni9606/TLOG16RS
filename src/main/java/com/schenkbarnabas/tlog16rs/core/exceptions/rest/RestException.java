package com.schenkbarnabas.tlog16rs.core.exceptions.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by bschenk on 7/17/17.
 */
public class RestException extends WebApplicationException {
    public RestException(int status, String message) {
        super(Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
