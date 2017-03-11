package example.fussen.daemon.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import example.fussen.daemon.account.SyncAdapter;

/**
 * Created by Fussen on 2017/2/23.
 *
 * 此服务需能交给操作系统使唤
 */

public class SyncService extends Service {


    // Storage for an instance of the sync adapter
    private static SyncAdapter sSyncAdapter = null;
    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    public SyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock) {
            sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
