package com.example.abhishek.mapp;

import com.example.abhishek.mapp.other.*;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;

public class NameActivity extends Activity {

	private static final String TAG = NameActivity.class.getSimpleName();

	private Button btnlogin,btnsignup,btnmap1,btnmap2;
	private EditText txtid,txtpwd;


	private Utils utils;

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
		setContentView(R.layout.activity_name);

		getActionBar().hide();



		//for broadcast receiver
		this.registerReceiver(receiver, new IntentFilter("broadcast"));


		final Intent intent = new Intent(this,FirstService.class);
		startService(intent);// so that service not stopped until we call on stop service

		bindService(intent, sc, Context.BIND_AUTO_CREATE);
		status=true;



		btnlogin = (Button) findViewById(R.id.btnlogin);
		btnsignup = (Button) findViewById(R.id.btnsignup);
		btnmap1 = (Button) findViewById(R.id.btnmap1);
		btnmap2 = (Button) findViewById(R.id.btnmap2);
		txtid = (EditText) findViewById(R.id.emailid);
		txtpwd = (EditText) findViewById(R.id.pswd);

		utils = new Utils(getApplicationContext());



		btnlogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(txtid.getText().toString().length() == 0) {
					showToast("Please input email id");
				}else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtid.getText().toString()).matches()) {    //if format for email address not correct
					showToast("Please input correct email id");
				}else if(txtpwd.getText().toString().length() == 0) {
					showToast("Please input password");
				}else if(txtpwd.getEditableText().toString().trim().length()<8){
					showToast("password should be minimum 8 chars");
				}  else {
					bindedService.sendMessageToServer(utils.getSendLoginMessageJSON(txtid.getText().toString(),
							txtpwd.getText().toString()));
				}
			}
		});


		btnsignup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NameActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});


		btnmap1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NameActivity.this,
						AMapsActivity.class);
				startActivity(intent);
			}
		});


		btnmap2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NameActivity.this,
						FriendSearch.class);
				startActivity(intent);
			}

		});



		/**
		 * Creating web socket client. This will have callback methods
		 * */

	}

//
//	public void startTheService(View v) {
//		Intent intent = new Intent(NameActivity.this,
//				FirstService.class);
//		intent.putExtra("msg","Frm name");
//		startService(intent);
//	}
//	public void stopTheService(View v) {
//		stopService(new Intent(this,FirstService.class));
//	}


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
			String success="Success",fail="Fail",login="login",friendrequest="friendrequest",sessiondetails="sessiondetails",friendconfirm="friendconfirm";

			String flag = jObj.getString("flag");
			//if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
				// if the flag is 'message', new message received

			 //String localSessionID = utils.getSessionId().toString();

				String what = jObj.getString("what");

				String sessionId = jObj.getString("sessionId");
				if(what.equals(sessiondetails)) {
					utils.storeSessionId(sessionId);
					showToast("SessionID stored");

				}
				else if(sessionId.equals(utils.getSessionId().toString())) {

					if (what.equals(login)) {


						String message = jObj.getString("message");

						if (message.equals(success)) {
							showToast("Login Succesful");
							Intent intent = new Intent(NameActivity.this,
									AMapsActivity.class);
							startActivity(intent);
							finish();
						} else if (message.equals(fail)) {
							showToast("Either Email ID or Password entered is Incorrect or Account not yet verified");

						}
					} else if (what.equals(friendrequest)) {
						String fromid = jObj.getString("fromid");
						showToast("Friend Request From " + fromid);
						DialogAppear(fromid,"Friend Request");
						//bindedService.sendMessageToServer(utils.getSendFriendConfirmJSON(fromid));
						//showToast("Friend Request Confirmed From "+fromid);
					}

					else if (what.equals(friendconfirm)) {
						String fromid = jObj.getString("fromid");
						String message = jObj.getString("message");

						if (message.equals(success)) {
							showToast("Friend Request was confirmed by " + fromid);
						} else if (message.equals(fail)) {
							showToast("Friend Request was rejected by "+fromid);

						}
					}

			//	}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

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

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//
//		if(client != null & client.isConnected()){
//			client.disconnect();
//		}
//	}

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

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}

