package com.dtheng.playback.spela;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by danielthengvall on 8/23/14.
 */
public class Devices extends Base implements DevicesFragment.OnFragmentInteractionListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        frame.setId(R.id.container);
        setContentView(frame, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        if (savedInstanceState == null) {
            DevicesFragment newFragment = new DevicesFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container, newFragment).commit();
        }
    }

    public void onFragmentInteraction(Uri uri) { }

    public void onFragmentInteraction(String str) { }

}