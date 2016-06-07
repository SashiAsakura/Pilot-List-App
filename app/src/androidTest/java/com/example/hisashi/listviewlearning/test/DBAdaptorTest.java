package com.example.hisashi.listviewlearning.test;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.hisashi.listviewlearning.DBAdaptor;
import com.example.hisashi.listviewlearning.Log;
import com.example.hisashi.listviewlearning.TimeRecord;

import java.util.ArrayList;

/**
 * Created by hisashi on 2015-09-25.
 */
public class DBAdaptorTest extends AndroidTestCase {
    private static final String TAG = "DBAdaptorTest-";

    public void testCreateDB() {
        String tag = TAG + "testCreateDB";
        Log.v(tag, "testCreateDB started...");

        Log.v(tag, "    - creating new DB...");
        DBAdaptor dbAdaptor = new DBAdaptor(mContext);
        assertTrue(dbAdaptor.checkDBOpen());
        Log.v(tag, "    - created new DB successfully:");
        Log.d(tag, "testCreateDB PASS");
    }

    public void testInsertToDB() {
        String tag = TAG + "testInsertDB";
        Log.d(tag, "testInsertToDB started...");
        String insertingTime = "100";
        String insertingNotes = "inserting notes";

        Log.v(tag, "    - inserting (100, inserting notes) to DB...");
        DBAdaptor dbAdaptor = new DBAdaptor(mContext);
        long rowIDOfNewRecord = dbAdaptor.insertToDB(insertingTime, insertingNotes);
        assertTrue(rowIDOfNewRecord != -1);
        Log.d(tag, "    - inserted successfully: rowIDOfNewRecord=" + rowIDOfNewRecord);

        Log.v(tag, "    - checking DB size");
        Cursor cursor = dbAdaptor.getAllTimeRecords();
        assertEquals(cursor.getCount(), rowIDOfNewRecord);
        Log.d(tag, "    - table size consistent: tableSize=" + cursor.getCount()
                + ", rowIDofNewRecord=" + rowIDOfNewRecord);

        assertTrue(cursor.moveToLast());
        String queriedTime = cursor.getString(cursor.getColumnIndex(DBAdaptor.TIMETRACKER_COLUMN_TIME));
        String queriedNotes = cursor.getString(cursor.getColumnIndex(DBAdaptor.TIMETRACKER_COLUMN_NOTES));
        assertEquals(insertingTime, queriedTime);
        assertEquals(insertingNotes, queriedNotes);
        Log.d(tag, "    - inserted & queried data consistent: insertedTime=" + insertingTime
                + ", insertedNoted=" + insertingNotes);
        Log.d(tag, "testInsertToDB finished PASS");
    }

    public void testGetAllRecords() {
        String tag = TAG + "testGetAllRecordsDB";
        Log.v(tag, "testGetAllRecords started...");
        DBAdaptor dbAdaptor = new DBAdaptor(mContext);

        Log.v(tag, "    - getting DB data...");
        Cursor cursor = dbAdaptor.getAllTimeRecords();
        if (!cursor.moveToFirst()) {
            Log.d(tag, "    - checked DB size is 0...");
            return;
        }

        Log.v(tag, "    - checked DB size is > 0, so storing existing data in DB...");
        assertTrue(cursor.moveToFirst());
        ArrayList timeRecords = new ArrayList<TimeRecord>();
        do {
            timeRecords.add(new TimeRecord(cursor.getString(1), cursor.getString(2)));
        } while (cursor.moveToNext());
        Log.d(TAG, "    - stored existing DB data successfully... existing data=" + timeRecords.toString());

        Log.v(tag, "    - checking DB size equal to cursor's record size...");
        assertEquals(cursor.getCount(), timeRecords.size());
        Log.d(tag, "    - checked DB & cursor size successfully: DB data=" + timeRecords.toString());

        Log.v(tag, "    - deleting all the data in DB...");
        assertTrue(dbAdaptor.deleteDB() == cursor.getCount());
        if (dbAdaptor.getAllTimeRecords().getCount() == 0) {
            Log.d(tag, "    - deleted DB successfully.");
        }

        Log.v(TAG, "    - checking DB is open...");
        if (!dbAdaptor.checkDBOpen()) {
            Log.d(TAG, "    - checked DB was closed");
        }
        if (dbAdaptor.checkDBOpen()) {
            Log.d(TAG, "    - checked DB was read-only");
        }

        Log.d(TAG, "    - checked DB is open and writable...");
        Log.v(tag, "    - reinserting cursor data to DB...");
        for (Object timeRecord : timeRecords) {
            TimeRecord tr = (TimeRecord) timeRecord;
            dbAdaptor.insertToDB(tr.getTime(), tr.getNotes());
            Log.v(tag, "    - inserting record=" + tr.getTime() + " & " + tr.getNotes());
        }

        Log.v(tag, "    - checking DB and cursor size equal...");
        Cursor cursor2 = dbAdaptor.getAllTimeRecords();
        assertEquals(cursor.getCount(), cursor2.getCount());
        Log.d(tag, "    - checked original & modified DBSize equal: cursor.getCount=" + cursor.getCount()
                + ", cursor2.getCount=" + cursor2.getCount());

        if (cursor.getCount() > 0) {
            assertTrue(cursor.moveToFirst());
            assertTrue(cursor2.moveToFirst());
        }

        Log.v(tag, "    - checking DB & cursor data consistency...");
        do {
            assertEquals(cursor.getString(1), cursor2.getString(1));
            assertEquals(cursor.getString(2), cursor2.getString(2));
        } while (cursor.moveToNext() && cursor2.moveToNext());
        Log.d(tag, "    - checked DB & cursor data consistency successfully");
        Log.d(tag, "testGetAllRecords finished PASS");
    }
}
