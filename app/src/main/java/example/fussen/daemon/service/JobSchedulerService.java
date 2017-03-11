package example.fussen.daemon.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import example.fussen.daemon.App;


/**
 * Created by Fussen on 2017/2/21.
 * Android 5.0+ 使用的 JobScheduler.
 * 运行在 :Daemon 子进程中.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        startService(new Intent(App.sApp, WorkService.class));
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
