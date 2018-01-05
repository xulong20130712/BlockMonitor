package baidu.com.blockmonitorlib.monitor;

/**
 * Created by xulong on 2018/1/5.
 */

public class MonitorConfiguration {

    public boolean ignoreDbugger= true;
    public boolean reportAllThreadInfo= true;
    public boolean saveToFile= true;
    public String logPath= "";
    public String logName= "block_trace.txt";
    public int fps= 45;


    private MonitorConfiguration() {

    }

    private static class InstanceHolder {

        private static MonitorConfiguration monitorConfiguration= new MonitorConfiguration();
    }

    public static MonitorConfiguration getInstance() {

        return InstanceHolder.monitorConfiguration;
    }
}
