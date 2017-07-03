package com.schenkbarnabas.tlog16rs.core.beans;

/**
 * Created by bschenk on 7/3/17.
 */
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
public class ModifyTaskRB {
    private int year;
    private int month;
    private int day;
    private String taskId;
    private String startTime;
    private String newTaskId;
    private String newComment;
    private String newStartTime;
    private String newEndTime;
}
