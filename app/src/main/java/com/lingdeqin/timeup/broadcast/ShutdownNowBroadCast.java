package com.lingdeqin.timeup.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lingdeqin.timeup.util.PowerUtil;

/**
 * Created by lingdeqin on 2017/8/29.
 */

public class ShutdownNowBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerUtil.shutdown();
    }
}
