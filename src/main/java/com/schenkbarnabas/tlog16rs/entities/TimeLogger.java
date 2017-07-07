package com.schenkbarnabas.tlog16rs.entities;

import com.schenkbarnabas.tlog16rs.core.exceptions.NotNewMonthException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains the months
 * Created by bschenk on 6/27/17.
 */
@Entity
public class TimeLogger {

    public TimeLogger() {
    }

    @Id
    @GeneratedValue
    int id;

    /**
     * The months
     */
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    List<WorkMonth> months = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<WorkMonth> getMonths() {
        return months;
    }

    public void setMonths(List<WorkMonth> months) {
        this.months = months;
    }



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

}
