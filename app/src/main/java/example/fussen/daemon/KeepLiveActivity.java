package example.fussen.daemon;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

public class KeepLiveActivity extends Activity {

    private static final String TAG = "[KeepLiveActivity]";
    public static WeakReference<KeepLiveActivity> instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = new WeakReference<>(this);
        Window window = getWindow();
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = 0;
        attributes.y = 0;
        attributes.height = 1;
        attributes.width = 1;
        window.setAttributes(attributes);

        Log.d(TAG, "============onCreate==========");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "=============onResume==========");
        if (isScreenOn()) {
            finishSelf();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=============onDestroy==========");
        if (instance != null && instance.get() == this) {
            instance = null;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        finishSelf();
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        finishSelf();
        return super.onTouchEvent(motionEvent);
    }


    /**
     * 关闭自己
     */
    public void finishSelf() {
        if (!isFinishing()) {
            finish();
        }
    }

    /**
     * 判断主屏幕是否点亮
     *
     * @return
     */
    private boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return powerManager.isInteractive();
        } else {
            return powerManager.isScreenOn();
        }
    }
}
