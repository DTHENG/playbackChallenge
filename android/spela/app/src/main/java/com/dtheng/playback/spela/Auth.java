package com.dtheng.playback.spela;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dtheng.playback.spela.model.User;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Auth extends Base {

    private Button playButton;
    private EditText firstName;
    private EditText lastInitial;
    private ContextWrapper context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        playButton = (Button)findViewById(R.id.playButton);
        firstName = (EditText)findViewById(R.id.firstName);
        lastInitial = (EditText)findViewById(R.id.lastInitial);
        context = this;

        playButton.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    String firstNameStr = firstName.getText().toString();
                    String lastInitialStr = lastInitial.getText().toString();
                    String deviceId = "Galaxy S3";
                    if (authenticate(firstNameStr, lastInitialStr, deviceId)) {

                        User newUser = new User();
                        newUser.firstName = firstNameStr;
                        newUser.lastInitial = lastInitialStr;
                        newUser.id = firstNameStr + lastInitialStr;
                        IO.set(newUser, "user", context);
                    }
                }
            }
        );
    }

    public final boolean authenticate(String firstName, String lastInitial, String device) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://playback.dtheng.com/api");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("auth", "true"));
            nameValuePairs.add(new BasicNameValuePair("first_name", firstName));
            nameValuePairs.add(new BasicNameValuePair("last_initial", lastInitial));
            nameValuePairs.add(new BasicNameValuePair("device_id", device));
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
        getMenuInflater().inflate(R.menu.auth, menu);
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
