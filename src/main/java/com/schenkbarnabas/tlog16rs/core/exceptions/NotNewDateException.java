package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 6/28/17.
 */
public class NotNewDateException extends Exception implements HttpStatusForException {
    @Override
    public int getStatus() {
        return HttpStatusForException.NOT_NEW_DATE_STATUS;
    }

    @Override
    public String getHttpMessage() {
        return HttpStatusForException.NOT_NEW_DATE_MESSAGE;
    }
}
