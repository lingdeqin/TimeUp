package com.lingdeqin.timeup.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lingdeqin.timeup.util.SPUtil;

/**
 * Created by lingdeqin on 2017/8/29.
 */

public class TestBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"广播测试",Toast.LENGTH_SHORT).show();
        SPUtil.saveBoolean("isFeasible",true);
    }
}
