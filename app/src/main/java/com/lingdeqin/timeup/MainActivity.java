package com.lingdeqin.timeup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lingdeqin.timeup.broadcast.FlightModeOffBroadCast;
import com.lingdeqin.timeup.broadcast.FlightModeOnBroadCast;
import com.lingdeqin.timeup.broadcast.ShutdownBroadCast;
import com.lingdeqin.timeup.broadcast.TestBroadCast;
import com.lingdeqin.timeup.util.Constants;
import com.lingdeqin.timeup.util.PowerUtil;
import com.lingdeqin.timeup.util.SPUtil;

import java.io.DataOutputStream;
import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener , Switch.OnCheckedChangeListener{

    private static final String TAG = "MainActivity";
    private static int REQ_CODE = 123;
    private Switch shutdown;
    private Switch flightMode_start;
    private Switch flightMode_end;
    private Switch flightModeTest;
    private TextView txt_shutdown;
    private TextView txt_flightMode_start;
    private TextView txt_flightMode_end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test).setOnClickListener(this);
        findViewById(R.id.btn_tp_shutdown).setOnClickListener(this);
        findViewById(R.id.btn_fm_start).setOnClickListener(this);
        findViewById(R.id.btn_fm_end).setOnClickListener(this);
        findViewById(R.id.btn_shutdownTest).setOnClickListener(this);
        shutdown = (Switch) findViewById(R.id.switch_shutdown);
        flightMode_start = (Switch) findViewById(R.id.switch_flightMode_start);
        flightMode_end = findViewById(R.id.switch_flightMode_end);
        flightModeTest = (Switch) findViewById(R.id.switch_flightModeTest);
        shutdown.setOnCheckedChangeListener(this);
        flightMode_start.setOnCheckedChangeListener(this);
        flightMode_end.setOnCheckedChangeListener(this);
        flightModeTest.setOnCheckedChangeListener(this);
        txt_shutdown = (TextView) findViewById(R.id.txt_shutdown);
        txt_flightMode_start = (TextView) findViewById(R.id.txt_flightMode_start);
        txt_flightMode_end = (TextView) findViewById(R.id.txt_flightMode_end);
        init();
        check();
        setShutdown(true);
    }

    public void init(){
        shutdown.setChecked(SPUtil.getBoolean(Constants.SPKey.BOOL_SHUTDOWN));
        flightMode_start.setChecked(SPUtil.getBoolean(Constants.SPKey.BOOL_FLIGHT_MODE_START));
        flightMode_end.setChecked(SPUtil.getBoolean(Constants.SPKey.BOOL_FLIGHT_MODE_END));
        flightModeTest.setChecked(PowerUtil.isFlightMode(getApplicationContext()));
        txt_shutdown.setText(SPUtil.getString(Constants.SPKey.STR_SHUTDOWN_TIME));
        txt_flightMode_start.setText(SPUtil.getString(Constants.SPKey.STR_FLIGHT_MODE_TIME_START));
        txt_flightMode_end.setText(SPUtil.getString(Constants.SPKey.STR_FLIGHT_MODE_TIME_END));
    }

    public void check(){
        if(isRoot()){
            if(!SPUtil.getBoolean(Constants.SPKey.BOOL_IS_ROOT)){
                SPUtil.saveBoolean(Constants.SPKey.BOOL_IS_ROOT,true);
                Toast.makeText(getApplicationContext(),"以获取ROOT权限",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"没有ROOT权限，功能将不能使用",Toast.LENGTH_SHORT).show();
        }
//        if(!SPUtil.getBoolean("isFeasible")){
//            isFeasible();
//        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M  && !Settings.canDrawOverlays(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("权限提醒");
            builder.setMessage("当前无授权，无法在除本应用页面使用关机提醒，请授予权限！");
            builder.setCancelable(false);
            builder.setPositiveButton("去授予", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent,REQ_CODE);
                }
            });
            builder.show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_test:
                Intent intent = new Intent();
                intent.setClass(this.getApplicationContext(),TestActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_shutdownTest:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("关机提醒");
                builder.setMessage("关机？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PowerUtil.shutdown();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.btn_tp_shutdown:
                showTimePickerDialog(Constants.SPKey.STR_SHUTDOWN_TIME, txt_shutdown, new CallBack() {
                    @Override
                    public void ling() {
                        setShutdown(true);
                    }
                });
                break;

            case R.id.btn_fm_start:
                showTimePickerDialog(Constants.SPKey.STR_FLIGHT_MODE_TIME_START, txt_flightMode_start, new CallBack() {
                    @Override
                    public void ling() {
                        setFlightModeStart(true);
                    }
                });
                break;

            case R.id.btn_fm_end:
                showTimePickerDialog(Constants.SPKey.STR_FLIGHT_MODE_TIME_END, txt_flightMode_end, new CallBack() {
                    @Override
                    public void ling() {
                        setFlightModeEnd(true);
                    }
                });
                break;
        }
    }

    interface CallBack{
        void ling();
    }

    public void showTimePickerDialog(final String key, final TextView textView , final CallBack callback){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hour = 0;
        int minute = 0;
        if(SPUtil.getString(key)==null){
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }else{
            String time = SPUtil.getString(key);
            hour = Integer.parseInt(time.substring(0,2));
            minute = Integer.parseInt(time.substring(3,5));
        }
        new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view,int hourOfDay,int minute)
            {
                String h = null;
                String m = null;
                if(hourOfDay >= 10)
                    h = hourOfDay+"";
                else
                    h = "0"+hourOfDay;
                if(minute >= 10)
                    m = minute+"";
                else
                    m = "0"+minute;
                textView.setText(h+":"+m);
                SPUtil.saveString(key,h+":"+m);
                callback.ling();
            }
        }, hour, minute,true).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.switch_flightModeTest:
                PowerUtil.setFlightModeOn(b);
                break;
            case R.id.switch_shutdown:
                SPUtil.saveBoolean(Constants.SPKey.BOOL_SHUTDOWN,b);
                if(b){
                    setShutdown(true);
                }else{
                    setShutdown(false);
                    Toast.makeText(getApplicationContext(),"定时关机已关闭",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switch_flightMode_start:
                SPUtil.saveBoolean(Constants.SPKey.BOOL_FLIGHT_MODE_START,b);
                if(b){
                    setFlightModeStart(true);
                }else{
                    setFlightModeStart(false);
                    Toast.makeText(getApplicationContext(),"定时飞行模式已关闭",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switch_flightMode_end:
                SPUtil.saveBoolean(Constants.SPKey.BOOL_FLIGHT_MODE_END,b);
                if(b){
                    setFlightModeEnd(true);
                }else{
                    setFlightModeEnd(false);
                    Toast.makeText(getApplicationContext(),"定时飞行模式已关闭",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void am(){
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = 2 * 1000;
        Intent intent = new Intent(MainActivity.this,ShutdownBroadCast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        PowerUtil.setAlarm(manager,pendingIntent,time);
    }

    public synchronized boolean isRoot(){
        Process process = null;
        DataOutputStream os = null;
        try{
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0){
                return true;
            }else{
                return false;
            }
        } catch (Exception e){
            return false;
        }finally{
            try{
                if (os != null){
                    os.close();
                }
                process.destroy();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void isFeasible(){

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 1 * 1000;
        Intent intent = new Intent(MainActivity.this, TestBroadCast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        PowerUtil.setAlarm(manager,pendingIntent,time);
    }


    public void setShutdown(Boolean b){

        if(SPUtil.getString(Constants.SPKey.STR_SHUTDOWN_TIME) == null)
            return;

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, ShutdownBroadCast.class);
        PendingIntent shutdownIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        if(b && SPUtil.getBoolean(Constants.SPKey.BOOL_SHUTDOWN)){
            Calendar shutdown = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            String time = SPUtil.getString(Constants.SPKey.STR_SHUTDOWN_TIME);
            int hour = Integer.parseInt(time.substring(0,2));
            int minute = Integer.parseInt(time.substring(3,5));
            shutdown.set(Calendar.HOUR_OF_DAY,hour);
            shutdown.set(Calendar.MINUTE, minute);
            shutdown.set(Calendar.SECOND, 0);
            shutdown.set(Calendar.MILLISECOND, 0);

            if(shutdown.before(now)){
                //如果关机时间小于现在时间，加一天
                shutdown.add(Calendar.DATE,1);
            }
            PowerUtil.setAlarm(manager,shutdownIntent,shutdown);
            long t = shutdown.getTimeInMillis() - now.getTimeInMillis();

            long s = t/1000;
            int m = (int) (s/60);
            int h = m/60;
            if(h>0){
                Toast.makeText(getApplicationContext(),"手机将于"+h+"小时"+(m-60*h)+"分后关机",Toast.LENGTH_SHORT).show();
            }else if(m>0){
                Toast.makeText(getApplicationContext(),"手机将于"+m+"分"+(s-m*60)+"秒后关机",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"手机将于"+s+"秒后关机",Toast.LENGTH_SHORT).show();
            }
        } else {
            manager.cancel(shutdownIntent);
        }
    }

    public void setFlightModeStart(Boolean b){
        if(SPUtil.getString(Constants.SPKey.STR_FLIGHT_MODE_TIME_START) == null)
            return;

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, FlightModeOnBroadCast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        if(b && SPUtil.getBoolean(Constants.SPKey.BOOL_FLIGHT_MODE_START)){
            Calendar flightModeStart = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            String time = SPUtil.getString(Constants.SPKey.STR_FLIGHT_MODE_TIME_START);
            int hour = Integer.parseInt(time.substring(0,2));
            int minute = Integer.parseInt(time.substring(3,5));
            flightModeStart.set(Calendar.HOUR_OF_DAY,hour);
            flightModeStart.set(Calendar.MINUTE, minute);
            flightModeStart.set(Calendar.SECOND, 0);
            flightModeStart.set(Calendar.MILLISECOND, 0);

            if(flightModeStart.before(now)){
                flightModeStart.add(Calendar.DATE,1);
            }
            PowerUtil.setAlarm(manager,pendingIntent,flightModeStart);
            long t = flightModeStart.getTimeInMillis() - now.getTimeInMillis();

            long s = t/1000;
            int m = (int) (s/60);
            int h = m/60;
            if(h>0){
                Toast.makeText(getApplicationContext(),"手机将于"+h+"小时"+(m-60*h)+"分后开启飞行模式",Toast.LENGTH_SHORT).show();
            }else if(m>0){
                Toast.makeText(getApplicationContext(),"手机将于"+m+"分"+(s-m*60)+"秒后开启飞行模式",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"手机将于"+s+"秒后开启飞行模式",Toast.LENGTH_SHORT).show();
            }
        } else {
            manager.cancel(pendingIntent);
        }
    }



    public void setFlightModeEnd(Boolean b){
        if(SPUtil.getString(Constants.SPKey.STR_FLIGHT_MODE_TIME_END) == null)
            return;

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, FlightModeOffBroadCast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        if(b && SPUtil.getBoolean(Constants.SPKey.BOOL_FLIGHT_MODE_END)){
            Calendar flightModeEnd = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            String time = SPUtil.getString(Constants.SPKey.STR_FLIGHT_MODE_TIME_END);
            int hour = Integer.parseInt(time.substring(0,2));
            int minute = Integer.parseInt(time.substring(3,5));
            flightModeEnd.set(Calendar.HOUR_OF_DAY,hour);
            flightModeEnd.set(Calendar.MINUTE, minute);
            flightModeEnd.set(Calendar.SECOND, 0);
            flightModeEnd.set(Calendar.MILLISECOND, 0);

            if(flightModeEnd.before(now)){
                flightModeEnd.add(Calendar.DATE,1);
            }
            PowerUtil.setAlarm(manager,pendingIntent,flightModeEnd);
            long t = flightModeEnd.getTimeInMillis() - now.getTimeInMillis();

            long s = t/1000;
            int m = (int) (s/60);
            int h = m/60;
            if(h>0){
                Toast.makeText(getApplicationContext(),"手机将于"+h+"小时"+(m-60*h)+"分后关闭飞行模式",Toast.LENGTH_SHORT).show();
            }else if(m>0){
                Toast.makeText(getApplicationContext(),"手机将于"+m+"分"+(s-m*60)+"秒后关闭飞行模式",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"手机将于"+s+"秒后关闭飞行模式",Toast.LENGTH_SHORT).show();
            }
        } else {
            manager.cancel(pendingIntent);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_CODE) {
            if (Build.VERSION.SDK_INT >= 23) {
                if(Settings.canDrawOverlays(this)) {
                    Toast.makeText(getApplicationContext(),"权限授予成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"权限授予失败，将无法使用全局提醒",Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
