package baidu.com.blockmonitorlib.monitor;

import android.view.Choreographer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by xulong on 2018/1/4.
 */

public class VsyncMonitor implements Choreographer.FrameCallback {

    private Choreographer choreographer;

    private long frameStartTime = 0;
    private int framesRendered = 0;

    private List<FpsListener> listeners = new ArrayList<FpsListener>();
    private int interval = 500;

    private VsyncMonitor() {

        choreographer = Choreographer.getInstance();
    }

    private static class InstanceHolder {

        private static VsyncMonitor instance= new VsyncMonitor();
    }

    public static VsyncMonitor getInstance() {

        return InstanceHolder.instance;
    }

    /**
     *初始化并且开始侦测FPS
     *
     * @param interval 每一帧所有时间的阀值，单位毫秒
     * @param fpsListener 接受预警的监听者
     */
    public void initForStart(final int interval, final FpsListener fpsListener) {

        this.setInterval(interval);
        this.addListener(fpsListener);
        this.start();
    }

    public void start() {
        choreographer.postFrameCallback(this);
    }

    public void stop() {
        frameStartTime = 0;
        framesRendered = 0;
        choreographer.removeFrameCallback(this);
    }

    public void addListener(FpsListener fpsListener) {
        listeners.add(fpsListener);
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public void doFrame(long frameTimeNanos) {

        long currentTimeMillis= TimeUnit.NANOSECONDS.toMillis(frameTimeNanos);
        if (frameStartTime> 0) {
            final long timeSpan= currentTimeMillis- frameStartTime;
            framesRendered++;
            if (timeSpan> interval) {

                final double fps= framesRendered* 1000/ (double) timeSpan;
                if(fps< 55) {

                    LogMonitor.getInstance().startMonitor();
                    frameStartTime= currentTimeMillis;
                    framesRendered= 0;
                    for (FpsListener fpsListener: listeners) {
                        fpsListener.fpsNotification(fps);
                    }
                }
            }
        } else {
            frameStartTime= currentTimeMillis;
        }
        choreographer.postFrameCallback(this);
    }
}
