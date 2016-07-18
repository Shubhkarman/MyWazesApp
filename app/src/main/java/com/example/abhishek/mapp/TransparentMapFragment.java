package com.example.abhishek.mapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.MapFragment;

/**
 * Created by papa on 21-04-2016.
 */
public class TransparentMapFragment extends MapFragment {

    public TransparentMapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view,
                             Bundle savedInstance) {
        View layout = super.onCreateView(inflater, view, savedInstance);

        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setBackgroundColor(getResources().getColor(
                android.R.color.transparent));
        ((ViewGroup) layout).addView(frameLayout, new ViewGroup.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        return layout;
    }

    public static TransparentMapFragment newInstance(String abc) {
        TransparentMapFragment tsf = new TransparentMapFragment();
        return tsf;
    }
}