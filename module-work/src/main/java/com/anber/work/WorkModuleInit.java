package com.anber.work;

import android.app.Application;

import com.goldze.base.base.IModuleInit;
import com.goldze.base.utils.KLog;

/**
 * Created by goldze on 2018/6/21 0021.
 */

public class WorkModuleInit implements IModuleInit {
    @Override
    public boolean onInitAhead(Application application) {
        KLog.e("工作模块初始化 -- onInitAhead");
        return false;
    }

    @Override
    public boolean onInitLow(Application application) {
        KLog.e("工作模块初始化 -- onInitLow");
        return false;
    }
}
