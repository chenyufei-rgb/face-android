package com.example.project_android.util;

import android.app.Application;
import android.content.Context;

//它继承了Android的Application类。可以用来存储和访问应用的上下文环境，通常用于在整个应用中共享资源和数据。
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //getApplicationContext()方法来获取当前应用的上下文
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
