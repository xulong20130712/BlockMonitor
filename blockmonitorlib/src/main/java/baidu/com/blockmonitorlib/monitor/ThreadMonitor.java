package baidu.com.blockmonitorlib.monitor;

import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xulong on 2018/1/4.
 */

public class ThreadMonitor implements Runnable {

    private static final Object EXIT = new Object();
    private static final ThreadLocal<ThreadMonitor> RUNNINGS = new ThreadLocal<ThreadMonitor>();
    private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private static Handler handler = new Handler(Looper.getMainLooper());

    private static final String FIELD_mQueue = "mQueue";
    private static final String METHOD_next = "next";

    /**
     * Install SafeLooper in the main thread
     * <p>
     * Notice the action will take effect in the next event loop
     */
    public static void start() {

        handler.removeMessages(0, EXIT);
        handler.post(new ThreadMonitor());
    }

    /**
     * Exit SafeLooper after millis in the main thread
     * <p>
     * Notice the action will take effect in the next event loop
     */
    public static void stopDelay(long millis) {

        handler.removeMessages(0, EXIT);
        handler.sendMessageDelayed(handler.obtainMessage(0, EXIT), millis);
    }

    /**
     * Exit SafeLooper in the main thread
     * <p>
     * Notice the action will take effect in the next event loop
     */
    public static void stop() {

        stopDelay(0);
    }

    /**
     * Tell if the SafeLooper is running in the current thread
     */
    public static boolean isSafe() {

        return RUNNINGS.get() != null;
    }

    /**
     * The same as Thread.setDefaultUncaughtExceptionHandler
     */
    public static void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler h) {

        uncaughtExceptionHandler = h;
    }

    @Override
    public void run() {

        Log.e("+-->", "---monitor run---");
        if (RUNNINGS.get() != null)
            return;

        Method next;
        Field target;
        try {
            Method methodNext= MessageQueue.class.getDeclaredMethod("next");
            methodNext.setAccessible(true);
            next = methodNext;
            Field fieldTarget= Message.class.getDeclaredField("target");
            fieldTarget.setAccessible(true);
            target= fieldTarget;
        } catch (Exception e) {
            return;
        }
        RUNNINGS.set(this);
        MessageQueue queue = Looper.myQueue();
        Binder.clearCallingIdentity();
        final long ident = Binder.clearCallingIdentity();
        for(;;) {

            try {

                Message msg = (Message) next.invoke(queue);
                if (msg== null || msg.obj == EXIT) {

                    break;
                }
                //开始检测
                LogMonitor.getInstance().startMonitor();
                Handler h = (Handler) target.get(msg);
                h.dispatchMessage(msg);
                final long newIdent = Binder.clearCallingIdentity();
                if(newIdent== ident|| Build.VERSION.SDK_INT< 20) {

                    msg.recycle();
                }
            } catch (Exception e) {

                Log.e("+-->", "---monitor exception---"+ e.getLocalizedMessage());
                Thread.UncaughtExceptionHandler h = uncaughtExceptionHandler;
                Throwable ex = e;
                if (e instanceof InvocationTargetException) {
                    ex = ((InvocationTargetException) e).getCause();
                    if (ex == null) {
                        ex = e;
                    }
                }
                // e.printStackTrace(System.err);
                if (h != null) {
                    h.uncaughtException(Thread.currentThread(), ex);
                }
                new Handler().post(this);
                break;
            } finally {

                //停止检测
                LogMonitor.getInstance().removeMonitor();
            }
        }
        RUNNINGS.set(null);
        Log.e("+-->", "---monitor Message next finish---");
    }
}
