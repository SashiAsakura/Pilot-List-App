package com.example.hisashi.listviewlearning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by hisashi on 2015-09-21.
 */
public class AddTimeActivity extends AppCompatActivity {
    private final String TAG = "AddTimeActivity";
    private DBAdaptor databaseHelper;
    private Intent passedIntent;
    private String originalTime;
    private String originalNotes;
    private String recordID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_time);

        this.databaseHelper = new DBAdaptor(this);

        // set UP navigation with app icon in the action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setHintToEditTexts();

        this.passedIntent = getIntent();
        setIntentValuesToEditText();
        enableSaveButtonIfTextNotEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_action_bar, menu);
        setupShareActionButton(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupShareActionButton(Menu menu) {
        MenuItem share = menu.findItem(R.id.action_share);
        String textToBeShared = this.originalTime + ", " + this.originalNotes;
        Intent intent = new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, textToBeShared);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(intent);
        }
    }

    private void setHintToEditTexts() {
        TextInputLayout timeTextInputLayout = (TextInputLayout) findViewById(R.id.time_text_input_layout);
        TextInputLayout notesTextInputLayout = (TextInputLayout) findViewById(R.id.notes_text_input_layout);
        timeTextInputLayout.setHint("Time");
        notesTextInputLayout.setHint("Notes");
    }

    private void setIntentValuesToEditText() {
        Log.v(TAG, "setIntentValuesToEditText called...");

        if(this.passedIntent != null) {
            EditText timeEditText = (EditText) findViewById(R.id.add_time_edit_text);
            EditText notesEditText = (EditText) findViewById(R.id.add_notes_edit_text);
            timeEditText.setText(this.originalTime = this.passedIntent.getStringExtra("time"));
            notesEditText.setText(this.originalNotes = this.passedIntent.getStringExtra("notes"));
            this.recordID = this.passedIntent.getStringExtra("recordID");
            Log.v(TAG, "    - recordID=" + this.recordID + ", time=" + this.originalTime
                + ", notes=" + this.originalNotes);
        }
        else {
            Log.v(TAG, "    - passed intent is null...");
            this.showSoftKeyboard();
        }
    }

    private void showSoftKeyboard() {
        View view = (EditText) findViewById(R.id.add_time_edit_text);
        if (view.requestFocus()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void enableSaveButtonIfTextNotEmpty() {
        EditText timeEditText = (EditText) findViewById(R.id.add_time_edit_text);

        boolean timeEditTextEmpty = timeEditText.getText().toString().isEmpty();
        if(timeEditTextEmpty) {
            Button saveButton = (Button) findViewById(R.id.save_button);
            saveButton.setEnabled(false);
        }

        timeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Button saveButton = (Button) findViewById(R.id.save_button);
                saveButton.setEnabled(true);
            }
        });
    }

    public void onCancel(final View view) {
        finish();
    }

    public void onSave(final View view) {
        Log.v(TAG, "onSave called...");

        EditText timeView = (EditText) findViewById(R.id.add_time_edit_text);
        String time = timeView.getText().toString();
        if (exitWhenTimeStringIsEmpty(time)) {
            return;
        }

        EditText notesView = (EditText) findViewById(R.id.add_notes_edit_text);
        String notes = notesView.getText().toString();

        boolean insertOrUpdateToDBSuccess = tryInsertOrUpdateDB(time, notes);

        if (insertOrUpdateToDBSuccess) {
            Log.d(TAG, "Updated the record to DB successfully: newTime=" + time + ", newNotes=" + notes);
            this.passedIntent.putExtra("time", time);
            this.passedIntent.putExtra("notes", notes);
            this.setResult(RESULT_OK, this.passedIntent);
        }
        else {
            Log.d(TAG, "Updating the record to DB unsuccessful");
            this.setResult(RESULT_CANCELED, this.passedIntent);
        }

        finish();
    }

    private boolean tryInsertOrUpdateDB(final String time, final String notes) {
        boolean insertOrUpdateToDBSuccess = false;
        if (this.recordID == null) { // adding new record
            if(this.databaseHelper.insertToDB(time, notes) >= 0) {
                insertOrUpdateToDBSuccess = true;
            }
        }
        else { // updating existing record
            boolean valuesChanged = !this.originalTime.equals(time) || !this.originalNotes.equals(notes);
            if (valuesChanged) {
                if (this.databaseHelper.updateToDB(this.recordID, time, notes) == 1) {
                    insertOrUpdateToDBSuccess = true;
                }
            }
            else { // save button pressed but no values have changed, so exit
                finish();
            }
        }

        return insertOrUpdateToDBSuccess;
    }

    private boolean exitWhenTimeStringIsEmpty(final String typedTime) {
        if (typedTime == null || typedTime.isEmpty()) {
            Log.d("AddTimeActivity", "unable to add: you need to type in time to add");
            Toast.makeText(this, "Add Unsuccessful: Please type in time.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }
}
