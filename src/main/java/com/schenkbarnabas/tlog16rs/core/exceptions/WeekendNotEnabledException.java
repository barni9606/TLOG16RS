package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 6/28/17.
 */
public class WeekendNotEnabledException extends Exception implements HttpStatusForException {
    @Override
    public int getStatus() {
        return HttpStatusForException.WEEKEND_NOT_ENABLED_STATUS;
    }

    @Override
    public String getHttpMessage() {
        return HttpStatusForException.WEEKEND_NOT_ENABLED_MESSAGE;
    }
}
