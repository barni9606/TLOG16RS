package com.schenkbarnabas.tlog16rs.core.exceptions;

/**
 * Created by bschenk on 7/18/17.
 */
public interface HttpStatusForException {
    int getStatus();
    String getHttpMessage();


    int EMPTY_TIME_FIELD_STATUS = 451;
    int FUTURE_WORK_STATUS = 452;
    int INVALID_TASK_ID_STATUS = 453;
    int NEGATIVE_MINUTES_OF_WORK_STATUS = 454;
    int NO_TASK_ID_STATUS = 455;
    int NOT_EXPECTED_TIME_ORDER_STATUS = 456;
    int NOT_NEW_DATE_STATUS = 457;
    int NOT_NEW_MONTH_STATUS = 458;
    int NOT_SEPARATED_TIMES_STATUS = 459;
    int NOT_THE_SAME_MONTH_STATUS = 460;
    int WEEKEND_NOT_ENABLED_STATUS = 461;

    String EMPTY_TIME_FIELD_MESSAGE = "Empty time field";
    String FUTURE_WORK_MESSAGE = "Future work";
    String INVALID_TASK_ID_MESSAGE = "Invalid task id";
    String NEGATIVE_MINUTES_OF_WORK_MESSAGE = "Negative minutes of work";
    String NO_TASK_ID_MESSAGE = "No task id";
    String NOT_EXPECTED_TIME_ORDER_MESSAGE = "Not expected time order";
    String NOT_NEW_DATE_MESSAGE = "Not new date";
    String NOT_NEW_MONTH_MESSAGE = "Not new month";
    String NOT_SEPARATED_TIMES_MESSAGE = "Not separated times";
    String NOT_THE_SAME_MONTH_MESSAGE = "Not the same month";
    String WEEKEND_NOT_ENABLED_MESSAGE = "Weekend not enabled";
}
