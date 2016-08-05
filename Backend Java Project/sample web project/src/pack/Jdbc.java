package pack;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Jdbc {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/mywaves";
	
	//To send email
	Email email = new Email();

	// Database credentials
	static final String USER = "root";
	static final String PASS = "root";
	
//	SocketServer socketServer = new SocketServer();

	public String newuser(String userid,String password, String name, String number) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String result="",success="Success",fail="Fail";
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			/*Connection conn2 = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement statement = conn2
					.prepareStatement("SELECT * FROM userinfo WHERE userid = '" + userid + "'");
			ResultSet result2 = statement.executeQuery();
			
			String retrievedUseremail="";

			while (result2.next()) {
				retrievedUseremail = result2.getString("userid");
			}

			if (retrievedUseremail.equals(userid) ) {
				//status = "Success";
				result =fail;
			}

			else {*/
				//status = "Fail";
				
					// STEP 4: Execute a query
					System.out.println("Creating statement...");
			
					
					/*
					 * PASSWORD HASHING.... FUNCTIONS DEFINED AT THE END
					 * 
					 * 
					 */
					 String generatedSecuredPasswordHash = null;
					try {
						generatedSecuredPasswordHash = generateStorngPasswordHash(password);
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidKeySpecException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				     System.out.println(generatedSecuredPasswordHash);
				     System.out.println(generatedSecuredPasswordHash.length());
					
					
					stmt = conn.prepareStatement("INSERT INTO userinfo(userid,password,name,number) VALUES(?,?,?,?)");
					stmt.setString(1, userid);
					stmt.setString(2, generatedSecuredPasswordHash);
					stmt.setString(3, name);
					stmt.setString(4, number);
					int b = stmt.executeUpdate();
					if (b>0){
						result =success;
						
						
						//To Verify Email ID Send New Mail
						
						email.emailsend(userid, generatedSecuredPasswordHash);
						
						//System.out.println("1 record inserted...");
						//return result;
					}
					else{
						result=fail;
						//return result;
					}

			//}

			
			
				
			
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			result="Fail";
			//return result;
			//System.exit(1);
		} catch (ClassNotFoundException clsNotFoundEx) {
			clsNotFoundEx.printStackTrace();
			System.exit(1);
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				//System.exit(1);
			}
		}
		return result;

	}
	
	public String ConfirmEmail(String useremail, String password) {
		String retrievedUseremail = "";
		String retrievedPassword = "";
		boolean retrievedEmailVerify = false;	
		String status = "";
		try {
			
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM userinfo WHERE userid = '" + useremail + "'");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				retrievedUseremail = result.getString("userid");
				retrievedPassword = result.getString("password");
				retrievedEmailVerify = result.getBoolean("emailverify");
			}
			if(retrievedEmailVerify==false)
			{
	
				if (retrievedUseremail.equals(useremail) && retrievedPassword.equals(password)) {
					status = "Success";
					
					Class.forName("com.mysql.jdbc.Driver");
	
					PreparedStatement statement2 = con
							.prepareStatement("UPDATE userinfo SET emailverify = true WHERE userid = '" + useremail + "'");
					int b = statement2.executeUpdate();
					if (b>0){
						status ="Success";
						
						//System.out.println("1 record inserted...");
						//return result;
					}
					else{
						status="Fail";
						//return result;
					}
				}
				else {
					status = "Fail";
				}
			}else status = "Fail";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;


	}

	
	

	public String Login(String useremail, String password,String SessionID) {

		String retrievedUseremail = "";
		String retrievedPassword = "";
		boolean retrievedEmailVerify = false;	
		String status = "";
		try {
			
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM userinfo WHERE userid = '" + useremail + "'");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				retrievedUseremail = result.getString("userid");
				retrievedPassword = result.getString("password");
				retrievedEmailVerify = result.getBoolean("emailverify");
			}
			if(retrievedEmailVerify==true)
			{
				String generatedSecuredPasswordHash = null;
				try {
					generatedSecuredPasswordHash = generateStorngPasswordHash(password);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			     System.out.println(generatedSecuredPasswordHash);
	
	
				if (retrievedUseremail.equals(useremail) && retrievedPassword.equals(generatedSecuredPasswordHash)) {
					status = "Success";
					
					Class.forName("com.mysql.jdbc.Driver");
	
					PreparedStatement statement2 = con
							.prepareStatement("UPDATE userinfo SET sessionid ='"+ SessionID+"',logedin = true WHERE userid = '" + useremail + "'");
					int b = statement2.executeUpdate();
					if (b>0){
						status ="Success";
						
						//System.out.println("1 record inserted...");
						//return result;
					}
					else{
						status="Fail";
						//return result;
					}
	
	
					
				}else {
					status = "Fail";
				}
			}else status = "Fail";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;

	}
	
	
	

	public String Location(String latitude, String longitude, String SessionID) {

		String status = "";
		try {
			
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement statement = con
					.prepareStatement("UPDATE userinfo SET clatitude ='"+ latitude+"',clongitude = '"+longitude+"' WHERE sessionid = '" + SessionID + "' AND logedin = true");
			int b = statement.executeUpdate();
			if (b>0){
				status ="Success";
				//System.out.println("1 record inserted...");
				//return result;
			}
			else{
				status="Fail";
				//return result;
			}
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;

	}

	public String Logout(String SessionID) {

		String status = "";
		try {
			
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement statement = con
					.prepareStatement("UPDATE userinfo SET logedin = false WHERE sessionid = '" + SessionID + "' AND logedin = true");
			int b = statement.executeUpdate();
			if (b>0){
				status ="Success";
				//System.out.println("1 record inserted...");
				//return result;
			}
			else{
				status="Fail";
				//return result;
			}
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;

	}
	
	
	
	
	
	public String newreport(String lat, String lon, String userid, String message,int numusers) {

		String reportid = "";
		String status = "";
		boolean flag = false;
		try {

			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

			Class.forName("com.mysql.jdbc.Driver");

			PreparedStatement statement3 = con.prepareStatement("INSERT INTO reports(userid,lat,lon,type,numusers,val,confirm) VALUE('"
					+ userid + "','" + lat + "','" + lon + "','" + message + "','"+numusers+"','"+0+"',false)");
			int b = statement3.executeUpdate();
			if (b > 0) {
				status = "Success";

				System.out.println("report inserted...");
				// return result;
			} else {
				status = "Fail";
				System.out.println("report NOT inserted...");
				// return result;
			}

			PreparedStatement statement = con
					.prepareStatement("SELECT reportid FROM reports ORDER BY reportid DESC LIMIT 1");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				reportid = result.getString("reportid");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportid;

	}

	public ResultSet getReportInfo(String reportid) {

		String status = "";
		boolean flag = false;
		ResultSet result=null;
		try {

			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

			Class.forName("com.mysql.jdbc.Driver");


			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM reports WHERE reportid = '" + reportid + "'");
			result = statement.executeQuery();



		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}
	
	public ResultSet getConfirmedReportInfo() {

		String status = "";
		boolean flag = false;
		ResultSet result=null;
		try {

			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

			Class.forName("com.mysql.jdbc.Driver");


			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM reports WHERE confirm = 1");
			result = statement.executeQuery();



		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	
	
	public String updatereport(String reportid,String reply) {

		String status = "";
		int value=0,numusers=0;
		boolean flag = false;
		try {

			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

			Class.forName("com.mysql.jdbc.Driver");
			

			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM reports WHERE reportid = '" + reportid + "'");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
			value = result.getInt("val");
			numusers = result.getInt("numusers");

			}

			numusers--;
			if(reply.equals("Yes")){
				value++;
				PreparedStatement statement2 = con
						.prepareStatement("UPDATE reports SET val =  "+value+",numusers = "+numusers+" WHERE reportid = '" + reportid + "'");
				int b = statement2.executeUpdate();
				if (b>0){
					status ="Success";
					//System.out.println("1 record inserted...");
					//return result;
				}
				else{
					status="Fail";
					//return result;
				}	
			}
			else if(reply.equals("No")){
				value--;
				PreparedStatement statement2 = con
						.prepareStatement("UPDATE reports SET val =  "+value+",numusers = "+numusers+" WHERE reportid = '" + reportid + "'");
				int b = statement2.executeUpdate();
				if (b>0){
					status ="Success";
					//System.out.println("1 record inserted...");
					//return result;
				}
				else{
					status="Fail";
					//return result;
				}	
			}else if(reply.equals("IDK")){
				PreparedStatement statement2 = con
						.prepareStatement("UPDATE reports SET numusers = "+numusers+" WHERE reportid = '" + reportid + "'");
				int b = statement2.executeUpdate();
				if (b>0){
					status ="Success";
					//System.out.println("1 record inserted...");
					//return result;
				}
				else{
					status="Fail";
					//return result;
				}	
			}
			
			PreparedStatement statement3 = con
					.prepareStatement("SELECT * FROM reports WHERE reportid = '" + reportid + "'");
			ResultSet result2 = statement3.executeQuery();

			while (result2.next()) {
			value = result2.getInt("val");
			numusers = result2.getInt("numusers");

			}
			if(numusers==0){
				
				if(value>0){
					PreparedStatement statement4 = con
							.prepareStatement("UPDATE reports SET confirm = true WHERE reportid = '" + reportid + "'");
					int b = statement4.executeUpdate();
					if (b>0){
						status ="Confirm";
						//System.out.println("1 record inserted...");
						//return result;
					}
					else{
						status="Fail";
						//return result;
					}
				}
				else{
					PreparedStatement statement4 = con
							.prepareStatement("DELETE FROM reports WHERE reportid = '" + reportid + "'");
					int b = statement4.executeUpdate();
					if (b>0){
						status ="Confirm";
						//System.out.println("1 record inserted...");
						//return result;
					}
					else{
						status="Fail";
						//return result;
					}
					
				}
					
				
			}

			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;

	}
	

	


	public String FriendAdd(String friendemail, String SessionID) {

		String retrievedUseremail = "";
		String status = "";
		boolean flag=false;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM userinfo WHERE sessionid = '" + SessionID + "'");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				retrievedUseremail = result.getString("userid");
				
			}
			PreparedStatement statement2 = con
					.prepareStatement("SELECT * FROM friends WHERE userid = '" + retrievedUseremail + "' and friendid = '" + friendemail + "'");
			ResultSet result2 = statement2.executeQuery();
			
			while (result2.next()) {
				String s = result2.getString("userid");
				if(s.equals(retrievedUseremail)){
					System.out.println("email id match		:" +s+"		and retrived:	"+retrievedUseremail);
					flag= true;
				}
			}

			if(flag==false)
			{
				System.out.println("This is new friend");
	
					
					Class.forName("com.mysql.jdbc.Driver");
	
					PreparedStatement statement3 = con
							.prepareStatement("INSERT INTO friends(userid,friendid) VALUE('"+retrievedUseremail+"','"+friendemail+"'),('"+friendemail+"','"+retrievedUseremail+"')");
					int b = statement3.executeUpdate();
					if (b>0){
						status ="Success";
						
						System.out.println("friend inserted...");
						//return result;
					}
					else{
						status="Fail";
						System.out.println("friend NOT inserted...");
						//return result;
					}
					
				}else {
					status = "FailAE";// for friend already exits
					System.out.println("friend already exists...");
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;


	}
	
	

	
	
	
	public boolean AlreadyFriend(String friendemail, String SessionID) {

		String retrievedUseremail = "";
		//String status = "";
		boolean flag=false;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM userinfo WHERE sessionid = '" + SessionID + "'");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				retrievedUseremail = result.getString("userid");
				
			}
			PreparedStatement statement2 = con
					.prepareStatement("SELECT * FROM friends WHERE userid = '" + retrievedUseremail + "' and friendid = '" + friendemail + "'");
			ResultSet result2 = statement2.executeQuery();
			
			while (result2.next()) {
				String s = result2.getString("userid");
				if(s.equals(retrievedUseremail)){
					System.out.println("email id match		:" +s+"		and retrived:	"+retrievedUseremail);
					flag= true;
				}
			}

			if(flag==false)
			{
				System.out.println("This is new friend");
				//status = "Success";
					
					
			}else {
				//status = "FailAE";// for friend already exits
				System.out.println("friend already exists...");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;


	}
	
	
	
	public String newsharedmap( String userid, String email) {

		String sharedid = "";
		String status = "";
		boolean flag = false;
		List<String> emails = Arrays.asList(email.split("\\s*,\\s*"));

		try {

			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

			Class.forName("com.mysql.jdbc.Driver");

			PreparedStatement statement3 = con.prepareStatement("INSERT INTO sharedmap(userid,otherusers,numusers,val,confirm) VALUE('"
					+ userid + "','"  + email + "','"+emails.size()+"','"+0+"',false)");
			int b = statement3.executeUpdate();
			if (b > 0) {
				status = "Success";

				System.out.println("report inserted...");
				// return result;
			} else {
				status = "Fail";
				System.out.println("report NOT inserted...");
				// return result;
			}

			PreparedStatement statement = con
					.prepareStatement("SELECT sharedid FROM sharedmap ORDER BY sharedid DESC LIMIT 1");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				sharedid = result.getString("sharedid");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sharedid;

	}
	
	
	
	
	
	
	public String updateshared(String sharedid,String reply) {

		String status = "";
		int value=0,numusers=0;
		boolean flag = false;
		try {

			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

			Class.forName("com.mysql.jdbc.Driver");
			

			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM sharedmap WHERE sharedid = '" + sharedid + "'");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
			value = result.getInt("val");
			numusers = result.getInt("numusers");

			}

			if(reply.equals("Success")){
				value++;
				PreparedStatement statement2 = con
						.prepareStatement("UPDATE sharedmap SET val =  "+value+" WHERE sharedid = '" + sharedid + "'");
				int b = statement2.executeUpdate();
				if (b>0){
					status ="Success";
					//System.out.println("1 record inserted...");
					//return result;
				}
				else{
					status="Fail";
					//return result;
				}	
			}
			else if(reply.equals("Fail")){
				PreparedStatement statement4 = con
						.prepareStatement("DELETE FROM sharedmap WHERE sharedid = '" + sharedid + "'");
					int b = statement4.executeUpdate();
					if (b>0){
						status ="Delete";
						//System.out.println("1 record inserted...");
						//return result;
					}
					else{
						status="Fail";
						//return result;
					}
			}
			PreparedStatement statement3 = con
					.prepareStatement("SELECT * FROM sharedmap WHERE sharedid = '" + sharedid + "'");
			ResultSet result2 = statement3.executeQuery();

			while (result2.next()) {
			value = result2.getInt("val");
			numusers = result2.getInt("numusers");

			}
			if(numusers==value){
				
					PreparedStatement statement4 = con
							.prepareStatement("UPDATE sharedmap SET confirm = true WHERE sharedid = '" + sharedid + "'");
					int b = statement4.executeUpdate();
					if (b>0){
						status ="Confirm";
						//System.out.println("1 record inserted...");
						//return result;
					}
					else{
						status="Fail";
						//return result;
					}
			}
			else{
					
			}
						

		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;

	}
	
	
	
	
	
	
	
	
	
	
	public ResultSet getSharedInfo(String sharedid) {

		String status = "";
		boolean flag = false;
		ResultSet result=null;
		try {

			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

			Class.forName("com.mysql.jdbc.Driver");


			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM sharedmap WHERE sharedid = '" + sharedid + "'");
			result = statement.executeQuery();



		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	
	
	
	


	

	
	
	


	public String getSessionID(String email) {

		String sessionID = null;
		//String status = "";
		//boolean flag=false;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM userinfo WHERE userid = '" + email + "' AND logedin = true");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				sessionID = result.getString("sessionid");				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sessionID;
	}

	

	public String getEmailID(String sessionID) {

		String emailID = null;
		//String status = "";
		//boolean flag=false;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to database...");
			Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM userinfo WHERE sessionid = '" + sessionID + "' AND logedin = true");
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				emailID = result.getString("userid");				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailID;
	}

	
	
	
	
	
	
	 public static String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
	    {
	        int iterations = 1000;
	        char[] chars = password.toCharArray();
	        String s = "blahblah";
	        byte[] salt = s.getBytes();
	         
	        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        byte[] hash = skf.generateSecret(spec).getEncoded();
	        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
	    }
	     
	    public static String getSalt() throws NoSuchAlgorithmException
	    {
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	        byte[] salt = new byte[16];
	        sr.nextBytes(salt);
	        return salt.toString();
	    }
	     
	    public static String toHex(byte[] array) throws NoSuchAlgorithmException
	    {
	        BigInteger bi = new BigInteger(1, array);
	        String hex = bi.toString(16);
	        int paddingLength = (array.length * 2) - hex.length();
	        if(paddingLength > 0)
	        {
	            return String.format("%0"  +paddingLength + "d", 0) + hex;
	        }else{
	            return hex;
	        }
	    }

}

/*
 * // JDBC driver name and database URL static final String JDBC_DRIVER =
 * "com.mysql.jdbc.Driver"; static final String DB_URL =
 * "jdbc:mysql://localhost/mywaves";
 * 
 * // Database credentials static final String USER = "root"; static final
 * String PASS = "root";
 * 
 * public void newuser(String id, String name, String phone) { Connection conn =
 * null; Statement stmt = null; System.out.println(id);
 * System.out.println(name); System.out.println(phone); try{ //STEP 2: Register
 * JDBC driver Class.forName("com.mysql.jdbc.Driver");
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
 * 
 * 
 * }//end FirstExample
 */