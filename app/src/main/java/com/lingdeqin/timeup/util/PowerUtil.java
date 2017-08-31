package com.lingdeqin.timeup.util;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.lingdeqin.timeup.broadcast.ShutdownNowBroadCast;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * Created by lingdeqin on 2017/8/24.
 */

public class PowerUtil {

    /**
     * 设置手机飞行模式
     * @param enabling true:设置为飞行模式 false:取消飞行模式
     */
    public static void setFlightModeOn(boolean enabling) {
        if(enabling){
            execShell("settings put global airplane_mode_on 1\n" +
                    "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
        }else {
            execShell("settings put global airplane_mode_on 0\n" +
                    "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
        }
    }

    /**
     * 判断手机是否是飞行模式
     * @param context
     * @return
     */
    public static boolean isFlightMode(Context context){
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) ;
        return (isAirplaneMode == 1)?true:false;
    }


    public static boolean isScreenOn(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //true为打开，false为关闭
        return powerManager.isScreenOn();
    }

    public static boolean isScreenLock(Context context){
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }



    public static void execShell(String cmd){
        try{
            //权限设置
            Process p = Runtime.getRuntime().exec("su");
            //获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
            //将命令写入
            dataOutputStream.writeBytes(cmd+"\n");
            dataOutputStream.writeBytes("exit\n");
            //提交命令
            dataOutputStream.flush();
            //关闭流操作
            dataOutputStream.close();
            outputStream.close();
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    public static void shutdown(){
        if(getSystemVersion() >= 21){
            execShell("svc power shutdown");
        }else{
            execShell("reboot -p");
        }
    }

    public static void reboot(){
        if(getSystemVersion() >=21){
            execShell("svc power reboot");
        }else{
            execShell("reboot");
        }
    }

    public static void safeMode(){
        execShell("setprop persist.sys.safemode 1");
        reboot();
    }

    public static void recovery(){
        if(getSystemVersion() >=21){
            execShell("svc power reboot recovery");
        }else{
            execShell("reboot recovery");
        }
    }

    public static void bootloader(){
        if(getSystemVersion() >=21){
            execShell("svc power reboot bootloader");
        }else{
            execShell("reboot bootloader");
        }
    }

    public static int getSystemVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static void setAlarm(AlarmManager manager,PendingIntent pendingIntent,long time){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+time,pendingIntent);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            manager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+time,pendingIntent);
        }else{
            manager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+time,pendingIntent);
        }
    }

    public static void setAlarm(AlarmManager manager,PendingIntent pendingIntent,Calendar calendar){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            manager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }else{
            manager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }
    }


}
