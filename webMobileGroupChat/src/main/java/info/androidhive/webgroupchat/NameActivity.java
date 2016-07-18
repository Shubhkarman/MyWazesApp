package info.androidhive.webgroupchat;

import info.androidhive.webgroupchat.other.Message;
import info.androidhive.webgroupchat.other.Utils;
import info.androidhive.webgroupchat.other.WsConfig;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;

public class NameActivity extends Activity {

	private static final String TAG = NameActivity.class.getSimpleName();

	private Button btnlogin,btnsignup;
	private EditText txtid,txtpwd;

	private WebSocketClient client;

	private Utils utils;

	private String name = null;

	// JSON flags to identify the kind of JSON response
	private static final String TAG_SELF = "self", TAG_NEW = "new",
			TAG_MESSAGE = "message", TAG_EXIT = "exit";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_name);

		getActionBar().hide();

		btnlogin = (Button) findViewById(R.id.btnlogin);
		btnsignup = (Button) findViewById(R.id.btnsignup);
		txtid = (EditText) findViewById(R.id.emailid);
		txtpwd = (EditText) findViewById(R.id.pswd);

		utils = new Utils(getApplicationContext());



		btnlogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtid.getText().toString()).matches()) {    //if format for email address not correct
					showToast("Please input correct email id");
				} else if(txtpwd.getText().toString().length() == 0) {
					showToast("Please input password");
				} else {
					sendMessageToServer(utils.getSendLoginMessageJSON(txtid.getText().toString(),
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



		/**
		 * Creating web socket client. This will have callback methods
		 * */
		client = new WebSocketClient(URI.create(WsConfig.URL_WEBSOCKET
		), new WebSocketClient.Listener() {
			//+ URLEncoder.encode(name)), new WebSocketClient.Listener() {
			@Override
			public void onConnect() {

			}

			/**
			 * On receiving the message from web socket server
			 * */
			@Override
			public void onMessage(String message) {
				Log.d(TAG, String.format("Got string message! %s", message));

				parseMessage(message);

			}

			@Override
			public void onMessage(byte[] data) {
				Log.d(TAG, String.format("Got binary message! %s",
						bytesToHex(data)));

				// Message will be in JSON format
				parseMessage(bytesToHex(data));
			}

			/**
			 * Called when the connection is terminated
			 * */
			@Override
			public void onDisconnect(int code, String reason) {

				String message = String.format(Locale.US,
						"Disconnected! Code: %d Reason: %s", code, reason);

				showToast(message);

				// clear the session id from shared preferences
				utils.storeSessionId(null);
			}

			@Override
			public void onError(Exception error) {
				Log.e(TAG, "Error! : " + error);

				showToast("Error! : " + error);
			}

		}, null);

		client.connect();
	}

	/**
	 * Method to send message to web socket server
	 * */
	public void sendMessageToServer(String message) {
		if (client != null && client.isConnected()) {
			client.send(message);
		}
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
			String success="Success",fail="Fail",signup="signup",login="login";

			String flag = jObj.getString("flag");
			if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
				// if the flag is 'message', new message received

				String message = jObj.getString("message");
				String what = jObj.getString("what");
				String sessionId = jObj.getString("sessionId");

				if(message.equals(success)){
					showToast("Login Succesful");
					Intent intent = new Intent(NameActivity.this,
							welcome.class);
					startActivity(intent);
				}
				else if(message.equals(fail)){
					if(what.equals(login)) {
						showToast("Either Email ID or Password entered is Incorrect");
					}
					else if(what.equals(signup)){
						showToast("This Email ID is already being used");
					}
				}


			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(client != null & client.isConnected()){
			client.disconnect();
		}
	}

	private void showToast(final String message) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_LONG).show();
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

