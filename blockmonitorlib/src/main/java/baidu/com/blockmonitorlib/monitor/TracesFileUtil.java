package baidu.com.blockmonitorlib.monitor;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by xulong on 2018/1/5.
 */

public class TracesFileUtil {

    // 文件名
    private String fileName;

    // 文件所在目录路径
    private String fileDirPath;

    // 文件对象
    private File file;

    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;
    private boolean boundSuccess= false;

    // 文件最大只能为5MB
    private final static long MAX_FILE_SIZE= 1024* 1024* 5;

    // 最大的脏数据量1KB,系统必须触发一次强制刷
    private long MAX_FLUSH_DATA_SIZE= 1024;

    // 最大的刷间隔,系统必须触发一次强制刷
    private long MAX_FLUSH_TIME_GAP= 1000;

    // 文件写入位置
    private long writePosition= 0;

    // 最后一次刷数据的时候
    private long lastFlushTime;

    // 上一次刷的文件位置
    private long lastFlushFilePosition= 0;

    public TracesFileUtil() {
        super();
        this.fileName= MonitorConfiguration.getInstance().logName;
        this.fileDirPath= MonitorConfiguration.getInstance().logPath;
        if(TextUtils.isEmpty(fileDirPath)|| TextUtils.isEmpty(fileName)) {

            throw new IllegalArgumentException("please init first!!!!");
        }
        File fileDir= new File(fileDirPath);
        if(!fileDir.exists()) {

            fileDir.mkdir();
        }
        this.file= new File(fileDir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static class InstanceHolder {

        private static TracesFileUtil instance= new TracesFileUtil();
    }

    public static TracesFileUtil getInstance() {

        return InstanceHolder.instance;
    }

    /**
     *
     * 内存映照文件绑定
     * @return
     */
    public synchronized boolean boundChannelToByteBuffer(long size) {

        try {

            RandomAccessFile raf= new RandomAccessFile(file, "rw");
            this.fileChannel= raf.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
            this.boundSuccess= false;
            return false;
        }
        try {

            this.mappedByteBuffer= this.fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, size);
        } catch (IOException e) {
            e.printStackTrace();
            this.boundSuccess= false;
            return false;
        }
        this.boundSuccess= true;
        return true;
    }

    /**
     * 写数据：先将之前的文件删除然后重新
     * @param data
     * @return
     */
    public synchronized boolean writeData(byte[] data) {

        return false;
    }

    /**
     * 在文件末尾追加数据
     * @param data
     * @return
     * @throws Exception
     */
    public synchronized boolean appendData(byte[] data) throws Exception {

        writePosition= writePosition+ data.length;
        if (!boundSuccess) {
            boundChannelToByteBuffer(writePosition);
        }
        if (writePosition >= MAX_FILE_SIZE) {   // 如果写入data会超出文件大小限制，不写入

            flush();
            writePosition= writePosition- data.length;
            Log.e("+-->", "File="+ file.toURI().toString()+ " is written full.");
            Log.e("+-->", "already write data length:"+ writePosition+ ", max file size="+ MAX_FILE_SIZE);
            return false;
        }
        this.mappedByteBuffer.put(data);
        // 检查是否需要把内存缓冲刷到磁盘
        if ( (writePosition- lastFlushFilePosition > this.MAX_FLUSH_DATA_SIZE)||
                (System.currentTimeMillis()- lastFlushTime > this.MAX_FLUSH_TIME_GAP && writePosition > lastFlushFilePosition) ) {

            flush();   // 刷到磁盘
        }
        return true;
    }

    public synchronized void flush() {

        this.mappedByteBuffer.force();
        this.lastFlushTime= System.currentTimeMillis();
        this.lastFlushFilePosition= writePosition;
        try {
            this.fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.boundSuccess= false;
    }

    public long getLastFlushTime() {
        return lastFlushTime;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDirPath() {
        return fileDirPath;
    }

    public boolean isBundSuccess() {
        return boundSuccess;
    }

    public File getFile() {
        return file;
    }

    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }

    public long getWritePosition() {
        return writePosition;
    }

    public long getLastFlushFilePosition() {
        return lastFlushFilePosition;
    }

    public long getMAX_FLUSH_DATA_SIZE() {
        return MAX_FLUSH_DATA_SIZE;
    }

    public long getMAX_FLUSH_TIME_GAP() {
        return MAX_FLUSH_TIME_GAP;
    }
}
