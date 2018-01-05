package baidu.com.blockmonitorlib.monitor;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by xulong on 2018/1/5.
 *
 * 保存相应的日志内容
 */

public class TraceSaveUtil {


    // 文件名
    private String fileName;

    // 文件所在目录路径
    private String fileDirPath;

    // 文件对象
    private File file;
    private File copyFile;
    private FileWriter writer= null;
    private boolean needChange2Append= false;

    // 文件最大只能为5MB
    private final static long MAX_FILE_SIZE= 1024* 1024* 5;
//    private final static long MAX_FILE_SIZE= 1024* 10;

    private TraceSaveUtil() {

        fileName= MonitorConfiguration.getInstance().logName;
        fileDirPath= MonitorConfiguration.getInstance().logPath;
        if(TextUtils.isEmpty(fileDirPath)|| TextUtils.isEmpty(fileName)) {

            throw new IllegalArgumentException("please init first!!!!");
        }
        File fileDir= new File(fileDirPath);
        if(!fileDir.exists()) {

            fileDir.mkdir();
        }
        file= new File(fileDir, fileName);
        if (!file.exists()) {

            try {

                file.createNewFile();
                writer= new FileWriter(file, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        copyFile= new File(fileDir, "uploadTrace.txt");
        if (!copyFile.exists()) {

            try {

                copyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class InstanceHolder {

        private static TraceSaveUtil instance= new TraceSaveUtil();
    }

    public static TraceSaveUtil getInstance() {

        return InstanceHolder.instance;
    }

    /**
     * 保存traces文件，如果超出最大文件大小限制则上传上次文件并行保存此次文件
     *
     * @param content
     * @return
     * @throws IOException
     */
    public synchronized boolean saveToFile(String content) throws IOException {

        try {

            //如果本地文件大于MAX_FILE_SIZE，则执行相应策略：1：舍弃以前内容；2：上报文件内容
            if(file.length()+ content.length()> MAX_FILE_SIZE) {

                copyTracesFile();
                writer= new FileWriter(file, false);
            }else {

                writer= null;
                writer= new FileWriter(file, true);
            }
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            writer.close();
        }
        return true;
    }

    /**
     * 现将traces file拷贝到其他位置，然后上传
     * @throws IOException
     */
    public void copyTracesFile() throws IOException {

        FileChannel inChannel = null, outChannel = null;
        try {

            inChannel = new FileInputStream(file).getChannel();
            outChannel = new FileOutputStream(copyFile).getChannel();
            long size= inChannel.size();
            MappedByteBuffer buffer= inChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
            outChannel.write(buffer);
        }catch (Exception e) {

            e.printStackTrace();
        }finally {

            inChannel.close();
            outChannel.close();
        }
        //upload copyFile
    }
}
