package com.thunder.nytsearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anlinsquall on 17/3/17.
 */

public class Article {
    String webUrl;
    String headline;
    String thumbnail;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Article(String webUrl, String headline, String thumbnail) {
        this.webUrl = webUrl;
        this.headline = headline;
        this.thumbnail = thumbnail;
    }

    public static ArrayList<Article> fromJsonArray(JSONArray jsonArray){
        ArrayList<Article> results = new ArrayList<Article>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String webUrl = jsonObject.getString("web_url");
                String headline = jsonObject.getJSONObject("headline").getString("main");
                String thumnail = "";
                JSONArray multimedia = jsonObject.getJSONArray("multimedia");
                if (multimedia.length() > 0){
                    JSONObject multimediaJson = multimedia.getJSONObject(0);
                    thumnail = "http://www.nytimes.com/" + multimediaJson.getString("url");
                }
                results.add(new Article(webUrl, headline, thumnail));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
