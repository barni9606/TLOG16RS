package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 6/28/17.
 */
public class NotSeparatedTimesException extends Exception implements HttpStatusForException {
    @Override
    public int getStatus() {
        return HttpStatusForException.NOT_SEPARATED_TIMES_STATUS;
    }

    @Override
    public String getHttpMessage() {
        return HttpStatusForException.NOT_SEPARATED_TIMES_MESSAGE;
    }
}
