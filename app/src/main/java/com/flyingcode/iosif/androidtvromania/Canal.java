package com.flyingcode.iosif.androidtvromania;

/**
 * Created by iosif on 10/3/15.
 */
public class Canal {
    private int name;
    private int station_code;
    private String url;
    private String img_url;

    public Canal(int name, String url, String img_url) {
        this.name = name;
        this.url = url;
        this.img_url = img_url;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getStation_code() {
        return station_code;
    }

    public void setStation_code(int station_code) {
        this.station_code = station_code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
