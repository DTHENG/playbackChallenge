package com.dtheng.playback.spela;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtheng.playback.spela.model.User;

public class Player extends Base {

    private Button previous;
    private Button play;
    private Button pause;
    private Button next;

    private Button devices;

    private ProgressBar progressBar;

    private TextView title;
    private TextView artist;

    private ContextWrapper context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        previous = (Button)findViewById(R.id.previous);
        play = (Button)findViewById(R.id.play);
        pause = (Button)findViewById(R.id.pause);

        devices = (Button)findViewById(R.id.device);

        context = this;

        devices.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent devicesView = new Intent(context, Devices.class);
                        context.startActivity(devicesView);
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
