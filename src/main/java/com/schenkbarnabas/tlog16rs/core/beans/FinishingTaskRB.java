package com.schenkbarnabas.tlog16rs.core.beans;

/**
 * Created by bschenk on 7/3/17.
 */
@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
public class FinishingTaskRB {
    private int year;
    private int month;
    private int day;
    private String taskId;
    private String startTime;
    private String endTime;
}
