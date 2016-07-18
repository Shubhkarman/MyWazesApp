package pack;


import org.json.JSONException;
import org.json.JSONObject;
 
public class JSONUtils {
 
    // flags to identify the kind of json response on client side
    private static final String FLAG_SELF = "self", FLAG_NEW = "new",
            FLAG_MESSAGE = "message", FLAG_EXIT = "exit";
 
    public JSONUtils() {
    }
    
    // WHAT ALL AM I USING
    //SendReturnMessageJson,	getClientDetailsJson,	getSendAllRequestJson
    
    
    public String SendReturnMessageJson(String sessionId,String message,String what) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
            jObj.put("what", what);
 
            json = jObj.toString();
 
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
    
    
    public String SendReportMessageJson(String sessionId,String message,String what,String latitude,String longitude,String from,String reportid) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
            jObj.put("what", what);
            jObj.put("reportid", reportid);
            jObj.put("latitude", latitude);
            jObj.put("longitude", longitude);
            jObj.put("from", from);
 
            json = jObj.toString();
 
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
    
    
 
    
    /*
     * JSON when message needs to be sent to all the clients
     */
    public String getSendAllRequestJson(String sessionId, String fromemailid,String what,String message) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", sessionId);
            jObj.put("fromid", fromemailid);
            jObj.put("what", what);
            jObj.put("message", message);

 
            json = jObj.toString();
 
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
    
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    /*
     * JSON when message needs to be sent to all the clients
     */
    public String getSendAllMessageJson(String sessionId, String fromName,
            String message) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", sessionId);
            jObj.put("name", fromName);
            jObj.put("message", message);
 
            json = jObj.toString();
 
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
    
    
 
    /**
     * Json when client needs it's own session details
     * */
    public String getClientDetailsJson(String sessionId, String what) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_SELF);
            jObj.put("sessionId", sessionId);
            jObj.put("what", what);
 
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
 
    /**
     * Json to notify all the clients about new person joined
     * */
    public String getNewClientJson(String sessionId, String name,
            String message, int onlineCount) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_NEW);
            jObj.put("name", name);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
            jObj.put("onlineCount", onlineCount);
 
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
 
    /**
     * Json when the client exits the socket connection
     * */
    public String getClientExitJson(String sessionId, String name,
            String message, int onlineCount) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_EXIT);
            jObj.put("name", name);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
            jObj.put("onlineCount", onlineCount);
 
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
 
}