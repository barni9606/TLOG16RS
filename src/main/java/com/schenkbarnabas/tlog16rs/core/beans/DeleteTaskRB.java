package com.schenkbarnabas.tlog16rs.core.beans;

/**
 * Created by bschenk on 7/3/17.
 */
@lombok.NoArgsConstructor
@lombok.Setter
@lombok.Getter
public class DeleteTaskRB {
    private int year;
	private int month;
	private int day;
	private String taskId;
	private String startTime;
}
