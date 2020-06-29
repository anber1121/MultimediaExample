package com.anber.multimediaexample;

import com.goldze.base.base.BaseApplication;
import com.goldze.base.config.ModuleLifecycleConfig;

/**
 * Created by anber on 2019/6/21
 */

public class AppApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化组件(靠前)
        ModuleLifecycleConfig.getInstance().initModuleAhead(this);
        //....
        //初始化组件(靠后)
        ModuleLifecycleConfig.getInstance().initModuleLow(this);
    }
}
