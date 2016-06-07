package com.example.hisashi.listviewlearning;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by hisashi on 2015-10-20.
 */
public class SearchActivity extends AppCompatActivity {
    private static final int ADD_TIME_RECORD_REQUEST_CODE = 1;
    private final String TAG = "SearchActivity";
    private ListView listView;
    private TimeTrackerAdaptor timeTrackerAdaptor;
    private DBAdaptor databaseHelper;
    private String searchQuery;
    private Handler backendThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        this.backendThread = new Handler();
        this.databaseHelper = new DBAdaptor(this);

        // set UP navigation with app icon in the action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent searchQueryIntent = getIntent();
        this.searchQuery = searchQueryIntent.getStringExtra("searchQuery");
        setTitle("Search '" + this.searchQuery + "'");

        fetchTimeRecordsToListWith(this.searchQuery);
    }

    private void fetchTimeRecordsToListWith(final String query) {
        final Dialog dialog = setupFetching();

        // this is backend thread
        Thread thread = new Thread() {
            public void run() {
                final Cursor cursor = databaseHelper.getTimeRecordsWith(query);
                updateList(cursor);

                // this is executed on UI thread after backend thread is finished
                backendThread.post(new Runnable() {
                    @Override
                    public void run() {
                        if (cursor.getCount() <= 0) {
                            Toast.makeText(getApplicationContext(), "No time record found", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            timeTrackerAdaptor.highlightSearchQueryInTextView(query);
                        }

                        dialog.dismiss();
                    }
                });
            }
        };
        thread.start();
    }

    private void updateList(final Cursor cursor) {
        if(cursor.getCount() <= 0) {
            return;
        }

        this.timeTrackerAdaptor = new TimeTrackerAdaptor(this, cursor, 0);
        this.listView.setAdapter(this.timeTrackerAdaptor);
    }

    private Dialog setupFetching() {
        Log.d(TAG, "setupFetching called, setting up list view...");
        Dialog dialog = ProgressDialog.show(this, "Loading", "Loading time records");

        final Activity activity = this;
        this.listView = (ListView) findViewById(R.id.timeRecordsList);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor selectedTimeRecord = (Cursor) parent.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), AddTimeActivity.class);
                intent.putExtra("time", selectedTimeRecord.getString(1));
                intent.putExtra("notes", selectedTimeRecord.getString(2));
                intent.putExtra("recordID", selectedTimeRecord.getString(0));

                // Window transition from main view to timeEditText view per Google material design
                View timeEditTextView = findViewById(R.id.time_text_view);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, timeEditTextView, "windowTransition");
                startActivityForResult(intent, ADD_TIME_RECORD_REQUEST_CODE);
            }
        });
        return dialog;
    }
}
