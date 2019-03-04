package com.rino.ekky.footballforum;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String id;
    private String email;
    private String username;
    private String club_id;

    public User(String id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public User() {

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClub_id() {
        return club_id;
    }

    public void setClub_id(String club_id) {
        this.club_id = club_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.email);
        dest.writeString(this.username);
        dest.writeString(this.club_id);
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.email = in.readString();
        this.username = in.readString();
        this.club_id = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
