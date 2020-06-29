package com.anber.multimediaexample.http.response;

import java.io.Serializable;

public class LiveStreamUrl implements Serializable {
    /**
     * url
     */
    private String url;
    /**
     * ratio
     */
    private String ratio;
    /**
     * desc
     */
    private String desc;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getRatio() {
        return ratio;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
