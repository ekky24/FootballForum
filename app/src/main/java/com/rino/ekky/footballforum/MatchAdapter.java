package com.rino.ekky.footballforum;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchHolder> {
    private ArrayList<Match> listData;
    private Context context;
    private RequestBuilder<PictureDrawable> requestBuilder;

    public MatchAdapter(Context context) {
        this.context = context;
    }

    public void setListData(ArrayList<Match> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public MatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jadwal_item, parent, false);
        return new MatchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchHolder holder, int position) {
        final Match match = listData.get(position);
        String tempComp = match.getCompetition();

        if (match.getCompetition().equalsIgnoreCase("UEFA CHAMPIONS LEAGUE")) {
            tempComp = "UCL";
        }

        if (match.getDateInMillis() == 0) {
            Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(match.getUtcDate().substring(0, 4)));
                calendar.set(Calendar.MONTH, Integer.parseInt(match.getUtcDate().substring(5, 7))-1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(match.getUtcDate().substring(8, 10)));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(match.getUtcDate().substring(11, 13)));
                calendar.set(Calendar.MINUTE, Integer.parseInt(match.getUtcDate().substring(14, 16)));
                calendar.set(Calendar.SECOND, 0);
                calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            match.setDateInMillis(calendar.getTimeInMillis());
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(match.getDateInMillis());
        String tempDate = calendar.get(Calendar.DAY_OF_MONTH) + " " + new SimpleDateFormat("MMM").format(calendar.getTime()) +
                " " + calendar.get(Calendar.YEAR);

        holder.txtMatchStatus.setText(match.getStatus());
        holder.txtMatchEvent.setText(tempComp);
        holder.txtMatchEventDetail.setText("Matchday " + match.getMatchday());
        holder.txtMatchDate.setText(tempDate);
        holder.txtScoreHome.setText(match.getScoreHomeTeamFull());
        holder.txtScoreAway.setText(match.getScoreAwayTeamFull());

        if (holder.txtMatchStatus.getText().toString().equals("SCHEDULED")) {
            holder.txtVersus.setText("VS");
        } else {
            holder.txtVersus.setText("-");
        }

        holder.imgHomeTeam.setImageDrawable(null);
        holder.imgAwayTeam.setImageDrawable(null);

        holder.imgButtonForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ForumActivity.class);
                intent.putExtra("match", match);
                context.startActivity(intent);
            }
        });

        new LoadClubAsync(holder).execute(match);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class MatchHolder extends RecyclerView.ViewHolder {
        ImageView imgHomeTeam, imgAwayTeam;
        TextView txtMatchStatus, txtMatchDate, txtMatchEvent, txtVersus,
                txtScoreHome, txtScoreAway, txtMatchEventDetail;
        ImageButton imgButtonForum;

        public MatchHolder(View itemView) {
            super(itemView);
            imgHomeTeam = itemView.findViewById(R.id.img_club_home);
            imgAwayTeam = itemView.findViewById(R.id.img_club_away);
            txtMatchStatus = itemView.findViewById(R.id.txt_match_status);
            txtMatchDate = itemView.findViewById(R.id.txt_match_date);
            txtMatchEvent = itemView.findViewById(R.id.txt_match_event);
            txtScoreHome = itemView.findViewById(R.id.txt_score_home);
            txtScoreAway = itemView.findViewById(R.id.txt_score_away);
            txtMatchEventDetail = itemView.findViewById(R.id.txt_match_event_detail);
            txtVersus = itemView.findViewById(R.id.txt_versus);
            imgButtonForum = itemView.findViewById(R.id.img_button_forum);
        }
    }

    private class LoadClubAsync extends AsyncTask<Match, Void, Match> {
        private MatchHolder holder;

        public LoadClubAsync(MatchHolder holder) {
            this.holder = holder;
        }

        @Override
        protected Match doInBackground(final Match... matches) {
            final ArrayList<String> crestUrl = new ArrayList<>();
            SyncHttpClient client = new SyncHttpClient();

            String url = "http://api.football-data.org/v2/teams/" + matches[0].getHomeTeam();
            String url2 = "http://api.football-data.org/v2/teams/" + matches[0].getAwayTeam();
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
                        matches[0].setLogoHomeUrl(response.getString("crestUrl"));
                        matches[0].setHomeTeamName(response.getString("shortName"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("api_error " + statusCode, error.toString());
                }
            });
            client.get(url2, new AsyncHttpResponseHandler() {
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
                        matches[0].setLogoAwayUrl(response.getString("crestUrl"));
                        matches[0].setAwayTeamName(response.getString("shortName"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("api_error " + statusCode, error.toString());
                }
            });
            return matches[0];
        }

        @Override
        protected void onPostExecute(Match match) {
            super.onPostExecute(match);

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
                requestBuilder = GlideApp.with(context)
                        .as(PictureDrawable.class)
                        .transition(withCrossFade())
                        .listener(new SvgSoftwareLayerSetter());

                Uri uri = Uri.parse(tempUrlHome);
                requestBuilder
                        .load(uri)
                        .into(holder.imgHomeTeam);
            } else {
                Glide.with(context)
                        .load(tempUrlHome)
                        .into(holder.imgHomeTeam);
            }

            if (substrTempAway.equals("svg")) {
                //Log.e("svg_tes", tempUrl);
                requestBuilder = GlideApp.with(context)
                        .as(PictureDrawable.class)
                        .transition(withCrossFade())
                        .listener(new SvgSoftwareLayerSetter());

                Uri uri = Uri.parse(tempUrlAway);
                requestBuilder
                        .load(uri)
                        .into(holder.imgAwayTeam);
            }
            else {
                Glide.with(context)
                        .load(tempUrlAway)
                        .into(holder.imgAwayTeam);
            }

            //Toast.makeText(context, match.getLogoHomeUrl(), Toast.LENGTH_SHORT).show();
            //Log.e("api_error", match.getLogoHomeUrl());
        }
    }
}
