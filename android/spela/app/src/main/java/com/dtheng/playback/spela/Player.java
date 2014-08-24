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

import com.dtheng.playback.spela.model.Response;
import com.dtheng.playback.spela.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        next = (Button)findViewById(R.id.next);

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

        previous.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        update(true, false, true, context);
                    }
                }
        );

        play.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    update(true, false, false, context);
                }
            }
        );

        pause.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    update(false, false, false, context);
                }
            }
        );

        next.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    update(true, true, false, context);
                }
            }
        );
    }

    private static boolean update(boolean play, boolean next, boolean previous, ContextWrapper context) {


        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://playback.dtheng.com/api");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("update", "true"));
            nameValuePairs.add(new BasicNameValuePair("user", ((User) IO.get("user", new TypeToken<User>(){}.getType(), context)).id));
            nameValuePairs.add(new BasicNameValuePair("state", play ? "PLAY" : "PAUSE"));
            nameValuePairs.add(new BasicNameValuePair("next", next +""));
            nameValuePairs.add(new BasicNameValuePair("previous", previous +""));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            return true;

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return false;
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
