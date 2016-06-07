package com.example.hisashi.listviewlearning.test;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;

import com.example.hisashi.listviewlearning.Log;
import com.example.hisashi.listviewlearning.R;
import com.example.hisashi.listviewlearning.ShowListActivity;

import org.junit.Before;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by hisashi on 2015-10-07.
 */
public class EspressoTest extends ActivityInstrumentationTestCase2 {
    private Activity myActivity;
    private final String TAG = "EspressoTest";

    public EspressoTest() {
        super(ShowListActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        myActivity = getActivity();
    }

    public void testAddTime() {
        Log.d(TAG, "testAddtime started...");

        Log.v(TAG, "    - clicking floating add button...");
        onView(withId(R.id.floating_add_button)).perform(click());

        Log.v(TAG, "    - focusing on editTextTimeView...");
        onView(withId(R.id.add_time_edit_text)).perform(typeText("Espresso typing time"), ViewActions.closeSoftKeyboard());

        Log.v(TAG, "    - checking the typed string is correct...");
        onView(withId(R.id.add_time_edit_text)).check(matches(withText("Espresso typing time")));

        Log.v(TAG, "    - clicking Save button");
        onView(withId(R.id.save_button)).perform(click());

        Log.v(TAG, "    - checking added timeRecord in the list...");

    }
}
