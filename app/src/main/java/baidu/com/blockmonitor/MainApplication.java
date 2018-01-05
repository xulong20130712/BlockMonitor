package baidu.com.blockmonitor;

import android.app.Application;
import android.util.Log;

import java.io.File;

import baidu.com.blockmonitorlib.monitor.FpsListener;
import baidu.com.blockmonitorlib.monitor.LogMonitor;
import baidu.com.blockmonitorlib.monitor.LooperMonitor;
import baidu.com.blockmonitorlib.monitor.MonitorConfiguration;
import baidu.com.blockmonitorlib.monitor.VsyncMonitor;

/**
 * Created by xulong on 2018/1/4.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {

        initBlockMonitor();
//        Looper.getMainLooper().setMessageLogging(LooperMonitor.getInstance());
        LooperMonitor.getInstance().println(">>>>>>>Dispatching to Application start");
        super.onCreate();
        LooperMonitor.getInstance().println("<<<<<<<Finished to Application finish");

//        ThreadMonitor.start();

        VsyncMonitor.getInstance().initForStart(20, new FpsListener() {
            @Override
            public void fpsNotification(double fps) {
                Log.e("+-->", "---fpsNotification---"+ fps);

                LogMonitor.getInstance().startMonitor();
            }
        });
    }

    private void initBlockMonitor() {

        String logFilePath= this.getFilesDir().getAbsolutePath()+ File.separator+ "traces";
        MonitorConfiguration.getInstance().logPath= logFilePath;
        MonitorConfiguration.getInstance().logName= "block_traces.txt";
        MonitorConfiguration.getInstance().ignoreDbugger= false;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        VsyncMonitor.getInstance().stop();
    }
}