package com.example.abhishek.mapp.other;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.maps.model.LatLng;

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

	public String getSendCurrentLocationJSON(LatLng ll) {
		String json = null;
		String what= "location";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("lat", ll.latitude);
			jObj.put("long", ll.longitude);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}
	public String getSendSharedCurrentLocationJSON(LatLng ll) {
		String json = null;
		String what= "sharedcurrentlocation";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("lat", ll.latitude);
			jObj.put("long", ll.longitude);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}




	public String getSendSharedLocationJSON(LatLng ll) {
		String json = null;
		String what= "sharedlocation";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("lat", ll.latitude);
			jObj.put("long", ll.longitude);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}






	public String getSendLogoutJSON() {
		String json = null;
		String what= "logout";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}


	public String getSendFriendRequestJSON(String emailid) {
		String json = null;
		String what= "friendrequest";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("email", emailid);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}
	//message is Success(YES) or Fail(No)
	public String getSendFriendConfirmJSON(String emailid,String result) {
		String json = null;
		String what= "friendconfirm";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("email", emailid);
			jObj.put("result", result);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}




	public String getSendSharedRequestJSON(String emailids) {
		String json = null;
		String what= "sharedrequest";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("email", emailids);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}
	//message is Success(YES) or Fail(No)
	public String getSendSharedConfirmJSON(String emailid,String result,String sharedid) {
		String json = null;
		String what= "sharedconfirm";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("email", emailid);
			jObj.put("result", result);
			jObj.put("sharedid",sharedid);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}






	public String getSendReportConfirmJSON(String reportid, String result) {

		String json = null;
		String what= "reportreply";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("reportid", reportid);
			jObj.put("reply", result);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}


	public String getSendReportJSON(LatLng ll,String msg) {

		String json = null;
		String what= "reportadd";
		//String msg = "Heavy Traffic";

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("what", what);
			jObj.put("lat", ll.latitude);
			jObj.put("long", ll.longitude);
			jObj.put("msg", msg);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}
}
