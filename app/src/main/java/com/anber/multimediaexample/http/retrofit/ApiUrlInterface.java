package com.anber.multimediaexample.http.retrofit;

public class ApiUrlInterface {

    //广州环境
//    public static String baseUrl = "http://videokit.hwcloudlive.com:32517";
////    "http://49.4.5.10:32517";
//
//    //获取登录验证码
//    public static String urlGetCode = baseUrl + "/api/user/v1.0/login/msg";
////    "http://49.4.5.10:32517/api/user/v1.0/login_code";
//
//    //验证码登录
//    public static String urlCodeLogin = baseUrl + "/api/user/v1.0/login";
////    "http://49.4.5.10:32517/api/user/v1.0/app/login";
//
//    //获取注册验证码
//    public static String urlSubmitGetCode = baseUrl + "/api/user/v1.0/registered/msg";
////    "http://49.4.5.10:32517/api/user/v1.0/app/registered_code"
//
//    //注册
//    public static String urlSubmit = baseUrl + "/api/user/v1.0/registered";
////    "http://49.4.5.10:32517/api/user/v1.0/app/registered";
//
//    //查询短视频列表
//    public final static String queryVodList = "/api/query/v1.0/recommend";
//
//    //查询直播列表
//    public final static String queryLiveList = "/api/query/v1.0/live/stream";
//
//    //查询作品列表
//    public final static String queryPublishList = "/api/query/v1.0/video";
//
//    //查询喜欢的视频列表
//    public final static String queryLikeList = "/api/query/v1.0/like/mylike";
////    "/api/short_video/v1.0/app/liked_assets"
//
//    //登录直播房间
//    public final static String loginLiveRoomApi = "ws://49.4.82.100:80/api//websocket/live/";
//
//    //获取ak、sk、securityToken环境
//    public static String getCredentialBaseUrl = "http://139.9.73.252:32517";
//
//    //获取临时ak、sk、securityToken
//    public final static String getCredential = "/api/vod/v1.0/app/credential";
//
//    //上传视频
//    public final static String addUrlToService = "/api/query/v1.0/test/structure";

    //-----------------------------------------------------------------------------------------------------//

    public static String domainName = "fastvideo.hwcloudlive.com";
//    public static String domainName = "139.9.188.30";

    //北京环境
    public static String baseUrl = "http://" + domainName + ":32517";
//    public static String baseUrl = "http://49.4.5.10:32517";

    //获取登录验证码
    public static String urlGetCode = baseUrl + "/api/user/v1.0/login_code";

    //验证码登录
    public static String urlCodeLogin = baseUrl + "/api/user/v1.0/app/login";

    //获取注册验证码
    public static String urlSubmitGetCode = baseUrl + "/api/user/v1.0/app/registered_code";

    //注册
    public static String urlSubmit = baseUrl + "/api/user/v1.0/app/registered";

    //查询短视频列表
    public final static String queryVodList = "/api/short_video/v1.0/app/assets";

    //点赞短视频
    public final static String doLike = "/api/like/v1.0/app/like?";

    //取消点赞短视频
    public final static String deleteLike = "/api/like/v1.0/app/like?";

    //取消点赞短视频
    public final static String deleteVideo = "/api/short_video/v1.0/app/asset?";

    //关注用户
    public final static String attention = "/api/user/v1.0/app/attention";

    //查看关注关系
    public final static String isFollowed = "/api/user/v1.0/app/attention_status?";

    //查询是否已经点赞短视频
    public final static String isLike = "/api/like/v1.0/app/status?";

    //查询评论
    public final static String UrlQueryComment = "/api/comment/v1.0/app/comments?";

    //发表评论
    public final static String UrlAddComment = "/api/comment/v1.0/app/comment?";

    //查询直播列表
    public final static String queryLiveList = "/api/video_live/v1.0/app/live_streams";

    //查询作品列表
    public final static String queryPublishList = "/api/short_video/v1.0/app/assets";

    //查询喜欢的视频列表
    public final static String queryLikeList = "/api/short_video/v1.0/app/liked_assets";

    //快视频登录直播房间
//    public final static String loginLiveRoomApi = "ws://"+domainName+":30790/websocket/live/";
//    public final static String loginLiveRoomApi = "ws://49.4.5.10:30790/websocket/live/";
    public final static String loginLiveRoomApi = "ws://"+domainName+":30791/websocket/live/";

    //工具包登录直播房间
    public final static String loginLiveRoomApiForKit = "ws://"+domainName+":30790/websocket/live/";

    //查询房间信息
    public final static String queryLiveRoomInformation = "/api/video_live/v1.0/app/live_streams?";

    //上传视频
    public final static String addUrlToService = "/api/query/v1.0/test/structure";

    //查询用户资料
    public final static String queryUserInformation = "/api/user/v1.0/app/info?";

    //获取ak、sk、securityToken环境
    public static String getCredentialBaseUrl = "http://"+domainName+":32517";

    //获取临时ak、sk、securityToken
    public final static String getCredential = "/api/short_video/v1.0/app/credential";

    //用户登出
    public final static String logout = "/api/user/v1.0/app/logout";

//__________________________________________________________________________________________________________________________

}
