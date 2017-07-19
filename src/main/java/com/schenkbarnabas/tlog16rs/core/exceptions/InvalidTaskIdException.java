package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 6/28/17.
 */
public class InvalidTaskIdException extends Exception implements HttpStatusForException {
    @Override
    public int getStatus() {
        return HttpStatusForException.INVALID_TASK_ID_STATUS;
    }

    @Override
    public String getHttpMessage() {
        return HttpStatusForException.INVALID_TASK_ID_MESSAGE;
    }
}
