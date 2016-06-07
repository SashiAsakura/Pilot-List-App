package com.example.hisashi.listviewlearning;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by hisashi on 2015-09-17.
 */
public final class TimeTrackerAdaptor extends CursorAdapter {
    private static final String TAG = "TimeTrackerAdaptor";
    private ListView listView;
    private final int TIME_COLUMN_INDEX = 1;
    private final int NOTES_COLUMN_INDEX = 2;
    private final int LAST_UPDATED_COLUMN_INDEX = 3;
    private final int STATUS_COLUMN_INDEX = 4;

    public TimeTrackerAdaptor(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.listView = (ListView) ((Activity) context).findViewById(R.id.timeRecordsList);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.time_record_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(TAG, "    searchActivity - bindView called...");

        TextView timeView = (TextView) view.findViewById(R.id.time_text_view);
        String time = cursor.getString(TIME_COLUMN_INDEX);
        if (time.isEmpty()) {
            Log.d(this.getClass().toString(), "time queried from DB empty");
        }

        timeView.setText(time);

        TextView notesView = (TextView) view.findViewById(R.id.notes_text_view);
        notesView.setText(cursor.getString(NOTES_COLUMN_INDEX));

        TextView lastUpdatedView = (TextView) view.findViewById(R.id.last_updated_text_view);
        lastUpdatedView.setText(cursor.getString(LAST_UPDATED_COLUMN_INDEX));

        TextView statusTextView = (TextView) view.findViewById(R.id.status_text_view);
        statusTextView.setText(cursor.getString(STATUS_COLUMN_INDEX));
    }

    public void highlightSearchQueryInTextView(final String searchQuery) {
        Log.v(TAG, "search - highlightSearchQueryInTextView called...");

        for (int i = 0; i < this.listView.getCount(); i++) {
            View view = this.listView.getChildAt(i);
            TextView timeTextView = (TextView) view.findViewById(R.id.time_text_view);
            String comparedString = timeTextView.getText().toString();
            highlightSearchQueryInTextView(timeTextView, searchQuery, comparedString);

            TextView notesTextView = (TextView) view.findViewById(R.id.notes_text_view);
            comparedString = notesTextView.getText().toString();
            highlightSearchQueryInTextView(notesTextView, searchQuery, comparedString);
        }
    }

    private void highlightSearchQueryInTextView(final TextView textView, final String searchQuery, final String comparedString) {
        Log.v(TAG, "highlightStringInTextViewWithQuery called...");
        if (textView == null) {
            Log.v(TAG, " view is null...");
            return;
        }

        Log.v(TAG, "    - search - comparedString=" + comparedString + ", query=" + searchQuery);
        SpannableString str = new SpannableString(comparedString);

        if (searchQuery != null & !searchQuery.isEmpty()) {
            int startIndex = 0;
            while (true) {
                startIndex = comparedString.indexOf(searchQuery, startIndex);
                if (startIndex >= 0) {
                    str.setSpan(new BackgroundColorSpan(Color.YELLOW),
                            startIndex, startIndex + searchQuery.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    startIndex++;
                } else {
                    break;
                }
            }
        }

        Log.v(TAG, " search - str=" + str);
        textView.setText(str);
    }
}

