package com.example.hisashi.listviewlearning;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.splunk.mint.Mint;

import java.util.ArrayList;

public class ShowListActivity extends AppCompatActivity implements AsyncDwonload{
    private static final int ADD_TIME_RECORD_REQUEST_CODE = 1;
    private static final String TAG = "ShowListActivity";

    private TimeTrackerAdaptor timeTrackerAdaptor;
    private DBAdaptor databaseHelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private CoordinatorLayout coordinatorLayout;
    AsyncDownloadFromClioServer asyncDownloadFromClioServer;
    private Context currentContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        this.currentContext = this;
        Log log = Log.getInstance(this);
        this.coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content_coordinator_layout);

        setupRealTimeBugAnalytics();
        setupNavigationUpButton();
        setupSwipeRefresh();
        setupFloatingAddButton(this);

        this.databaseHelper = new DBAdaptor(this);
        fetchAllTimeRecordsToList();
    }

    private void setupNavigationUpButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_up);
    }

    private void setupRealTimeBugAnalytics() {
        Mint.enableDebug();
        Mint.initAndStartSession(this, "b452b897");
    }

    private void refreshList() {
        Log.v(TAG, "    - refreshList called...");
        checkInternetConnection();
        httpGetFromServer();
    }


    private void setupSwipeRefresh() {
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshContainer);

        // Setup refresh listener which triggers new data loading
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        this.swipeRefreshLayout.setColorSchemeResources(R.color.lightblue);
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Log.v(TAG, "Network conneciton is turned off...");
            alertAndAskToEnableNetwork();
        }
    }

    private void alertAndAskToEnableNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to refresh. Please turn on mobile network or Wi-Fi in the Settings.")
                .setTitle("Network connection turned off")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void httpGetFromServer() {
            // Async Task's execute() can be called ONLY ONCE per instance,
            // therefore instantiating a new object of AsycTask here
            asyncDownloadFromClioServer = new AsyncDownloadFromClioServer();
            asyncDownloadFromClioServer.delegate = (AsyncDwonload) currentContext;
            asyncDownloadFromClioServer.execute();
    }

    // This method will be called after asynchronous execute() method on
    // AsyncDownloadFromClioServer is finished
    @Override
    public void postAsyncExecute(ArrayList<Record> records) {
        Log.v(TAG, "postAsyncExecute called....");
        NetworkResult networdResult = this.databaseHelper.batchInsertUniqueRecordsToDB(records);
        if (networdResult == NetworkResult.New_Data_Downloaded) {
            Toast.makeText(this, "New data downloaded.", Toast.LENGTH_SHORT).show();
        }
        else if (networdResult == NetworkResult.No_New_Data_Found) {
            Toast.makeText(this, "No new data found.", Toast.LENGTH_SHORT).show();
        }

        this.timeTrackerAdaptor.changeCursor(this.databaseHelper.getAllTimeRecords());
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupFloatingAddButton(final Context context) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicking floating add button...");
                Intent intent = new Intent(context, AddTimeActivity.class);
                startActivityForResult(intent, ADD_TIME_RECORD_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_action_bar, menu);
        setupSearchViewActionButton(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchViewActionButton(Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        // collapse search view when back button pressed
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.v(TAG, "onFocusChange called...");
                if (!hasFocus) {
                    Log.v(TAG, "    - back button pressed...");
                    searchView.onActionViewCollapsed();
                    searchView.setQuery("", false);
                }
            }
        });

        // search query when submit button on search widget pressed
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v(TAG, "onQueryTextSubmit called...");
//                fetchTimeRecordsToListWith(query);
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.home:
                handleNavigationUpButton();

                // handle Window transition back from detail activity
                supportFinishAfterTransition();
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_share:
                return true;
            case R.id.action_refresh:
                Log.v(TAG, "action_refresh is pressed...");
                refreshList();
                return true;
            case R.id.action_help:
                return true;
            case R.id.action_about:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleNavigationUpButton() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                    .startActivities();
        }
        else {
            NavUtils.navigateUpTo(this,upIntent);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TIME_RECORD_REQUEST_CODE) {
            if( resultCode == RESULT_OK) {
                String time = data.getStringExtra("time");
                String notes = data.getStringExtra("notes");
                Log.v(TAG, "   updateList - getting data back from intent: time=" + time + ", notes=" + notes);

                setupSnackbarUndoButton();
                this.timeTrackerAdaptor.changeCursor(this.databaseHelper.getAllTimeRecords());
            }
            else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(this, "Add unsuccessful.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupSnackbarUndoButton() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "New time record is added", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "New time record is deleted!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    private void fetchAllTimeRecordsToList() {
        Dialog dialog = ProgressDialog.show(this, "Loading", "Loading time records");
        setupFetching();
        Cursor cursor = this.databaseHelper.getAllTimeRecords();
        updateList(cursor);
        dialog.dismiss();
    }

    private void fetchTimeRecordsToListWith(String query) {
        Dialog dialog = ProgressDialog.show(this, "Loading", "Loading time records");
        setupFetching();
        Cursor cursor = this.databaseHelper.getTimeRecordsWith(query);
        updateList(cursor);
        dialog.dismiss();
    }

    private void updateList(Cursor cursor) {
        if(cursor.getCount() <= 0) {
            Toast.makeText(this, "No time record found", Toast.LENGTH_SHORT).show();
        }

        this.timeTrackerAdaptor = new TimeTrackerAdaptor(this, cursor, 0);
        this.listView.setAdapter(this.timeTrackerAdaptor);
    }

    private void setupFetching() {
        Log.d(TAG, "setupFetching called, setting up list view...");

        final Activity activity = this;
        this.listView = (ListView) findViewById(R.id.timeRecordsList);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor selectedTimeRecord = (Cursor) parent.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), AddTimeActivity.class);
                intent.putExtra("time", selectedTimeRecord.getString(1));
                intent.putExtra("notes", selectedTimeRecord.getString(2));
                intent.putExtra("recordID", Integer.toString(position + 1));

                // Window transition from main view to timeEditText view per Google material design
                View timeEditTextView = findViewById(R.id.time_text_view);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, timeEditTextView, "windowTransition");

                startActivityForResult(intent, ADD_TIME_RECORD_REQUEST_CODE);
            }
        });

        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "long pressed...");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");

                Cursor selectedTimeRecord = (Cursor) parent.getItemAtPosition(position);
                String textToBeShared = selectedTimeRecord.getString(1) + ", " + selectedTimeRecord.getString(2);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textToBeShared);
                startActivity(Intent.createChooser(sendIntent, "Share text to"));
                return true;
            }
        });
    }
}
