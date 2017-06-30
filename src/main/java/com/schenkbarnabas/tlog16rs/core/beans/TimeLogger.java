package com.schenkbarnabas.tlog16rs.core.beans;

import com.schenkbarnabas.tlog16rs.core.exceptions.NotNewMonthException;

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
}
