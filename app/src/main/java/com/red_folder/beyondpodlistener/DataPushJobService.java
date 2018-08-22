package com.red_folder.beyondpodlistener;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.IBinder;

public class DataPushJobService extends JobService {
    public static final int JOBID = 1221;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        DataPush dataPush = new DataPush(this);
        dataPush.run();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
