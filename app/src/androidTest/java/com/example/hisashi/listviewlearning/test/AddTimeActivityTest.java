package com.example.hisashi.listviewlearning.test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.hisashi.listviewlearning.AddTimeActivity;
import com.example.hisashi.listviewlearning.Log;
import com.example.hisashi.listviewlearning.R;

/**
 * Created by hisashi on 2015-09-28.
 */
public class AddTimeActivityTest extends ActivityInstrumentationTestCase2{
    private static final String TAG = "AddTimeActivityTest";
    private Button saveButton;
    private EditText timeText;

    public AddTimeActivityTest() {
        super(AddTimeActivity.class);
    }

    public void setUp() {
        this.saveButton = (Button) getActivity().findViewById(R.id.save_button);
        this.timeText = (EditText) getActivity().findViewById(R.id.add_time_edit_text);
        setActivityInitialTouchMode(true);
    }

    public void testSoftKeyboardShown() {
        Log.d(TAG, "testSoftKeyboardShown started...");

        View view = this.getActivity().findViewById(R.id.add_time_edit_text);
        if (view.requestFocus()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assertTrue(inputMethodManager.isAcceptingText());
            Log.d(TAG, "testSoftKeyboardShown PASS");
        } else {
            assertTrue(false);
            Log.d(TAG, "testSoftKeyboardShown FAILED");
        }
    }

    public void testSaveButton_layout() {
        View view = getActivity().getWindow().getDecorView();
        ViewAsserts.assertOnScreen(view, this.saveButton);

        ViewGroup.LayoutParams layoutParams = this.saveButton.getLayoutParams();
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testTimeView_layout() {
        View view = getActivity().getWindow().getDecorView();
        ViewAsserts.assertOnScreen(view, this.timeText);

        assertEquals(this.timeText.getHint(), getActivity().getString(R.string.time_text_hint));
        ViewGroup.LayoutParams layoutParams = this.timeText.getLayoutParams();
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.FILL_PARENT);
    }

    public void testEnablingSaveButtonIfTextNotEmpty() {
        Button saveButton = (Button) getActivity().findViewById(R.id.save_button);
        assertFalse(saveButton.isEnabled());

        final EditText timeText = (EditText) getActivity().findViewById(R.id.add_time_edit_text);
        assertTrue(timeText.getText().toString().isEmpty());

        sendActivityKeyStrokesOf(timeText, "20:00 from instrumentation test");

        assertTrue(saveButton.isEnabled());
        Log.d(TAG, "testEnablingSaveButtonIfTextNotEmpty PASS");
    }

    // Developer Android Documentation:
    // http://developer.android.com/intl/ja/tools/testing/activity_testing.html#WhatToTest
    public void testKeyStrokesSentToActivity() {
        Log.v(TAG, "testKeyStrokesSentToActivity started...");

        Log.v(TAG, "    - checking time editText view empty as default...");
        final EditText timeText = (EditText) getActivity().findViewById(R.id.add_time_edit_text);
        assertTrue(timeText.getText().toString().isEmpty());

        Log.v(TAG, "    - sending key strokes '20:00 from instrumentation test'...");
        sendActivityKeyStrokesOf(timeText, "20:00 from instrumentation test");
        assertFalse(timeText.getText().toString().isEmpty());
        Log.v(TAG, "    - checked time editText view not empty.");

        EditText sentTimeText = (EditText) getActivity().findViewById(R.id.add_time_edit_text);
        assertEquals(timeText, sentTimeText);
        Log.d(TAG, "testKeyStrokesSentToActivity PASS");
    }

    // Developer Android Documentation:
    // http://developer.android.com/intl/ja/training/activity-testing/activity-functional-testing.html#keyinput
    private void sendActivityKeyStrokesOf(final EditText timeText, String s) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                timeText.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(s);
        getInstrumentation().waitForIdleSync();
    }
}