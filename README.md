# NetMonitor

# 使用广播监听网络变化

###需求确认

* 监听当前网络的状态和类型
* 类似京东客户端，当网络发生变化时相应更新UI界面

![image](https://raw.githubusercontent.com/GHdeng/NetMonitor/master/NetMonitor2.gif)

github地址：https://github.com/GHdeng/NetMonitor

###制作流程
1. 使用广播监听当前网络的状态。
2. 配合Application周期注册监听，使得每个界面都继续监听
3. 抽出BaseActivity类实现回调

#####1.继承BroadcastReceiver实现onReceive方法来判断当前网络是否连接，然后通过更新NetChangeObserver来实现回调。
加入权限
```java
< uses-permission android:name="android.permission.INTERNET" />
< uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /> 
< uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
```java
/**
 * 使用广播去监听网络
 * Created by deng on 16/9/13.
 */
public class NetStateReceiver extends BroadcastReceiver {

    public final static String CUSTOM_ANDROID_NET_CHANGE_ACTION = "com.zhanyun.api.netstatus.CONNECTIVITY_CHANGE";
    private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private final static String TAG = NetStateReceiver.class.getSimpleName();

    private static boolean isNetAvailable = false;
    private static NetUtils.NetType mNetType;
    private static ArrayList<NetChangeObserver> mNetChangeObservers = new ArrayList<NetChangeObserver>();
    private static BroadcastReceiver mBroadcastReceiver;

    private static BroadcastReceiver getReceiver() {
        if (null == mBroadcastReceiver) {
            synchronized (NetStateReceiver.class) {
                if (null == mBroadcastReceiver) {
                    mBroadcastReceiver = new NetStateReceiver();
                }
            }
        }
        return mBroadcastReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mBroadcastReceiver = NetStateReceiver.this;
        if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION) || intent.getAction().equalsIgnoreCase(CUSTOM_ANDROID_NET_CHANGE_ACTION)) {
            if (!NetUtils.isNetworkAvailable(context)) {
                LogHelper.e(this.getClass(), "<--- network disconnected --->");
                isNetAvailable = false;
            } else {
                LogHelper.e(this.getClass(), "<--- network connected --->");
                isNetAvailable = true;
                mNetType = NetUtils.getAPNType(context);
            }
            notifyObserver();
        }
    }

    /**
     * 注册
     *
     * @param mContext
     */
    public static void registerNetworkStateReceiver(Context mContext) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CUSTOM_ANDROID_NET_CHANGE_ACTION);
        filter.addAction(ANDROID_NET_CHANGE_ACTION);
        mContext.getApplicationContext().registerReceiver(getReceiver(), filter);
    }

    /**
     * 清除
     *
     * @param mContext
     */
    public static void checkNetworkState(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(CUSTOM_ANDROID_NET_CHANGE_ACTION);
        mContext.sendBroadcast(intent);
    }

    /**
     * 反注册
     *
     * @param mContext
     */
    public static void unRegisterNetworkStateReceiver(Context mContext) {
        if (mBroadcastReceiver != null) {
            try {
                mContext.getApplicationContext().unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {

            }
        }

    }

    public static boolean isNetworkAvailable() {
        return isNetAvailable;
    }

    public static NetUtils.NetType getAPNType() {
        return mNetType;
    }

    private void notifyObserver() {
        if (!mNetChangeObservers.isEmpty()) {
            int size = mNetChangeObservers.size();
            for (int i = 0; i < size; i++) {
                NetChangeObserver observer = mNetChangeObservers.get(i);
                if (observer != null) {
                    if (isNetworkAvailable()) {
                        observer.onNetConnected(mNetType);
                    } else {
                        observer.onNetDisConnect();
                    }
                }
            }
        }
    }

    /**
     * 添加网络监听
     *
     * @param observer
     */
    public static void registerObserver(NetChangeObserver observer) {
        if (mNetChangeObservers == null) {
            mNetChangeObservers = new ArrayList<NetChangeObserver>();
        }
        mNetChangeObservers.add(observer);
    }

    /**
     * 移除网络监听
     *
     * @param observer
     */
    public static void removeRegisterObserver(NetChangeObserver observer) {
        if (mNetChangeObservers != null) {
            if (mNetChangeObservers.contains(observer)) {
                mNetChangeObservers.remove(observer);
            }
        }
    }
}
```

#####2.回调接口
``` java
/**
 * 网络改变观察者，观察网络改变后回调的方法
 * Created by deng on 16/9/13.
 */
public interface NetChangeObserver {

    /**
     * 网络连接回调 type为网络类型
     */
     void onNetConnected(NetUtils.NetType type);

    /**
     * 没有网络
     */
     void onNetDisConnect();
}
```

#####3.网络状态工具类
```java
public class NetUtils {

    public static enum NetType {
        WIFI, CMNET, CMWAP, NONE
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    public static NetType getAPNType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NetType.NONE;
        }
        int nType = networkInfo.getType();

        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase(Locale.getDefault()).equals("cmnet")) {
                return NetType.CMNET;
            } else {
                return NetType.CMWAP;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return NetType.WIFI;
        }
        return NetType.NONE;
    }
}
```

#####4.在Application中注册
```java
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        /*开启网络广播监听*/
         NetStateReceiver.registerNetworkStateReceiver(instance);
    }
    
    @Override
    public void onLowMemory() {
        if (instance != null) {
		            NetStateReceiver.unRegisterNetworkStateReceiver(instance);
            android.os.Process.killProcess(android.os.Process.myPid());
            exitApp();
        }
        super.onLowMemory();
    }
```

#####5.为了监听每一个Activity就抽取出来一个抽象类
```
	/**
     * 网络观察者
     */
    protected NetChangeObserver mNetChangeObserver = null;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
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

	@Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
		             NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
    }
```
# Use
##### Maven
```java
<dependency>
  <groupId>com.caption</groupId>
  <artifactId>netmonitorlibrary</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

##### Gradle
```java
compile 'com.caption:netmonitorlibrary:1.0.0'
```
