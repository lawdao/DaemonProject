package example.fussen.daemon;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import example.fussen.daemon.receiver.ScreenReceiver;
import example.fussen.daemon.service.WorkService;


/**
 * Created by Fussen on 2017/2/21.
 */

public class App extends Application {


    static String ACCOUNT_TYPE = "example.fussen.daemon";
    static String CONTENT_AUTHORITY = "example.fussen.daemon.provider";
    public static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        //我们现在需要服务运行, 将标志位重置为 false
        WorkService.sShouldStopService = false;
        startService(new Intent(this, WorkService.class));

        registerScreenReceiver();

        addAccount();


    }




    /**
     * 启用账户自动同步
     */
    public void addAccount() {
        //添加账号

        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);

        Account account = null;

        Account[] accountsByType = accountManager.getAccountsByType(ACCOUNT_TYPE);

        if (accountsByType.length > 0) {
            account = accountsByType[0];
        } else {
            account = new Account(getString(R.string.app_name), ACCOUNT_TYPE);
        }

        if (accountManager.addAccountExplicitly(account, null, null)) {

            //开启同步，设置同步周期  30秒
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, CONTENT_AUTHORITY, Bundle.EMPTY, 30);
        }

    }


    /**
     * 注册screen状态广播接收器
     */
    private void registerScreenReceiver() {

        ScreenReceiver screenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenReceiver, filter);
    }

    public static Context getContext() {
        return sApp;
    }
}
