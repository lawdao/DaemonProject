package example.fussen.daemon.task;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import example.fussen.daemon.KeepLiveActivity;


/**
 * Created by Fussen on 2017/2/21.
 * <p>
 * 主要防止其他应用也使用此种方式
 */


public class CheckTopTask implements Runnable {
    private static final String TAG = "[CheckTopTask]";
    private Context context;

    public CheckTopTask(Context context) {
        this.context = context;
    }

    public static void startForeground(Context context) {
        try {
            Intent intent = new Intent(context, KeepLiveActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "e:" + e);
        }
    }

    @Override
    public void run() {
        boolean foreground = isForeground(context);
        Log.d(TAG, "===foreground:===" + foreground);
        if (!foreground) {
            startForeground(context);
        }
    }


    /**
     * 通过获取当前在顶端运行的进程判断自己应用程序是否在前台
     *
     * @param context
     * @return
     */
    private boolean isForeground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if (runningAppProcesses != null) {
                int myPid = android.os.Process.myPid();
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {

                    Log.d(TAG, "=====runningProcessName:=====" + runningAppProcessInfo.processName);
                    if (runningAppProcessInfo.pid == myPid) {
                        return runningAppProcessInfo.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "=====Exception:=====" + e);
        }
        return false;
    }
}
