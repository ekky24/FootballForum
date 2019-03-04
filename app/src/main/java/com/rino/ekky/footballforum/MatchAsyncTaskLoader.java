package com.rino.ekky.footballforum;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MatchAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Match>> {
    private ArrayList<Match> data;
    private Context context;
    private String id;
    private boolean hasResult = false;
    private String type;

    public MatchAsyncTaskLoader(Context context, String id, String type) {
        super(context);

        onContentChanged();
        this.context = context;
        this.id = id;
        this.data = new ArrayList<>();
        this.type = type;
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
    public void deliverResult(ArrayList<Match> data) {
        this.data = data;
        hasResult = true;
        super.deliverResult(data);
    }

    @Override
    public ArrayList<Match> loadInBackground() {
        String url = "";
        SyncHttpClient client = new SyncHttpClient();
        final ArrayList<Match> matches = new ArrayList<>();

        if (type.equals("FINISHED")) {
            url = "http://api.football-data.org/v2/teams/" + this.id + "/matches?status=FINISHED";
        }
        else if (type.equals("LIVE")) {
            url = "http://api.football-data.org/v2/teams/" + this.id + "/matches?status=LIVE";
        }
        else if (type.equals("SCHEDULED")) {
            url = "http://api.football-data.org/v2/teams/" + this.id + "/matches?status=SCHEDULED";
        }
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
                    JSONArray list = response.getJSONArray("matches");

                    for (int i=0; i<list.length(); i++) {
                        JSONObject matchObject = list.getJSONObject(i);
                        Match match = new Match(matchObject, type);
                        matches.add(match);
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
        return matches;
    }

    protected void onReleaseResources(ArrayList<Match> data) {
        // do nothing
    }
}
