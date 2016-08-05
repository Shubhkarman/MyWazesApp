
package pack;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Maps;
import com.mysql.jdbc.log.Log;

@ServerEndpoint("/chat")
public class SocketServer {
	
	
	
	// set to store all the live sessions
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
 
//    // Mapping between session and person name
//    private static final HashMap<String, String> nameSessionPair = new HashMap<String, String>();
//    
    private static final HashMap<String, Session> IDSessionPair = new HashMap<String, Session>();
    
    private static final HashMap<String, String> SessionSharedPair = new HashMap<String, String>();
        
    
    
    private JSONUtils jsonUtils = new JSONUtils();
 
//    // Getting query params
//    public static Map<String, String> getQueryMap(String query) {
//        Map<String, String> map = Maps.newHashMap();
//        if (query != null) {
//            String[] params = query.split("&");
//            for (String param : params) {
//                String[] nameval = param.split("=");
//                map.put(nameval[0], nameval[1]);
//            }
//        }
//        return map;
//    }
 
    /**
     * Called when a socket connection opened
     * */
    @OnOpen
    public void onOpen(Session session) {
 
        System.out.println(session.getId() + " has opened a connection");
 
        //Map<String, String> queryParams = getQueryMap(session.getQueryString());
 
        IDSessionPair.put(session.getId(), session);
        
        String name = "";
 
//        if (queryParams.containsKey("name")) {
// 
//            // Getting client name via query param
//            name = queryParams.get("name");
//            try {
//                name = URLDecoder.decode(name, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
// 
//            // Mapping client name and session id
//            nameSessionPair.put(session.getId(), name);
//        }
// 
        // Adding session to session list
        sessions.add(session);
 
        try {
            // Sending session id to the client that just connected
            System.out.println("Sending client his session id");
        	session.getBasicRemote().sendText(
                    jsonUtils.getClientDetailsJson(session.getId(),
                            "sessiondetails"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
 
    }
 
	
	
		
	
	
	
	
	
	
	
	
	
	
	
	Jdbc jdbc = new Jdbc();

	/**
	 * method called when new message received from any client
	 * 
	 * @param message
	 *            JSON message from client
	 * @throws SQLException 
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws SQLException {

		System.out.println("Message from " + session.getId() + ": " + message);
		String what = null, result = null,signup="signup",login="login";
		try {
			JSONObject jObj = new JSONObject(message);
			what = jObj.getString("what");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (what.toString().equals("signup")) {
			String email = null, password=null,uname = null, unumber = null;
			try {
				JSONObject jObj = new JSONObject(message);
				email = jObj.getString("email");
				password = jObj.getString("password");
				uname = jObj.getString("name");
				unumber = jObj.getString("number");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result= jdbc.newuser(email,password, uname, unumber);
			sendMessage(session, session.getId(), result,what);
			
			
		} 
		else if (what.equals("login")) {
			String email = null, password = null;
			try {
				JSONObject jObj = new JSONObject(message);
				email = jObj.getString("email");
				password = jObj.getString("password");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result = jdbc.Login(email, password,session.getId());
			if(result.equals("Success"))
			{
				// Mapping client name and session id
	//			nameSessionPair.put(session.getId(), email);
				// adding session in sessions
				sessions.add(session);
			}
			sendMessage(session, session.getId(), result,what);
			UploadAllReport(session);
			
		} 
		else if (what.equals("location")) {
			String latitude = null, longitude = null;
			try {
				JSONObject jObj = new JSONObject(message);
				latitude = jObj.getString("lat");
				longitude = jObj.getString("long");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result = jdbc.Location(latitude, longitude,session.getId());
			sendMessage(session, session.getId(), result,what);
		}
		else if (what.equals("reportadd")) {
			String latitude = null, longitude = null,reportid="",msg="";
			try {
				JSONObject jObj = new JSONObject(message);
				latitude = jObj.getString("lat");
				longitude = jObj.getString("long");
				msg=jObj.getString("msg");
				reportid = jdbc.newreport(latitude,longitude,jdbc.getEmailID(session.getId()),msg,sessions.size()-1);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			SendReport(latitude, longitude,session.getId(),reportid,msg);
			
		}
		else if (what.equals("reportreply")) {
			String reply = null,reportid="",confirm=null;
			String lat=null,lon=null;String reportType="";
			try {
				JSONObject jObj = new JSONObject(message);
				reply = jObj.getString("reply");
				reportid = jObj.getString("reportid");
				confirm=jdbc.updatereport(reportid,reply);
				
				if(confirm.equals("Confirm")){
					
					ResultSet reportinfo = jdbc.getReportInfo(reportid); 

					while (reportinfo.next()) {
						reportType = reportinfo.getString("type");
//						lat = Double.parseDouble(reportinfo.getString("lat"));
//						lon = Double.parseDouble(reportinfo.getString("lon"));
						lat =reportinfo.getString("lat");
						lon =reportinfo.getString("lon");
					}

					UploadReport(lat,lon,reportType,reportid);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}

		else if (what.equals("logout")) {
			
			result = jdbc.Logout(session.getId());
			sendMessage(session, session.getId(), result,what);
		}
		else if (what.equals("friendrequest")) {
			String email = null;
			try {
				JSONObject jObj = new JSONObject(message);
				email = jObj.getString("email");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//result = jdbc.FriendRequest(email,session.getId());
			
			if(jdbc.AlreadyFriend(email,session.getId())) // already freinds
			{
				result="FailAE";
				sendMessage(session, session.getId(), result,what);
			}
			else
			{
				String friendsSessionID = jdbc.getSessionID(email);
				if(friendsSessionID==null)
				{
					result="FailNA"; //Not Available... i.e. offline
				}
				else
				{
					// to email converted to sessionID  and from session converted to email
					sendfriendRequest(jdbc.getSessionID(email), jdbc.getEmailID(session.getId()));
					
					System.out.println("Friend Request Sent");
				}
				
			}	
		
		}
		else if (what.equals("friendconfirm")) {
			String email = null,ans = null;
			try {
				JSONObject jObj = new JSONObject(message);
				email = jObj.getString("email");
				ans = jObj.getString("result");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(ans.equals("Success")){
				result = jdbc.FriendAdd(email,session.getId());
	
				System.out.println("Friend Confirmed to DB");
				// to email converted to sessionID  and from session converted to email
				sendFriendConfirm(jdbc.getSessionID(email), jdbc.getEmailID(session.getId()), ans);
			} else if(ans.equals("Fail")){
				System.out.println("Friend Rejected");
				// to email converted to sessionID  and from session converted to email
				sendFriendConfirm(jdbc.getSessionID(email), jdbc.getEmailID(session.getId()), ans);					
			}

			System.out.println("Friend Confirm Sent");
		
			
		}
		else if (what.equals("sharedrequest")) {
			String email = null;
			try {
				JSONObject jObj = new JSONObject(message);
				email = jObj.getString("email");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			List<String> emails = Arrays.asList(email.split("\\s*,\\s*"));
			
			
			boolean check =true;
			for(int i=0;i<emails.size();i++)
			{
			    System.out.println(" -->"+emails.get(i));
			
			
					if(jdbc.AlreadyFriend(emails.get(i),session.getId())) // already freinds
					{
						String friendsSessionID = jdbc.getSessionID(emails.get(i));
						if(friendsSessionID==null)
						{
							result="FailNA"; //Not Available... i.e. offline
							sendMessage(session, session.getId(), result,what);
							check=false;
							break;
						}
						else
						{
							// to email converted to sessionID  and from session converted to email
							//sendsharedRequest(jdbc.getSessionID(email), jdbc.getEmailID(session.getId()));
							
							//System.out.println("Friend Request Sent");
						}

					}
					else
					{
						result="FailAE";
						sendMessage(session, session.getId(), result,what);
						break;

					}	
			}
			
			if(check ==true)
			{
				//save in database
				String sharedid = jdbc.newsharedmap(jdbc.getEmailID(session.getId()),email);
				
				for(int i=0;i<emails.size();i++)
				{
					// to email converted to sessionID  and from session converted to email
					sendsharedRequest(jdbc.getSessionID(email), jdbc.getEmailID(session.getId()),sharedid);
					
					System.out.println("Shared Request Sent");
				}
				
			}
		}	else if (what.equals("sharedconfirm")) {
			String sharedid = null,ans = null,from=null;
			try {
				JSONObject jObj = new JSONObject(message);
				sharedid = jObj.getString("sharedid");
				ans = jObj.getString("result");
				from = jObj.getString("email");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result = jdbc.updateshared(sharedid,ans);
			
			if(result.equals("Delete"))
			{
				sendMessage(IDSessionPair.get(jdbc.getSessionID(from)), jdbc.getSessionID(from), result, what);
			}
			else if(result.equals("Confirm"))
			{
				
				// FILL SESSIONSHAREDPAIR WITH ALL IDS AND CORESPONDING SESSIONIDS FOR EASY SEND
				
				ResultSet resultSet = jdbc.getSharedInfo(sharedid);
				String others=null;
				
				while (resultSet.next()) {
					others = resultSet.getString("otherusers");
				}
				
				List<String> emails = Arrays.asList(others.split("\\s*,\\s*"));

				SessionSharedPair.clear();
				for(int i=0;i<emails.size();i++){
					SessionSharedPair.put(emails.get(i),jdbc.getSessionID(emails.get(i)));
				}
				SessionSharedPair.put(from,jdbc.getSessionID(from));
				
				
				
				
				
				for (String key : SessionSharedPair.keySet()) {
					
					
					sendMessage(IDSessionPair.get(SessionSharedPair.get(key)),SessionSharedPair.get(key) , result, what);
				
				
				}
				
//				sendMessage(IDSessionPair.get(jdbc.getSessionID(from)), jdbc.getSessionID(from), result, what);
				
			}
			else if(result.equals("Success"))
			{
				
			}
	
	
			System.out.println("Shared one Confirmed to DB");
			System.out.println(result);
		
			
		}

		else if (what.equals("sharedlocation")) {
			String latitude = null, longitude = null,sessionId =null;
			try {
				JSONObject jObj = new JSONObject(message);
				latitude = jObj.getString("lat");
				longitude = jObj.getString("long");
				sessionId = jObj.getString("sessionId");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			SendSharedLocation(latitude,longitude,jdbc.getEmailID(sessionId),what);
			
			//	sendMessage(IDSessionPair.get(SessionSharedPair.get(key)),SessionSharedPair.get(key) , result, what);
			
			//}
			

		}
		
		else if (what.equals("sharedcurrentlocation")) {
			String latitude = null, longitude = null,sessionId =null;
			try {
				JSONObject jObj = new JSONObject(message);
				latitude = jObj.getString("lat");
				longitude = jObj.getString("long");
				sessionId = jObj.getString("sessionId");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			SendSharedLocation(latitude,longitude,jdbc.getEmailID(sessionId),what);
			
			//	sendMessage(IDSessionPair.get(SessionSharedPair.get(key)),SessionSharedPair.get(key) , result, what);
			
			//}
			

		}

		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void SendSharedLocation(String latitude, String longitude, String from,String what) {
		// TODO Auto-generated method stub
		//for (Session s : sessions) {
		for (String key : SessionSharedPair.keySet()) {	
			  
			String json = null;
           // String what = "sharedlocation";
                           
            json = jsonUtils.SendReportMessageJson(SessionSharedPair.get(key), "",what,latitude,longitude,from,"");
            
 
            try {
                System.out.println("Sending Shared location Update To: " + SessionSharedPair.get(key)+ ", "
                        + json);
 
                IDSessionPair.get(SessionSharedPair.get(key)).getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + SessionSharedPair.get(key) + ", "
                        + e.getMessage());
                e.printStackTrace();
            }
        }
		
		
		
	}
	

	public void UploadReport(String latitude, String longitude, String reportType,String reportid) {
		// TODO Auto-generated method stub
		for (Session s : sessions) {
            String json = null;
            String message =reportType,what = "reportupdate";
                           
            json = jsonUtils.SendReportMessageJson(s.getId(), message,what,latitude,longitude,"Server",reportid);
            
 
            try {
                System.out.println("Sending Report Update To: " + s.getId() + ", "
                        + json);
 
                s.getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + s.getId() + ", "
                        + e.getMessage());
                e.printStackTrace();
            }
        }
		
		
		
	}
	
	
	
	
	public void UploadAllReport(Session s) {
		// TODO Auto-generated method stub
		//for (Session s : sessions) {
		String reportType=null,latitude=null,longitude=null,reportid=null;
		ResultSet reportinfo =jdbc.getConfirmedReportInfo();  
		try {
			while (reportinfo.next()) {
				reportType = reportinfo.getString("type");
//			lat = Double.parseDouble(reportinfo.getString("lat"));
//			lon = Double.parseDouble(reportinfo.getString("lon"));
				latitude =reportinfo.getString("lat");
				longitude =reportinfo.getString("lon");

				String json = null;
	            String message =reportType,what = "reportupdate";
	                           
	            json = jsonUtils.SendReportMessageJson(s.getId(), message,what,latitude,longitude,"Server",reportid);
	            
	 
	            try {
	                System.out.println("Sending Report Update To: " + s.getId() + ", "
	                        + json);
	 
	                s.getBasicRemote().sendText(json);
	            } catch (IOException e) {
	                System.out.println("error in sending. " + s.getId() + ", "
	                        + e.getMessage());
	                e.printStackTrace();
	            }

			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		
		
		
	}
	
	
	
	
	
	
	
	
	public void SendReport(String latitude, String longitude, String sessionId,String reportid,String msg) {
		// TODO Auto-generated method stub
		for (Session s : sessions) {
//			Session mine = IDSessionPair.get(sessionId);
//			if(s!=mine){
            
			if(s.getId()!=sessionId){
				String json = null;
	            String what = "reportquery";
	            String from = jdbc.getEmailID(sessionId);
	                           
	            json = jsonUtils.SendReportMessageJson(s.getId(), msg,what,latitude,longitude,from,reportid);
	            
	 
	            try {
	                System.out.println("Sending Message To: " + s.getId() + ", "
	                        + json);
	 
	                s.getBasicRemote().sendText(json);
	            } catch (IOException e) {
	                System.out.println("error in sending. " + s.getId() + ", "
	                        + e.getMessage());
	                e.printStackTrace();
	            }
			}
        }
		
		
		
	}

	private void sendMessage(Session s, String sessionId, String message,String what) {

		String json = jsonUtils.SendReturnMessageJson(sessionId, message,what);
		try {
			System.out.println("Sending Message To: " + sessionId + ", " + json);

			s.getBasicRemote().sendText(json);
		} catch (IOException e) {
			System.out.println("error in sending. " + s.getId() + ", " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	


/**
 * Method called when a connection is closed
 */

	@OnClose
	public void onClose(Session session) {

	//	System.out.println(
	//			"Session "+session.getId()+"   name : "+nameSessionPair.get(session.getId())+" has ended");
		
		String result=jdbc.Logout(session.getId()),success="Success";
		if(result.equals(success))
			System.out.println("Succesfully logged out");
		else
			System.out.println("Couldn't log out ???");
		sessions.remove(session);

	}
	
	
	
	
	
	
	
	
	
	
	

    /**
     * Method to send message to all clients
     * name is from name
     * sessionid is to whom it is being sent sessionid  
     * */
    private void sendfriendRequest(String sessionId, String name) {
 
        // Looping through all the sessions and sending the message individually
            String json = null;
            String what = "friendrequest";
                // Normal chat conversation message
                json = jsonUtils.getSendAllRequestJson(sessionId, name,what,"");
            try {
                System.out.println("Sending Message To: " + sessionId + ", "
                        + json);
 
                IDSessionPair.get(sessionId).getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + sessionId + ", "
                        + e.getMessage());
                e.printStackTrace();
            
            }
    }
	
    
    private void sendFriendConfirm(String sessionId, String name,String message) {
    	 
        // Looping through all the sessions and sending the message individually
            String json = null;
            String what = "friendconfirm";
                // Normal chat conversation message
                json = jsonUtils.getSendAllRequestJson(sessionId, name,what,message);
            try {
                System.out.println("Sending Message To: " + sessionId + ", "
                        + json);
 
                IDSessionPair.get(sessionId).getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + sessionId + ", "
                        + e.getMessage());
                e.printStackTrace();
            
            }
    }
    

    /**
     * Method to send message to all clients
     * name is from name
     * sessionid is to whom it is being sent sessionid  
     * */
    private void sendsharedRequest(String sessionId, String name,String sharedid) {
 
        // Looping through all the sessions and sending the message individually
            String json = null;
            String what = "sharedrequest";
                // Normal chat conversation message
                json = jsonUtils.getSendAllRequestJson(sessionId, name,what,sharedid);
            try {
                System.out.println("Sending Message To: " + sessionId + ", "
                        + json);
 
                IDSessionPair.get(sessionId).getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + sessionId + ", "
                        + e.getMessage());
                e.printStackTrace();
            
            }
    }


	
	

	
	
	
	
	
	
	
	
	
	
	

	
	
	

    /**
     * Method to send message to all clients
     * name is from name
     * sessionid is for whom it is sessionid  
     * */
    private void sendRequestToAll(String sessionId, String name) {
 
        // Looping through all the sessions and sending the message individually
        for (Session s : sessions) {
            String json = null;
            String what = "friendrequest";
                // Normal chat conversation message
                json = jsonUtils.getSendAllRequestJson(sessionId, name,what,"");
            try {
                System.out.println("Sending Message To: " + sessionId + ", "
                        + json);
 
                s.getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + s.getId() + ", "
                        + e.getMessage());
                e.printStackTrace();
            }
        }
    }
	

	
	
	
	
	
	
	
	
	
	
	

    /**
     * Method to send message to all clients
     * 
     * @param sessionId
     * @param message
     *            message to be sent to clients
     * @param isNewClient
     *            flag to identify that message is about new person joined
     * @param isExit
     *            flag to identify that a person left the conversation
     * */
    private void sendMessageToAll(String sessionId, String name,
            String message, boolean isNewClient, boolean isExit) {
 
        // Looping through all the sessions and sending the message individually
        for (Session s : sessions) {
            String json = null;
 
            // Checking if the message is about new client joined
            if (isNewClient) {
                json = jsonUtils.getNewClientJson(sessionId, name, message,
                        sessions.size());
 
            } else if (isExit) {
                // Checking if the person left the conversation
                json = jsonUtils.getClientExitJson(sessionId, name, message,
                        sessions.size());
            } else {
                // Normal chat conversation message
                json = jsonUtils
                        .getSendAllMessageJson(sessionId, name, message);
            }
 
            try {
                System.out.println("Sending Message To: " + sessionId + ", "
                        + json);
 
                s.getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + s.getId() + ", "
                        + e.getMessage());
                e.printStackTrace();
            }
        }
    }
	


}

















// set to store all the live sessions
// private static final Set<Session> sessions = Collections
// .synchronizedSet(new HashSet<Session>());

// Mapping between session and person name
// private static final HashMap<String, String> nameSessionPair = new
// HashMap<String, String>();

// private JSONUtils jsonUtils = new JSONUtils();

// Getting query params
// public static Map<String, String> getQueryMap(String query) {
// Map<String, String> map = Maps.newHashMap();
// if (query != null) {
// String[] params = query.split("&");
// for (String param : params) {
// String[] nameval = param.split("=");
// map.put(nameval[0], nameval[1]);
// }
// }
// return map;
// }
//

/**
 * Called when a socket connection opened
 */
/*
 * @OnOpen public void onOpen(Session session) {
 * 
 * System.out.println(session.getId() + " has opened a connection");
 * 
 * // Map<String, String> queryParams = getQueryMap(session.getQueryString());
 * 
 * String name = "";
 * 
 * if (queryParams.containsKey("name")) {
 * 
 * // Getting client name via query param name = queryParams.get("name"); try {
 * name = URLDecoder.decode(name, "UTF-8"); } catch
 * (UnsupportedEncodingException e) { e.printStackTrace(); }
 * 
 * // Mapping client name and session id nameSessionPair.put(session.getId(),
 * name); }
 * 
 * // Adding session to session list sessions.add(session);
 * 
 * try { // Sending session id to the client that just connected
 * session.getBasicRemote().sendText(
 * jsonUtils.getClientDetailsJson(session.getId(), "Your session details")); }
 * catch (IOException e) { e.printStackTrace(); }
 * 
 * // Notifying all the clients about new person joined
 * sendMessageToAll(session.getId(), name, " joined conversation!", true,
 * false);
 * 
 * }
 */

/**
 * Method called when a connection is closed
 */
/*
 * @OnClose public void onClose(Session session) {
 * 
 * System.out.println("Session " + session.getId() + " has ended");
 * 
 * // Getting the client name that exited String name =
 * nameSessionPair.get(session.getId());
 * 
 * // removing the session from sessions list sessions.remove(session);
 * 
 * // Notifying all the clients about person exit
 * sendMessageToAll(session.getId(), name, " left conversation!", false, true);
 * 
 * }
 */
/**
 * Method to send message to all clients
 * 
 * @param sessionId
 * @param message
 *            message to be sent to clients
 * @param isNewClient
 *            flag to identify that message is about new person joined
 * @param isExit
 *            flag to identify that a person left the conversation
 */
/*
 * private void sendMessageToAll(String sessionId, String name, String message,
 * boolean isNewClient, boolean isExit) {
 * 
 * // Looping through all the sessions and sending the message individually for
 * (Session s : sessions) { String json = null;
 * 
 * // Checking if the message is about new client joined if (isNewClient) { json
 * = jsonUtils.getNewClientJson(sessionId, name, message, sessions.size());
 * 
 * } else if (isExit) { // Checking if the person left the conversation json =
 * jsonUtils.getClientExitJson(sessionId, name, message, sessions.size()); }
 * else { // Normal chat conversation message json = jsonUtils
 * .getSendAllMessageJson(sessionId, name, message); }
 * 
 * try { System.out.println("Sending Message To: " + sessionId + ", " + json);
 * 
 * s.getBasicRemote().sendText(json); } catch (IOException e) {
 * System.out.println("error in sending. " + s.getId() + ", " + e.getMessage());
 * e.printStackTrace(); } } }
 */
/*
 * 
 * // JDBC driver name and database URL static final String JDBC_DRIVER =
 * "com.mysql.jdbc.Driver"; static final String DB_URL =
 * "jdbc:mysql://localhost/mywaves";
 * 
 * // Database credentials static final String USER = "root"; static final
 * String PASS = "root";
 * 
 * public void newuser(String id, String name, String phone) { Connection conn =
 * null; Statement stmt = null; try{ //STEP 2: Register JDBC driver
 * Class.forName("com.mysql.jdbc.Driver");
 * 
 * //STEP 3: Open a connection System.out.println("Connecting to database...");
 * conn = DriverManager.getConnection(DB_URL,USER,PASS);
 * 
 * //STEP 4: Execute a query System.out.println("Creating statement..."); stmt =
 * conn.createStatement(); String sql; sql = "INSERT INTO userinfo VALUES(" +
 * id+"," + name+","+ phone+")"; ResultSet rs = stmt.executeQuery(sql);
 * 
 * //STEP 5: Extract data from result set while(rs.next()){ //Retrieve by column
 * name int id = rs.getInt("empid"); //int age = rs.getInt("empname"); String
 * first = rs.getString("empname"); // String last = rs.getString("last");
 * 
 * //Display values System.out.print("ID: " + id); // System.out.print(", Age: "
 * + age); System.out.print(", First: " + first); System.out.println();
 * //System.out.println(", Last: " + last); }
 * 
 * //STEP 6: Clean-up environment rs.close(); stmt.close(); conn.close();
 * }catch(SQLException se){ //Handle errors for JDBC se.printStackTrace();
 * }catch(Exception e){ //Handle errors for Class.forName e.printStackTrace();
 * }finally{ //finally block used to close resources try{ if(stmt!=null)
 * stmt.close(); }catch(SQLException se2){ }// nothing we can do try{
 * if(conn!=null) conn.close(); }catch(SQLException se){ se.printStackTrace();
 * }//end finally try }//end try System.out.println("Goodbye!"); }//end main
 * 
 * }
 * 
 * 
 */