package com.example.abhishek.mapp;


import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by papa on 19-04-2016.
 */
//public class DialogMapFragment extends DialogFragment {
public class Dialo extends DialogFragment {
    private SupportMapFragment fragment;

    public Dialo() {
        fragment = new SupportMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapdialog, container, false);
        getDialog().setTitle("Map");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //transaction.add(R.id.mapView, fragment).commit();

        return view;
    }



    public SupportMapFragment getFragment() {
        return fragment;
    }
}