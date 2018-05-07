package com.deng.netmonitor.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.caption.netmonitorlibrary.netstatelib.NetChangeObserver;
import com.caption.netmonitorlibrary.netstatelib.NetStateReceiver;
import com.caption.netmonitorlibrary.netstatelib.NetUtils;

/**
 * Created by deng on 2016/9/30.
 */

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 网络观察者
     */
    protected NetChangeObserver mNetChangeObserver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 网络改变的一个回掉类
        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetUtils.NetType type) {
                onNetworkConnected(type);
            }

            @Override
            public void onNetDisConnect() {
                onNetworkDisConnected();
            }
        };

        //开启广播去监听 网络 改变事件
        NetStateReceiver.registerObserver(mNetChangeObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 网络连接状态
     *
     * @param type 网络状态
     */
    protected abstract void onNetworkConnected(NetUtils.NetType type);

    /**
     * 网络断开的时候调用
     */
    protected abstract void onNetworkDisConnected();

}
