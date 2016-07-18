package com.example.abhishek.mapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.mapp.other.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendSearch extends Activity {

    private Utils utils;

    Button btnsend;
    EditText emailid;


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
            status=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    // TO BIND WITH SERVICE
    public void bindS(View v){
        Intent intent = new Intent(this,FirstService.class);
        bindService(intent,sc, Context.BIND_AUTO_CREATE);
        status=true;
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
        setContentView(R.layout.activity_friend_search);


        //for broadcast receiver
        this.registerReceiver(receiver, new IntentFilter("broadcast"));


        final Intent intent = new Intent(this,FirstService.class);

        bindService(intent, sc, Context.BIND_AUTO_CREATE);
        status=true;

        utils = new Utils(getApplicationContext());



        btnsend = (Button) findViewById(R.id.btnsend);
        emailid= (EditText) findViewById(R.id.editTextID);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindedService.sendMessageToServer(utils.getSendFriendRequestJSON(emailid.getText().toString()));
            }
        });
    }


    /**
     * Parsing the JSON message received from server The intent of message will
     * be identified by JSON node 'flag'. flag = self, message belongs to the
     * person. flag = new, a new person joined the conversation. flag = message,
     * a new message received from server. flag = exit, somebody left the
     * conversation.
     * */
    private void parseMessage(final String msg) {

        try {
            JSONObject jObj = new JSONObject(msg);
            String success="Success",fail="Fail",friendconfirm="friendconfirm",failae="FailAE",friendrequest="friendrequest";

            String flag = jObj.getString("flag");
           // if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
                // if the flag is 'message', new message received


                String what = jObj.getString("what");
                String sessionId = jObj.getString("sessionId");
                if(sessionId.equals(utils.getSessionId().toString())) {
                    if (what.equals(friendconfirm)) {
                        String message = jObj.getString("message");

                        if (message.equals(success)) {
                            showToast("Friend Request Accepted");

                        } else if (message.equals(fail)) {
                            showToast("Friend Request Not Accepted");
                        } else if (message.equals(failae)) {
                            showToast("Friend Already Exists");
                        }

                    }else if (what.equals(friendrequest)) {
                        String fromid = jObj.getString("fromid");
                        showToast("Friend Request From " + fromid);
                        DialogAppear(fromid, "Friend Request");
                        //bindedService.sendMessageToServer(utils.getSendFriendConfirmJSON(fromid));
                        //showToast("Friend Request Confirmed From "+fromid);
                    }else if (what.equals(friendconfirm)) {
                        String fromid = jObj.getString("fromid");
                        String message = jObj.getString("message");

                        if (message.equals(success)) {
                            showToast("Friend Request was confirmed by " + fromid);
                        } else if (message.equals(fail)) {
                            showToast("Friend Request was rejected by "+fromid);

                        }
                    }

                }
            //}
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//
//		if(client != null & client.isConnected()){
//			client.disconnect();
//		}
//	}



    private void DialogAppear(final String fromid, final String Title) {

        final String friendrequest = "Friend Request";

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