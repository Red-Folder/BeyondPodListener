package com.red_folder.beyondpodlistener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends Activity
{
    private Button _Start, _Stop;
    //private Intent listenerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _Start = (Button) findViewById(R.id.start);
        _Stop = (Button) findViewById(R.id.stop);

        _Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListenerService.class);
                intent.setAction(ListenerService.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);
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
        /*
        final Context currentContext = this;
        Thread t = new Thread() {
            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread

                DataPush push = new DataPush(currentContext);
                if (push.ready()) {
                    PodModel model = new PodModel();
                    model.setEpisodeName("Hello World");
                    push.push(model.toJson());
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
        */
        //listenerIntent = new Intent(this, ListenerService.class);
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
