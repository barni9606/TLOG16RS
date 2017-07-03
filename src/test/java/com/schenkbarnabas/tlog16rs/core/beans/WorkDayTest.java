package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.schenkbarnabas.tlog16rs.core.exceptions.FutureWorkException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NegativeMinutesOfWorkException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotSeparatedTimesException;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.*;

/**
 * Created by bschenk on 6/28/17.
 */
public class WorkDayTest {
    @Test
    public void test01() throws Exception {
        Task task = new Task("1234", "7:30", "8:45", "asd");
        WorkDay workDay = new WorkDay();
        workDay.addTask(task);
        Assert.assertEquals(-375, workDay.getExtraMinPerDay());
    }

    @Test
    public void test02() throws Exception {
        WorkDay workDay = new WorkDay();
        Assert.assertEquals( -1 * workDay.getRequiredMinPerDay(), workDay.getExtraMinPerDay());
    }

    @Test(expected = NegativeMinutesOfWorkException.class)
    public void test03() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.setRequiredMinPerDay(-3);
    }

    @Test(expected = NegativeMinutesOfWorkException.class)
    public void test04() throws Exception {
        WorkDay workDay = new WorkDay(-3, LocalDate.now());
    }

    @Test(expected = FutureWorkException.class)
    public void test05() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.setActualDay(LocalDate.MAX.getYear(), LocalDate.MAX.getMonth().getValue(), LocalDate.MAX.getDayOfMonth() );
    }

    @Test(expected = FutureWorkException.class)
    public void test06() throws Exception {
        new WorkDay(450, LocalDate.MAX);
    }

    @Test
    public void test07() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.addTask(new Task("1234", "7:30", "8:45", ""));
        workDay.addTask(new Task("1234", "8:45", "9:45", ""));
        Assert.assertEquals(135, workDay.getSumPerDay());
    }

    @Test
    public void test08() throws Exception {
        Assert.assertEquals(0, (new WorkDay()).getSumPerDay());
    }

    @Test
    public void test09() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.addTask(new Task("1234", "7:30", "8:45", ""));
        workDay.addTask(new Task("1234", "9:30", "11:45", ""));
        Assert.assertEquals(LocalTime.of(11, 45), workDay.endTimeOfTheLastTask());
    }

    @Test
    public void test10() throws Exception {
        Assert.assertEquals(null, (new WorkDay()).endTimeOfTheLastTask());
    }

    @Test(expected = NotSeparatedTimesException.class)
    public void test11() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.addTask(new Task("1234", "7:30", "8:45", ""));
        workDay.addTask(new Task("1234", "8:30", "9:45", ""));
    }

    @Test
    public void test12() throws Exception {
        WorkDay workDay = new WorkDay(123, LocalDate.now());
        Assert.assertEquals(123, workDay.getRequiredMinPerDay());
        Assert.assertEquals(LocalDate.now(), workDay.getActualDay());
    }

    @Test
    public void test13() throws Exception {
        WorkDay workDay = new WorkDay(LocalDate.now());
        Assert.assertEquals(450, workDay.getRequiredMinPerDay());
        Assert.assertEquals(LocalDate.now(), workDay.getActualDay());
    }

    @Test
    public void test14() throws Exception {
        WorkDay workDay = new WorkDay(1234);
        Assert.assertEquals(1234, workDay.getRequiredMinPerDay());
        Assert.assertEquals(LocalDate.now(), workDay.getActualDay());
    }

    @Test
    public void test15() throws Exception {
        WorkDay workDay = new WorkDay();
        Assert.assertEquals(450, workDay.getRequiredMinPerDay());
        Assert.assertEquals(LocalDate.now(), workDay.getActualDay());
    }

    @Test
    public void test16() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.setActualDay(2016, 9, 1);
        Assert.assertEquals(LocalDate.of( 2016, 9, 1), workDay.getActualDay());
    }

    @Test
    public void test17() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.setRequiredMinPerDay(300);
        Assert.assertEquals(300, workDay.getRequiredMinPerDay());
    }

    @Test(expected = EmptyTimeFieldException.class)
    public void test18() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.addTask(new Task("1234"));
        workDay.getSumPerDay();
    }

    @Test(expected = NotSeparatedTimesException.class)
    public void test19() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.addTask(new Task("1234", "8:45", "9:50", ""));
        workDay.addTask(new Task("1234", "8:20", "8:45", ""));
    }

}