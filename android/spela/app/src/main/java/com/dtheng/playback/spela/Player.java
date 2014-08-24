package com.dtheng.playback.spela;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

        //SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.filename);
        //Picture picture = svg.getPicture();
        //Drawable drawable = svg.createPictureDrawable();



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

                        if ( ! isAuth) return;
                        if (isUpdating) return;

                        isUpdating = true;

                        InputStream inputStream = null;
                        String result = null;
                        try {

                            // create HttpClient
                            HttpClient httpclient = new DefaultHttpClient();

                            // make GET request to the given URL
                            HttpResponse httpResponse = httpclient.execute(
                                    new HttpGet("http://playback.dtheng.com/api?user="+
                                            ((User) IO.get("user", new TypeToken<User>(){}.getType(), context)).id));

                            // receive response as inputStream
                            inputStream = httpResponse.getEntity().getContent();

                            // convert inputstream to string
                            if(inputStream != null)
                                result = convertInputStreamToString(inputStream);

                        } catch (Exception e) {
                            //Log.d("InputStream", e.getLocalizedMessage());
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
                            System.out.println(new Gson().toJson(response));

                            previous.setEnabled(response.previous != null);
                            next.setEnabled(response.next != null);

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
                                    play.setEnabled(false);
                                    pause.setEnabled(true);
                                    break;
                                }
                                case PAUSE: {
                                    double elapsed = (double) response.position;
                                    double percent = (elapsed / (double) response.current.length) * 100d;

                                    progressBar.setProgress((int) percent);
                                    play.setEnabled(true);
                                    pause.setEnabled(false);
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
        return super.onOptionsItemSelected(item);
    }
}
