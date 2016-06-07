package com.example.hisashi.listviewlearning;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hisashi on 2015-09-18.
 */
public class TimeRecordTest {

    @Test
    public void testGetTime() throws Exception {
        TimeRecord timeRecord = new TimeRecord("40:00", "Testing getTime()");
        assertEquals("40:00", timeRecord.getTime());
    }

    @Test
    public void testSetTime() throws Exception {
        TimeRecord timeRecord = new TimeRecord("old time", "notes");
        timeRecord.setTime("new time");
        assertEquals("new time", timeRecord.getTime());
    }

    @Test
    public void testGetNotes() throws Exception {
        TimeRecord timeRecord = new TimeRecord("40:00", "Testing getNotes()");
        assertEquals("Testing getNotes()", timeRecord.getNotes());
    }

    @Test
    public void testSetNotes() throws Exception {
        TimeRecord timeRecord = new TimeRecord("40:00", "old notes");
        timeRecord.setNotes("new notes");
        assertEquals("new notes", timeRecord.getNotes());
    }
}