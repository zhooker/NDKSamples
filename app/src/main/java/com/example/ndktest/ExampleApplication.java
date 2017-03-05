package com.example.ndktest;

import android.app.Application;
import android.os.StrictMode;


/**
 * Created by zhooker on 2017/3/4.
 */

public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectCustomSlowCalls() //API等级11，使用StrictMode.noteSlowCode
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()   // or .detectAll() for all detectable problems
//                .penaltyDialog() //弹出违规提示对话框
//                .penaltyLog() //在Logcat 中打印违规异常信息
//                .penaltyFlashScreen() //API等级11
//                .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects() //API等级11
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }

}
