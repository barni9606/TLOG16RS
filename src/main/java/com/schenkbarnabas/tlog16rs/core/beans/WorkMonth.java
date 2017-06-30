package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotNewDateException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotTheSameMonthException;
import com.schenkbarnabas.tlog16rs.core.exceptions.WeekendNotEnabledException;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * This class consists of the days of a month
 * Created by bschenk on 6/27/17.
 */
public class WorkMonth {
    /**
     * Days
     */
    @lombok.Getter
    private List<WorkDay> days = new ArrayList<>();
    /**
     * The year and month
     */
    private YearMonth date;
    /**
     * The sum of the time of tasks' in this month
     */
    private long sumPerMonth;
    /**
     * The sum of the time required per days in this month
     */
    private long requiredMinPerMonth;

    /**
     * Constructor for year and month
     * @param year
     * @param month
     */
    public WorkMonth(int year, int month) {
        date = YearMonth.of(year, month);
    }

    /**
     *
     * @return all the extra min in the month
     * @throws EmptyTimeFieldException the tasks in the month cannot have time set as null
     */
    public long getExtraMinPerMonth() throws EmptyTimeFieldException {
        long result = 0;
        for (WorkDay workDay : days) {
            result += workDay.getExtraMinPerDay();
        }
        return result;
    }

    /**
     * The new day cannot already be in the day list
     * @param wd the new day
     * @return true if the day wasn't already in the list, false otherwise
     */
    public boolean isNewDate(WorkDay wd) {
        return days.stream().noneMatch(day -> day.getActualDay().equals(wd.getActualDay()));
    }

    /**
     * The day must be in the same month as the month
     * @param wd
     * @return
     */
    public boolean isSameMonth(WorkDay wd) {
        return date.getYear() == wd.getActualDay().getYear() && date.getMonth().equals(wd.getActualDay().getMonth());
    }

    /**
     *
     * @param wd the day
     * @param isWeekendEnabled the day can be on a weekend
     * @throws WeekendNotEnabledException trying to add a day in the weekend while weekend is not enabled
     * @throws NotNewDateException the day must not be already in the list
     * @throws NotTheSameMonthException the day must be in the same month
     */
    public void addWorkDay(WorkDay wd, boolean isWeekendEnabled) throws WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException {

        if (isWeekendEnabled || Util.isWeekDay(wd.getActualDay())) {
            if (!isSameMonth(wd)) {
                throw new NotTheSameMonthException();
            } else if (!isNewDate(wd)) {
                throw new NotNewDateException();
            } else {
                days.add(wd);
            }
        } else {
            throw new WeekendNotEnabledException();
        }

    }

    /**
     *
     * @param wd The day
     * @throws WeekendNotEnabledException trying to add a day in the weekend while weekend is not enabled
     * @throws NotNewDateException the day must not be already in the list
     * @throws NotTheSameMonthException the day must be in the same month
     */
    public void addWorkDay(WorkDay wd) throws WeekendNotEnabledException, NotTheSameMonthException, NotNewDateException {
        addWorkDay(wd, false);
    }

    /**
     *
     * @return the sum of all the tasks' time
     * @throws EmptyTimeFieldException none of the tasks can have any time set for null
     */
    public long getSumPerMonth() throws EmptyTimeFieldException {
        sumPerMonth = 0;
        for (WorkDay workDay : days) {
            sumPerMonth += workDay.getSumPerDay();
        }
        return sumPerMonth;
    }

    /**
     *
     * @return the sum of the required time of the tasks in the month in minutes
     */
    public long getRequiredMinPerMonth() {
        requiredMinPerMonth = 0;
        for (WorkDay workDay : days) {
            requiredMinPerMonth += workDay.getRequiredMinPerDay();
        }
        return requiredMinPerMonth;
    }

    protected YearMonth getDate() {
        return date;
    }
}
