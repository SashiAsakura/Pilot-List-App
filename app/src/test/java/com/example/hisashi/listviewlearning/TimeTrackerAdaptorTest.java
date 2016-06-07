//package com.example.hisashi.listviewlearning;
//
//import org.junit.Test;
//
//import java.util.ArrayList;
//
//import static junit.framework.Assert.assertEquals;
//
///**
// * Created by hisashi on 2015-09-18.
// */
//public class TimeTrackerAdaptorTest {
//    private ArrayList<TimeRecord> timeRecords = new ArrayList<>();
//    private TimeRecord tr1 = new TimeRecord("10:00", "1st notes");
//    private TimeRecord tr2 = new TimeRecord("20:00", "2nd notes");
//    private TimeRecord tr3 = new TimeRecord("30:00", "3rd notes");
//
//    @Test
//    public void testAddTimeRecord() {
//        timeRecords.add(tr1);
//        timeRecords.add(tr2);
//        timeRecords.add(tr3);
//
//        TimeTrackerAdaptor timeTrackerAdaptor = new TimeTrackerAdaptor();
//        timeTrackerAdaptor.addTimeRecord(tr1);
//        timeTrackerAdaptor.addTimeRecord(tr2);
//        timeTrackerAdaptor.addTimeRecord(tr3);
//        assertEquals(timeRecords, timeTrackerAdaptor.getList());
//
//        TimeRecord tr4 = new TimeRecord("40:00", "4th notes");
//        timeTrackerAdaptor.addTimeRecord(tr4);
//        assertNotEquals(timeRecords, timeTrackerAdaptor.getList());
//
//        timeRecords.add(tr4);
//        assertEquals(timeRecords, timeTrackerAdaptor.getList());
//    }
//
//    @Test
//    public void testGetCount() throws Exception {
//        timeRecords.add(tr1);
//        timeRecords.add(tr2);
//        timeRecords.add(tr3);
//        assertEquals(3, timeRecords.size());
//    }
//
//    @Test
//    public void testGetItem() throws Exception {
//        assertEquals(true, true);
//    }
//
//    @Test
//    public void testGetItemId() throws Exception {
//        assertEquals(true, true);
//    }
//
//    @Test
//    public void testGetView() throws Exception {
//        assertEquals(true, true);
//    }
//}