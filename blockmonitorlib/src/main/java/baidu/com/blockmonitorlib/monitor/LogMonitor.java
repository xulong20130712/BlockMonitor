package baidu.com.blockmonitorlib.monitor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

/**
 * Created by xulong on 2018/1/4.
 */

public class LogMonitor {

    private HandlerThread mHandlerThread = new HandlerThread("log");
    private Handler mHandler;
    private static final long VSYNC_STEP= 100;

    private LogMonitor() {
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    private static Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement s : stackTrace) {
                sb.append(s.toString() + "\n");
            }
            Log.e("+-->", sb.toString());
        }
    };

    private static class InstanceHodler {

        private static LogMonitor instance= new LogMonitor();
    }

    public static LogMonitor getInstance() {
        return InstanceHodler.instance;
    }

    public void startMonitor() {
        mHandler.postDelayed(mRunnable, VSYNC_STEP);
    }

    public void removeMonitor() {
        mHandler.removeCallbacks(mRunnable);
    }

}