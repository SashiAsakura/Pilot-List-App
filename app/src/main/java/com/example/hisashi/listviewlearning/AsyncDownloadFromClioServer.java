package com.example.hisashi.listviewlearning;

import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by hisashi on 2015-10-28.
 */
public class AsyncDownloadFromClioServer extends AsyncTask<Void, Void, ArrayList<Record>> {
    private final String TAG = "AsyncDownloadFromClioServer";
    private static final String CLIO_SERVER_URL = "https://app.goclio.com/api/v2/matters";
    private static final String CLIO_SERVER_AUTHORIZATION_KEY = "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a";
    private ArrayList<Record> records;
    public AsyncDwonload delegate = null;

    @Override
    protected ArrayList<Record> doInBackground(Void... params) {
        try {
            String httpResponse = HTTPController.getHTTPResponseStringFrom(CLIO_SERVER_URL, CLIO_SERVER_AUTHORIZATION_KEY);
            this.records = JSONParser.parseMatterJson(httpResponse);

        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "    - exception=" + e.getMessage());
        }

        return this.records;
    }

    @Override
    public void onPostExecute(ArrayList<Record> records) {
        Log.v(TAG, "onPostExecute called...");
        this.delegate.postAsyncExecute(records);
    }
}
