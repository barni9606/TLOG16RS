package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.entities.Task;
import com.schenkbarnabas.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.schenkbarnabas.tlog16rs.core.exceptions.InvalidTaskIdException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NoTaskIdException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotExpectedTimeOrderException;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;

/**
 * Created by bschenk on 6/28/17.
 */
public class TaskTest {
    @Test(expected = NotExpectedTimeOrderException.class)
    public void wrongTimeOrderTest1() throws Exception{
        new Task("1234","8:45","07:30","asd");

    }

    @Test(expected = EmptyTimeFieldException.class)
    public void emptyTimeTest2() throws Exception{
        new Task("1234","12:30",null,"");
    }

    @Test
    public void getMinPerTask3() throws Exception{
        Task task = new Task("1234", "7:30", "8:45", "");
        Assert.assertEquals(75, task.getMinPerTask());
    }

    @Test(expected = InvalidTaskIdException.class)
    public void invalidRedmineTaskIdTest4() throws Exception{
        new Task("154858");
    }

    @Test(expected = InvalidTaskIdException.class)
    public void invalidLtTaskIdTest5() throws Exception{
        new Task("LT-154858");
    }

    @Test(expected = NoTaskIdException.class)
    public void noTaskIdTest6() throws Exception{
        new Task(null);
    }

    @Test
    public void getCommentTest7() throws Exception {
        Task task = new Task("1234", "7:30", "8:45", null);
        Assert.assertEquals("", task.getComment());
    }

    @Test
    public void endTimeRoundConstructorTest8() throws Exception{
        Task task1 = new Task("1234", "7:30", "7:50", "");
        Task task2 = new Task("1234", 7,30, 7,50, "");

        Assert.assertEquals(LocalTime.of(7,45), task1.getEndTime());
        Assert.assertEquals(LocalTime.of(7,45), task2.getEndTime());
    }

    @Test
    public void endTimeRoundedBySetEndTimeTest9() throws Exception {
        Task task = new Task("1234");
        task.setStartTime("7:30");
        task.setEndTime("7:50");
        Assert.assertEquals(LocalTime.of(7,45), task.getEndTime());
        task.setEndTime(LocalTime.of(7,49));
        Assert.assertEquals(LocalTime.of(7,45), task.getEndTime());
        task.setEndTime(7, 55);
        Assert.assertEquals(LocalTime.of(8,0), task.getEndTime());
    }

    @Test
    public void endTimeRoundedBySetStartTimeTest10() throws Exception{
        Task task = new Task("1234");
        task.setEndTime("7:50");
        task.setStartTime("7:30");
        Assert.assertEquals(LocalTime.of(7,45), task.getEndTime());
        task.setStartTime(LocalTime.of(7, 31));
        Assert.assertEquals(LocalTime.of(7,46), task.getEndTime());
        task.setStartTime(7, 32);
        Assert.assertEquals(LocalTime.of(7,47), task.getEndTime());
    }

    @Test(expected = NoTaskIdException.class)
    public void setTaskIdNullTest11() throws Exception{
        Task task = new Task("1234");
        task.setTaskId(null);
    }

    @Test(expected = InvalidTaskIdException.class)
    public void invalidTaskIdSetTaskIdTest12() throws Exception{
        Task task = new Task("1234");
        task.setTaskId("12354123");
    }

    @Test(expected = NotExpectedTimeOrderException.class)
    public void startTimeLaterSetStartTimeTest13() throws Exception{
        Task task = new Task("1234");
        task.setEndTime("13:30");
        task.setStartTime("13:40");
    }

    @Test(expected = NotExpectedTimeOrderException.class)
    public void endTimeEarlierSetEndTimeTest14() throws Exception{
        Task task = new Task("1234");
        task.setStartTime("13:40");
        task.setEndTime("13:30");
    }

    @Test(expected = EmptyTimeFieldException.class)
    public void getMinPerTaskFailTest15() throws Exception{
        Task task = new Task("1234");
        task.getMinPerTask();
    }

    @Test
    public void setStartTwiceTest16() throws Exception{
        Task task = new Task("1234");
        task.setStartTime("7:30");
        task.setEndTime("7:45");
        task.setStartTime("7:00");
        Assert.assertEquals(LocalTime.of(7,0), task.getStartTime());
    }

    @Test
    public void setEndTwiceTest17() throws Exception{
        Task task = new Task("1234");
        task.setStartTime("7:30");
        task.setEndTime("7:45");
        task.setEndTime("8:00");
        Assert.assertEquals(LocalTime.of(8,0), task.getEndTime());
    }

    @Test
    public void simpleTask() throws Exception{
        Task task = new Task("1234", "7:30", "8:00", "asd");
        Assert.assertEquals("1234", task.getTaskId());
        Assert.assertEquals(LocalTime.of(7,30), task.getStartTime());
        Assert.assertEquals(LocalTime.of(8,0), task.getEndTime());
        Assert.assertEquals("asd", task.getComment());
    }
}