package com.schenkbarnabas.tlog16rs.entities;

import com.schenkbarnabas.tlog16rs.core.beans.Util;
import com.schenkbarnabas.tlog16rs.core.exceptions.EmptyTimeFieldException;
import com.schenkbarnabas.tlog16rs.core.exceptions.InvalidTaskIdException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NoTaskIdException;
import com.schenkbarnabas.tlog16rs.core.exceptions.NotExpectedTimeOrderException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * An object of this class represents a redmine or LT task
 * Created by bschenk on 6/27/17.
 */
@Entity
@lombok.EqualsAndHashCode(of = {"taskId", "startTime"}, doNotUseGetters = true)
public class Task {
    public Task() {
    }


    @Id
    @GeneratedValue
    int id;

    /**
     * ID of the task
     */
    private String taskId;

    /**
     * When the task was started
     */
    private LocalTime startTime;

    /**
     * When the task was finished
     */
    private LocalTime endTime;
    /**
     * Comment
     */
    private String comment;

    private long minPerTask;
    /**
     * Constructor for every field with ints
     * @param taskId ID of the task
     * @param startHour When the task was started
     * @param startMin When the task was started
     * @param endHour When the task was finished
     * @param endMin When the task was finished
     * @param comment Comment
     * @throws NotExpectedTimeOrderException startTime must be before endTime
     * @throws InvalidTaskIdException the ID must be valid (either LT or redmine ID)
     * @throws NoTaskIdException the ID cannot be null or ""
     */
    public Task(String taskId, int startHour, int startMin, int endHour, int endMin, String comment) throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException {
        this(taskId);
        LocalTime tempStartTime = LocalTime.of(startHour, startMin);
        LocalTime tempEndTime = Util.roundToMultipleQuarterHour(tempStartTime, LocalTime.of(endHour, endMin));
        if(tempStartTime.isAfter(tempEndTime)){
            throw new NotExpectedTimeOrderException();
        }
        this.startTime = tempStartTime;
        this.endTime = tempEndTime;
        this.comment = comment;
        if(this.comment == null){
            this.comment = "";
        }
    }

    /**
     * Constructor for every field with Strings
     * @param taskId ID of the task
     * @param startTime When the task was started
     * @param endTime When the task was finished
     * @param comment Comment
     * @throws NotExpectedTimeOrderException startTime must be before endTime
     * @throws EmptyTimeFieldException the time strings cannot be nulls
     * @throws InvalidTaskIdException the ID must be valid (either LT or redmine ID)
     * @throws NoTaskIdException the ID cannot be null or ""
     */
    public Task(String taskId, String startTime, String endTime, String comment) throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException {
        this(taskId);
        if(startTime == null || endTime == null){
           throw new EmptyTimeFieldException();
        }
        LocalTime tempStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("H:m"));
        LocalTime tempEndTime = Util.roundToMultipleQuarterHour(tempStartTime, LocalTime.parse(endTime, DateTimeFormatter.ofPattern("H:m")));
        if(tempStartTime.isAfter(tempEndTime)){
            throw new NotExpectedTimeOrderException();
        }
        this.startTime = tempStartTime;
        this.endTime = tempEndTime;
        this.comment = comment;
        if(this.comment == null){
            this.comment = "";
        }
    }

    /**
     * Only ID constructor
     * @param taskId ID of the task
     * @throws InvalidTaskIdException the ID must be valid (either LT or redmine ID)
     * @throws NoTaskIdException the ID cannot be null or ""
     */
    public Task(String taskId) throws InvalidTaskIdException, NoTaskIdException {
        if(taskId == null || taskId.equals("")){
            throw new NoTaskIdException();
        }
        if(!(isValidLTTaskId(taskId) || isValidRedmineTaskId(taskId))){
            throw new InvalidTaskIdException();
        }
        this.taskId = taskId;
    }


    public String getTaskId() {
        return taskId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    /**
     *
     * @return the time spent on the task in minutes
     * @throws EmptyTimeFieldException the startTime and the endTime cannot be null when this method is called
     */
    public long getMinPerTask() throws EmptyTimeFieldException {
        if(endTime == null || startTime == null){
            throw new EmptyTimeFieldException();
        }
        return (endTime.toSecondOfDay() - startTime.toSecondOfDay()) / 60;
    }

    /**
     *
     * @param taskId determines if the ID given is valid or not
     * @return true if valid, false if invalid
     * @deprecated Replaced by {@link #isValidRedmineTaskId(String)} and {@link #isValidLTTaskId(String)}
     */
    @Deprecated
    public boolean isValidTask(String taskId){
        Pattern pattern = Pattern.compile("^((\\d{4})|(LT-\\d{4})){1}$");
        return pattern.matcher(taskId).find();
    }

    /**
     *
     * @param taskId determines if the ID given is a valid redmine task or not
     * @return true if valid, false if invalid
     */
    public boolean isValidRedmineTaskId(String taskId){
        Pattern pattern = Pattern.compile("^\\d{4}$");
        return pattern.matcher(taskId).find();
    }

    /**
     *
     * @param taskId determines if the ID given is a valid LT task or not
     * @return true if valid, false if invalid
     */
    public boolean isValidLTTaskId(String taskId){
        Pattern pattern = Pattern.compile("^LT-\\d{4}$");
        return pattern.matcher(taskId).find();
    }

    /**
     *
     * @return the String representation of the task
     */
    @Override
    public String toString() {
        return "timelogger.Task{" +
                "taskId='" + taskId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", comment='" + comment + '\'' +
                '}';
    }

    /**
     *
     * @return the startTime of the task
     * @throws EmptyTimeFieldException if startTime is null
     */
    public LocalTime getStartTime() throws EmptyTimeFieldException {
        if(startTime == null){
            throw new EmptyTimeFieldException();
        }
        return startTime;
    }

    /**
     *
     * @return the endTime of the task
     * @throws EmptyTimeFieldException if endTime is null
     */
    public LocalTime getEndTime() throws EmptyTimeFieldException {
        if(endTime == null){
            throw new EmptyTimeFieldException();
        }
        return endTime;
    }

    /**
     * Sets taskID
     * @param taskId ID of the task
     * @throws NoTaskIdException task ID cannot be null
     * @throws InvalidTaskIdException ID must be a valid redmine or LT task ID
     */
    public void setTaskId(String taskId) throws NoTaskIdException, InvalidTaskIdException {
        if(taskId == null){
            throw new NoTaskIdException();
        }
        if(!(isValidLTTaskId(taskId) || isValidRedmineTaskId(taskId))){
            throw new InvalidTaskIdException();
        }
        this.taskId = taskId;
    }

    /**
     * Sets startTime
     * @param startTime startTime as LocalTime
     * @throws NotExpectedTimeOrderException the startTime must be before the endTime
     */
    public void setStartTime(LocalTime startTime) throws NotExpectedTimeOrderException {
        this.startTime = startTime;
        if(endTime != null){
            if(this.startTime.isAfter(this.endTime)){
                throw new NotExpectedTimeOrderException();
            }
            endTime = Util.roundToMultipleQuarterHour(this.startTime, endTime);
        } else {
            endTime = LocalTime.of(startTime.getHour(), startTime.getMinute());
        }
    }

    /**
     * Sets startTime
     * @param startTime startTime as String
     * @throws NotExpectedTimeOrderException the startTime must be before the endTime
     */
    public void setStartTime(String startTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        if(startTime == null){
            throw new EmptyTimeFieldException();
        }
        setStartTime(LocalTime.parse(startTime, DateTimeFormatter.ofPattern("H:m")));
    }

    /**
     * Sets startTime
     * @param startHour hour of startTime as int
     * @param startMin minutes of startTime as int
     * @throws NotExpectedTimeOrderException the startTime must be before the endTime
     */
    public void setStartTime(int startHour, int startMin) throws NotExpectedTimeOrderException {
        setStartTime(LocalTime.of(startHour, startMin));
    }

    /**
     * Sets startTime
     * @param endTime startTime as LocalTime
     * @throws NotExpectedTimeOrderException the endTime must be after startTime
     */
    public void setEndTime(LocalTime endTime) throws NotExpectedTimeOrderException {
        if(startTime == null){
            this.endTime = endTime;
            return;
        }
        this.endTime = Util.roundToMultipleQuarterHour(this.startTime, endTime);
        if(this.startTime.isAfter(this.endTime)){
            throw new NotExpectedTimeOrderException();
        }
    }

    /**
     * Sets startTime
     * @param endTime startTime as String
     * @throws NotExpectedTimeOrderException the endTime must be after startTime
     */
    public void setEndTime(String endTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        if(endTime == null){
            throw new EmptyTimeFieldException();
        }
        setEndTime(LocalTime.parse(endTime, DateTimeFormatter.ofPattern("H:m")));
    }

    /**
     * Sets startTime
     * @param endHour hour of startTime as int
     * @param endMin minutes of startTime as int
     * @throws NotExpectedTimeOrderException the endTime must be after startTime
     */
    public void setEndTime(int endHour, int endMin) throws NotExpectedTimeOrderException {
        setEndTime(LocalTime.of(endHour, endMin));

    }

    /**
     * Sets the comment
     * @param comment comment String
     */
    public void setComment(String comment) {
        this.comment = comment;
        if(this.comment == null){
            this.comment = "";
        }
    }
}
