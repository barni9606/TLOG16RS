package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.core.exceptions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by bschenk on 6/27/17.
 */
public class WorkDay {
    /**
     * Tasks of the day
     */
    @lombok.Getter
    private List<Task> tasks = new ArrayList<>();
    /**
     * The minimum required work time for the day in minutes
     */
    @lombok.Getter
    private long requiredMinPerDay;
    /**
     * The date this object represents
     */
    @lombok.Getter
    private LocalDate actualDay;
    /**
     * The sum of time spent on tasks on the actual day
     */
    private long sumPerDay;

    /**
     * Constructor for required minutes and actual day
     * @param requiredMinPerDay required time of the day
     * @param actualDay the day
     * @throws NegativeMinutesOfWorkException requiredMinPerDay must not be negative
     * @throws FutureWorkException the day can only be today or in the past
     */
    public WorkDay(long requiredMinPerDay, LocalDate actualDay) throws NegativeMinutesOfWorkException, FutureWorkException {
        if(requiredMinPerDay < 0){
            throw new NegativeMinutesOfWorkException();
        }
        if(actualDay.isAfter(LocalDate.now())){
            throw new FutureWorkException();
        }
        this.requiredMinPerDay = requiredMinPerDay;
        this.actualDay = actualDay;
    }

    /**
     * Constructor with default values: 450, today
     * @throws NegativeMinutesOfWorkException requiredMinPerDay must not be negative
     * @throws FutureWorkException the day can only be today or in the past
     */
    public WorkDay() throws NegativeMinutesOfWorkException, FutureWorkException {
        this(450, LocalDate.now());
    }

    /**
     * Constructor for actual day, required mins: 450
     * @param actualDay the day
     * @throws NegativeMinutesOfWorkException requiredMinPerDay must not be negative
     * @throws FutureWorkException the day can only be today or in the past
     */
    public WorkDay(LocalDate actualDay) throws NegativeMinutesOfWorkException, FutureWorkException {
        this(450, actualDay);
    }

    /**
     * Constructor for required mins, actual day: today
     * @param requiredMinPerDay required time for the day
     * @throws NegativeMinutesOfWorkException requiredMinPerDay must not be negative
     * @throws FutureWorkException the day can only be today or in the past
     */
    public WorkDay(long requiredMinPerDay) throws NegativeMinutesOfWorkException, FutureWorkException {
        this(requiredMinPerDay, LocalDate.now());
    }

    /**
     *
     * @return how much more time was spent on this day than the required in minutes
     * @throws EmptyTimeFieldException the tasks' time fields must not be null
     */
    public long getExtraMinPerDay() throws EmptyTimeFieldException {
        return getSumPerDay() - requiredMinPerDay;
    }

    /**
     * Adds task to the list
     * @param t task to be added
     * @throws NotSeparatedTimesException the task's period of time must be separated from those' already in the list
     * @throws NotExpectedTimeOrderException the task's start and finish time should follow each other in this order
     */
    public void addTask(Task t) throws NotSeparatedTimesException, NotExpectedTimeOrderException {
        if(!Util.isSeparatedTime(tasks, t)){
            throw new NotSeparatedTimesException();
        }

        try {
            if (Util.isMultipleQuarterHour(t.getStartTime(), t.getEndTime())) {
                tasks.add(t);
            }
        } catch (EmptyTimeFieldException e){
            tasks.add(t);
        }

    }

    /**
     * The sum of the time spent on the tasks
     * @return the sum of time spent on the tasks
     * @throws EmptyTimeFieldException The tasks' start and end time cannot be null for this to work
     */
    public long getSumPerDay() throws EmptyTimeFieldException {
        sumPerDay = 0;
        for(Task task : tasks){
            try {
                sumPerDay += task.getMinPerTask();
            } catch (EmptyTimeFieldException e) {
                throw e;
            }
        }
        return sumPerDay;
    }

    /**
     * Sets the required time for day in minutes
     * @param requiredMinPerDay
     * @throws NegativeMinutesOfWorkException the required time cannot be negative
     */
    public void setRequiredMinPerDay(long requiredMinPerDay) throws NegativeMinutesOfWorkException {
        if(requiredMinPerDay < 0){
            throw new NegativeMinutesOfWorkException();
        }
        this.requiredMinPerDay = requiredMinPerDay;
    }

    /**
     * Sets the actual day
     * @param year
     * @param month
     * @param day
     * @throws FutureWorkException the actual day can only be in the past or today
     */
    public void setActualDay(int year, int month, int day) throws FutureWorkException {
        LocalDate tempActualDay = LocalDate.of(year, month, day);
        if(tempActualDay.isAfter(LocalDate.now())){
            throw new FutureWorkException();
        }
        this.actualDay = tempActualDay;
    }

    /**
     *
     * @return the last task's end time
     * @throws EmptyTimeFieldException the last task's end time shouldn't be null
     */
    public LocalTime endTimeOfTheLastTask() throws EmptyTimeFieldException {
        if(tasks.size() == 0){
            return null;
        }
        return tasks.get(tasks.size() - 1).getEndTime();
    }
}
