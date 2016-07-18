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

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private Button btnSend;
	private EditText inputemail, inputname,inputnumber,inputpassword,confirmpassword;
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
		setContentView(R.layout.activity_signup);

		utils = new Utils(getApplicationContext());

		getActionBar().hide();

		//for broadcast receiver
		this.registerReceiver(receiver, new IntentFilter("broadcast"));


		Intent intent = new Intent(this,FirstService.class);
		// bcuz service already started by name activity
		//startService(intent);

		bindService(intent,sc, Context.BIND_AUTO_CREATE);
		status=true;



		btnSend = (Button) findViewById(R.id.button);
		inputemail = (EditText) findViewById(R.id.name);
		inputname = (EditText) findViewById(R.id.name2);
		inputnumber = (EditText) findViewById(R.id.name3);
		inputpassword = (EditText) findViewById(R.id.name4);
		confirmpassword = (EditText) findViewById(R.id.name5);


		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (inputemail.getText().toString().length() == 0) {
					showToast("Please input email id");
				} else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inputemail.getText().toString()).matches()) {    //if format for email address not correct
					showToast("Please input correct email id");
				} else if (inputpassword.getEditableText().toString().trim().length() < 8) {
					showToast("password should be minimum 8 chars");
				} else if (inputpassword.getEditableText().toString() == confirmpassword.getEditableText().toString()) {
					showToast("password does not match");
				} else if (inputname.getText().toString().length() == 0) {
					showToast("Please input Name");
				} else if (inputnumber.getText().toString().trim().length() != 10) {
					showToast("Please input correct Number(10 digits)");
				} else {
					bindedService.sendMessageToServer(utils.getSendSignupMessageJSON(inputemail.getText().toString(),
							inputpassword.getText().toString(), inputname.getText().toString(), inputnumber.getText().toString()));
				}
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
			String success="Success",fail="Fail",signup="signup",friendrequest="friendrequest",friendconfirm="friendconfirm";

			String flag = jObj.getString("flag");
			//if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
				// if the flag is 'message', new message received


				String what = jObj.getString("what");
				String sessionId = jObj.getString("sessionId");

				if(sessionId.equals(utils.getSessionId().toString())) {
					if (what.equals(signup)) {
						String message = jObj.getString("message");
						if (message.equals(success)) {
							showToast("Signup Succesful");

							Intent intent = new Intent(this,
									welcome.class);
							startActivity(intent);
						} else {
							showToast("This Email ID is already being used");
						}

					}else if (what.equals(friendrequest)) {
						String fromid = jObj.getString("fromid");
						showToast("Friend Request From " + fromid);
						DialogAppear(fromid,"Friend Request");
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


/*

	 * Parsing the JSON message received from server The intent of message will
	 * be identified by JSON node 'flag'. flag = self, message belongs to the
	 * person. flag = new, a new person joined the conversation. flag = message,
	 * a new message received from server. flag = exit, somebody left the
	 * conversation.
	 *
private void parseMessage(final String msg) {

	try {
		JSONObject jObj = new JSONObject(msg);

		// JSON node 'flag'
		String flag = jObj.getString("flag");

		// if flag is 'self', this JSON contains session id
		if (flag.equalsIgnoreCase(TAG_SELF)) {

			String sessionId = jObj.getString("sessionId");

			// Save the session id in shared preferences
			utils.storeSessionId(sessionId);

			Log.e(TAG, "Your session id: " + utils.getSessionId());

		} else if (flag.equalsIgnoreCase(TAG_NEW)) {
			// If the flag is 'new', new person joined the room
			String name = jObj.getString("name");
			String message = jObj.getString("message");

			// number of people online
			String onlineCount = jObj.getString("onlineCount");

			showToast(name + message + ". Currently " + onlineCount
					+ " people online!");

		} else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
			// if the flag is 'message', new message received
			String fromName = name;
			String message = jObj.getString("message");
			String sessionId = jObj.getString("sessionId");
			boolean isSelf = true;

			// Checking if the message was sent by you
			if (!sessionId.equals(utils.getSessionId())) {
				fromName = jObj.getString("name");
				isSelf = false;
			}

			Message m = new Message(fromName, message, isSelf);

			// Appending the message to chat list
			//	appendMessage(m);

		} else if (flag.equalsIgnoreCase(TAG_EXIT)) {
			// If the flag is 'exit', somebody left the conversation
			String name = jObj.getString("name");
			String message = jObj.getString("message");

			showToast(name + message);
		}

	} catch (JSONException e) {
		e.printStackTrace();
	}

}
*/
