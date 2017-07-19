package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 6/28/17.
 */
public class NegativeMinutesOfWorkException extends Exception implements HttpStatusForException {
    @Override
    public int getStatus() {
        return HttpStatusForException.NEGATIVE_MINUTES_OF_WORK_STATUS;
    }

    @Override
    public String getHttpMessage() {
        return HttpStatusForException.NEGATIVE_MINUTES_OF_WORK_MESSAGE;
    }
}
