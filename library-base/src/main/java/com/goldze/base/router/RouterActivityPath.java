package com.goldze.base.router;

/**
 * 用于组件开发中，ARouter单Activity跳转的统一路径注册
 * 在这里注册添加路由路径，需要清楚的写好注释，标明功能界面
 * Created by anber on 2019/6/21
 */

public class RouterActivityPath {
    /**
     * 主业务组件
     */
    public static class Main {
        private static final String MAIN = "/main";
        /*主业务界面*/
        public static final String PAGER_MAIN = MAIN + "/Main";
    }

    /**
     * 身份验证组件
     */
    public static class Sign {
        private static final String SIGN = "/sign";
        /*登录界面*/
        public static final String PAGER_LOGIN = SIGN + "/Login";

        /*注册界面*/
        public static final String PAGER_REGISTER = SIGN + "/Register";
    }

    /**
     * 用户组件
     */
    public static class User {
        private static final String USER = "/user";
        /*用户详情*/
        public static final String PAGER_USERDETAIL = USER + "/UserDetail";
        /*设置*/
        public static final String PAGER_SETTING = USER + "/Setting";
    }

    /**
     * 更新版本组件
     */
    public static class Update {
        private static final String Update = "/update";
        /*版本详情*/
        public static final String PAGER_VERSIONDETAIL = Update + "/VersionDetail";
    }

    /**
     * 播放器组件
     * +
     */
    public static class Weplayer {
        private static final String Weplayer = "/weplayer";
        /*短视频播放界面*/
        public static final String PAGER_WEPLAYER = Weplayer + "/Weplayer";
        /*直播播放界面*/
        public static final String PAGER_LIVEPLAY = Weplayer + "/LivePlay";
        /*点播播放界面*/
        public static final String PAGER_PLAYER = Weplayer + "/Player";
    }

    /**
     * 短视频组件
     * +
     */
    public static class Shortvideo {
        private static final String Shortvideo = "/shortvideo";
        /*抖音特效界面*/
        public static final String PAGER_SHORTVIDEO = Shortvideo + "/Shortvideo";
        /*视频拍摄界面*/
        public static final String PAGER_KIT_SHORTVIDEO = Shortvideo + "/KitShortVideo";
        /*短视频主界面*/
        public static final String PAGER_KIT_MAIN = Shortvideo + "/ShortVideoMain";
        /*视频编辑界面*/
        public static final String PAGER_SHORTVIDEO_EDIT = Shortvideo + "/ShortVideoEdit";
        /*粒子特效界面*/
        public static final String PAGER_SHORTVIDEO_PARTICLE = Shortvideo + "/Particle";
        /*虚拟背景抠像界面*/
        public static final String PAGER_SHORTVIDEO_SCENE = Shortvideo + "/Scene";
    }

    /**
     * 上传频组件
     * +
     */
    public static class DrawBmp {
        private static final String DrawBmp = "/drawbmp";
        /*播放界面*/
        public static final String PAGER_DRAWBMP = DrawBmp + "/DrawBmp";
    }

    /**
     * 上传频组件
     * +
     */
    public static class Upload {
        private static final String Upload = "/upload";
        /*播放界面*/
        public static final String PAGER_UPLOAD = Upload + "/Upload";
    }

    /**
     * webrtc频组件
     * +
     */
    public static class WebRTC {
        private static final String Webrtc = "/webrtc";
        /*播放界面*/
        public static final String PAGER_WEBRTC = Webrtc + "/Webrtc";
    }

    /**
     * 推流组件
     * +
     */
    public static class Push {
        private static final String Push = "/push";
        /*主播推流界面*/
        public static final String PAGER_PUSH = Push + "/Push";
        /*观众推流界面*/
        public static final String PAGER_AUDIENCE_PUSH = Push + "/AudiencePush";
        /*录屏推流界面*/
        public static final String PAGER_RECORD_PUSH = Push + "/RecordPush";
        /*直播列表界面*/
        public static final String PAGER_KIT = Push + "/Kit";
    }

    /**
     * Kit直播间组件
     */
    public static class LiveRoom {
        private static final String LiveRoom = "/liveroom";

        /*直播列表界面*/
        public static final String PAGER_LIVEROOM = LiveRoom + "/LiveRoom";

        /*注册界面*/
//        public static final String PAGER_REGISTER = SIGN + "/Register";
    }
}
