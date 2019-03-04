package com.rino.ekky.footballforum;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ClubAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Club>> {
    private ArrayList<Club> data;
    private Context context;
    private String id;
    private boolean hasResult = false;

    public ClubAsyncTaskLoader(Context context, String id) {
        super(context);

        onContentChanged();
        this.context = context;
        this.id = id;
        this.data = new ArrayList<>();
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged()) {
            forceLoad();
        }
        else if (hasResult) {
            deliverResult(data);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (hasResult) {
            onReleaseResources(data);
            data = null;
            hasResult = false;
        }
    }

    @Override
    public void deliverResult(ArrayList<Club> data) {
        this.data = data;
        hasResult = true;
        super.deliverResult(data);
    }

    @Override
    public ArrayList<Club> loadInBackground() {
        String url = "";
        SyncHttpClient client = new SyncHttpClient();
        final ArrayList<Club> clubItems = new ArrayList<>();

        url = "http://api.football-data.org/v2/competitions/" + id + "/teams";
        //Log.e("api_error", url);
        client.addHeader("X-Auth-Token", "e4eecedd721c4bed80ed93df672eb1fa");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                setUseSynchronousMode(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject response = new JSONObject(result);
                    //Log.e("api_error", result);
                    JSONArray list = response.getJSONArray("teams");

                    for (int i=0; i<list.length(); i++) {
                        JSONObject clubObject = list.getJSONObject(i);
                        Club club = new Club(clubObject);
                        clubItems.add(club);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("api_error " + statusCode, error.toString());
            }
        });
        return clubItems;
    }

    protected void onReleaseResources(ArrayList<Club> data) {
        // do nothing
    }
}
