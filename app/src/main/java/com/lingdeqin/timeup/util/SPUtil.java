package com.lingdeqin.timeup.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lingdeqin.timeup.MyApplication;

import java.util.Set;

/**
 * Created by lingdeqin on 2017/8/24.
 */

public class SPUtil {

    static Context context = MyApplication.getContext();

    public static void saveInt(String key,int value){
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void saveFloat(String key,float value){
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void saveLong(String key,long value){
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void saveString(String key,String value){
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveBoolean(String key,Boolean value){
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void saveStringSet(String key,Set<String> value){
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public static int getInt(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key,0);
    }

    public static float getFloat(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key,0);
    }

    public static long getLong(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key,0);
    }

    public static boolean getBoolean(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,false);
    }

    public static String getString(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }

    public static Set<String> getStringSet(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(key,null);
    }

}
