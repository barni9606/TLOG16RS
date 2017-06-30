package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.core.exceptions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
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
                    month.getDate().equals(YearMonth.of(workDay.getActualDay().getYear(), workDay.getActualDay().getMonthValue()))).findFirst().get();
            workMonth.addWorkDay(workDay);
        } else {
            WorkMonth workMonth = new WorkMonth(workDay.getActualDay().getYear(), workDay.getActualDay().getMonthValue());
            workMonth.addWorkDay(workDay);
            months.add(workMonth);
        }
    }

    public void startTask(Task task, LocalDate localDate) throws EmptyTimeFieldException, FutureWorkException, NotExpectedTimeOrderException, NotSeparatedTimesException, WeekendNotEnabledException {
        if(months.contains(new WorkMonth(localDate.getYear(), localDate.getMonthValue()))) {
            WorkMonth workMonth = months.stream().filter(month ->
                    month.getDate().equals(YearMonth.of(localDate.getYear(), localDate.getMonthValue()))).findFirst().get();
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
}
