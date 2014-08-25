package com.dtheng.playback.spela;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtheng.playback.spela.model.Device;
import com.dtheng.playback.spela.model.Response;
import com.dtheng.playback.spela.model.User;
import com.google.gson.Gson;
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

/**
 * author : Daniel Thengvall
 */
public class Devices extends ListActivity {

    private List<Device> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devices_list);
        if (devices == null) {
            InputStream inputStream = null;
            String result = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(
                        new HttpGet("http://playback.dtheng.com/api?user=" +
                                ((User) IO.get("user", new TypeToken<User>() {
                                }.getType(), this)).id));
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
            } catch (Exception e) {
            }
            if (result == null) {
                devices = new ArrayList<Device>();
                return;
            }
            Response response = new Gson().fromJson(result, new TypeToken<Response>() {}.getType());
            devices = response.devices;
        }
        ArrayAdapter<Device> adapter = new ArrayAdapter<Device>(this,
                android.R.layout.simple_list_item_1, devices);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://playback.dtheng.com/api");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("update", "true"));
            nameValuePairs.add(new BasicNameValuePair("user", ((User) IO.get("user", new TypeToken<User>() {}.getType(), this)).id));
            nameValuePairs.add(new BasicNameValuePair("device_id", devices.get(position) +""));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        finish();
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
}