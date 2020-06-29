package com.anber.multimediaexample.utils;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class KitLiveRoomQueryLiveListResponse {

    @SerializedName("live_stream_info_array")
    private List<LiveStreamInfo> liveStreamInfos;

    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<LiveStreamInfo> getLiveStreamInfos() {
        return liveStreamInfos;
    }

    public void setLiveStreamInfos(List<LiveStreamInfo> liveStreamInfos) {
        this.liveStreamInfos = liveStreamInfos;
    }

    public class LiveStreamInfo {

        /**
         * 主播id
         */
        private String userId;

        /**
         * 主播用户名
         */
        private String userName;

        /**
         * 房间ID
         */
        private String roomId;

        /**
         * 在线人数
         */
        @SerializedName("onlineCount")
        private int onLineCount;

        /**
         * 播放地址
         */
        private List<LiveStreamUrl> live_stream_url_array;

        /**
         * 主播状态
         */
        private int status;

        /**
         * 观众userId和连麦流ID
         */
        private Map<String, String> lianMaiMap;


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public int getOnLineCount() {
            return onLineCount;
        }

        public List<LiveStreamUrl> getUrl() {
            return live_stream_url_array;
        }

        public void setUrl(List<LiveStreamUrl> url) {
            this.live_stream_url_array = url;
        }

        public void setOnLineCount(int onLineCount) {
            this.onLineCount = onLineCount;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Map<String, String> getLianMaiMap() {
            return lianMaiMap;
        }

        public void setLianMaiMap(Map<String, String> lianMaiMap) {
            this.lianMaiMap = lianMaiMap;
        }
    }
}
