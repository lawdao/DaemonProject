package example.fussen.daemon.service;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import java.util.concurrent.TimeUnit;

import example.fussen.daemon.App;
import example.fussen.daemon.receiver.MyWakefulReceiver;
import example.fussen.daemon.receiver.WakeUpReceiver;
import example.fussen.daemon.utils.FileUtil;
import example.fussen.daemon.utils.TimeUitl;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Fussen on 2017/2/21.
 * <p>
 * 需要正常工作的服务
 */

public class WorkService extends Service {

    static final int HASH_CODE = 1;

    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;

    public static Subscription sSubscription;

    static String TAG = "[WorkService]";

    public static Intent serviceIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.serviceIntent = intent;
        return onStart(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        onStart(intent, 0, 0);
        return null;
    }


    /**
     * 最近任务列表中划掉卡片时回调
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        onEnd(rootIntent);
    }

    /**
     * 设置-正在运行中停止服务时回调
     */
    @Override
    public void onDestroy() {
        onEnd(null);

        String text = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          =============WorkService死了=============";
        FileUtil.writeFile(text);
    }


    /**
     * 系统会根据不同的内存状态来回调
     *
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {

        String text = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          =============onTrimMemory=============";
        FileUtil.writeFile(text);

        switch (level) {
            case TRIM_MEMORY_RUNNING_MODERATE:   //表示应用程序正常运行，并且不会被杀掉。但是目前手机的内存已经有点低了，系统可能会开始根据LRU缓存规则来去杀死进程了

                String TRIM_MEMORY_RUNNING_MODERATE = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          =============目前手机的内存已经有点低了，系统可能会开始根据LRU缓存规则来去杀死进程了=============";
                FileUtil.writeFile(TRIM_MEMORY_RUNNING_MODERATE);
                break;
            case TRIM_MEMORY_RUNNING_LOW:        //目前手机的内存已经非常低了

                String TRIM_MEMORY_RUNNING_LOW = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          =============目前手机的内存已经非常低了=============";
                FileUtil.writeFile(TRIM_MEMORY_RUNNING_LOW);
                break;
            case TRIM_MEMORY_RUNNING_CRITICAL:  //系统已经根据LRU缓存规则杀掉了大部分缓存的进程了

                String TRIM_MEMORY_RUNNING_CRITICAL = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          =============系统已经根据LRU缓存规则杀掉了大部分缓存的进程了=============";
                FileUtil.writeFile(TRIM_MEMORY_RUNNING_CRITICAL);
                break;
            default:
                String defaultLevel = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          =============defaultLevel=============" + level;
                FileUtil.writeFile(defaultLevel);
                break;
        }
    }

    static void startService() {
        //检查服务是否不需要运行
        if (sShouldStopService) return;
        //若还没有取消订阅，说明任务仍在运行，为防止重复启动，直接 return
        if (sSubscription != null && !sSubscription.isUnsubscribed()) return;

        //----------业务逻辑----------

        System.out.println("检查磁盘中是否有上次销毁时保存的数据");


        //================本地记录日志================
        String content = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          检查磁盘中是否有上次销毁时保存的数据";

        FileUtil.writeFile(content);
        //=============本地记录日志==============

        sSubscription = Observable
                .interval(30, TimeUnit.SECONDS)
                //取消任务时取消定时唤醒
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("保存数据到磁盘。");
                        App.sApp.sendBroadcast(new Intent(WakeUpReceiver.ACTION_CANCEL_JOB_ALARM_SUB));

                        //=============本地记录日志=============
                        String content = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          保存数据到磁盘";
                        FileUtil.writeFile(content);
                        //=============本地记录日志==============
                    }
                }).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long count) {

                        System.out.println("每 30 秒采集一次数据... count = " + count);


                        //=============本地记录日志=============
                        String content = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          每 30 秒采集一次数据... count =" + count;
                        FileUtil.writeFile(content);
                        //=============本地记录日志==============


                        if (count > 0 && count % 18 == 0) {

                            System.out.println("保存数据到磁盘。 saveCount = " + (count / 18 - 1));

                            String text = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          ================保存数据到磁盘。 saveCount=" + (count / 18 - 1);
                            FileUtil.writeFile(text);
                        }
                    }
                });

        //----------业务逻辑----------
    }


    /**
     * 停止服务并取消定时唤醒
     * <p>
     * 停止服务使用取消订阅的方式实现，而不是调用 Context.stopService(Intent name)。因为：
     * 1.stopService 会调用 Service.onDestroy()，而 WorkService 做了保活处理，会把 Service 再拉起来；
     * 2.我们希望 WorkService 起到一个类似于控制台的角色，即 WorkService 始终运行 (无论任务是否需要运行)，
     * 而是通过 onStart() 里自定义的条件，来决定服务是否应当启动或停止。
     */
    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sSubscription != null) sSubscription.unsubscribe();
        //取消 Job / Alarm / Subscription
        App.sApp.sendBroadcast(new Intent(WakeUpReceiver.ACTION_CANCEL_JOB_ALARM_SUB));

        //停止唤醒CPU
        MyWakefulReceiver.completeWakefulIntent(serviceIntent);
    }


    /**
     * 1.防止重复启动，可以任意调用startService(Intent i);
     * 2.利用漏洞启动前台服务而不显示通知;
     * 3.在子线程中运行定时任务，处理了运行前检查和销毁时保存的问题;
     * 4.启动守护服务;
     * 5.守护 Service 组件的启用状态, 使其不被 MAT 等工具禁用.
     */
    int onStart(Intent intent, int flags, int startId) {
        //启动前台服务而不显示通知的漏洞已在 API Level 25 修复，大快人心！
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            //利用漏洞在 API Level 17 及以下的 Android 系统中，启动前台服务而不显示通知
            startForeground(HASH_CODE, new Notification());
            //利用漏洞在 API Level 18 及以上的 Android 系统中，启动前台服务而不显示通知
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                startService(new Intent(App.sApp, WorkNotificationService.class));
        }

        //启动守护服务，运行在:watch子进程中
        startService(new Intent(App.sApp, DaemonService.class));

        //----------业务逻辑----------

        //实际使用时，根据需求，将这里更改为自定义的条件，判定服务应当启动还是停止 (任务是否需要运行)
        if (sShouldStopService) stopService();
        else startService();

        //----------业务逻辑----------

        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), DaemonService.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        return START_STICKY;
    }


    void onEnd(Intent rootIntent) {
        System.out.println("保存数据到磁盘。");
        startService(new Intent(App.sApp, WorkService.class));
        startService(new Intent(App.sApp, DaemonService.class));
    }

    public static class WorkNotificationService extends Service {

        /**
         * 利用漏洞在 API Level 18 及以上的 Android 系统中，启动前台服务而不显示通知
         */
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(WorkService.HASH_CODE, new Notification());
            stopSelf();
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

}
