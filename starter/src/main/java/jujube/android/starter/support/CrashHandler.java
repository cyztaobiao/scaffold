package jujube.android.starter.support;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import gov.zjch.uploader.Constants;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

        private Context context;
        private Thread.UncaughtExceptionHandler defaultHandler;

        public CrashHandler(Context context, Thread.UncaughtExceptionHandler defaultHandler) {
            this.context = context;
            this.defaultHandler = defaultHandler;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if (!handleException(ex) && defaultHandler != null) {
                defaultHandler.uncaughtException(thread, ex);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            }
        }
        private boolean handleException(Throwable ex) {
            if (ex == null) {
                return false;
            }

            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            final String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + Constants.APP_DIR + File.separator + "error.log";
            try {
                ex.printStackTrace();
                PrintWriter p = new PrintWriter(new FileOutputStream(filePath));
                ex.printStackTrace(p);
                p.flush();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }
    }