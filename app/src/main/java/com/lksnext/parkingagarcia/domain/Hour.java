package com.lksnext.parkingagarcia.domain;

import java.util.Date;

public class Hour {

    long startTime; // In milliseconds
    long endTime; // In milliseconds

    public Hour() {

    }

    public Hour(Date startDate, Date endDate) {
        this.startTime = startDate.getTime();
        this.endTime = endDate.getTime();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isOverlapping(Hour h){
        return (this.startTime >= h.startTime && this.startTime <= h.endTime) ||
                (this.endTime >= h.startTime && this.endTime <= h.endTime) ||
                (this.startTime <= h.startTime && this.endTime >= h.endTime);
    }
}
