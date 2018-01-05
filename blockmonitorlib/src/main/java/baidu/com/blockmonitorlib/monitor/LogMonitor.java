package baidu.com.blockmonitorlib.monitor;

import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.text.SimpleDateFormat;

/**
 * Created by xulong on 2018/1/4.
 *
 * 日志提取
 */

public class LogMonitor {

    private HandlerThread mHandlerThread = new HandlerThread("BlockLogThread");
    private Handler mHandler;
    private static final long VSYNC_STEP= 100;

    private MonitorConfiguration configuration= MonitorConfiguration.getInstance();

    private LogMonitor() {
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            StringBuilder sb = new StringBuilder();
            sb.append(formatTime());
            sb.append("--------------------------------------------\n");
            StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement s : stackTrace) {
                sb.append(s.toString() + "\n");
            }
            if(configuration.saveToFile) {

                TraceSaveUtil logFileUtil= TraceSaveUtil.getInstance();
                try {

                    logFileUtil.saveToFile(sb.toString());
                }catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    };

    //格式化日志时间
    private String formatTime() {

        SimpleDateFormat format =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String date= "";
        long time= System.currentTimeMillis();
        date= format.format(time);
        return date;
    }

    private static class InstanceHodler {

        private static LogMonitor instance= new LogMonitor();
    }

    public static LogMonitor getInstance() {
        return InstanceHodler.instance;
    }

    public void startMonitor() {

        if(configuration!= null) {

            if (configuration.ignoreDbugger&& Debug.isDebuggerConnected()) {

                return;
            }
        }
        mHandler.postDelayed(mRunnable, VSYNC_STEP);
    }

    public void removeMonitor() {
        mHandler.removeCallbacks(mRunnable);
    }

}