package com.rino.ekky.footballforum;

import org.json.JSONObject;

public class Club {
    private String logoUrl;
    private String name;
    private String id;

    public Club(String id, String name, String logoUrl) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
    }

    public Club(JSONObject object) {
        try {
            this.id = String.valueOf(object.getInt("id"));
            this.name = object.getString("name");
            this.logoUrl = object.getString("crestUrl");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
