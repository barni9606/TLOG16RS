package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 6/28/17.
 */
public class NotExpectedTimeOrderException extends Exception implements HttpStatusForException{
    @Override
    public int getStatus() {
        return HttpStatusForException.NOT_EXPECTED_TIME_ORDER_STATUS;
    }

    @Override
    public String getHttpMessage() {
        return HttpStatusForException.NOT_EXPECTED_TIME_ORDER_MESSAGE;
    }
}
