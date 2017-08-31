package com.lingdeqin.timeup.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lingdeqin.timeup.util.PowerUtil;

/**
 * Created by lingdeqin on 2017/8/31.
 */

public class FlightModeOffBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerUtil.setFlightModeOn(false);
        Toast.makeText(context,"已关闭飞行模式",Toast.LENGTH_SHORT).show();
    }
}
