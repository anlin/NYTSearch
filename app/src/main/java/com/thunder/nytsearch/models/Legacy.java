
package com.thunder.nytsearch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Legacy {

    @SerializedName("wide")
    @Expose
    private String wide;
    @SerializedName("wideheight")
    @Expose
    private String wideheight;
    @SerializedName("widewidth")
    @Expose
    private String widewidth;
    @SerializedName("xlargewidth")
    @Expose
    private String xlargewidth;
    @SerializedName("xlarge")
    @Expose
    private String xlarge;
    @SerializedName("xlargeheight")
    @Expose
    private String xlargeheight;
    @SerializedName("thumbnailheight")
    @Expose
    private String thumbnailheight;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("thumbnailwidth")
    @Expose
    private String thumbnailwidth;

    public String getWide() {
        return wide;
    }

    public void setWide(String wide) {
        this.wide = wide;
    }

    public String getWideheight() {
        return wideheight;
    }

    public void setWideheight(String wideheight) {
        this.wideheight = wideheight;
    }

    public String getWidewidth() {
        return widewidth;
    }

    public void setWidewidth(String widewidth) {
        this.widewidth = widewidth;
    }

    public String getXlargewidth() {
        return xlargewidth;
    }

    public void setXlargewidth(String xlargewidth) {
        this.xlargewidth = xlargewidth;
    }

    public String getXlarge() {
        return xlarge;
    }

    public void setXlarge(String xlarge) {
        this.xlarge = xlarge;
    }

    public String getXlargeheight() {
        return xlargeheight;
    }

    public void setXlargeheight(String xlargeheight) {
        this.xlargeheight = xlargeheight;
    }

    public String getThumbnailheight() {
        return thumbnailheight;
    }

    public void setThumbnailheight(String thumbnailheight) {
        this.thumbnailheight = thumbnailheight;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailwidth() {
        return thumbnailwidth;
    }

    public void setThumbnailwidth(String thumbnailwidth) {
        this.thumbnailwidth = thumbnailwidth;
    }

}
