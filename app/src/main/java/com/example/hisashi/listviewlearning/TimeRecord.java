package com.example.hisashi.listviewlearning;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hisashi on 2015-09-17.
 */
public final class TimeRecord {
    private String time;
    private String notes;
    private String lastUpdated;

    public TimeRecord(String time, String notes) {
        this.time = time;
        this.notes = notes;
        this.lastUpdated = new SimpleDateFormat("MMM d, h:mm a").format(new Date());
    }

    @Override
    public String toString() {
        return "Time=" + this.time + ", notes=" + this.notes;
    }

    public String getTime() { return this.time; }

    public void setTime(String that) {
        this.time = that;
    }

    public String getNotes() { return this.notes; }

    public void setNotes(String that) {
        this.notes = that;
    }

    public String getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(String that) {
        this.lastUpdated = that;
    }
}
