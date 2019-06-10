package com.red_folder.beyondpodlistener;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;

public class MainActivity extends Activity
{
    static final String TAG = "MainActivity";

    private TextView _FeedName, _EpisodeName, _FileCount;
    private ProgressBar _Progress, _CurrentlyPlaying;
    private Button _Start, _Stop, _Push, _Reset;
    private LinearLayout _PodcastDetail, _WaitingForPodCastDetails;

    //private Intent listenerIntent;

    private ToBePushedFileObserver _toBePushedFileObserver;
    private DataPushJobService _dataPushJobService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AppCenter.start(getApplication(), BuildConfig.AppCenterSecretKey, Analytics.class, Crashes.class);

        setContentView(R.layout.activity_main);

        final Context currentContext = this;

        _PodcastDetail = (LinearLayout) findViewById(R.id.podcastDetails);
        _WaitingForPodCastDetails = (LinearLayout) findViewById(R.id.waitingForPodcastDetails);

        _FeedName = (TextView) findViewById(R.id.feedName);
        _EpisodeName = (TextView) findViewById(R.id.episodeName);
        _Progress = (ProgressBar) findViewById(R.id.progress);
        _CurrentlyPlaying = (ProgressBar) findViewById(R.id.currentlyPlaying);

        _FileCount = (TextView) findViewById(R.id.fileCount);

        _Start = (Button) findViewById(R.id.start);
        _Stop = (Button) findViewById(R.id.stop);
        _Push = (Button) findViewById(R.id.push);
        _Reset = (Button) findViewById(R.id.reset);

        _Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListenerService.class);
                intent.setAction(ListenerService.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);

                /*
                JobInfo.Builder builder = new JobInfo.Builder(DataPushJobService.JOBID, new ComponentName(currentContext, DataPushJobService.class));
                builder.setPeriodic(15* 60 * 1000);
                builder.setRequiredNetworkType(NETWORK_TYPE_ANY);
                JobInfo jobInfo = builder.build();

                JobScheduler js = getSystemService(JobScheduler.class);
                js.schedule(jobInfo);
                Log.i(TAG, "JOB SCHEDULED!");
                */
            }
        });

        _Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListenerService.class);
                intent.setAction(ListenerService.ACTION_STOP_FOREGROUND_SERVICE);
                stopService(intent);

                _WaitingForPodCastDetails.setVisibility(View.VISIBLE);
                _PodcastDetail.setVisibility(View.GONE);
            }
        });

        _Push.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               Thread t = new Thread() {
                   public void run() {
                       Looper.prepare(); //For Preparing Message Pool for the child Thread

                       DataPush dataPush = new DataPush(currentContext);
                       dataPush.run();

                       Looper.loop(); //Loop in the message queue
                   }
               };

               t.start();
           }
        });

        _Reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Thread t = new Thread() {
                    public void run() {
                        Looper.prepare(); //For Preparing Message Pool for the child Thread

                        DataPush dataPush = new DataPush(currentContext);
                        dataPush.reset();

                        Looper.loop(); //Loop in the message queue
                    }
                };

                t.start();
            }
        });

        if (!DataPushJobService.isScheduled(this)) {
            DataPushJobService.scheduleJob(this);
        }
    }

    protected void onResume()
    {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BeyondPodReceiver.PLAYBACK_STATUS_ACTION);
        filter.addAction(ToBePushedFileObserver.TO_BE_PUSHED_CHANGE_ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        // TODO - Request latest pod details

        _toBePushedFileObserver = new ToBePushedFileObserver(this, getFilesDir());
        _toBePushedFileObserver.startWatching();

        _FileCount.setText(String.valueOf(_toBePushedFileObserver.getCount()));
    };

    protected void onPause()
    {
        super.onPause();

        _toBePushedFileObserver.stopWatching();
        _toBePushedFileObserver = null;

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    };

    protected  void onDestroy()
    {
        super.onDestroy();

        if (DataPushJobService.isScheduled(this)) {
            DataPushJobService.cancelJob(this);
        }
    }

    /*
    View.OnClickListener _ControlsListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String cmd = null;
            switch (v.getId())
            {
                case R.id.start:
                    startForegroundService(listenerIntent);
                    break;
                case R.id.stop:
                    stopService(listenerIntent);
                    break;
            }
        }
    };
    */

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == BeyondPodReceiver.PLAYBACK_STATUS_ACTION) {
                try {
                    _WaitingForPodCastDetails.setVisibility(View.GONE);
                    _PodcastDetail.setVisibility(View.VISIBLE);

                    _FeedName.setText(intent.getStringExtra("FeedName"));
                    _EpisodeName.setText(intent.getStringExtra("EpisodeName"));
                    long duration = intent.getLongExtra("EpisodeDuration", -1);
                    long position = intent.getLongExtra("EpisodePosition", -1);
                    boolean playing = intent.getBooleanExtra("Playing", false);

                    _Progress.setMax((int) duration);
                    _Progress.setProgress((int) position);

                    if (playing) {
                        _CurrentlyPlaying.setVisibility(View.VISIBLE);
                    } else {
                        _CurrentlyPlaying.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Unable to update current podcast details", ex);
                }
            }

            if (action == ToBePushedFileObserver.TO_BE_PUSHED_CHANGE_ACTION) {
                try {
                    _FileCount.setText(String.valueOf(intent.getIntExtra("FileCount", 0)));
                } catch (Exception ex) {
                    Log.e(TAG, "Unable to set file count", ex);
                }
            }
        }
    };
}
