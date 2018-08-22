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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;

public class MainActivity extends Activity
{
    static final String TAG = "MainActivity";

    private Button _Start, _Stop, _Push, _Reset;
    //private Intent listenerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context currentContext = this;

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
                startService(intent);
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
    }

    protected void onResume()
    {
        super.onResume();
    };

    protected void onPause()
    {
        super.onPause();
    };

    protected  void onDestroy()
    {
        super.onDestroy();
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
}
