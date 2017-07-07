package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.entities.Task;
import com.schenkbarnabas.tlog16rs.entities.WorkDay;
import com.schenkbarnabas.tlog16rs.entities.WorkMonth;
import com.schenkbarnabas.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotNewDateException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotTheSameMonthException;
import com.schenkbarnabas.tlog16rs.core.exceptions.WeekendNotEnabledException;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by bschenk on 6/28/17.
 */
public class WorkMonthTest {
    @Test
    public void test01() throws Exception {
        Task task1 = new Task("1234", "7:30", "8:45", "");
        WorkDay workDay1 = new WorkDay(420, LocalDate.of(2016,9,1));
        workDay1.addTask(task1);
        Task task2 = new Task("1234", "8:45", "9:45", "");
        WorkDay workDay2 = new WorkDay(420, LocalDate.of(2016,9,2));
        workDay2.addTask(task2);

        WorkMonth workMonth = new WorkMonth(2016, 9);
        workMonth.addWorkDay(workDay1 );
        workMonth.addWorkDay(workDay2 );
        Assert.assertEquals(135, workMonth.getSumPerMonth());
    }

    @Test
    public void test02() throws Exception {
        Assert.assertEquals(0, (new WorkMonth(2016, 8).getSumPerMonth()));
    }

    @Test
    public void test03() throws Exception {
        Task task1 = new Task("1234", "7:30", "8:45", "");
        WorkDay workDay1 = new WorkDay(420, LocalDate.of(2016, 9, 1));
        workDay1.addTask(task1);
        Task task2 = new Task("1234", "8:45", "9:45", "");
        WorkDay workDay2 = new WorkDay(420, LocalDate.of(2016, 9, 2));
        workDay2.addTask(task2);
        WorkMonth workMonth = new WorkMonth(2016, 9);
        workMonth.addWorkDay(workDay1);
        workMonth.addWorkDay(workDay2);
        Assert.assertEquals(-705, workMonth.getExtraMinPerMonth());

    }

    @Test
    public void test04() throws Exception {
        Assert.assertEquals(0, (new WorkMonth(2016, 1).getExtraMinPerMonth()));
    }

    @Test
    public void test05() throws Exception {
        WorkMonth workMonth = new WorkMonth(2016, 9);
        workMonth.addWorkDay(new WorkDay(420, LocalDate.of(2016,9,1)));
        workMonth.addWorkDay(new WorkDay(420, LocalDate.of(2016,9,2)));
        Assert.assertEquals(840, workMonth.getRequiredMinPerMonth());
    }

    @Test
    public void test06() throws Exception {
        Assert.assertEquals(0, (new WorkMonth(2016, 7)).getRequiredMinPerMonth());
    }

    @Test
    public void test07() throws Exception {
        WorkDay workDay = new WorkDay(LocalDate.of(2016, 9, 9));
        WorkMonth workMonth = new WorkMonth(2016, 9);
        workDay.addTask(new Task("1235", "7:30", "8:45", ""));
        workMonth.addWorkDay(workDay);
        Assert.assertEquals(workDay.getSumPerDay(), workMonth.getSumPerMonth());
    }

    @Test
    public void test08() throws Exception {
        WorkDay workDay = new WorkDay(LocalDate.of(2016, 8, 28));
        WorkMonth workMonth = new WorkMonth(2016, 8);
        workMonth.addWorkDay(workDay, true);
        Assert.assertEquals(workDay.getSumPerDay(), workMonth.getSumPerMonth());
    }

    @Test(expected = WeekendNotEnabledException.class)
    public void test09() throws Exception {
        WorkDay workDay = new WorkDay(LocalDate.of(2016, 8, 28));
        WorkMonth workMonth = new WorkMonth(2016, 8);
        workMonth.addWorkDay(workDay);
    }

    @Test(expected = NotNewDateException.class)
    public void test10() throws Exception {
        WorkDay workDay1 = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkDay workDay2 = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkMonth workMonth = new WorkMonth(2016, 9);
        workMonth.addWorkDay(workDay1);
        workMonth.addWorkDay(workDay2);
    }

    @Test(expected = NotTheSameMonthException.class)
    public void test11() throws Exception {
        WorkDay workDay1 = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkDay workDay2 = new WorkDay(LocalDate.of(2016, 8, 30));
        WorkMonth workMonth = new WorkMonth(2016, 9);
        workMonth.addWorkDay(workDay1);
        workMonth.addWorkDay(workDay2);
    }

    @Test(expected = EmptyTimeFieldException.class)
    public void test12() throws Exception {
        Task task = new Task("1234");
        WorkDay workDay = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkMonth workMonth = new WorkMonth(2016, 9);
        workDay.addTask(task);
        workMonth.addWorkDay(workDay);
        workMonth.getSumPerMonth();
    }

    @Test(expected = EmptyTimeFieldException.class)
    public void test13() throws Exception {
        Task task = new Task("1234");
        WorkDay workDay = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkMonth workMonth = new WorkMonth(2016, 9);
        workDay.addTask(task);
        workMonth.addWorkDay(workDay);
        workMonth.getExtraMinPerMonth();
    }

}