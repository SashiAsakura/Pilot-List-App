package com.example.hisashi.listviewlearning.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hisashi.listviewlearning.R;
import com.example.hisashi.listviewlearning.ShowListActivity;

/**
 * Created by hisashi on 2015-09-18.
 */
public class ShowListActivityTest extends ActivityInstrumentationTestCase2<ShowListActivity> {
    private ShowListActivity showList;
    private ListView actualTimeRecord;

    public ShowListActivityTest() {
        super(ShowListActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        showList = getActivity();
        actualTimeRecord= (ListView) showList.findViewById(R.id.timeRecordsList);

    }

    public void testPreconditions() {
        assertNotNull("showList is null", showList);
        assertNotNull("listView is null", actualTimeRecord);
    }

//    public void testListItemText() {
//        int listSize = showList.timeTrackerAdaptor.getCount();
//        for (int i = 0; i < listSize; i++) {
//            final TimeRecord expectedTimeRecord = (TimeRecord)showList.timeTrackerAdaptor.getItem(i);
//
//            assertEquals(expectedTimeRecord.getTime(), getTimeStringFromListIndexAt(actualTimeRecord, i));
//            Log.d("expectedTime=", expectedTimeRecord.getTime());
//            assertEquals(expectedTimeRecord.getNotes(), getNoteStringFromListIndexAt(actualTimeRecord, i));
//            Log.d("expectedNotes=", expectedTimeRecord.getNotes());
//        }
//    }

    private String getTimeStringFromListIndexAt(final ListView timeRecordList, int index) {
        View firstTimeRecordView = timeRecordList.getChildAt(index);
        TextView timeView = (TextView) firstTimeRecordView.findViewById(R.id.add_time_edit_text);

        Log.d("actualTime=", timeView.getText().toString());
        return timeView.getText().toString();
    }

    private String getNoteStringFromListIndexAt(final ListView timeRecordList, int index) {
        View firstTimeRecordView = timeRecordList.getChildAt(index);
        TextView notesView = (TextView) firstTimeRecordView.findViewById(R.id.add_notes_edit_text);

        Log.d("actualNotes=", notesView.getText().toString());
        return notesView.getText().toString();
    }
}
