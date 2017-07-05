package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.core.exceptions.*;
import io.dropwizard.servlets.tasks.TaskServlet;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains the months
 * Created by bschenk on 6/27/17.
 */
public class TimeLogger {
    /**
     * The months
     */
    @lombok.Getter
    private List<WorkMonth> months = new ArrayList<>();

    /**
     * tests if the given month isn't already in the months list
     * @param wm
     * @return
     */
    public boolean isNewMonth(WorkMonth wm){
        return months.stream().noneMatch(month -> month.getDate().equals(wm.getDate()));
    }

    /**
     * Adds month
     * @param wm
     * @throws NotNewMonthException the month's date should be different from those' already in the list
     */
    public void addMonth(WorkMonth wm) throws NotNewMonthException {
        if(isNewMonth(wm)){
            months.add(wm);
        } else {
            throw new NotNewMonthException();
        }
    }

    public void addDay(WorkDay workDay) throws WeekendNotEnabledException, NotTheSameMonthException, NotNewDateException {
        if(months.contains(new WorkMonth(workDay.getActualDay().getYear(), workDay.getActualDay().getMonthValue()))){
            WorkMonth workMonth = months.stream().filter(month ->
                    month.equals(new WorkMonth(workDay.getActualDay().getYear(), workDay.getActualDay().getMonthValue()))).findFirst().get();
            workMonth.addWorkDay(workDay);
        } else {
            WorkMonth workMonth = new WorkMonth(workDay.getActualDay().getYear(), workDay.getActualDay().getMonthValue());
            workMonth.addWorkDay(workDay);
            months.add(workMonth);
        }
    }

    public void startTask(Task task, LocalDate localDate) throws EmptyTimeFieldException, FutureWorkException,
            NotExpectedTimeOrderException, NotSeparatedTimesException, WeekendNotEnabledException {
        if(months.contains(new WorkMonth(localDate.getYear(), localDate.getMonthValue()))) {
            WorkMonth workMonth = months.stream().filter(month ->
                    month.equals(new WorkMonth(localDate.getYear(), localDate.getMonthValue()))).findFirst().get();
            try {
                if(workMonth.getDays().contains(new WorkDay(localDate))){

                    WorkDay workDay = workMonth.getDays().stream().filter(day -> day.getActualDay().equals(localDate)).findFirst().get();
                    workDay.addTask(task);
                } else {
                    WorkDay workDay = new WorkDay(localDate);
                    workDay.addTask(task);
                    addDay(workDay);
                }
            } catch (NegativeMinutesOfWorkException e) {
                e.printStackTrace();
            } catch (NotTheSameMonthException e) {
                e.printStackTrace();
            } catch (NotNewDateException e) {
                e.printStackTrace();
            }

        } else {
            try {
                WorkDay workDay = new WorkDay(localDate);
                workDay.addTask(task);
                addDay(workDay);
            } catch (NegativeMinutesOfWorkException e) {
                e.printStackTrace();
            } catch (NotTheSameMonthException e) {
                e.printStackTrace();
            } catch (NotNewDateException e) {
                e.printStackTrace();
            }
        }
    }

    public WorkMonth getMonth(String yearString, String monthString) {
        int year = Integer.valueOf(yearString);
        int month = Integer.valueOf(monthString);
        WorkMonth m = new WorkMonth(year, month);
        WorkMonth workMonth = null;
        if(months.contains(m)){
            workMonth = months.stream().filter(wm -> wm.equals(m)).findFirst().get();
        } else {
            workMonth = m;
            try {
                addMonth(workMonth);
            } catch (NotNewMonthException e) {
                e.printStackTrace();
            }
        }
        return workMonth;

    }

    public WorkDay getDay(String yearString, String monthString, String dayString) throws NegativeMinutesOfWorkException,
            FutureWorkException {
        WorkMonth workMonth = getMonth(yearString, monthString);
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
                workMonth.addWorkDay(workDay);
            } catch (WeekendNotEnabledException e) {
                e.printStackTrace();
            } catch (NotTheSameMonthException e) {
                e.printStackTrace();
            } catch (NotNewDateException e) {
                e.printStackTrace();
            }
        }
        return workDay;
    }

    public Task finishTask(Task tempTask, FinishingTaskRB finishingTaskRB) throws NegativeMinutesOfWorkException,
            FutureWorkException, EmptyTimeFieldException, NotExpectedTimeOrderException, NoTaskIdException, InvalidTaskIdException, NotSeparatedTimesException {
        WorkDay workDay = getDay(String.valueOf(finishingTaskRB.getYear()), String.valueOf(finishingTaskRB.getMonth()), String.valueOf(finishingTaskRB.getDay()));
        Task task;
        if(workDay.getTasks().contains(tempTask)){
            task = workDay.getTasks().stream().filter(t -> t.equals(tempTask)).findFirst().get();
            task.setEndTime(tempTask.getEndTime());
        } else {
            task = tempTask;
            workDay.addTask(task);
        }
        return task;
    }

    public Task modifyTask(Task tempTask, ModifyTaskRB modifyTaskRB) throws NegativeMinutesOfWorkException,
            FutureWorkException, NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException {
        WorkDay workDay = getDay(String.valueOf(modifyTaskRB.getYear()), String.valueOf(modifyTaskRB.getMonth()), String.valueOf(modifyTaskRB.getDay()));
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
        return task;
    }

    public Task deleteTask(Task tempTask, DeleteTaskRB deleteTaskRB) throws NegativeMinutesOfWorkException, FutureWorkException {
        WorkDay workDay = getDay(String.valueOf(deleteTaskRB.getYear()), String.valueOf(deleteTaskRB.getMonth()), String.valueOf(deleteTaskRB.getDay()));
        Task task = null;
        if(workDay.getTasks().contains(tempTask)){
            task = workDay.getTasks().stream().filter(t -> t.equals(tempTask)).findFirst().get();
            workDay.getTasks().remove(task);
        }
        return task;
    }
}
