package com.rino.ekky.footballforum;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MatchReceiver extends BroadcastReceiver {
    SharedPreferences preferences;
    ArrayList<Match> matches;
    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = context.getSharedPreferences("football_forum", Context.MODE_PRIVATE);
        matches = new ArrayList<>();

        MatchTodayAsync async = new MatchTodayAsync(context);
        async.execute();
    }

    public void setReleaseTodayAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MatchReceiver.class);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        int requestCode = 100;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private class MatchTodayAsync extends AsyncTask<Void, Void, ArrayList<Match>> {
        Context context;

        public MatchTodayAsync(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Match> doInBackground(Void... voids) {
            String url = "http://api.football-data.org/v2/teams/" + preferences.getString("club_id", "") + "/matches?status=SCHEDULED";
            SyncHttpClient client = new SyncHttpClient();
            final ArrayList<Match> movieItemses = new ArrayList<>();

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
                            Match match = new Match(matchObject, "SCHEDULED");
                            movieItemses.add(match);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
            return movieItemses;
        }

        @Override
        protected void onPostExecute(ArrayList<Match> movieItems) {
            super.onPostExecute(movieItems);
            int i = 1;
            boolean isMatchAvailable = false;
            Calendar calendarNow = Calendar.getInstance();
            calendarNow.set(Calendar.HOUR_OF_DAY, 0);
            calendarNow.set(Calendar.MINUTE, 0);
            calendarNow.set(Calendar.SECOND, 0);

            String tglNow = calendarNow.get(Calendar.DAY_OF_MONTH) + calendarNow.get(Calendar.MONTH) + calendarNow.get(Calendar.YEAR) + "";

            for (Match movie : movieItems) {
                Calendar calendarMatch = Calendar.getInstance();
                calendarMatch.setTimeInMillis(movie.getDateInMillis());
                String tglMatch = calendarMatch.get(Calendar.DAY_OF_MONTH) + calendarMatch.get(Calendar.MONTH) + calendarMatch.get(Calendar.YEAR) + "";
                Log.e("notif_error", tglNow + ": " + tglMatch);

                if (tglNow.equals(tglMatch)) {
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.favicon)
                            .setContentTitle("Jangan Lewatkan!")
                            .setContentText("Tim kesayanganmu main hari ini.")
                            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setAutoCancel(true);
                    manager.notify(i, builder.build());
                    i++;
                    isMatchAvailable = true;
                }
            }

            if (!isMatchAvailable) {
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.favicon)
                            .setContentTitle("Sayang Sekali!")
                            .setContentText("Tidak ada match hari ini.")
                            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setAutoCancel(true);
                    manager.notify(i, builder.build());
                    i++;
            }
            Log.e("notif_error", "post exec");
        }
    }
}
