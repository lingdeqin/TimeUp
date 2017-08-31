package com.lingdeqin.timeup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.lingdeqin.timeup.util.PowerUtil;

public class TestActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.btn_flightMode).setOnClickListener(this);
        findViewById(R.id.btn_shutDown).setOnClickListener(this);
        findViewById(R.id.btn_reboot).setOnClickListener(this);
        findViewById(R.id.btn_safeMode).setOnClickListener(this);
        findViewById(R.id.btn_recovery).setOnClickListener(this);
        findViewById(R.id.btn_bootloader).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_flightMode:
                if(PowerUtil.isFlightMode(getApplicationContext()))
                    PowerUtil.setFlightModeOn(false);
                else
                    PowerUtil.setFlightModeOn(true);
                break;
            case R.id.btn_shutDown:
                PowerUtil.shutdown();
                break;
            case R.id.btn_reboot:
                PowerUtil.reboot();
                break;
            case R.id.btn_safeMode:
                PowerUtil.safeMode();
                break;
            case R.id.btn_recovery:
                PowerUtil.recovery();
                break;
            case R.id.btn_bootloader:
                PowerUtil.bootloader();
                break;
        }
    }
}
