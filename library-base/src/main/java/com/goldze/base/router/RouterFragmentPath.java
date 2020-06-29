package com.goldze.base.router;

/**
 * 用于组件开发中，ARouter多Fragment跳转的统一路径注册
 * 在这里注册添加路由路径，需要清楚的写好注释，标明功能界面
 * Created by anber on 2019/6/21
 */

public class RouterFragmentPath {
    /**
     * 首页组件
     */
    public static class Home {
        private static final String HOME = "/home";
        /*首页*/
        public static final String PAGER_HOME = HOME + "/Home";
    }

    /**
     * 体验组件
     */
    public static class Work {
        private static final String WORK = "/work";
        /*体验*/
        public static final String PAGER_WORK = WORK + "/Work";
    }

    /**
     * 反馈组件
     */
    public static class Msg {
        private static final String MSG = "/msg";
        /*反馈*/
        public static final String PAGER_MSG = MSG + "/msg/Msg";
    }

    /**
     * 用户组件
     */
    public static class User {
        private static final String USER = "/user";
        /*我的*/
        public static final String PAGER_ME = USER + "/Me";
    }

    /**
     * 推流组件
     */
    public static class Push {
        private static final String PUSH = "/push";
        /*直播推流*/
        public static final String PAGER_PUSH = PUSH + "/Push";
    }

    /**
     * 推流组件
     */
    public static class Upload {
        private static final String UPLOAD = "/upload";
        /*我的*/
        public static final String PAGER_UPLOAD = UPLOAD + "/Upload";
    }
}
