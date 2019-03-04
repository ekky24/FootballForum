package com.rino.ekky.footballforum;


import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailJadwalFragment extends Fragment {
    TextView txtHomeName, txtAwayName, txtHomeScore, txtAwayScore, txtCompetition, txtStage, txtMatchday, txtGroup, txtDate, txtStatus;
    ImageView imgHomeTeam, imgAwayTeam;
    private RequestBuilder<PictureDrawable> requestBuilder;
    Match match;
    SharedPreferences preferences;
    CountDownTimer timer;
    String type;
    int counter = 0;
    boolean isRunning = false;

    public DetailJadwalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_jadwal, container, false);

        match = getActivity().getIntent().getParcelableExtra("match");
        type = getActivity().getIntent().getStringExtra("type");
        preferences = getActivity().getSharedPreferences("football_forum", Context.MODE_PRIVATE);

        txtHomeName = view.findViewById(R.id.txt_home_name);
        txtAwayName = view.findViewById(R.id.txt_away_name);
        txtHomeScore = view.findViewById(R.id.txt_score_home);
        txtAwayScore = view.findViewById(R.id.txt_score_away);
        txtCompetition = view.findViewById(R.id.txt_competition);
        txtStage = view.findViewById(R.id.txt_stage);
        txtMatchday = view.findViewById(R.id.txt_matchday);
        txtGroup = view.findViewById(R.id.txt_group);
        txtDate = view.findViewById(R.id.txt_date);
        txtStatus = view.findViewById(R.id.txt_status);
        imgHomeTeam = view.findViewById(R.id.img_club_home);
        imgAwayTeam = view.findViewById(R.id.img_club_away);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(match.getDateInMillis());

        int jam = calendar.get(Calendar.HOUR_OF_DAY);
        int menit = calendar.get(Calendar.MINUTE);
        String jamFinal = "";
        String menitFinal = "";

        if (jam < 10) {
            jamFinal = "0" + jam;
        }
        else {
            jamFinal = "" + jam;
        }

        if (menit < 10) {
            menitFinal = "0" + menit;
        }
        else {
            menitFinal = "" + menit;
        }

        String tempDate = calendar.get(Calendar.DAY_OF_MONTH) + " " + new SimpleDateFormat("MMM").format(calendar.getTime()) +
                " " + calendar.get(Calendar.YEAR) + " pukul " + jamFinal + ":" + menitFinal;

        txtHomeName.setText(match.getHomeTeamName());
        txtAwayName.setText(match.getAwayTeamName());
        txtHomeScore.setText(match.getScoreHomeTeamFull());
        txtAwayScore.setText(match.getScoreAwayTeamFull());
        txtCompetition.setText(match.getCompetition());
        txtStage.setText(match.getStage());
        txtMatchday.setText(match.getMatchday());
        txtGroup.setText(match.getGroup());
        txtDate.setText(tempDate);
        txtStatus.setText(match.getStatus());

        String tempUrlHome = match.getLogoHomeUrl();
        String substrTempHome = "";
        if (tempUrlHome != null) {
            substrTempHome = tempUrlHome.substring(tempUrlHome.length() - 3);
        }

        String tempUrlAway = match.getLogoAwayUrl();
        String substrTempAway = "";
        if (tempUrlAway != null) {
            substrTempAway = tempUrlAway.substring(tempUrlAway.length()-3);
        }

        if (substrTempHome.equals("svg")) {
            //Log.e("svg_tes", tempUrl);
            requestBuilder = GlideApp.with(this)
                    .as(PictureDrawable.class)
                    .transition(withCrossFade())
                    .listener(new SvgSoftwareLayerSetter());

            Uri uri = Uri.parse(tempUrlHome);
            requestBuilder
                    .load(uri)
                    .into(imgHomeTeam);
        } else {
            Glide.with(this)
                    .load(tempUrlHome)
                    .into(imgHomeTeam);
        }

        if (substrTempAway.equals("svg")) {
            //Log.e("svg_tes", tempUrl);
            requestBuilder = GlideApp.with(this)
                    .as(PictureDrawable.class)
                    .transition(withCrossFade())
                    .listener(new SvgSoftwareLayerSetter());

            Uri uri = Uri.parse(tempUrlAway);
            requestBuilder
                    .load(uri)
                    .into(imgAwayTeam);
        }
        else {
            Glide.with(this)
                    .load(tempUrlAway)
                    .into(imgAwayTeam);
        }

        return view;
    }

    private class ScoreAsync extends AsyncTask<Void, Void, ArrayList<Match>> {
        Context context;

        public ScoreAsync(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Match> doInBackground(Void... voids) {
            String url = "http://api.football-data.org/v2/teams/" + preferences.getString("club_id", "") + "/matches?status=FINISHED";
            SyncHttpClient client = new SyncHttpClient();
            final ArrayList<Match> matchFinal = new ArrayList<>();

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

                        for (int j=0; j<list.length(); j++) {
                            JSONObject matchObject = list.getJSONObject(j);
                            Match matchItem = new Match(matchObject, type);
                            Log.e("api_error", "item: " + matchItem.getId() + " actual: " + match.getId() + " id: " + preferences.getString("club_id", ""));

                            if (matchItem.getId().equals(match.getId())) {
                                matchFinal.add(matchItem);
                                break;
                            }
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
            return matchFinal;
        }

        @Override
        protected void onPostExecute(ArrayList<Match> movieItems) {
            counter++;
            super.onPostExecute(movieItems);
            boolean updating = false;

            try {
                Toast.makeText(getActivity(), movieItems.get(0).getScoreHomeTeamFull() + "-" + movieItems.get(0).getScoreAwayTeamFull(), Toast.LENGTH_SHORT).show();

                if (!txtHomeScore.getText().toString().equals(movieItems.get(0).getScoreHomeTeamFull())) {
                    updating = true;
                }
                if (!txtAwayScore.getText().toString().equals(movieItems.get(0).getScoreAwayTeamFull())) {
                    updating = true;
                }
                if (counter % 2 == 0) {
                    updating = true;
                }

                if (updating) {
                    txtHomeScore.setText(movieItems.get(0).getScoreHomeTeamFull());
                    txtAwayScore.setText(movieItems.get(0).getScoreAwayTeamFull());

                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.favicon)
                            .setContentTitle("Update Skor")
                            .setContentText(txtHomeName.getText().toString() + " " + movieItems.get(0).getScoreHomeTeamFull() + " - " + movieItems.get(0).getScoreAwayTeamFull() + " " + txtAwayName.getText().toString())
                            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setAutoCancel(true);
                    manager.notify(0, builder.build());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //Log.e("notif_error", "post exec");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRunning) {
            timer.cancel();
        }

        if(type.equals("FINISHED")) {
            timer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    isRunning = true;
                }

                @Override
                public void onFinish() {
                    ScoreAsync async = new ScoreAsync(getActivity());
                    async.execute();
                    timer.start();
                    isRunning = false;
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(type.equals("FINISHED")) {
            timer.cancel();
            isRunning = false;
        }
    }
}
