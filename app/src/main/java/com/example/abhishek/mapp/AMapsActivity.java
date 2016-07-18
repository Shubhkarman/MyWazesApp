package com.example.abhishek.mapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.mapp.other.Utils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;



import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class AMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    Button btnlogout, btnfrnd, btnshared;

    LatLng arr[] = new LatLng[100];
    int count = 0;


    private Utils utils;

    // Client name
    private String name = null;





    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";


    // For connecting with Service
    FirstService bindedService;
    boolean status;
    // SERVICE CONNECTION FOR SERVICE
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FirstService.LocalBinder binder = (FirstService.LocalBinder) service;
            bindedService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    // TO BIND WITH SERVICE
    public void bindS(View v) {
        Intent intent = new Intent(this, FirstService.class);
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
        status = true;
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showToast("BroadCast received");
            String message = intent.getStringExtra("message");
            parseMessage(message);
            Log.d("receiver", "Got message: " + message);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amapss);
        if (googleServicesAvailable()) {
            Toast.makeText(this, "perrrfecto", Toast.LENGTH_SHORT).show();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        btnlogout = (Button) findViewById(R.id.logoutbutton);

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindedService.sendMessageToServer(utils.getSendLogoutJSON());
//                Intent intent = new Intent(AMapsActivity.this,
//                        NameActivity.class);
//                startActivity(intent);
            }
        });


        btnshared = (Button) findViewById(R.id.button6);

        btnshared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AMapsActivity.this,
                        SharedFriendActivity.class);
                startActivity(intent);
            }
        });


        btnfrnd = (Button) findViewById(R.id.btnreport);

        btnfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AMapsActivity.this,
                        FriendSearch.class);
                startActivity(intent);
               // Double latitude = 30.7177071250852, longitude = 76.78357768803835;
               // DialogAppearReport("0", "Heavy Traffic", latitude, longitude);
//            DialogAppearReport("shubi", "Heavy Traffic");
//                setMarker("", latitude, longitude);
//                goToLocationZoom(latitude - 0.005,longitude,15);


//      bindedService.sendMessageToServer(utils.getSendReportJSON());
//                Intent intent = new Intent(AMapsActivity.this,
//                        NameActivity.class);
//                startActivity(intent);
            }
        });


        utils = new Utils(getApplicationContext());
        //for broadcast receiver
        this.registerReceiver(receiver, new IntentFilter("broadcast"));


        Intent intent = new Intent(this, FirstService.class);
        // bcuz service already started by name activity
        //startService(intent);

        bindService(intent, sc, Context.BIND_AUTO_CREATE);
        status = true;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapmapTypeNone:
                mMap.setMapType(mMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mMap.setMapType(mMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mMap.setMapType(mMap.MAP_TYPE_HYBRID);
                break;
            case R.id.action_report:
               Intent i = new Intent(AMapsActivity.this,Report.class);
                startActivity(i);
                break;
            case R.id.action_goto:
                Intent intent = new Intent(AMapsActivity.this,Goto.class);
                startActivity(intent);
                break;


            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "cannt connect to google play services", Toast.LENGTH_LONG).show();

        }
        return false;

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.textView);


                    TextView tvLat = (TextView) v.findViewById(R.id.textView2);
                    TextView tvLng = (TextView) v.findViewById(R.id.textView3);
                    LatLng ll = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvLat.setText("LAT=" + ll.latitude);
                    tvLng.setText("LNG=" + ll.longitude);
                    // arr[count++]=ll;

                    //send current location
                    bindedService.sendMessageToServer(utils.getSendCurrentLocationJSON(ll));
                    bindedService.sendMessageToServer(utils.getSendReportJSON(ll,"Heavy Traffic"));

                    return v;
                }
            });
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));

            }
        });
        // Add a marker in Sydney and move the camera
      /*  LatLng sydney = new LatLng(-34, 151);
        LatLng Marker2 = new LatLng(-34.563,78.23);
        LatLng Marker1 = new LatLng(-34.546,78.23);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(Marker1).title("Marker1"));
        mMap.addMarker(new MarkerOptions().position(Marker2).title("Marker2"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        goToLocationZoom(30.7333, 76.779, 12);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
/*        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
*/
    }

    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mMap.moveCamera(update);

    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }

    Marker marker;

    public void geoLocate(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString().trim();
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        Address address = list.get(0);
        String locality = address.getLocality();
        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        goToLocationZoom(lat, lng, 15);
        setMarker(locality, lat, lng);


    }

    Circle circle;
    Marker marker1;

    /*//Marker marker2;
    //Polyline line;
 */
    private void setMarker(String title, double lat, double lng) {
        if (marker != null) {
            removeEveryThing();
        }
        MarkerOptions options = new MarkerOptions()
                .title(title)
                .position(new LatLng(lat, lng));
        if (marker1 == null) {
            marker1 = mMap.addMarker(options);
            //}else if(marker2==null)
            //{
            //   marker2=mMap.addMarker(options);
            //  drawLine();
        } else {
            removeEveryThing();
            marker1 = mMap.addMarker(options);
        }
        //    circle = drawCircle(new LatLng(lat,lng));
    }

    /*private void drawLine() {
        PolylineOptions options = new PolylineOptions()
                                .add(marker1.getPosition())
                                .add(marker2.getPosition())
                                        .color(Color.BLUE)
                .width(3);
                                        line = mMap.addPolyline(options);
    }

     private Circle drawCircle(LatLng latLng) {
          CircleOptions options =new CircleOptions()
                  .center(latLng)
                  .radius(1000)
                  .fillColor(0x33FF0000)
                  .strokeWidth(3)
                  .strokeColor(Color.BLUE);
          return mMap.addCircle(options);
      }
    */
    private void removeEveryThing() {
        marker1.remove();
        marker1 = null;
        //marker2.remove();
        //marker2=null;
        //line.remove();
        // circle.remove();
        //circle=null;

    }


    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See  the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(this, "cannt get current location", Toast.LENGTH_LONG).show();
        } else {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
            mMap.animateCamera(update);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AMaps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.abhishek.mapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AMaps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.abhishek.mapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    /**
     * Parsing the JSON message received from server The intent of message will
     * be identified by JSON node 'flag'. flag = self, message belongs to the
     * person. flag = new, a new person joined the conversation. flag = message,
     * a new message received from server. flag = exit, somebody left the
     * conversation.
     */
    private void parseMessage(final String msg) {

        try {
            JSONObject jObj = new JSONObject(msg);
            final String success = "Success", fail = "Fail", location = "location", logout = "logout", friendrequest = "friendrequest", friendconfirm = "friendconfirm",
                    reportquery = "reportquery", reportupdate = "reportupdate";

            String flag = jObj.getString("flag");
            // if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
            // if the flag is 'message', new message received

            String what = jObj.getString("what");
            String sessionId = jObj.getString("sessionId");

            if (sessionId.equals(utils.getSessionId().toString())) {
                if (what.equals(location)) {
                    String message = jObj.getString("message");

                    if (message.equals(success)) {
                        showToast("Location Saved");

                    } else {
                        showToast("Location not saved");
                    }

                } else if (what.equals(logout)) {
                    String message = jObj.getString("message");


                    if (message.equals(success)) {
                        showToast("Logout Succesful");
                        Intent intent = new Intent(AMapsActivity.this,
                                NameActivity.class);
                        startActivity(intent);
                    } else if (message.equals(fail)) {
                        showToast("Logout Unsuccessful ?");

                    }
                } else if (what.equals(friendrequest)) {
                    String fromid = jObj.getString("fromid");
                    showToast("Friend Request From " + fromid);
                    DialogAppear(fromid, "Friend Request");
                    //bindedService.sendMessageToServer(utils.getSendFriendConfirmJSON(fromid));
                    //showToast("Friend Request Confirmed From "+fromid);
                } else if (what.equals(friendconfirm)) {
                    String fromid = jObj.getString("fromid");
                    String message = jObj.getString("message");

                    if (message.equals(success)) {
                        showToast("Friend Request was confirmed by " + fromid);
                    } else if (message.equals(fail)) {
                        showToast("Friend Request was rejected by " + fromid);

                    }
                } else if (what.equals(reportquery)) {
                    String from = jObj.getString("from");
                    String reportid = jObj.getString("reportid");
                    String message = jObj.getString("message");
                    Double latitude = Double.parseDouble(jObj.getString("latitude"));
                    Double longitude = Double.parseDouble(jObj.getString("longitude"));
                    showToast("Report From " + from);
                    DialogAppearReport(reportid, message, latitude, longitude);

//                    DialogAppearReport("shubi", "Heavy Traffic", latitude, longitude);
                    //bindedService.sendMessageToServer(utils.getSendFriendConfirmJSON(fromid));
                    //showToast("Friend Request Confirmed From "+fromid);
//                        setMarker(reportquery, latitude, longitude);
//                        goToLocationZoom(latitude-0.005,longitude,15);
                } else if (what.equals(reportupdate)) {
                    String message = jObj.getString("message");
                    Double latitude = Double.parseDouble(jObj.getString("latitude"));
                    Double longitude = Double.parseDouble(jObj.getString("longitude"));
                    showToast("New Report " + message + " Uploaded");
                    setMarker(message, latitude, longitude);
                    goToLocationZoom(latitude, longitude, 15);
                }

            }
            //}
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void DialogAppearReport(final String reportid, final String Message, Double lat, Double lon) {
//        Intent intent = new Intent(AMapsActivity.this,MapDialogFragment.class);
//        startActivity(intent);

        FragmentManager fm = getSupportFragmentManager();
        MapDialogFragment mapDialogFragment = new MapDialogFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);
        args.putString("Message", Message);
        args.putString("reportid", reportid);
        mapDialogFragment.setArguments(args);
        mapDialogFragment.show(fm, "fragment_edit_name");

    }
    //java.net.ConnectException: failed to connect to /192.168.1.106 (port 8080): connect failed: ETIMEDOUT (Connection timed out)





    public void yesclick(String reportid) {
        showToast("Sent");
        bindedService.sendMessageToServer(utils.getSendReportConfirmJSON(reportid, "Yes"));
    }

    public void noclick(String reportid) {
        showToast("Sent");
        bindedService.sendMessageToServer(utils.getSendReportConfirmJSON(reportid, "No"));
    }
    public void idkclick(String reportid) {
        showToast("Sent");
        bindedService.sendMessageToServer(utils.getSendReportConfirmJSON(reportid, "IDK"));
    }





/*
    private void DialogAppearReport(final String fromid, final String Message,Double Lat,Double Lon) {

        final String reportquery = "reportquery", Title = "Report Query";
        MapView mv = (MapView) findViewById(R.id.map_view);

        final Dialog d = new Dialog(AMapsActivity.this);
      //  d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
       // d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(mv);
//        mv.getMap().clear();
//        mv.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(h.getPosicion(), 17));
//        final MarkerOptions options = new MarkerOptions();
//        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//        options.position(h.getPosicion());
//        mv.getMap().addMarker(options);
        d.show();



    }

*/

/*    private void DialogAppearReport(final String fromid, final String Message) {

        final String reportquery="reportquery",Title = "Report Query";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Title);
        builder.setMessage("Do you accept " + Message + " from " + fromid);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                bindedService.sendMessageToServer(utils.getSendFriendConfirmJSON(fromid, "Success"));


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                bindedService.sendMessageToServer(utils.getSendFriendConfirmJSON(fromid, "Fail"));


            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


*/


    private void DialogAppear(final String fromid, final String Title) {

        final String friendrequest = "Friend Request",reportquery="reportquery";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Title);
        builder.setMessage("Do you accept " + Title + " from " + fromid);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Title.equals(friendrequest)) {
                    bindedService.sendMessageToServer(utils.getSendFriendConfirmJSON(fromid, "Success"));
                }

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Title.equals("Friend Request")) {
                    bindedService.sendMessageToServer(utils.getSendFriendConfirmJSON(fromid, "Fail"));
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }




    private void showToast(final String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Plays device's default notification sound
     * */
    public void playBeep() {

        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}