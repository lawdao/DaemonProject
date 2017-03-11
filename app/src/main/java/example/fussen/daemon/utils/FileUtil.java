package example.fussen.daemon.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Fussen on 2017/2/22.
 */

public class FileUtil {
    public static final String LOG_PATH = Environment.getExternalStorageDirectory().toString() + "/Daemon";


    public static void writeFile(String content) {

        try {

            if (!new File(LOG_PATH).exists()) {
                new File(LOG_PATH).mkdirs();
            }

            File file = new File(LOG_PATH + "/Log.txt");

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            //将文件记录指针移动到最后
            raf.seek(file.length());
            // 输出文件内容
            String contentFile = content + "\r\n";
            raf.write(contentFile.getBytes());
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
