package com.lingdeqin.timeup.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by lingdeqin on 2017/8/26.
 */

public class MyAlarmBroadCast extends BroadcastReceiver {
    private static final String TAG = "MyAlarmBroadCast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
        Toast.makeText(context, "闹钟提示：时间到！", Toast.LENGTH_LONG).show();
    }
}
