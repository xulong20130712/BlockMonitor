package baidu.com.blockmonitor;

import android.app.Application;
import android.util.Log;

import baidu.com.blockmonitorlib.monitor.FpsListener;
import baidu.com.blockmonitorlib.monitor.LogMonitor;
import baidu.com.blockmonitorlib.monitor.LooperMonitor;
import baidu.com.blockmonitorlib.monitor.VsyncMonitor;

/**
 * Created by xulong on 2018/1/4.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {

//        Looper.getMainLooper().setMessageLogging(LooperMonitor.getInstance());
        LooperMonitor.getInstance().println("<<<<<<Application start");
        super.onCreate();
        LooperMonitor.getInstance().println(">>>>>>Application finish");

//        ThreadMonitor.start();

        VsyncMonitor.getInstance().initForStart(90, new FpsListener() {
            @Override
            public void fpsNotification(double fps) {
                Log.e("+-->", "---fpsNotification---"+ fps);

                LogMonitor.getInstance().startMonitor();
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        VsyncMonitor.getInstance().stop();
    }
}