package com.deng.netmonitor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deng.netmonitor.R;
import com.deng.netmonitor.base.BaseActivity;
import com.caption.netmonitorlibrary.netStateLib.NetUtils;

public class MainActivity extends BaseActivity {

    private TextView mTvState;
    private RelativeLayout mRlContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvState = (TextView) findViewById(R.id.tv_state);
        mRlContent = (RelativeLayout) findViewById(R.id.rl_state_content);
    }

    @Override
    protected void onNetworkConnected(NetUtils.NetType type) {
        mTvState.setText("网络连接正常\n" + type.name());
        mRlContent.setVisibility(View.GONE);
    }

    @Override
    protected void onNetworkDisConnected() {
        mTvState.setText("网络连接断开");
        mRlContent.setVisibility(View.VISIBLE);
    }
}
