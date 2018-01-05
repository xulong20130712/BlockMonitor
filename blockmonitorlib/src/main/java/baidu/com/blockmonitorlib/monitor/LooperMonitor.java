package baidu.com.blockmonitorlib.monitor;

import android.text.TextUtils;
import android.util.Log;
import android.util.Printer;

/**
 * Created by xulong on 2018/1/4.
 */

public class LooperMonitor implements Printer {


    public static final String LOG_START= "Dispatching to ";
    public static final String LOG_END= "Finished to ";

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
        if(TextUtils.isEmpty(x)) {

            return;
        }
        if(x.contains(LOG_START)) {

            LogMonitor.getInstance().startMonitor();
        }else if(x.contains(LOG_END)) {

            LogMonitor.getInstance().removeMonitor();
        }
    }
}
