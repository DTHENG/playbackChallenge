package com.dtheng.playback.spela;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtheng.playback.spela.model.Device;
import com.dtheng.playback.spela.model.Response;
import com.dtheng.playback.spela.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * author : Daniel Thengvall
 */
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

    private boolean isAuth;
    private boolean isUpdating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        previous = (Button)findViewById(R.id.previous);
        play = (Button)findViewById(R.id.play);
        pause = (Button)findViewById(R.id.pause);
        next = (Button)findViewById(R.id.next);
        title = (TextView)findViewById(R.id.title);
        artist = (TextView)findViewById(R.id.artist);
        devices = (Button)findViewById(R.id.device);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
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
        isAuth = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if ( ! isAuth || isUpdating) return;
                        isUpdating = true;
                        InputStream inputStream = null;
                        String result = null;
                        try {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpResponse httpResponse = httpclient.execute(
                                    new HttpGet("http://playback.dtheng.com/api?user="+
                                            ((User) IO.get("user", new TypeToken<User>(){}.getType(), context)).id));
                            inputStream = httpResponse.getEntity().getContent();
                            if(inputStream != null)
                                result = convertInputStreamToString(inputStream);
                        } catch (Exception e) {
                        }
                        if (result == null) {
                            isAuth = false;
                            context.startActivity(new Intent(context, Auth.class));
                            isUpdating = false;
                            return;
                        }
                        try {
                            Response response = new Gson().fromJson(result, new TypeToken<Response>() {}.getType());
                            if (response == null) {
                                isAuth = false;
                                context.startActivity(new Intent(context, Auth.class));
                                isUpdating = false;
                                return;
                            }
                            if (response.previous == null) {
                                previous.setBackgroundResource(R.drawable.prev_btn_disabled);
                            } else {
                                previous.setBackgroundResource(R.drawable.prev);
                            }
                            if (response.next == null) {
                                next.setBackgroundResource(R.drawable.next_btn_disabled);
                            } else {
                                next.setBackgroundResource(R.drawable.next);
                            }
                            title.setText(response.current.title);
                            artist.setText(response.current.artist);
                            for (Device device : response.devices) {
                                if (device.is_playing) {
                                    devices.setText(device.name);
                                    break;
                                }
                            }
                            switch (response.state) {
                                case PLAY: {
                                    double elapsed = (double) (response.current_time - response.current.started) / 1000d;
                                    double percent = (elapsed / (double) response.current.length) * 100d;
                                    progressBar.setProgress((int) percent);
                                    play.setBackgroundResource(R.drawable.play_btn_active);
                                    pause.setBackgroundResource(R.drawable.pause);
                                    break;
                                }
                                case PAUSE: {
                                    double elapsed = (double) response.position;
                                    double percent = (elapsed / (double) response.current.length) * 100d;
                                    progressBar.setProgress((int) percent);
                                    play.setBackgroundResource(R.drawable.play);
                                    pause.setBackgroundResource(R.drawable.pause_btn_active);
                                }
                            }
                        } catch (JsonSyntaxException jse) {
                            isAuth = false;
                            context.startActivity(new Intent(context, Auth.class));
                        }
                        isUpdating = false;
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        isAuth = true;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    private static boolean update(boolean play, boolean next, boolean previous, ContextWrapper context) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://playback.dtheng.com/api");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("update", "true"));
            nameValuePairs.add(new BasicNameValuePair("user", ((User) IO.get("user", new TypeToken<User>(){}.getType(), context)).id));
            nameValuePairs.add(new BasicNameValuePair("state", play ? "PLAY" : "PAUSE"));
            nameValuePairs.add(new BasicNameValuePair("next", next +""));
            nameValuePairs.add(new BasicNameValuePair("previous", previous +""));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
            return true;
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player, menu);
        return true;
    }
}