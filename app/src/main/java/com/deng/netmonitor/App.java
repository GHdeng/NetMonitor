package com.deng.netmonitor;

import android.app.Application;

import com.caption.netmonitorlibrary.netStateLib.NetStateReceiver;

/**
 * Created by deng on 2016/9/30.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //动态注册网络变化广播
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //实例化IntentFilter对象
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            NetConnectionReceiver netBroadcastReceiver = new NetConnectionReceiver();
            //注册广播接收
            registerReceiver(netBroadcastReceiver, filter);
        }
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
