package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 6/29/17.
 */
public class NotNewMonthException extends Exception implements HttpStatusForException {
    @Override
    public int getStatus() {
        return HttpStatusForException.NOT_NEW_MONTH_STATUS;
    }

    @Override
    public String getHttpMessage() {
        return HttpStatusForException.NOT_NEW_MONTH_MESSAGE;
    }
}
