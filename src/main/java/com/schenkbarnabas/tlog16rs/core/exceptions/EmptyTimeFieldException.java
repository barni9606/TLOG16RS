package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 6/28/17.
 */
public class EmptyTimeFieldException extends Exception implements HttpStatusForException {
    @Override
    public int getStatus() {
        return HttpStatusForException.EMPTY_TIME_FIELD_STATUS;
    }

    @Override
    public String getHttpMessage() {
        return HttpStatusForException.EMPTY_TIME_FIELD_MESSAGE;
    }
}
