package baidu.com.blockmonitorlib.monitor;

import android.util.Log;
import android.util.Printer;

/**
 * Created by xulong on 2018/1/4.
 */

public class LooperMonitor implements Printer {


    private LooperMonitor() {

    }

    private static class InstanceHodler {

        private static LooperMonitor instance= new LooperMonitor();
    }

    public static LooperMonitor getInstance() {

        return InstanceHodler.instance;
    }

    @Override
    public void println(String x) {

        Log.e("+-->", "---println---"+ x);
    }
}
