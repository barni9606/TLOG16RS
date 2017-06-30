package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotExpectedTimeOrderException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Utility class for timelogger project
 * Created by bschenk on 6/27/17.
 */
public class Util {
    /**
     * The difference between startTime and endTime should be a multiple of 15 minutes
     * This method calculates and returns the endTime rounded
     * @param startTime start of a period of time
     * @param endTime end of a period of time
     * @return the end of the period rounded so the difference between this and the given startTime is a multiple of 15 minutes
     */
    public static LocalTime roundToMultipleQuarterHour(LocalTime startTime, LocalTime endTime){
        int min = (endTime.toSecondOfDay() - startTime.toSecondOfDay()) / 60;
        return endTime.minusMinutes(min).plusMinutes(Math.round(min / 15.0f) * 15);
    }

    /**
     * Tests if the difference of the given times is a multiple of 15 minutes
     * @param startTime start of a period of time
     * @param endTime end of a period of time
     * @return true if the period of time is a multiple of 15 minutes, false otherwise
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException
     */
    public static boolean isMultipleQuarterHour(LocalTime startTime, LocalTime endTime) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        if(startTime == null || endTime == null){
            throw new EmptyTimeFieldException();
        }
        if(startTime.isAfter(endTime)){
            throw new NotExpectedTimeOrderException();
        }
        int min = (endTime.toSecondOfDay() - startTime.toSecondOfDay()) / 60;
        return min % 15 == 0;
    }

    /**
     * Tests if the given day is a weekday or not
     * @param day
     * @return true if the day is a weekday, false if it is not
     */
    public static boolean isWeekDay(LocalDate day){
        DayOfWeek dayOfWeek = day.getDayOfWeek();
        return !(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
    }

    /**
     * Tests if task t's period of time is separated from all of the list's tasks' period
     * @param tasks the list (should be consisted of already separated tasks)
     * @param t the task to be tested
     * @return true if t is separated from the others
     */
    public static boolean isSeparatedTime(List<Task> tasks, Task t){
        if(tasks.isEmpty()){
            return true;
        }

        return tasks.stream().allMatch(task -> {
            try {
                return   (task.getStartTime().isAfter(t.getEndTime()) ^ task.getEndTime().isBefore(t.getStartTime())
                        ^ task.getStartTime().equals( t.getEndTime()) ^ task.getEndTime().equals(  t.getStartTime()))
                      && !task.getStartTime().equals( t.getStartTime())
                        ;
            } catch (EmptyTimeFieldException e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
