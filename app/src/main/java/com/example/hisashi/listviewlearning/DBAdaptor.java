package com.example.hisashi.listviewlearning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by hisashi on 2015-09-21.
 */
public final class DBAdaptor {
    private static final String TAG = "DBAdaptor";
    private static final int DB_VERSION = 16;
    public static final String DB_NAME = "timeTracker.db";
    public static final String TABLE_NAME = "timeRecords";
    public static final String TIMETRACKER_COLUMN_ID = "_id";
    public static final String TIMETRACKER_COLUMN_TIME = "time";
    public static final String TIMETRACKER_COLUMN_NOTES = "notes";
    public static final String TIMETRACKER_COLUMN_LAST_UPDATED = "lastUpdated";
    public static final String TIMETRACKER_COLUMN_STATUS = "status";
    private DBHelper dbHelper;

    private SQLiteDatabase database;

    public DBAdaptor(final Context context) {
        Log.v(TAG, "opening new DB...");
        this.dbHelper = new DBHelper(context);
        this.openDB();

        if(!doesDatabaseExist(context, "timeTracker.db")) {
            Log.d(TAG, " opening writable DB failed: DB=" + database);
        }
    }

    public long updateToDB(final String recordID, final String newTime, final String newNotes) {
        Log.v(TAG, "updateToDB called...");
        Log.v(TAG, "    - updating recordID=" + recordID + ", newTime=" + newTime + ", newNotes=" + newNotes);

        if (this.database.isReadOnly()) {
            Log.d(TAG, "    - checked DB is read-only... Unable to write.");
            return -1;
        }

        String lastUpdated = new SimpleDateFormat("MMM d, h:mm a").format(new Date());

        Log.d(TAG, "    - checked DB is writable. Inserting data...");
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMETRACKER_COLUMN_TIME, newTime);
        contentValues.put(TIMETRACKER_COLUMN_NOTES, newNotes);
        contentValues.put(TIMETRACKER_COLUMN_LAST_UPDATED, lastUpdated);
        return this.database.update(TABLE_NAME, contentValues, TIMETRACKER_COLUMN_ID + "=" + recordID, null);
    }

    public long insertToDB(final String time, final String notes) {
        Log.v(TAG, "insertToDB called...");

        if (this.database.isReadOnly()) {
            Log.d(TAG, "    - checked DB is read-only... Unable to write.");
            return -1;
        }

        String lastUpdated = new SimpleDateFormat("MMM d, h:mm a").format(new Date());

        Log.d(TAG, "    - checked DB is writable. Inserting data...");
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMETRACKER_COLUMN_TIME, time);
        contentValues.put(TIMETRACKER_COLUMN_NOTES, notes);
        contentValues.put(TIMETRACKER_COLUMN_LAST_UPDATED, lastUpdated);
        return this.database.insertOrThrow(TABLE_NAME, null, contentValues);
    }

    public void batchInsertToDB(final ArrayList<Record> records) {
        Log.v(TAG, "batchInsertToDB called...");

        if (records == null || records.size() == 0) {
            Log.v(TAG, "    - records is null or size=0...");
            return;
        }

        try {
            for (Record record : records) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TIMETRACKER_COLUMN_TIME, record.getClientName());
                contentValues.put(TIMETRACKER_COLUMN_NOTES, record.getDescription());
                contentValues.put(TIMETRACKER_COLUMN_LAST_UPDATED, record.getOpenDate());
                contentValues.put(TIMETRACKER_COLUMN_STATUS, record.getStatus());
                if (this.database.insertOrThrow(TABLE_NAME, null, contentValues) < 0) {
                    Log.v(TAG, "    - batchInsert failed with record=" + record.toString());
                }
            }
        } catch (SQLException e) {
            Log.v(TAG, e.getMessage());
        }
    }

    public NetworkResult batchInsertUniqueRecordsToDB(final ArrayList<Record> newRecords) {
        Log.v(TAG, "batchInsertUniqueRecordsToDB called...");

        if (newRecords == null || newRecords.size() == 0) {
            Log.v(TAG, "    - records is null or size=0...");
            return NetworkResult.No_New_Data_Found;
        }

        // get existing records from DB
        Cursor existingRecordsInDB = getAllTimeRecords();

        // DB is empty so insert all records
        if (!existingRecordsInDB.moveToFirst() && existingRecordsInDB.getCount() == 0) {
            Log.v(TAG, "existing DB is empty so inserting records...");
            batchInsertToDB(newRecords);
            return NetworkResult.New_Data_Downloaded;
        }

        Log.v(TAG, "existing DB contains records so going to filter...");
        // convert cursor to array list
        ArrayList<Record> dbRecords = new ArrayList<>();
        do {
            dbRecords.add(new Record(existingRecordsInDB.getString(1), null,
                    existingRecordsInDB.getString(2), existingRecordsInDB.getString(3), existingRecordsInDB.getString(4)));
        } while (existingRecordsInDB.moveToNext());

        // filter out duplicate records
        ArrayList<Record> uniqueRecords = newRecords;
        Iterator<Record> iterator = uniqueRecords.iterator();
        while (iterator.hasNext()){
            if (dbRecords.contains(iterator.next())) {
                iterator.remove();
            }
        }

        if (uniqueRecords.isEmpty()) {
            Log.v(TAG, "no new data to be inserted after removing duplicates...");
            return NetworkResult.No_New_Data_Found;
        }

        batchInsertToDB(uniqueRecords);
        return NetworkResult.New_Data_Downloaded;
    }

    public Cursor getAllTimeRecords() {
        Log.d(TAG, "querying all the time records from DB=" + TABLE_NAME);

        Cursor cursor = this.database.rawQuery("select * from " + TABLE_NAME, null);

        if (cursor == null) {
            throw new RuntimeException(TAG + ", error: cursor is null...");
        }

        if(cursor.getCount() <= 0) {
            Log.d(TAG, "cursor returns nothing...");

        }
        else {
            printQueriedRecords(cursor);
        }

        return cursor;
    }

    public Cursor getTimeRecordsWith(final String query) {
        Log.d(TAG, "querying time records with time=" + query + " from " + TABLE_NAME + "...");
        String wildcardQuery = "%" + query + "%";
        String queryString = "select * from timeRecords where time like ? or notes like ?";
        Cursor cursor = this.database.rawQuery(queryString, new String[] { wildcardQuery, wildcardQuery });

        if (cursor == null) {
            throw new RuntimeException(TAG + ", error: cursor is null...");
        }
        else if (cursor.getCount() <= 0){
            Log.v(TAG, "    - got query result with size=" + cursor.getCount());
        }

        printQueriedRecords(cursor);
        return cursor;
    }

    private void printQueriedRecords(final Cursor cursor) {
        while (cursor.moveToNext()) {
            Log.v(TAG, "    recordID=" + cursor.getString(0) + ", time=" + cursor.getString(1) + ", notes=" + cursor.getString(2));
        }
    }

    private static boolean doesDatabaseExist(final Context context, final String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    private void openDB() throws SQLException {
        this.database = this.dbHelper.getWritableDatabase();
    }

    public boolean checkDBOpen() {
        return this.database.isOpen();
    }

    public int deleteDB() {
        Log.d(TAG, "deleting DB...");
        return this.database.delete(TABLE_NAME, null, null);
    }

    public SQLiteDatabase getDB() {
        return this.database;
    }

    private static final class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            try {
                db.execSQL("Create table " + TABLE_NAME
                        + "(" + TIMETRACKER_COLUMN_ID + " integer primary key, "
                        + TIMETRACKER_COLUMN_TIME + " text, "
                        + TIMETRACKER_COLUMN_NOTES + " text, "
                        + TIMETRACKER_COLUMN_LAST_UPDATED + " text, "
                        + TIMETRACKER_COLUMN_STATUS + " text)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            Log.d(TAG, "Upgrading database form version " + oldVersion + " to "
                + newVersion + " which will destroy old data");
            db.execSQL("Drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
