package com.meitu.meiyancamera.Application;

import android.app.Application;
import android.os.Environment;

import com.meitu.core.JNIConfig;
import com.meitu.meiyancamera.Constant.Constant;
import com.meitu.meiyancamera.Utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zero on 2017/3/17.
 */

public class MeiyanApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        JNIConfig.instance().ndkInit(this, FileUtil.makeFileAndGetPath(Constant.BITMAP_CACHE_PATHNAME));
        initExceptionHandler();
    }

    private void initExceptionHandler() {
        final Thread.UncaughtExceptionHandler dueh = Thread
                .getDefaultUncaughtExceptionHandler();
		/* 处理未捕捉异常 */
        String LogPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "meiyan_crash_log.txt";
        final File LogFile = new File(LogPath);
        if (!LogFile.exists()) {
            // LogFile.getParentFile().mkdirs();
            try {
                LogFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                FileOutputStream fos = null;
                PrintStream ps = null;
                try {
                    fos = new FileOutputStream(LogFile, true);
                    ps = new PrintStream(fos);
                    ps.println(getFullTimestamp(System.currentTimeMillis()));
                    ex.printStackTrace(ps);
                } catch (FileNotFoundException e) {
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                dueh.uncaughtException(thread, ex);
            }
        });
    }

    public String getFullTimestamp(long millisecond) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
                .format(new Date(millisecond));
    }
}
