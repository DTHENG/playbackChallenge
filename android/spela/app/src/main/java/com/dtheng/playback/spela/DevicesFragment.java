package com.dtheng.playback.spela;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.dtheng.playback.spela.model.Device;

import java.util.List;
import java.util.Map;

/**
 * Created by danielthengvall on 8/23/14.
 */
public class DevicesFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private DevicesAdapter mAdapter;

    public DevicesFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Device[] devices = new Device[2];

        Device mac = new Device();
        mac.is_playing = true;
        mac.id = 0;
        mac.name = "MacBook Pro";
        devices[0] = mac;

        Device iPhone = new Device();
        iPhone.is_playing = false;
        iPhone.id = 1;
        iPhone.name = "iPhone";
        devices[1] = iPhone;


        mAdapter = new DevicesAdapter(getActivity(), devices);
        View view = inflater.inflate(R.layout.fragment_devices_list, container, false);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null == mListener) return;
        System.out.println("clicked!");
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }
}