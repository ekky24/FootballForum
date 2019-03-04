package com.rino.ekky.footballforum;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Match implements Parcelable {
    private String id;
    private String competition;
    private String matchday;
    private String stage;
    private String group;
    private long dateInMillis;
    private String utcDate;
    private String status;
    private String homeTeam;
    private String awayTeam;
    private String scoreHomeTeamHalf;
    private String scoreAwayTeamHalf;
    private String scoreHomeTeamFull;
    private String scoreAwayTeamFull;
    private String winner;
    private String logoHomeUrl;
    private String logoAwayUrl;
    private String homeTeamName;
    private String awayTeamName;
    private String month[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public Match(JSONObject object, String type) {
        try {
            this.id = String.valueOf(object.getInt("id"));
            this.competition = object.getJSONObject("competition").getString("name");
            this.matchday = String.valueOf(object.getInt("matchday"));
            this.stage = object.getString("stage");
            this.group = object.getString("group");
            this.utcDate = object.getString("utcDate");
            this.status = type;
            this.homeTeam = String.valueOf(object.getJSONObject("homeTeam").getInt("id"));
            this.awayTeam = String.valueOf(object.getJSONObject("awayTeam").getInt("id"));
            this.scoreHomeTeamHalf = String.valueOf(object.getJSONObject("score").getJSONObject("halfTime").getInt("homeTeam"));
            this.scoreAwayTeamHalf = String.valueOf(object.getJSONObject("score").getJSONObject("halfTime").getInt("awayTeam"));
            this.scoreHomeTeamFull = String.valueOf(object.getJSONObject("score").getJSONObject("fullTime").getInt("homeTeam"));
            this.scoreAwayTeamFull = String.valueOf(object.getJSONObject("score").getJSONObject("fullTime").getInt("awayTeam"));
            this.winner = object.getJSONObject("score").getString("winner");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            this.dateInMillis = sdf.parse(getUtcDate()).getTime();

            Log.e("cek_val", dateInMillis+"");

            /*Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);
            this.dateInMillis = calendar.get(Calendar.DAY_OF_MONTH) + "-" + month[calendar.get(Calendar.MONTH)] +
                    "-" + calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);*/
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getMatchday() {
        return matchday;
    }

    public void setMatchday(String matchday) {
        this.matchday = matchday;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getScoreHomeTeamFull() {
        return scoreHomeTeamFull;
    }

    public void setScoreHomeTeamFull(String scoreHomeTeamFull) {
        this.scoreHomeTeamFull = scoreHomeTeamFull;
    }

    public String getScoreAwayTeamFull() {
        return scoreAwayTeamFull;
    }

    public void setScoreAwayTeamFull(String scoreAwayTeamFull) {
        this.scoreAwayTeamFull = scoreAwayTeamFull;
    }

    public String getScoreHomeTeamHalf() {
        return scoreHomeTeamHalf;
    }

    public void setScoreHomeTeamHalf(String scoreHomeTeamHalf) {
        this.scoreHomeTeamHalf = scoreHomeTeamHalf;
    }

    public String getScoreAwayTeamHalf() {
        return scoreAwayTeamHalf;
    }

    public void setScoreAwayTeamHalf(String scoreAwayTeamHalf) {
        this.scoreAwayTeamHalf = scoreAwayTeamHalf;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getLogoHomeUrl() {
        return logoHomeUrl;
    }

    public void setLogoHomeUrl(String logoHomeUrl) {
        this.logoHomeUrl = logoHomeUrl;
    }

    public String getLogoAwayUrl() {
        return logoAwayUrl;
    }

    public void setLogoAwayUrl(String logoAwayUrl) {
        this.logoAwayUrl = logoAwayUrl;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getUtcDate() {
        return utcDate;
    }

    public void setUtcDate(String utcDate) {
        this.utcDate = utcDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.competition);
        dest.writeString(this.matchday);
        dest.writeString(this.stage);
        dest.writeString(this.group);
        dest.writeLong(this.dateInMillis);
        dest.writeString(this.utcDate);
        dest.writeString(this.status);
        dest.writeString(this.homeTeam);
        dest.writeString(this.awayTeam);
        dest.writeString(this.scoreHomeTeamHalf);
        dest.writeString(this.scoreAwayTeamHalf);
        dest.writeString(this.scoreHomeTeamFull);
        dest.writeString(this.scoreAwayTeamFull);
        dest.writeString(this.winner);
        dest.writeString(this.logoHomeUrl);
        dest.writeString(this.logoAwayUrl);
        dest.writeString(this.homeTeamName);
        dest.writeString(this.awayTeamName);
        dest.writeStringArray(this.month);
    }

    protected Match(Parcel in) {
        this.id = in.readString();
        this.competition = in.readString();
        this.matchday = in.readString();
        this.stage = in.readString();
        this.group = in.readString();
        this.dateInMillis = in.readLong();
        this.utcDate = in.readString();
        this.status = in.readString();
        this.homeTeam = in.readString();
        this.awayTeam = in.readString();
        this.scoreHomeTeamHalf = in.readString();
        this.scoreAwayTeamHalf = in.readString();
        this.scoreHomeTeamFull = in.readString();
        this.scoreAwayTeamFull = in.readString();
        this.winner = in.readString();
        this.logoHomeUrl = in.readString();
        this.logoAwayUrl = in.readString();
        this.homeTeamName = in.readString();
        this.awayTeamName = in.readString();
        this.month = in.createStringArray();
    }

    public static final Parcelable.Creator<Match> CREATOR = new Parcelable.Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel source) {
            return new Match(source);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };
}
