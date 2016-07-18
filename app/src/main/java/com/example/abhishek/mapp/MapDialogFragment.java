package com.example.abhishek.mapp;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * THIS WORKS... OTHER COMMENTED STUFF IN AmapsActivity AND Dialo, mapview,mapdialog DONT WORK
 * STUFF IT NEEDS
 * TranspartMapFragement.java
 * newdialg_map.xml
 */

public class MapDialogFragment extends DialogFragment {

    private View view;

    private GoogleMap mMap;
    private double lat;
    private double lon;
    private String Message,reportid;
    TextView txt;
    Button btnyes,btnno,btnidk;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(),
                R.style.Theme_CustomDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.newdialog_map, null);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        //dialog.setTitle(Message);
        // Creating Full Screen
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        btnyes = (Button) view.findViewById(R.id.btnyes);
        btnno = (Button) view.findViewById(R.id.btnno);
        btnidk = (Button) view.findViewById(R.id.btnidk);

        txt=(TextView) view.findViewById(R.id.textView7);


        //TO GET LAT LON
        lat =getArguments().getDouble("lat");
        lon = getArguments().getDouble("lon");
        Message=getArguments().getString("Message");
        reportid = getArguments().getString("reportid");
        txt.setText(Message);


        final AMapsActivity aMapsActivity =  (AMapsActivity) getActivity();

        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMapsActivity.yesclick(reportid);
                //getTargetFragment().onActivityResult(getTargetRequestCode(),1 , getActivity().getIntent());
                dismiss();

            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMapsActivity.noclick(reportid);
                dismiss();

            }
        });

        btnidk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMapsActivity.idkclick(reportid);
                dismiss();

            }
        });

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);


        initializeViews();

    }

    private void initializeViews() {

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((TransparentMapFragment) getActivity().getFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            mMap.getUiSettings().setZoomControlsEnabled(false);

            if (isGoogleMapsInstalled()) {
                if (mMap != null) {
                    setUpMap();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("installGoogleMaps");
                builder.setCancelable(false);
                builder.setPositiveButton("install", getGoogleMapsListener());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void setUpMap() {

        final LatLng position = new LatLng(lat, lon);
        mMap.clear();
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.addMarker(new MarkerOptions().position(position).snippet(""));
        goToLocationZoom(lat,lon,12);
    }


    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }


    public boolean isGoogleMapsInstalled() {
        try {
            getActivity().getPackageManager().getApplicationInfo(
                    "com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public android.content.DialogInterface.OnClickListener getGoogleMapsListener() {
        return new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.apps.maps"));
                startActivity(intent);

                // Finish the fragment so they can't circumvent the check
                if (getActivity() != null) {
                    Fragment fragment = (getActivity().getFragmentManager()
                            .findFragmentByTag(MapFragment.class.getName()));
                    FragmentTransaction ft = getActivity().getFragmentManager()
                            .beginTransaction();
                    ft.remove(fragment);
                    ft.commitAllowingStateLoss();
                }
            }

        };
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        if (getActivity() != null) {
            try {
                Fragment fragment = (getActivity().getFragmentManager()
                        .findFragmentById(R.id.map));
                FragmentTransaction ft = getActivity().getFragmentManager()
                        .beginTransaction();
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            } catch (Exception e) {

            }
        }

    }


}