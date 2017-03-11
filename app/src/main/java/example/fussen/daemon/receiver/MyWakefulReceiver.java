package example.fussen.daemon.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import example.fussen.daemon.service.WorkService;
import example.fussen.daemon.utils.FileUtil;
import example.fussen.daemon.utils.TimeUitl;

/**
 * Created by Fussen on 2017/2/22.
 * <p>
 * 唤醒锁 用来唤醒CPU
 */

public class MyWakefulReceiver extends WakefulBroadcastReceiver {

    static final String TAG = "[MyWakefulReceiver]";

    @Override
    public void onReceive(Context context, Intent intent) {

        String content = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          ================接收到系统休眠的信号，开始唤醒=====================";
        FileUtil.writeFile(content);
        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        Intent service = new Intent(context, WorkService.class);
        startWakefulService(context, service);
    }
}
