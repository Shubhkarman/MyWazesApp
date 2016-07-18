package info.androidhive.webgroupchat.other;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Utils {

	private Context context;
	private SharedPreferences sharedPref;

	private static final String KEY_SHARED_PREF = "ANDROID_WEB_CHAT";
	private static final int KEY_MODE_PRIVATE = 0;
	private static final String KEY_SESSION_ID = "sessionId",
			FLAG_MESSAGE = "message";

	public Utils(Context context) {
		this.context = context;
		sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF,
				KEY_MODE_PRIVATE);
	}

	public void storeSessionId(String sessionId) {
		Editor editor = sharedPref.edit();
		editor.putString(KEY_SESSION_ID, sessionId);
		editor.commit();
	}

	public String getSessionId() {
		return sharedPref.getString(KEY_SESSION_ID, null);
	}

	public String getSendMessageJSON(String message) {
		String json = null;

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("message", message);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	public String getSendSignupMessageJSON(String email,String password,String name, String number) {
		String json = null;
		String what= "signup";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("email", email);
			jObj.put("password", password);
			jObj.put("name", name);
			jObj.put("number", number);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}


	public String getSendLoginMessageJSON(String email,String password) {
		String json = null;
		String what= "login";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("email", email);
			jObj.put("password", password);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

}
