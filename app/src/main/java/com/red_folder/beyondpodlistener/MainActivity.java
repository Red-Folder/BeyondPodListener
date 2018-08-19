/*
 ** Copyright 2013, BeyondPod Team
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 *
 *  You can find the latest version of BeyondPod's public API at:
 *  http://www.beyondpod.mobi/android/help/FAQAPIs.htm
 */

package com.red_folder.beyondpodlistener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class MainActivity extends Activity
{
    TextView _Console, _Console1;
    Button _Play, _Pause, _Prev, _Next;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _Console = (TextView) findViewById(R.id.console);
        _Console1 = (TextView) findViewById(R.id.console1);

        TabHost tabs = (TabHost) this.findViewById(R.id.tabhost);
        tabs.setup();
        TabSpec tspec1 = tabs.newTabSpec("BeyondPod");
        tspec1.setIndicator("BeyondPod");
        tspec1.setContent(R.id.tab1);
        tabs.addTab(tspec1);

        TabSpec tspec2 = tabs.newTabSpec("Music");
        tspec2.setIndicator("Music");
        tspec2.setContent(R.id.tab2);
        tabs.addTab(tspec2);

        _Play = (Button) findViewById(R.id.play);
        _Pause = (Button) findViewById(R.id.pause);
        _Next = (Button) findViewById(R.id.next);
        _Prev = (Button) findViewById(R.id.prev);

        _Play.setOnClickListener(_TrasportControlsListener);
        _Pause.setOnClickListener(_TrasportControlsListener);
        _Next.setOnClickListener(_TrasportControlsListener);
        _Prev.setOnClickListener(_TrasportControlsListener);

        _Console.setText("Use the tabs to switch between the various BeyondPod brodcast notifications");

        _Console1.setText("Music broadcasts are disabled by default. To enable use: Menu > More > Settings > " +
                " Menu (Press Menu agan) > Advanced Settings > Publish Current Episode");

    }

    protected void onResume()
    {

        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("mobi.beyondpod.action.PLAYBACK_STATUS");

        /*
        filter.addAction("com.android.music.metachanged");
        filter.addAction("com.android.music.playstatechanged");
        filter.addAction("com.android.music.playbackcomplete");
        filter.addAction("com.android.music.queuechanged");
        filter.addAction("com.android.music.metachanged");
        */

        registerReceiver(mReceiver, filter);
    };

    protected void onPause()
    {

        super.onPause();
        unregisterReceiver(mReceiver);
    };

    View.OnClickListener _TrasportControlsListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String cmd = null;
            switch (v.getId())
            {
                case R.id.play:
                    cmd = "mobi.beyondpod.command.PLAY";
                    break;

                case R.id.pause:
                    cmd = "mobi.beyondpod.command.PAUSE";
                    break;

                case R.id.next:
                    cmd = "mobi.beyondpod.command.PLAY_NEXT";
                    break;

                case R.id.prev:
                    cmd = "mobi.beyondpod.command.PLAY_PREVIOUS";
                    break;

            }

            if (cmd != null)
            {
                Intent ibp = new Intent(cmd);
                sendBroadcast(ibp);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private BroadcastReceiver mReceiver = new BeyondPodReceiver();
}
