package com.lingdeqin.timeup.broadcast;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.lingdeqin.timeup.util.PowerUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lingdeqin on 2017/8/29.
 */

public class ShutdownBroadCast extends BroadcastReceiver {
    private static final String TAG = "ShutdownBroadCast";
    private AlarmManager manager;
    private PendingIntent pendingIntent;
    private TextView tv;
    private Handler handler;
    private Timer offTime;
    private Dialog dialog;
    @Override
    public void onReceive(final Context context, Intent intent) {

        boolean ifOpen =PowerUtil.isScreenOn(context);
        boolean ifLock =PowerUtil.isScreenLock(context);

        if(ifOpen && !ifLock){
            tv = new TextView(context);
            tv.setTextSize(16);
            SpannableStringBuilder span = new SpannableStringBuilder("\n缩进"+"时辰已到，您的手机将于60秒后关闭！");
            span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 3,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tv.setText(span);
            handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what > 0) {
                        ////动态显示倒计时
                        SpannableStringBuilder span = new SpannableStringBuilder("\n缩进"+"时辰已到，您的手机将于"+msg.what+"秒后关闭！");
                        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 3,
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        tv.setText(span);
                    } else {
                        ////倒计时结束自动关闭
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        offTime.cancel();
                    }
                    super.handleMessage(msg);
                }
            };
            offTime = new Timer(true);
            TimerTask tt = new TimerTask() {
                int countTime = 60;
                public void run() {
                    if (countTime > 0) {
                        countTime--;
                    }
                    Message msg = new Message();
                    msg.what = countTime;
                    handler.sendMessage(msg);
                }
            };
            offTime.schedule(tt, 1000, 1000);
            manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            long time = 60 * 1000;
            Intent intent2 = new Intent(context,ShutdownNowBroadCast.class);
            pendingIntent = PendingIntent.getBroadcast(context,0,intent2,0);
            PowerUtil.setAlarm(manager,pendingIntent,time);
            AlertDialog.Builder builder = new AlertDialog.Builder(context.getApplicationContext());
            builder.setTitle("关机提醒");
            builder.setView(tv);
            builder.setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    offTime.cancel();
                    dialog.cancel();
                    PowerUtil.shutdown();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    offTime.cancel();
                    manager.cancel(pendingIntent);
                    dialog.cancel();
                }
            });
            dialog = builder.create();
            if (Build.VERSION.SDK_INT >= 23) {
                if(Settings.canDrawOverlays(context)) {
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.show();
                } else {
                    PowerUtil.shutdown();
//                    Intent intent3 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                    intent3.setData(Uri.parse("package:" + context.getPackageName()));
//                    context.startActivity(intent3);
//                    return;
                }
            } else {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
            }

    }else{
            PowerUtil.shutdown();
        }
    }
}
