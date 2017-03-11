package example.fussen.daemon.account;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import example.fussen.daemon.App;
import example.fussen.daemon.service.WorkService;
import example.fussen.daemon.utils.FileUtil;
import example.fussen.daemon.utils.TimeUitl;

/**
 * Created by Fussen on 2017/2/23.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {


    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d("account sync", "-----------onPerformSync--------");

        mContext.startService(new Intent(App.sApp, WorkService.class));
        //=============本地记录日志=============
        String content = "time: " + TimeUitl.parse(System.currentTimeMillis()) + "          =================账户定时拉活服务================";
        FileUtil.writeFile(content);
        //=============本地记录日志==============
    }
}
