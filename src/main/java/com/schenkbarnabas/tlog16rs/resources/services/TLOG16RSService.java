package com.schenkbarnabas.tlog16rs.resources.services;

import com.avaje.ebean.Ebean;
import com.schenkbarnabas.tlog16rs.core.beans.DeleteTaskRB;
import com.schenkbarnabas.tlog16rs.core.beans.FinishingTaskRB;
import com.schenkbarnabas.tlog16rs.core.beans.ModifyTaskRB;
import com.schenkbarnabas.tlog16rs.core.exceptions.*;
import com.schenkbarnabas.tlog16rs.entities.Task;
import com.schenkbarnabas.tlog16rs.entities.TimeLogger;
import com.schenkbarnabas.tlog16rs.entities.WorkDay;
import com.schenkbarnabas.tlog16rs.entities.WorkMonth;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by bschenk on 7/7/17.
 */
public class TLOG16RSService {

    public void addDay(TimeLogger timeLogger, WorkDay workDay) throws WeekendNotEnabledException, NotTheSameMonthException, NotNewDateException {
        List<WorkMonth> months = timeLogger.getMonths();
        WorkMonth workMonth;
        if(months.contains(new WorkMonth(workDay.getActualDay().getYear(), workDay.getActualDay().getMonthValue()))){
            workMonth = months.stream().filter(month ->
                    month.equals(new WorkMonth(workDay.getActualDay().getYear(), workDay.getActualDay().getMonthValue()))).findFirst().get();
            workMonth.addWorkDay(workDay);
        } else {
            workMonth = new WorkMonth(workDay.getActualDay().getYear(), workDay.getActualDay().getMonthValue());
            workMonth.addWorkDay(workDay);
            try {
                addMonth(timeLogger, workMonth);
            } catch (NotNewMonthException | EmptyTimeFieldException e) {
                e.printStackTrace();
            }
        }
        try {
            calculateStatistics(timeLogger, workDay, workMonth);
        } catch (EmptyTimeFieldException | NotExpectedTimeOrderException e) {
            e.printStackTrace();
        }
    }

    public void startTask(TimeLogger timeLogger, Task task, LocalDate localDate) throws EmptyTimeFieldException, FutureWorkException,
            NotExpectedTimeOrderException, NotSeparatedTimesException, WeekendNotEnabledException {
        List<WorkMonth> months = timeLogger.getMonths();
        if(months.contains(new WorkMonth(localDate.getYear(), localDate.getMonthValue()))) {
            WorkMonth workMonth = months.stream().filter(month ->
                    month.equals(new WorkMonth(localDate.getYear(), localDate.getMonthValue()))).findFirst().get();
            try {
                WorkDay workDay;
                if(workMonth.getDays().contains(new WorkDay(localDate))){

                    workDay = workMonth.getDays().stream().filter(day -> day.getActualDay().equals(localDate)).findFirst().get();
                    workDay.addTask(task);
                } else {
                    workDay = new WorkDay(localDate);
                    workDay.addTask(task);
                    addDay(timeLogger, workDay);
                }
                calculateStatistics(timeLogger, workDay, workMonth);
            } catch (NegativeMinutesOfWorkException | NotTheSameMonthException | NotNewDateException e) {
                e.printStackTrace();
            }

        } else {
            try {
                WorkDay workDay = new WorkDay(localDate);
                workDay.addTask(task);
                addDay(timeLogger, workDay);
                workDay.getExtraMinPerDay();
            } catch (NegativeMinutesOfWorkException | NotTheSameMonthException | NotNewDateException e) {
                e.printStackTrace();
            }
        }
        Ebean.save(timeLogger);
    }

    public WorkMonth getMonth(TimeLogger timeLogger, String yearString, String monthString) {
        List<WorkMonth> months = timeLogger.getMonths();
        int year = Integer.valueOf(yearString);
        int month = Integer.valueOf(monthString);
        WorkMonth m = new WorkMonth(year, month);
        WorkMonth workMonth = null;
        if(months.contains(m)){
            workMonth = months.stream().filter(wm -> wm.equals(m)).findFirst().get();
        } else {
            workMonth = m;
            try {
                addMonth(timeLogger, workMonth);
            } catch (NotNewMonthException | EmptyTimeFieldException e) {
                e.printStackTrace();
            }
        }
        return workMonth;

    }

    public WorkDay getDay(TimeLogger timeLogger, String yearString, String monthString, String dayString) throws NegativeMinutesOfWorkException,
            FutureWorkException {
        WorkMonth workMonth = getMonth(timeLogger, yearString, monthString);
        int year = Integer.valueOf(yearString);
        int month = Integer.valueOf(monthString);
        int day = Integer.valueOf(dayString);
        WorkDay d = new WorkDay(LocalDate.of(year, month, day));
        WorkDay workDay;
        if(workMonth.getDays().contains(d)){
            workDay = workMonth.getDays().stream().filter(wd -> wd.equals(d)).findFirst().get();
        } else {
            workDay = d;
            try {
                addDay(timeLogger, workDay);
            } catch (WeekendNotEnabledException | NotTheSameMonthException | NotNewDateException e) {
                e.printStackTrace();
            }
            updateTimeLogger(timeLogger, workMonth);
        }

        return workDay;
    }

    public Task finishTask(TimeLogger timeLogger, Task tempTask, FinishingTaskRB finishingTaskRB) throws NegativeMinutesOfWorkException,
            FutureWorkException, EmptyTimeFieldException, NotExpectedTimeOrderException, NoTaskIdException, InvalidTaskIdException, NotSeparatedTimesException {
        WorkMonth workMonth = getMonth(timeLogger, String.valueOf(finishingTaskRB.getYear()), String.valueOf(finishingTaskRB.getMonth()));
        WorkDay workDay = getDay(timeLogger, String.valueOf(finishingTaskRB.getYear()), String.valueOf(finishingTaskRB.getMonth()), String.valueOf(finishingTaskRB.getDay()));
        Task task;
        if(workDay.getTasks().contains(tempTask)){
            task = workDay.getTasks().stream().filter(t -> t.equals(tempTask)).findFirst().get();
            task.setEndTime(tempTask.getEndTime());
        } else {
            task = tempTask;
            workDay.addTask(task);
        }
        calculateStatistics(timeLogger, workDay, workMonth);
        Ebean.save(timeLogger);
        return task;
    }

    public Task modifyTask(TimeLogger timeLogger, Task tempTask, ModifyTaskRB modifyTaskRB) throws NegativeMinutesOfWorkException,
            FutureWorkException, NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, EmptyTimeFieldException {
        WorkMonth workMonth = getMonth(timeLogger, String.valueOf(modifyTaskRB.getYear()), String.valueOf(modifyTaskRB.getMonth()));
        WorkDay workDay = getDay(timeLogger, String.valueOf(modifyTaskRB.getYear()), String.valueOf(modifyTaskRB.getMonth()), String.valueOf(modifyTaskRB.getDay()));
        Task task;
        if(workDay.getTasks().contains(tempTask)){
            task = workDay.getTasks().stream().filter(t -> t.equals(tempTask)).findFirst().get();
            task.setTaskId(modifyTaskRB.getNewTaskId());
            try {
                if(LocalTime.parse(modifyTaskRB.getNewStartTime(), DateTimeFormatter.ofPattern("H:m")).isAfter(task.getEndTime())){
                    task.setEndTime(modifyTaskRB.getNewEndTime());
                    task.setStartTime(modifyTaskRB.getNewStartTime());
                } else {
                    task.setStartTime(modifyTaskRB.getNewStartTime());
                    task.setEndTime(modifyTaskRB.getNewEndTime());

                }
            } catch (EmptyTimeFieldException e) {
                e.printStackTrace();
            }
            task.setComment(modifyTaskRB.getNewComment());
        } else {
            task = new Task(modifyTaskRB.getNewTaskId());
            task.setStartTime(modifyTaskRB.getNewStartTime());
            task.setEndTime(modifyTaskRB.getNewEndTime());
            task.setComment(modifyTaskRB.getNewComment());
            workDay.addTask(task);
        }
        calculateStatistics(timeLogger, workDay, workMonth);
        Ebean.save(timeLogger);
        return task;
    }

    public Task deleteTask(TimeLogger timeLogger, Task tempTask, DeleteTaskRB deleteTaskRB) throws NegativeMinutesOfWorkException, FutureWorkException, NotExpectedTimeOrderException, EmptyTimeFieldException {
        WorkMonth workMonth = getMonth(timeLogger, String.valueOf(deleteTaskRB.getYear()), String.valueOf(deleteTaskRB.getMonth()));
        WorkDay workDay = getDay(timeLogger, String.valueOf(deleteTaskRB.getYear()), String.valueOf(deleteTaskRB.getMonth()), String.valueOf(deleteTaskRB.getDay()));
        Task task = null;
        if(workDay.getTasks().contains(tempTask)){
            task = workDay.getTasks().stream().filter(t -> t.equals(tempTask)).findFirst().get();
            workDay.getTasks().remove(task);
            Ebean.delete(task);
        }
        calculateStatistics(timeLogger, workDay, workMonth);
        Ebean.save(timeLogger);
        return task;
    }

    private void updateTimeLogger(TimeLogger timeLogger, WorkMonth workMonth){
        Ebean.save(workMonth);
        Ebean.update(timeLogger);
    }

    private void calculateStatistics(TimeLogger timeLogger, WorkDay workDay, WorkMonth workMonth) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        workMonth.getRequiredMinPerMonth();
        workDay.getExtraMinPerDay();
        workMonth.getExtraMinPerMonth();
        updateTimeLogger(timeLogger, workMonth);
    }

    public TimeLogger getTimeLogger(){
        TimeLogger timeLogger;
        if (Ebean.find(TimeLogger.class).findList().isEmpty()) {
            timeLogger = new TimeLogger();
        } else {
            timeLogger = Ebean.find(TimeLogger.class).findUnique();
        }
        return timeLogger;
    }

    public void addMonth(TimeLogger timeLogger, WorkMonth workMonth) throws NotNewMonthException, EmptyTimeFieldException {
        timeLogger.addMonth(workMonth);
        workMonth.getRequiredMinPerMonth();
        workMonth.getExtraMinPerMonth();
        Ebean.save(timeLogger);
    }
}
