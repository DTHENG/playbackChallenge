package com.dtheng.playback.spela;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dtheng.playback.spela.model.Device;

/**
 * Created by danielthengvall on 8/23/14.
 */
public class DevicesAdapter extends ArrayAdapter<Device> {

    private final Activity context;
    private final Device[] devices;

    static class ViewHolder {
        public TextView buttonText;
    }

    public DevicesAdapter(Activity context, Device[] devices) {
        super(context, R.layout.devices_row, devices);
        this.context = context;
        this.devices = devices;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.devices_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.buttonText = (TextView) rowView.findViewById(R.id.secondLine);

            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.buttonText.setText("asdf");

        return rowView;
    }
}