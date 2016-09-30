package com.deng.netmonitor;

import android.app.Application;

import com.deng.netmonitor.netStateLib.NetStateReceiver;

/**
 * Created by deng on 2016/9/30.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /*开启网络广播监听*/
        NetStateReceiver.registerNetworkStateReceiver(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        NetStateReceiver.unRegisterNetworkStateReceiver(this);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
