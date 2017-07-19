package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 6/28/17.
 */
public class FutureWorkException extends Exception implements HttpStatusForException {
    @Override
    public int getStatus() {
        return HttpStatusForException.FUTURE_WORK_STATUS;
    }

    @Override
    public String getHttpMessage() {
        return HttpStatusForException.FUTURE_WORK_MESSAGE;
    }
}
