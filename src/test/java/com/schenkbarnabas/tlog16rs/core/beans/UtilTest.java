package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotExpectedTimeOrderException;
import org.junit.Assert;
import org.junit.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by bschenk on 6/29/17.
 */
public class UtilTest {

    @Test
    public void test01() throws Exception{
        LocalTime startTime = LocalTime.of(7, 30);
        LocalTime endTime = LocalTime.of(7, 50);
        endTime = Util.roundToMultipleQuarterHour(startTime, endTime);
        Assert.assertEquals(LocalTime.of(7, 45), endTime);
    }

    @Test
    public void test02() throws Exception {
        LocalTime startTime = LocalTime.of(7, 30);
        LocalTime endTime = LocalTime.of(7, 50);
        boolean isMultipleQuarterHour = Util.isMultipleQuarterHour(startTime, endTime);
        Assert.assertEquals(false, isMultipleQuarterHour);
    }

    @Test
    public void test03() throws Exception {
        LocalTime startTime = LocalTime.of(7, 30);
        LocalTime endTime = LocalTime.of(7, 45);
        boolean isMultipleQuarterHour = Util.isMultipleQuarterHour(startTime, endTime);
        Assert.assertEquals(true, isMultipleQuarterHour);
    }

    @Test(expected = EmptyTimeFieldException.class)
    public void test04() throws Exception {
        Util.isMultipleQuarterHour(null, LocalTime.of(7,45));
    }

    @Test(expected = NotExpectedTimeOrderException.class)
    public void test05() throws Exception {
        LocalTime startTime = LocalTime.of(8, 30);
        LocalTime endTime = LocalTime.of(7, 45);
        Util.isMultipleQuarterHour(startTime, endTime);
    }

    @Test
    public void test06() throws Exception {
        String string = "6:30-6:45\t5:30-6:30\ttrue\n"  +
                        "6:30-6:45\t6:45-7:00\ttrue\n"  +
                        "6:30-6:30\t5:30-6:30\ttrue\n"  +
                        "6:30-7:30\t7:30-7:30\ttrue\n"  +
                        "6:30-7:00\t6:00-6:45\tfalse\n" +
                        "6:30-7:00\t6:30-6:45\tfalse\n" +
                        "6:30-7:00\t6:45-7:15\tfalse\n" +
                        "6:30-7:00\t6:45-7:00\tfalse\n" +
                        "6:30-6:30\t6:30-7:00\tfalse\n" +
                        "6:30-7:30\t6:30-6:30\tfalse"
                        ;
        for(String lineString : string.split("\n")){
            String line[] = lineString.split("\t");
            String task1String[] = line[0].split("-");
            Task task1 = new Task("1234", task1String[0], task1String[1], "");
            List<Task> tasks = new ArrayList<>();
            tasks.add(task1);
            String task2String[] = line[1].split("-");
            Task task2 = new Task("1234", task2String[0], task2String[1], "");
            boolean test = line[2].equals("true");
            Assert.assertEquals(test, Util.isSeparatedTime(tasks, task2));
        }
    }


}