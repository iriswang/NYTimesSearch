package com.iris.nytimessearch.models;


import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

/**
 * Created by iris on 7/30/16.
 */

@org.parceler.Parcel
public class Article {

    private static final String IMAGE_PREFIX_URL = "http://www.nytimes.com/";
    String webUrl;
    String headline;
    String thumbNail;

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public String getWebUrl() {
        return webUrl;
    }


    public Article() {

    }

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = IMAGE_PREFIX_URL + multimediaJson.get("url");
            } else {
                this.thumbNail = "";
            }
        } catch (JSONException e) {

        }
    }

    public static ArrayList<Article> fromJsonArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

}
