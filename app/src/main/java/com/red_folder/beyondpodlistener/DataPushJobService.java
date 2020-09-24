package com.red_folder.beyondpodlistener;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.List;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;

public class DataPushJobService extends JobService {
    private static String TAG = "DataPushJobService";

    public static final int JOBID = 1221;

    private Thread mThread;
    final Runnable mWorker = new Runnable() {
        @Override public void run() {
            Log.v(TAG, "Starting Data Push");
            DataPush dataPush = new DataPush(DataPushJobService.this);
            dataPush.run();

            jobFinished(mRunningParams, false);
            Log.v(TAG, "Finished Data Push");
        }
    };

    JobParameters mRunningParams;

    public static void scheduleJob(Context context) {
        JobInfo.Builder builder = new JobInfo.Builder(DataPushJobService.JOBID, new ComponentName(context, DataPushJobService.class));
        builder.setPeriodic(15* 60 * 1000);
        builder.setRequiredNetworkType(NETWORK_TYPE_ANY);
        JobInfo jobInfo = builder.build();

        JobScheduler js = context.getSystemService(JobScheduler.class);
        js.schedule(jobInfo);
        Log.i(TAG, "JOB SCHEDULED!");
    }

    public static boolean isScheduled(Context context) {
        JobScheduler js = context.getSystemService(JobScheduler.class);
        List<JobInfo> jobs = js.getAllPendingJobs();
        if (jobs == null) {
            return false;
        }
        for (int i=0; i<jobs.size(); i++) {
            if (jobs.get(i).getId() == DataPushJobService.JOBID) {
                return true;
            }
        }
        return false;
    }

    // Cancel this job, if currently scheduled.
    public static void cancelJob(Context context) {
        JobScheduler js = context.getSystemService(JobScheduler.class);
        js.cancel(DataPushJobService.JOBID);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        if (!AppCenter.isConfigured()) {
            AppCenter.start(getApplication(), BuildConfig.AppCenterSecretKey, Analytics.class, Crashes.class);
        }

        Log.v(TAG, "onStartJob");
        mRunningParams = jobParameters;

        Log.v(TAG, "Starting worker");
        mThread = new Thread(mWorker);
        mThread.start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
