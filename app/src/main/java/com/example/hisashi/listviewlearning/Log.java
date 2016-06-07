package com.example.hisashi.listviewlearning;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Custom logging class to keep logs on device in addition to LogCat.
 * Wrapper class internally calling methods of the default Log class.
 * Singleton Pattern.
 */
public final class Log {
    private static Log instance = null;
    private static final String APP_NAME = "ListViewLearning";
    private static final String LOG_FILE_NAME = APP_NAME + "_log.txt";
    private static final String TAG = "Log";
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private BufferedWriter toWriteDevice;
    private File logFile;
    private Context context;
    private boolean deviceConnected = true;

    public static Log getInstance(Context context) {
        if (instance == null) {
            instance = new Log(context);
        }

        return instance;
    }

    public static void d(String tag, String message) {
        if (instance != null) { instance.write("D", tag, message);}
        android.util.Log.d(tag, message);
    }

    public static void v(String tag, String message) {
        if (instance != null) { instance.write("V", tag, message);}
        android.util.Log.v(tag, message);
    }

    public static void i(String tag, String message) {
        if (instance != null) { instance.write("I", tag, message);}
        android.util.Log.i(tag, message);
    }

    public static void w(String tag, String message) {
        if (instance != null) { instance.write("W", tag, message);}
        android.util.Log.w(tag, message);
    }

    public static void e(String tag, String message) {
        if (instance != null) { instance.write("E", tag, message);}
        android.util.Log.e(tag, message);
    }

    private Log(Context context) {
        this.context = context;

        if (externalDeviceStorageAvailableAndWritable()) {
            createLogFile();
        } else {
            android.util.Log.d(TAG, "not creating a log file on device as the program is running on computer.");
            this.deviceConnected = false;
        }
    }

    private void createLogFile() {
        android.util.Log.d(TAG, "creating a log file on device as the program is running on device.");

        this.logFile = instantiateFileWithExtStoragePath();
        try {
            if (!this.logFile.exists()) {
                android.util.Log.d(TAG, "creating a file=" + this.logFile.toString() + " as it didn't exist.");

                if(!this.logFile.createNewFile()) {
                    android.util.Log.d(TAG, "creating a file failed...");
                }
            }

            this.toWriteDevice = this.openLogFileAndReturnBuffWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean write(String level, String tag, String message) {
//        TODO: Creating log file on computer not yet implemented because of issue with creating file on Android Studio
        if (!this.deviceConnected) {
            return false;
        }

        try {
            Date date = new Date();
            if (this.toWriteDevice != null) {
//                android.util.Log.d(TAG, "writing to " + this.toWriteDevice.toString());

                // open file and set append=true
                this.toWriteDevice = openLogFileAndReturnBuffWriter();
                this.toWriteDevice.write(this.dateFormat.format(date) + level + "/" + tag + ": " + message + "\n");
                this.toWriteDevice.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @NonNull
    private BufferedWriter openLogFileAndReturnBuffWriter() throws IOException {
//        android.util.Log.d(TAG, "opening file " + this.logFile.toString());

        FileWriter fileWriter = new FileWriter(this.logFile, true);
        this.toWriteDevice = new BufferedWriter(fileWriter);
        return new BufferedWriter(this.toWriteDevice);
    }

    @NonNull
    private File instantiateFileWithExtStoragePath() {
        File extStorage = this.context.getExternalFilesDir(null);
        return new File(extStorage, LOG_FILE_NAME);
    }

    private boolean externalDeviceStorageAvailableAndWritable() {
        boolean externalStorageAvailable = false;
        boolean externalStorageWritable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            android.util.Log.d(TAG, "checked that external storage is available and writable");
            externalStorageAvailable = externalStorageWritable = true;
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            android.util.Log.d(TAG, "checked that external storage is only available but not writable");
            externalStorageAvailable = true;
        }

        return externalStorageWritable && externalStorageAvailable;
    }
}
