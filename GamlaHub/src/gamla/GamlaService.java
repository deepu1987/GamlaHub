package gamla;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;








@Path("/GamlaService")
public class GamlaService {
	final static String url = "jdbc:mysql://localhost:3306/Gamla";
	final static String user = "root";
	final static String pass = "";  
	/*final static String user = "DeepanshuMishra";
	final static String pass = "DeepanshuMishra";  */
   
    
    
    @GET
    @Path("/register/{user}/{password}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String register(@PathParam("user") String username, @PathParam("password") String password){
        String result="false";
        boolean x;
        //commnet enter new
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            String sql = "INSERT INTO login (user, password) VALUES (?, ?)";
            
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
        
             
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
            	result = "A new user was inserted successfully!";
               
            }
          
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }
    
    @POST
    @Path("/registerUser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String registerUser(@FormParam("email") String email, @FormParam("password") String password,@FormParam("mobileno") String mobileno, @FormParam("name") String name,  @FormParam("UserVerification") String UserVerification,@FormParam("macadress") String macadress){
        String result="false";
        int x = 0;
        Random rnd = new Random();
    	int otp = 100000 + rnd.nextInt(900000);
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            System.out.println("verictation-0-->>>>>>>>>>"+UserVerification);         
            PreparedStatement ps = con.prepareStatement("insert into user(Name, Email,Password,MobileNo,otp,UserVerification) values(?,?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, mobileno);
            ps.setString(5, otp+"");
            ps.setString(6, UserVerification);
            x = ps.executeUpdate();
            
            if(x==1){
            	
            	 String sql = "UPDATE registrationidforfcm SET MobileNo=? WHERE macadress=?";
	           	 PreparedStatement pstatement = con.prepareStatement(sql);
	           	 pstatement.setString(1, mobileno);
	           	 pstatement.setString(2, macadress);
	           	
	           	  
	           	 int rowsUpdated = pstatement.executeUpdate();
	           	 if (rowsUpdated > 0) {
	           	     System.out.println("An existing user was updated successfully!");
	           	     //result = "true";
	           	 }	
            	String authkey = "204882AnH8usahCKvL5ab1f865";
            	String sender = "GAMLAA";
            	String message = "Your verification code is : "+otp;
            	String otps = otp+"";
            	String emailid = email;
            	String mobilenumber = "91"+mobileno;
            	String otpexpiray = "5";
            	String res = "http://control.msg91.com/api/sendotp.php?template=&otp_length=&authkey="+authkey+"&message="+message+"&sender="+sender+"&mobile="+mobilenumber+"&otp="+otps+"&otp_expiry="+otpexpiray+"&email="+emailid;
   			    //System.out.println("===============>"+res);
            	
            	
            	HttpResponse<String> response = Unirest.post("http://control.msg91.com/api/sendotp.php?template=&otp_length=&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&message="+URLEncoder.encode(message, "UTF-8")+"&sender="+URLEncoder.encode(sender, "UTF-8")+"&mobile="+URLEncoder.encode(mobilenumber, "UTF-8")+"&otp="+URLEncoder.encode(otps, "UTF-8")+"&otp_expiry="+URLEncoder.encode(otpexpiray, "UTF-8")+"&email="+URLEncoder.encode(emailid, "UTF-8"))
            			  .asString();
            	
            	//HttpResponse<String> response = Unirest.post("https://www.google.ca/")  .asString();
            	 
                result = "true";
            }
            
            con.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }
    @POST
    @Path("/confirmUser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String confirmUser(@FormParam("otp") String otp, @FormParam("mobileno") String mobileno, @FormParam("email") String email, @FormParam("name") String name){
    	 String result="false";
       
         try{
             Class.forName("com.mysql.jdbc.Driver");
             Connection con = DriverManager.getConnection(url, user, pass);
             Statement statement = con.createStatement();
             ResultSet results = statement.executeQuery("Select otp from user where MobileNo ="+mobileno);
              System.out.println("email-----------<"+email);
             int count = 0;
             String otpval="";
             while (results.next()){
                  otpval = results.getString("otp");
             }
             if(otpval.equals(otp))
             {
            	 String sql = "UPDATE user SET verified=? WHERE MobileNo=?";
            	 
            	 PreparedStatement pstatement = con.prepareStatement(sql);
            	 pstatement.setString(1, "1");
            	 pstatement.setString(2, mobileno);
            	
            	  
            	 int rowsUpdated = pstatement.executeUpdate();
            	 if (rowsUpdated > 0) {
            	     System.out.println("An existing user was updated successfully!");
            	     
            	        String authkey = "204882AnH8usahCKvL5ab1f865";
            	    	String sender = "GAMLAA";
            	    	String adminmobilenumber = "919560475472,919891852578";
            	    	String message = name+" trying to register in Gamla seller hub. Email id: "+email+" and Mobile number: "+mobileno;
            	    			
            	    
            	    	try {
            				String res1 = "http://api.msg91.com/api/sendhttp.php?sender=GAMLAA&route=4&mobiles="+URLEncoder.encode(adminmobilenumber, "UTF-8")+"&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&country=91&message="+URLEncoder.encode(message, "UTF-8");
            			
            	    	System.out.println("message--------->"+res1);
            	    	} catch (UnsupportedEncodingException e1) {
            				// TODO Auto-generated catch block
            				e1.printStackTrace();
            			}
            			
            	    	
            	    	
            	    	try {
            	    	HttpResponse<String> response = Unirest.get("http://api.msg91.com/api/sendhttp.php?sender=GAMLAA&route=4&mobiles="+URLEncoder.encode(adminmobilenumber, "UTF-8")+"&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&country=91&message="+URLEncoder.encode(message, "UTF-8"))
            	    			  .asString();
            	    	
            	    	}
            	    	catch (Exception e) {
            				// TODO: handle exception
            			}
            	     
            	     result = "true";
            	 }
             }
             con.close();
                
         }
         catch (Exception e) {
			e.printStackTrace();
		}
         return result;
    
    
    }
    
    
    @POST
    @Path("/registerGamlaUser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String registerGamlaUser(@FormParam("email") String email, @FormParam("password") String password,@FormParam("mobileno") String mobileno, @FormParam("name") String name, @FormParam("macadress") String macadress){
        String result="false";
        int x = 0;
        Random rnd = new Random();
    	int otp = 100000 + rnd.nextInt(900000);
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
                        
            PreparedStatement ps = con.prepareStatement("insert into gamlauser(Email, MobileNo,Name,Password,otp) values(?,?,?,?,?)");
            ps.setString(1, email);
            ps.setString(2, mobileno);
            ps.setString(3, name);
            ps.setString(4, password);
            ps.setString(5, otp+"");
            x = ps.executeUpdate();
            
            if(x==1){
            	
            	String sql = "UPDATE registrationidforfcm SET MobileNo=? WHERE macadress=?";
            	 
            	 PreparedStatement pstatement = con.prepareStatement(sql);
            	 pstatement.setString(1, mobileno);
            	 pstatement.setString(2, macadress);
            	
            	  
            	 int rowsUpdated = pstatement.executeUpdate();
            	 if (rowsUpdated > 0) {
            	     System.out.println("An existing user was updated successfully!");
            	     //result = "true";
            	 }	
            	
            	
            	
            	String authkey = "204882AnH8usahCKvL5ab1f865";
            	String sender = "GAMLAA";
            	String message = "Your verification code is : "+otp;
            	String otps = otp+"";
            	String emailid = email;
            	String mobilenumber = "91"+mobileno;
            	String otpexpiray = "5";
            	String res = "http://control.msg91.com/api/sendotp.php?template=&otp_length=&authkey="+authkey+"&message="+message+"&sender="+sender+"&mobile="+mobilenumber+"&otp="+otps+"&otp_expiry="+otpexpiray+"&email="+emailid;
   			    //System.out.println("===============>"+res);
            	
            	
            	HttpResponse<String> response = Unirest.post("http://control.msg91.com/api/sendotp.php?template=&otp_length=&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&message="+URLEncoder.encode(message, "UTF-8")+"&sender="+URLEncoder.encode(sender, "UTF-8")+"&mobile="+URLEncoder.encode(mobilenumber, "UTF-8")+"&otp="+URLEncoder.encode(otps, "UTF-8")+"&otp_expiry="+URLEncoder.encode(otpexpiray, "UTF-8")+"&email="+URLEncoder.encode(emailid, "UTF-8"))
            			  .asString();
            	
            	//HttpResponse<String> response = Unirest.post("https://www.google.ca/")  .asString();
            	 
                result = "true";
            }
            
            con.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }
    
    @POST
    @Path("/ForgotPassword")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String ForgotPassword(@FormParam("mobileno") String mobileno,@FormParam("flag") String flag){
    	
    	String result = "false";
    	try {
    		 Class.forName("com.mysql.jdbc.Driver");
             Connection con = DriverManager.getConnection(url, user, pass);
             Statement statement = con.createStatement();
             ResultSet results;
             if(flag.equalsIgnoreCase("buyer"))
             {
            	  results = statement.executeQuery("Select Password from gamlauser where MobileNo ="+mobileno); 
             }
             else
             {
            	  results = statement.executeQuery("Select Password from user where MobileNo ="+mobileno);
             }
            
             if(results.next())
             {
            	
            	String authkey = "204882AnH8usahCKvL5ab1f865";
             	String mobilenumber = "91"+mobileno;
             	String message = "Your password is : "+results.getString("Password");
             	try {
             	HttpResponse<String> response = Unirest.get("http://api.msg91.com/api/sendhttp.php?sender=GAMLAA&route=4&mobiles="+URLEncoder.encode(mobilenumber, "UTF-8")+"&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&country=91&message="+URLEncoder.encode(message, "UTF-8"))
             			  .asString();
             	result = "true";
             	
             	}
             	catch (Exception e) {
         			// TODO: handle exception
         		}
             }
             else
             {
            	 result = "false";
             }
    	}
    	catch (Exception e) {
			// TODO: handle exception
		}
    	return result;
    	
    	
    }
    
    
    @POST
    @Path("/confirmGamlaUser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String confirmGamlaUser(@FormParam("otp") String otp, @FormParam("mobileno") String mobileno){
    	 String result="false";
       
         try{
             Class.forName("com.mysql.jdbc.Driver");
             Connection con = DriverManager.getConnection(url, user, pass);
             Statement statement = con.createStatement();
             ResultSet results = statement.executeQuery("Select otp from gamlauser where MobileNo ="+mobileno);
              
             int count = 0;
             String otpval="";
             while (results.next()){
                  otpval = results.getString("otp");
             }
             if(otpval.equals(otp))
             {
            	 String sql = "UPDATE gamlauser SET Verified=? WHERE MobileNo=?";
            	 
            	 PreparedStatement pstatement = con.prepareStatement(sql);
            	 pstatement.setString(1, "1");
            	 pstatement.setString(2, mobileno);
            	
            	  
            	 int rowsUpdated = pstatement.executeUpdate();
            	 if (rowsUpdated > 0) {
            	     System.out.println("An existing user was updated successfully!");
            	     result = "true";
            	 }
             }
             con.close();
                
         }
         catch (Exception e) {
			e.printStackTrace();
		}
         return result;
    
    
    }
    @POST
    @Path("/Login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String Login(@FormParam("email") String email, @FormParam("password") String password){
    	
    String result = "false";
    Connection con=null;
    try {
    	Class.forName("com.mysql.jdbc.Driver");
    	 con = DriverManager.getConnection(url, user, pass);
    	Statement mystmt = con.createStatement();
    	Statement mystmt1 = con.createStatement();
    	Statement mystmt2 = con.createStatement();
        ResultSet results = mystmt1.executeQuery("Select * from user where Email ='"+email+"'");
        ResultSet results2 = mystmt2.executeQuery("Select UserVerification from user where Email ='"+email+"'");
        ResultSet results1 = mystmt.executeQuery("SELECT COUNT(*) AS rowcount from user where Email ='"+email+"'");
       
        String savedpassword ="";
        String status = "";
      
        results1.next();
        int count = results1.getInt("rowcount");
        results1.close();
        System.out.println("row count -------->"+count);
        if(count>0)
        {
        	results2.next();
        	
        	System.out.println("row count -------->");
        	while(results.next())
        	{
        		 savedpassword = results.getString("Password");
        		 System.out.println("saved password------->"+savedpassword);
        		 if(savedpassword.equals(password))
             	{
             		System.out.println("saved passworddddd------->"+savedpassword);
             		status = results.getString("verified");
             		System.out.println("status ------->"+status);
             		if(status.equals("1"))
             		{
             			System.out.println("--1-->"+result);
             			
             			if(results2.getString("UserVerification").equalsIgnoreCase("Approved"))
                    	{
             				return "true";
                    	}
                    	else
                    	{
                    		return "your account not verified by admin, contact to administration";
                    	}
             			
             			
             		}
             		else
             		{
             			System.out.println("--2-->"+result);
             			
             			return "Mobile number not verified";
             		}
             		
             	}
             	else
             	{
             		System.out.println("--3-->"+result);
             		
             		return "password does not match";
             	}
        	}
        	
        	
        }
        else
        {
        	System.out.println("--4-->"+result);
        	
        	return "record not found";
        }
      
        
    }
    catch (Exception e) {
		// TODO: handle exception
	}
    
	finally {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

    System.out.println("--5-->"+result);
   
    return result;
    
    }
    
    @POST
    @Path("/LoginGamla")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String LoginGamla(@FormParam("mobile") String mobile, @FormParam("password") String password){
    	
    String result = "false";
    Connection con=null;
    try {
    	Class.forName("com.mysql.jdbc.Driver");
    	 con = DriverManager.getConnection(url, user, pass);
    	Statement mystmt = con.createStatement();
    	Statement mystmt1 = con.createStatement();
        ResultSet results = mystmt1.executeQuery("Select * from gamlauser where MobileNo ='"+mobile+"'");
        ResultSet results1 = mystmt.executeQuery("SELECT COUNT(*) AS rowcount from gamlauser where MobileNo ='"+mobile+"'");
       
        String savedpassword ="";
        String status = "";
      
        results1.next();
        int count = results1.getInt("rowcount");
        results1.close();
        System.out.println("row count -------->"+count);
        if(count>0)
        {
        	System.out.println("row count -------->");
        	while(results.next())
        	{
        		 savedpassword = results.getString("Password");
        		 System.out.println("saved password------->"+savedpassword);
        		 if(savedpassword.equals(password))
             	{
             		System.out.println("saved passworddddd------->"+savedpassword);
             		status = results.getString("verified");
             		System.out.println("status ------->"+status);
             		if(status.equals("1"))
             		{
             			System.out.println("--1-->"+result);
             			
             			return "true";
             		}
             		else
             		{
             			System.out.println("--2-->"+result);
             			
             			return "Mobile number not verified";
             		}
             		
             	}
             	else
             	{
             		System.out.println("--3-->"+result);
             		
             		return "password does not match";
             	}
        	}
        	
        }
        else
        {
        	System.out.println("--4-->"+result);
        	
        	return "record not found";
        }
      
        
    }
    catch (Exception e) {
		// TODO: handle exception
	}
    
	finally {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

    System.out.println("--5-->"+result);
   
    return result;
    
    }
    
    @POST
    @Path("/PersonalInformation")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String PersonalInformation(@FormParam("email") String params){
		Connection con = null;
		JSONObject obj = null;
		Statement mystmt = null,mystmt1 = null,mystmt2 = null,mystmt3 = null ;
    	
		try {
			obj = new JSONObject(params);
		} catch (JSONException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		JSONObject outerjson = new JSONObject();
		JSONObject objjson = null;
		String email = null;
		try {
			email = obj.getString("email");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
		Class.forName("com.mysql.jdbc.Driver");
    	con = DriverManager.getConnection(url, user, pass);
    	 mystmt = con.createStatement();
    	 mystmt1 = con.createStatement();
    	 mystmt2 = con.createStatement();
    	 mystmt3 = con.createStatement();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		String result = "false";
		try {
			
			
	        ResultSet results = mystmt.executeQuery("Select * from user where Email ='"+email+"'");
	        objjson = new JSONObject();
	        while(results.next())
	        {
	        	String name = results.getString("Name");
	        	String emaill = results.getString("Email");
	        	String mobileno = results.getString("MobileNo");
	        	
	        	objjson.put("name", name);
	        	objjson.put("email", emaill);
	        	objjson.put("mobileno", mobileno);
	        	
	        }
	        outerjson.put("personalInformation", objjson);
		}
		catch (Exception e) {
			try {
				objjson.put("message", "false");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    	
	        
	        try {
	        Statement mycountstmt = con.createStatement();
            ResultSet resultsBuisness = mystmt1.executeQuery("Select * from buisnessinformation where EmailId ='"+email+"'");
            ResultSet results1 = mycountstmt.executeQuery("SELECT COUNT(*) AS rowcount from buisnessinformation where EmailId ='"+email+"'");
            results1.next();
            int count = results1.getInt("rowcount");
            results1.close();
            objjson = new JSONObject();
            if(count<=0)
            {
            	objjson.put("status", "false");	
            }
           
	        while(resultsBuisness.next())
	        {
	        	String emaill = resultsBuisness.getString("EmailId");
	        	String mobileno = resultsBuisness.getString("MobileNo");
	        	String buisnesstype = resultsBuisness.getString("BuisnessType");
	        	String displayname = resultsBuisness.getString("DisplayName");
	        	String regBuisnessName = resultsBuisness.getString("RegisteredBuisnessName");
	        	String RegisteredBuisnessAdress = resultsBuisness.getString("RegisteredBuisnessAdress");
	        	String PinCode = resultsBuisness.getString("PinCode");
	        	String State = resultsBuisness.getString("State");
	        	String city	= resultsBuisness.getString("City");
	        	
	        	objjson.put("status", "true");	
	        	objjson.put("displayname", displayname);
	        	objjson.put("email", emaill);
	        	objjson.put("mobileno", mobileno);
	        	objjson.put("regBuisnessName", regBuisnessName);
	        	objjson.put("RegisteredBuisnessAdress", RegisteredBuisnessAdress);
	        	objjson.put("PinCode", PinCode);
	        	objjson.put("State", State);
	        	objjson.put("city", city);
	        	objjson.put("buisnestype", buisnesstype);
	        	
	        }
	        outerjson.put("buisnessinformation", objjson);
	        }
	        catch (Exception e) {
				try {
					objjson.put("message", "false");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	        
	        try {
            ResultSet resultsfinancial = mystmt2.executeQuery("Select * from financialinformation where EmailId ='"+email+"'");
            Statement mycountstmt1 = con.createStatement();
            ResultSet results1 = mycountstmt1.executeQuery("SELECT COUNT(*) AS rowcount from financialinformation where EmailId ='"+email+"'");
            results1.next();
            int count = results1.getInt("rowcount");
            objjson = new JSONObject();
            if(count<=0)
            {
            	objjson.put("status", "false");
            }
           
	        while(resultsfinancial.next())
	        {
	        	String PanCardNumber = resultsfinancial.getString("PanCardNumber");
	        	String emaill = resultsfinancial.getString("EmailId");
	        	String mobileno = resultsfinancial.getString("MobileNo");
	        	String PanCardName = resultsfinancial.getString("PanCardName");
	        	String BenificaryName = resultsfinancial.getString("BenificaryName");
	        	String BenificiarayBankAccountNumber = resultsfinancial.getString("BenificiarayBankAccountNumber");
	        	String BankIFSCCode = resultsfinancial.getString("BankIFSCCode");
	        	String BenificaryBankName = resultsfinancial.getString("BenificaryBankName");
	        	
	        	objjson.put("status", "true");
	        	objjson.put("PanCardNumber", PanCardNumber);
	        	objjson.put("email", emaill);
	        	objjson.put("mobileno", mobileno);
	        	objjson.put("PanCardName", PanCardName);
	        	objjson.put("BenificaryName", BenificaryName);
	        	objjson.put("BenificiarayBankAccountNumber", BenificiarayBankAccountNumber);
	        	objjson.put("BankIFSCCode", BankIFSCCode);
	        	objjson.put("BenificaryBankName", BenificaryBankName);
	        	
	        }
	        outerjson.put("financialinformation", objjson);
	        }
	        catch (Exception e) {
				try {
					objjson.put("message", "false");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	        try {
            ResultSet resultsWarehouse = mystmt3.executeQuery("Select * from warehouseinformation where Email ='"+email+"'");
            Statement mycountstmt2 = con.createStatement();
            ResultSet results1 = mycountstmt2.executeQuery("SELECT COUNT(*) AS rowcount from warehouseinformation where Email ='"+email+"'");
            results1.next();
            int count = results1.getInt("rowcount");
            objjson = new JSONObject();
            if(count<=0)
            {
            	objjson.put("status", "false");
            }
	        while(resultsWarehouse.next())
	        {
	        	String WarehouseName = resultsWarehouse.getString("WarehouseName");
	        	String emaill = resultsWarehouse.getString("Email");
	        	String mobileno = resultsWarehouse.getString("MobileNo");
	        	String WarehouseAdress = resultsWarehouse.getString("WarehouseAdress");
	        	String WarehousePincode	 = resultsWarehouse.getString("WarehousePincode	");
	        	String GstIn = resultsWarehouse.getString("GstIn");
	        	String WarehouseState = resultsWarehouse.getString("WarehouseState");
	        	String WarehouseCity = resultsWarehouse.getString("WarehouseCity");
	        	
	        	objjson.put("status", "true");
	        	objjson.put("WarehouseName", WarehouseName);
	        	objjson.put("email", emaill);
	        	objjson.put("mobileno", mobileno);
	        	objjson.put("WarehouseAdress", WarehouseAdress);
	        	objjson.put("WarehousePincode", WarehousePincode);
	        	objjson.put("GstIn", GstIn);
	        	objjson.put("WarehouseState", WarehouseState);
	        	objjson.put("WarehouseCity", WarehouseCity);
	        	
	        	
	        }
	        outerjson.put("warehouseinformation", objjson);
	        }
	        catch (Exception e) {
				try {
					objjson.put("message", "false");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	       
			
		
	    try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return outerjson+""; 
    	
    	
    }
    @POST
    @Path("/SaveBuisnessInformation")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String SaveBuisnessInformation(@FormParam("data") String data){
    	Connection conn = null;
    	int x=0;
    	String result = "false";
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
	    	conn = DriverManager.getConnection(url, user, pass);
	    	JSONObject obj = new JSONObject(data);
	    	
	    	 
	    	String mobileno = obj.getString("mobileno");
	    	String email = obj.getString("email");
	    	String slectedbuisnesstype = obj.getString("buisnesstype");
	    	String displayname = obj.getString("displayname");
	    	String buisnessname = obj.getString("buisnessname");
	    	String buisnessadress = obj.getString("buisnessadress");
	    	String pincode = obj.getString("pincode");
	    	String city = obj.getString("city");
	    	String state = obj.getString("state");
	    	
	    	PreparedStatement ps = conn.prepareStatement("insert into buisnessinformation(MobileNo, EmailId,BuisnessType,DisplayName,RegisteredBuisnessName,RegisteredBuisnessAdress,PinCode,State,City) values(?,?,?,?,?,?,?,?,?)");
	        ps.setString(1, mobileno);
	        ps.setString(2, email);
	        ps.setString(3, slectedbuisnesstype);
	        ps.setString(4, displayname);
	        ps.setString(5, buisnessname);
	        ps.setString(6,buisnessadress);
	        ps.setString(7,pincode);
	        ps.setString(8,state);
	        ps.setString(9,city);
	        x = ps.executeUpdate();
	            
	            if(x==1){
	            	
	            	result = "true";
	            }
	            else
	            {
	            	result = "Record Saved already";
	            }
    	}
    	catch (Exception e) {
			// TODO: handle exception
		}
    	return result;
    }
    
    @POST
    @Path("/SaveFinancialInformation")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String SaveFinancialInformation(@FormParam("data") String data){
    	Connection conn = null;
    	int x=0;
    	String result = "false";
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
	    	conn = DriverManager.getConnection(url, user, pass);
	    	JSONObject obj = new JSONObject(data);
	    	
	    	String mobileno = obj.getString("mobileno");
	    	String email = obj.getString("email");
	    	String pancardnmber = obj.getString("pancardnumber");
	    	String pancardname = obj.getString("pancardname");
	    	String benificiaryname = obj.getString("benificiaryname");
	    	String banbkname = obj.getString("bankname");
	    	String accountnumber = obj.getString("accountnumber");
	    	String ifsccode = obj.getString("ifsccode");
	    	
	    	PreparedStatement ps = conn.prepareStatement("insert into financialinformation(PanCardNumber, PanCardName,BenificaryName,BenificiarayBankAccountNumber,BankIFSCCode,BenificaryBankName,MobileNo,EmailId) values(?,?,?,?,?,?,?,?)");
	        ps.setString(1, pancardnmber);
	        ps.setString(2, pancardname);
	        ps.setString(3, benificiaryname);
	        ps.setString(4, accountnumber);
	        ps.setString(5, ifsccode);
	        ps.setString(6,banbkname);
	        ps.setString(7,mobileno);
	        ps.setString(8,email);
	      
	        x = ps.executeUpdate();
	            
	            if(x==1){
	            	
	            	result = "true";
	            }
	            else
	            {
	            	result = "Record Saved already";
	            }
    	}
    	catch (Exception e) {
			// TODO: handle exception
		}
    	return result;
    }
    
    @POST
    @Path("/SaveWarehouseInformation")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String SaveWarehouseInformation(@FormParam("data") String data){
    	
    	Connection conn = null;
    	int x=0;
    	String result = "false";
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
	    	conn = DriverManager.getConnection(url, user, pass);
	    	JSONObject obj = new JSONObject(data);
	    	
	    	
	     
	    	
	    	String mobileno = obj.getString("mobileno");
	    	System.out.println("-hellooooo--"+mobileno);
	    	String email = obj.getString("email");
	    	String warehouseadress = obj.getString("warehouseadress");
	    	String warehousecity = obj.getString("warehousecity");
	    	String warehousegstin = obj.getString("warehousegstin");
	    	String warehousename = obj.getString("warehousename");
	    	String warehousepincode = obj.getString("warehousepincode");
	    	String warehousestate = obj.getString("warehousestate");
	    	
	    	PreparedStatement ps = conn.prepareStatement("insert into warehouseinformation(Email, MobileNo,WarehouseName,WarehouseAdress,WarehousePincode,GstIn,WarehouseState,WarehouseCity) values(?,?,?,?,?,?,?,?)");
	        ps.setString(1, email);
	        ps.setString(2, mobileno);
	        ps.setString(3, warehousename);
	        ps.setString(4, warehouseadress);
	        ps.setString(5, warehousepincode);
	        ps.setString(6,warehousegstin);
	        ps.setString(7,warehousestate);
	        ps.setString(8,warehousecity);
	      
	        x = ps.executeUpdate();
	            
	            if(x==1){
	            	
	            	result = "true";
	            }
	            else
	            {
	            	result = "Record Saved already";
	            }
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    	try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result;
    }
    
    @POST
    @Path("/SaveProductList")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String SaveProductList(@FormParam("data") String data){
    	
    	Connection conn = null;
    	int x=0;
    	String result = "false";
    	
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
	    	conn = DriverManager.getConnection(url, user, pass);
	    	Statement mystmt = conn.createStatement();
	    	
	    	
	    	
	    	JSONObject obj = new JSONObject(data);
	    	
	    	String mobileno = obj.getString("mobileno");
	    	String email = obj.getString("email");
	    	String category = obj.getString("category");
	    	String productname = obj.getString("productname");
	    	String skuid = obj.getString("skuid");
	    	String mrp = obj.getString("mrp");
	    	String sellingprice = obj.getString("sellingprice");
	    	String weight = obj.getString("weight");
	    	String description = obj.getString("description");
	    	String shipindays = obj.getString("shipindays");
	    	String imagename = obj.getString("imagename");
	    	String image = obj.getString("image");
	    	String quantatity = obj.getString("quantatity");
	    	String status = obj.getString("status");
	    	
	    	
	    	ResultSet results  = mystmt.executeQuery("Select * from productlist where MerchantSkuId ='"+skuid+"'");
	    	if(!results.next())
	    	{
	    	System.out.println("image------------>"+image);
	    	
	    	 try {
	    		 	// Decode String using Base64 Class
	    		       byte[] imageByteArray = Base64.decodeBase64(image); 
	    		       FileOutputStream imageOutFile = new FileOutputStream("http://www.gamlahub.com/images/" + imagename);
	    		       imageOutFile.write(imageByteArray);
	    		       imageOutFile.close();
	    		       System.out.println("Image Successfully Stored");
	    		         } catch (FileNotFoundException fnfe) {
	    		             System.out.println("Image Path not found" + fnfe);
	    		         } catch (IOException ioe) {
	    		             System.out.println("Exception while converting the Image " + ioe);
	    		         }

	    	PreparedStatement ps = conn.prepareStatement("insert into productlist(ImagePath, Categoryname,MerchantSkuId,MRP,SellingPrice,weight,shipindays,description,email,mobileno,ProductName,ImageName,Quantatity,Status) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	        //ps.setString(1, "http://www.gamlahub.com/images/" + imagename);
	        ps.setString(1, "http://www.gamlahub.com/images/daisy.jpg");
	        ps.setString(2, category);
	        ps.setString(3, skuid);
	        ps.setString(4, mrp);
	        ps.setString(5, sellingprice);
	        ps.setString(6,weight);
	        ps.setString(7,shipindays);
	        ps.setString(8,description);
	        ps.setString(9,email);
	        ps.setString(10,mobileno);
	        ps.setString(11,productname);
	        ps.setString(12,imagename);
	        ps.setString(13,quantatity);
	        ps.setString(14,status);
	        x = ps.executeUpdate();
	            
	            if(x==1){
	            	
	            	result = "true";
	            }
	            else
	            {
	            	result = "Record Saved already";
	            }
    	}
	    	else
	    	{
	    		return "Sku Id already exist";
	    	}
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    	try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result;
    }
    
    @POST
    @Path("/GetCatalog")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String GetCatalog(@FormParam("data") String data){
		Connection con = null;
		JSONObject obj = null;
		JSONArray objarray = new JSONArray();
		String email=null;
		String mobileno,status = null;
		Statement mystmt=null;
		String searchquery = "";
		
    	
		try {
			obj = new JSONObject(data);
			email = obj.getString("email");
			mobileno = obj.getString("mobileno");
			status = obj.getString("status");
			searchquery = obj.getString("searchquery");
			Class.forName("com.mysql.jdbc.Driver");
	    	con = DriverManager.getConnection(url, user, pass);
	    	mystmt = con.createStatement();
	    	ResultSet results=null;
	    	if(searchquery.length()<=0)
	    	{
	    		results = mystmt.executeQuery("Select * from productlist where Email ='"+email+"' and mobileno = '"+mobileno+"' and Status = '"+status+"'");
	    	}
	    	else
	    	{
	    		results = mystmt.executeQuery("Select * from productlist where Email ='"+email+"' and mobileno = '"+mobileno+"' and Status = '"+status+"' and "+searchquery);
	    	}
	        if(results.next())
	        {
	        	results.previous();
	        	while(results.next())
	        	{
	        		obj = new JSONObject();
	        		obj.put("ProuctId", results.getString("ProuctId"));
	        		obj.put("ImagePath", results.getString("ImagePath"));
	        		obj.put("Categoryname", results.getString("Categoryname"));
	        		obj.put("MerchantSkuId", results.getString("MerchantSkuId"));
	        		obj.put("MRP", results.getString("MRP"));
	        		obj.put("SellingPrice", results.getString("SellingPrice"));
	        		obj.put("weight", results.getString("weight"));
	        		obj.put("shipindays", results.getString("shipindays"));
	        		obj.put("description", results.getString("description"));
	        		obj.put("email", results.getString("email"));
	        		obj.put("mobileno", results.getString("mobileno"));
	        		obj.put("ProductName", results.getString("ProductName"));
	        		obj.put("ImageName", results.getString("ImageName"));
	        		obj.put("Quantatity", results.getString("Quantatity"));
	        		obj.put("Status", results.getString("Status"));
	        		objarray.put(obj);
	        	}
	        	
	        }
	        else
	        {
	        	obj = new JSONObject();
	        	obj.put("status", "false");
	        	objarray.put(obj);
	        }
			
		} catch (JSONException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objarray+"";
    }
    
    @POST
    @Path("/UpdateQuantatity")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String UpdateQuantatity(@FormParam("productid") String productid,@FormParam("quantatity") String quantatity,@FormParam("status") String status){
    	 String result="false";
       
         try{
             Class.forName("com.mysql.jdbc.Driver");
             Connection con = DriverManager.getConnection(url, user, pass);
            
            	 String sql = "UPDATE productlist SET Quantatity=?, Status =? WHERE ProuctId=?";
            	 
            	 PreparedStatement pstatement = con.prepareStatement(sql);
            	 pstatement.setString(1, quantatity);
            	 pstatement.setString(2, status);
            	 pstatement.setString(3, productid);
            	
            	  
            	 int rowsUpdated = pstatement.executeUpdate();
            	 if (rowsUpdated > 0) {
            	     System.out.println("Quantatity updated successfully!");
            	     result = "true";
            	
             }
             con.close();
                
         }
         catch (Exception e) {
			e.printStackTrace();
		}
         return result;
    
    
    }
    @POST
    @Path("/UpdateStatus")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String UpdateStatus(@FormParam("productid") String productid,@FormParam("status") String status){
    	 String result="false";
       
         try{
             Class.forName("com.mysql.jdbc.Driver");
             Connection con = DriverManager.getConnection(url, user, pass);
             System.out.println("-----------productid---------------->"+productid+"================>"+status);
            	 String sql = "UPDATE productlist SET Status=? WHERE ProuctId=?";
            	 PreparedStatement pstatement = con.prepareStatement(sql);
            	 pstatement.setString(1, status);
            	 pstatement.setString(2, productid);
            	
            	  
            	 int rowsUpdated = pstatement.executeUpdate();
            	 if (rowsUpdated > 0) {
            	     System.out.println("Status updated successfully!");
            	     result = "true";
            	
             }
             con.close();
                
         }
         catch (Exception e) {
			e.printStackTrace();
		}
         return result;
    
    
    }
    
    @POST
    @Path("/UpdateProductDetail")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String UpdateProductDetail(@FormParam("productid") String productid,@FormParam("productname") String productname,@FormParam("productMrp") String productMrp,@FormParam("productSellingPrice") String productSellingPrice,@FormParam("productQuantatity") String productQuantatity,@FormParam("productDescription") String productDescription,@FormParam("status") String status){
    	 String result="false";
       
         try{
             Class.forName("com.mysql.jdbc.Driver");
             Connection con = DriverManager.getConnection(url, user, pass);
             System.out.println("-----------productid---------------->"+productid+"================>"+status);
            	 String sql = "UPDATE productlist SET ProductName=?,MRP=?,SellingPrice=?,Quantatity=?,description=? WHERE ProuctId=?";
            	 PreparedStatement pstatement = con.prepareStatement(sql);
            	 pstatement.setString(1, productname);
            	 pstatement.setString(2, productMrp);
            	 pstatement.setString(3, productSellingPrice);
            	 pstatement.setString(4, productQuantatity);
            	 pstatement.setString(5, productDescription);
            	 pstatement.setString(6, productid);
            	 int rowsUpdated = pstatement.executeUpdate();
            	 if (rowsUpdated > 0) {
            	     System.out.println("Record updated successfully!");
            	     result = "true";
            	
             }
             con.close();
                
         }
         catch (Exception e) {
			e.printStackTrace();
		}
         return result;
    
    
    }
    
    
    @POST
    @Path("/ProductListCatagoryWise")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String ProductListCatagoryWise(@FormParam("data") String data){
		Connection con = null;
		JSONObject obj = null;
		JSONArray objarray = new JSONArray();
		String catagory=null;
		String mobileno,status = null;
		String productid = "0";
		String SearchQuery = "";
		String filterQuery="";
		String filterPricequery="";
		Statement mystmt,mystmt1,mystmt2=null;
		int Sortby;
		int limitstart;
		int limitend;
		
		
    	
		try {
			obj = new JSONObject(data);
			status = obj.getString("status");
		    catagory = obj.getString("catagory");
		    Sortby = obj.getInt("Sort");
		    SearchQuery = obj.getString("Search");
		    productid = obj.getString("productid");
		    filterQuery = obj.getString("filterQuery");
		    filterPricequery = obj.getString("filterpricequery");
		    limitstart = Integer.parseInt(obj.getString("limitstart"));
		    limitend = Integer.parseInt(obj.getString("limitend"));
		    String OrderBY = "";
		    if(Sortby==0)
		    {
		    	OrderBY = "";
		    }
		    else if(Sortby==1)
		    {
		    	OrderBY = "ORDER BY SellingPrice DESC";
		    }
		    else
		    {
		    	OrderBY = "ORDER BY SellingPrice ASC";
		    }
			Class.forName("com.mysql.jdbc.Driver");
	    	con = DriverManager.getConnection(url, user, pass);
	    	mystmt = con.createStatement();
	    	mystmt2 = con.createStatement();
	    	
	    	ResultSet results2;
	    	ResultSet results;
	    	int rowcount;
	    	if(productid.equalsIgnoreCase("0"))
	    	{
	    		results2 = mystmt2.executeQuery("Select COUNT(*) AS rowcount from productlist where Categoryname ='"+catagory+"' and Status = '"+status+"' "+SearchQuery+" "+filterQuery+" "+filterPricequery+" "+OrderBY);
			    results2.next();
			    rowcount = results2.getInt("rowcount");
		        System.out.println("Select * from productlist where Categoryname ='"+catagory+"' and Status = '"+status+"' "+SearchQuery+" "+filterQuery+" "+filterPricequery+" "+OrderBY+" LIMIT "+limitstart+","+limitend);
		    	results = mystmt.executeQuery("Select * from productlist where Categoryname ='"+catagory+"' and Status = '"+status+"' "+SearchQuery+" "+filterQuery+" "+filterPricequery+" "+OrderBY+" LIMIT "+limitstart+","+limitend);
		    
	    	}
	    	else
	    	{
	    		
		    	results2 = mystmt2.executeQuery("Select COUNT(*) AS rowcount from productlist where ProuctId IN ("+productid+") and Status = '"+status+"' "+SearchQuery+" "+filterQuery+" "+filterPricequery+" "+OrderBY);
			    results2.next();
			    rowcount = results2.getInt("rowcount");
		        System.out.println("Select * from productlist where  ProuctId IN ("+productid+") and Status = '"+status+"' "+SearchQuery+" "+filterQuery+" "+filterPricequery+" "+OrderBY+" LIMIT "+limitstart+","+limitend);
		    	results = mystmt.executeQuery("Select * from productlist where  ProuctId IN ("+productid+") and Status = '"+status+"' "+SearchQuery+" "+filterQuery+" "+filterPricequery+" "+OrderBY+" LIMIT "+limitstart+","+limitend);
		    
	    	}
	    	if(results.next())
	        {
	        	results.previous();
	        	while(results.next())
	        	{
	        		obj = new JSONObject();
	        		obj.put("ProuctId", results.getString("ProuctId"));
	        		obj.put("ImagePath", results.getString("ImagePath"));
	        		obj.put("Categoryname", results.getString("Categoryname"));
	        		obj.put("MerchantSkuId", results.getString("MerchantSkuId"));
	        		obj.put("MRP", results.getString("MRP"));
	        		obj.put("SellingPrice", results.getString("SellingPrice"));
	        		obj.put("weight", results.getString("weight"));
	        		obj.put("shipindays", results.getString("shipindays"));
	        		obj.put("description", results.getString("description"));
	        		obj.put("email", results.getString("email"));
	        		obj.put("mobileno", results.getString("mobileno"));
	        		obj.put("ProductName", results.getString("ProductName"));
	        		obj.put("ImageName", results.getString("ImageName"));
	        		obj.put("Quantatity", results.getString("Quantatity"));
	        		obj.put("Status", results.getString("Status"));
	        		obj.put("rowcount",rowcount+"");
	        		
	        		mystmt1 = con.createStatement();
	    	        ResultSet results1 = mystmt1.executeQuery("Select * from buisnessinformation where EmailId ='"+results.getString("email")+"' and MobileNo = '"+results.getString("mobileno")+"'");
	        		while(results1.next())
	        		{
	        			obj.put("BuisnessInformationID", results1.getString("BuisnessInformationID"));
	        			obj.put("MobileNo", results1.getString("MobileNo"));
	        			obj.put("EmailId", results1.getString("EmailId"));
	        			obj.put("BuisnessType", results1.getString("BuisnessType"));
	        			obj.put("RegisteredBuisnessName", results1.getString("RegisteredBuisnessName"));
	        			obj.put("RegisteredBuisnessAdress", results1.getString("RegisteredBuisnessAdress"));
	        			obj.put("PinCode", results1.getString("PinCode"));
	        			obj.put("State", results1.getString("State"));
	        			obj.put("City", results1.getString("City"));
	        			obj.put("DisplayName", results1.getString("DisplayName"));
	        		}
	    	        
	    	        objarray.put(obj);
	        	}
	        }
	        else
	        {
	        	obj = new JSONObject();
	        	obj.put("status", "false");
	        	objarray.put(obj);
	        }
			
		} catch (JSONException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objarray+"";
    }
    @POST
    @Path("/SaveWishList")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String SaveWishList(@FormParam("mobileno") String mobileno, @FormParam("productid") String productid){
        String result="false";
        boolean x;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            String sql = "INSERT INTO wishlist (MobileNo, ProductId) VALUES (?, ?)";
            
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, mobileno);
            statement.setString(2, productid);
        
             
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
            	result = "Item Added to wishlist";
               
            }
          
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }
    @POST
    @Path("/GetwishListOrNot")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String GetwishListOrNot(@FormParam("mobileno") String mobileno, @FormParam("productid") String productid){
        String result="false";
        boolean x;
        Statement mystmt=null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            mystmt = conn.createStatement();
            String sql = "Select * from  wishlist where MobileNo = ? and productid =?";
            ResultSet results = mystmt.executeQuery("Select * from wishlist where MobileNo = '"+mobileno+"' and productid = '"+productid+"'");
            if(results.next())
            {
            	result = "true";
            }
            else
            {
            	result = "false";
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    @POST
    @Path("/RemoveFromWishList")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String RemoveFromWishList(@FormParam("mobileno") String mobileno, @FormParam("productid") String productid){
        String result="false";
        boolean x;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            String sql = "Delete from wishlist where MobileNo = ? and productid =?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, mobileno);
            statement.setString(2, productid);
            int rowsInserted = statement.executeUpdate();
            result = "true";
            if (rowsInserted > 0) {
            	result = "Item Removed to wishlist";
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    @POST
    @Path("/OrderPlaced")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String OrderPlaced(@FormParam("data") String data){
    	        String result="false";
    	        JSONArray jsonResultArray = new JSONArray();
    	        JSONObject objResult;
		        try{
		        	JSONArray objarray = new JSONArray(data);
				    boolean x;
		            Class.forName("com.mysql.jdbc.Driver");
		            Connection conn = DriverManager.getConnection(url, user, pass);
		            for(int i=0;i<objarray.length();i++) 
			        {
		            	objResult = new JSONObject();
			            JSONObject obj = objarray.getJSONObject(i);
			            Statement mystmt = null; 
			            mystmt = conn.createStatement();
			            ResultSet results = mystmt.executeQuery("Select Quantatity from productlist where ProuctId = '"+obj.getString("ProductID")+"'");
			            if(results.next())
			            {
			            	String quantatity = results.getString("Quantatity");
			            	int quantaityint = Integer.parseInt(quantatity);
			            	if(quantaityint<=0)
			            	{
			            		result = "Item Out of stock";
			            		//objResult.put(obj.getString("OrderId"), result);
			            		//jsonResultArray.put(objResult);
			            	}
			            	else
			            	{
			            		 String status="";
			            		 int updatedquantity = quantaityint-1;
	                             if(updatedquantity>0)
	                             {
	                            	 status = "In Stock";
	                             }
	                             else
	                             {
	                            	 status = "Out Stock";
	                             }
				            	 String sqlupdate = "UPDATE productlist SET Status=?,Quantatity=? WHERE ProuctId=?";
				            	 PreparedStatement pstatement = conn.prepareStatement(sqlupdate);
				            	 pstatement.setString(1, status);
				            	 pstatement.setString(2, updatedquantity+"");
				            	 pstatement.setString(3, obj.getString("ProductID"));
				            	 int rowsUpdated = pstatement.executeUpdate();
				            	 if (rowsUpdated > 0) {
				            		 String sql = "INSERT INTO OrderDetail (OrderId, ImagePath,CatagoryName,MerchantSkuId,MRP,SellingPrice,Weight,Shipindays,Description,SellerEmail,SellerMobile,ProductName,ImageName,Quantatity,Status,Timestamp,BuyerMobile,ProductID,DeliveryAdress,UserName,UserAdressMobileNumber,TimestampforSearch) VALUES (?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?,?,?,?,?,?)";
					 		            PreparedStatement statement = conn.prepareStatement(sql);
					 		            statement.setString(1, obj.getString("OrderId"));
					 		            statement.setString(2, obj.getString("ImagePath"));
					 		            statement.setString(3, obj.getString("CatagoryName"));
					 		            statement.setString(4, obj.getString("MerchantSkuId"));
					 		            statement.setString(5, obj.getString("MRP"));
					 		            statement.setString(6, obj.getString("SellingPrice"));
					 		            statement.setString(7, obj.getString("Weight"));
					 		            statement.setString(8, obj.getString("Shipindays"));
					 		            statement.setString(9, obj.getString("Description"));
					 		            statement.setString(10, obj.getString("SellerEmail"));
					 		            statement.setString(11, obj.getString("SellerMobile"));
					 		            statement.setString(12, obj.getString("ProductName"));
					 		            statement.setString(13, obj.getString("ImageName"));
					 		            statement.setString(14, obj.getString("Quantatity"));
					 		            statement.setString(15, obj.getString("Status"));
					 		            statement.setString(16, obj.getString("Timestamp"));
					 		            statement.setString(17, obj.getString("BuyerMobile"));
					 		            statement.setString(18, obj.getString("ProductID"));
					 		            statement.setString(19, obj.getString("DeliveryAdress"));
					 		            statement.setString(20, obj.getString("UserName"));
					 		            statement.setString(21, obj.getString("UserAdressMobileNumber"));
					 		            statement.setString(22, obj.getString("TimestampforSearch"));
					 		            int rowsInserted = statement.executeUpdate();
					 		            if (rowsInserted > 0) {
					 		           	result = "Item Placed Sucessfully";
					 		           	
					 		           	
					 		       	
					 		       	//===================================send message to buyer====================================================//
					 		       	String authkey = "204882AnH8usahCKvL5ab1f865";
					 		       	String sender = "GAMLAA";
					 		       	String mobilenumber = "91"+obj.getString("BuyerMobile");
					 		       
					 		       	try {
					 		   			String res = "http://api.msg91.com/api/sendhttp.php?sender=GAMLAA&route=4&mobiles="+URLEncoder.encode(mobilenumber, "UTF-8")+"&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&country=91&message="+URLEncoder.encode(obj.getString("message"), "UTF-8");
					 		   		
					 		       	System.out.println("message--------->"+res);
					 		       	} catch (UnsupportedEncodingException e1) {
					 		   			// TODO Auto-generated catch block
					 		   			e1.printStackTrace();
					 		   		}
					 		   		
					 		       	
					 		       	
					 		       	try {
					 		       	HttpResponse<String> response = Unirest.get("http://api.msg91.com/api/sendhttp.php?sender=GAMLAA&route=4&mobiles="+URLEncoder.encode(mobilenumber, "UTF-8")+"&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&country=91&message="+URLEncoder.encode(obj.getString("message"), "UTF-8"))
					 		       			  .asString();
					 		       	
					 		       	}
					 		       	catch (Exception e) {
					 		   			// TODO: handle exception
					 		   		}
					 		       	
					 		       	//================================================================================================================//
					 		       	
					 		       	//========================================send push notification to buyer=========================================//
					 		       	
					 		           	
					 		           	
					 		          String registarionidforbuyer;
					 		          
					 		          Statement mystmt1=null;
					 		          try{
					 		              Class.forName("com.mysql.jdbc.Driver");
					 		              Connection conn1 = DriverManager.getConnection(url, user, pass);
					 		              mystmt1 = conn1.createStatement();
					 		              String sql1 = "Select * from  registrationidforfcm where MobileNo = ?";
					 		              String buyermobilenumber = obj.getString("BuyerMobile");
					 		              ResultSet results1 = mystmt1.executeQuery("Select * from registrationidforfcm where MobileNo = '"+buyermobilenumber+"'");
					 		              if(results1.next())
					 		              {
					 		            	
					 		            	 registarionidforbuyer = results1.getString("RegistrationId");
					 		            	 System.out.println("token------------------->"+registarionidforbuyer);
					 		            	 SendPushNotification sp = new SendPushNotification();  
						 		   		     sp.SendPushNotificationToBuyer(obj.getString("message"),registarionidforbuyer);
					 		              }
					 		             
					 		          }
					 		          catch(Exception e){
					 		              e.printStackTrace();
					 		          }
					 		        
					 		           	  
					 		   		     
					 		       	//================================================================================================================//
					 		       	
					 		       	//========================================send message to seller==================================================//
					 		       
					 		   		  //String authkey = "204882AnH8usahCKvL5ab1f865";
							 		  //String sender = "GAMLAA";
							 		  String mobilenumberseller = "91"+obj.getString("SellerMobile");
							 		       
							 		  try {
							 		   	    String res = "http://api.msg91.com/api/sendhttp.php?sender=GAMLAA&route=4&mobiles="+URLEncoder.encode(mobilenumberseller, "UTF-8")+"&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&country=91&message="+URLEncoder.encode(obj.getString("messageForSeller"), "UTF-8");
							 		       	System.out.println("message--------->"+res);
							 		       	} catch (UnsupportedEncodingException e1) {
							 		   			// TODO Auto-generated catch block
							 		   			e1.printStackTrace();
							 		   		}
							 		       	try {
							 		       	HttpResponse<String> response = Unirest.get("http://api.msg91.com/api/sendhttp.php?sender=GAMLAA&route=4&mobiles="+URLEncoder.encode(mobilenumberseller, "UTF-8")+"&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&country=91&message="+URLEncoder.encode(obj.getString("messageForSeller"), "UTF-8"))
							 		       			  .asString();
							 		       	
							 		       	}
							 		       	catch (Exception e) {
							 		   			// TODO: handle exception
							 		   		}  
					 		   		 //================================================================================================================//
					 		       	
					 		       	//========================================send push notification to seller========================================//
					 		       	
							 		        String registarionidforseller;
							 		          
							 		          Statement mystmt2=null;
							 		          try{
							 		              Class.forName("com.mysql.jdbc.Driver");
							 		              Connection conn2 = DriverManager.getConnection(url, user, pass);
							 		              mystmt2 = conn2.createStatement();
							 		              String sql2 = "Select * from  registrationidforfcm where MobileNo = ?";
							 		              String sellermobilenumber = obj.getString("SellerMobile");
							 		              ResultSet results2 = mystmt2.executeQuery("Select * from registrationidforfcm where MobileNo = '"+sellermobilenumber+"'");
							 		              if(results2.next())
							 		              {
							 		            	
							 		            	 registarionidforseller = results2.getString("RegistrationId");
							 		            	 SendPushNotification spforseller = new SendPushNotification(); 
										 		     spforseller.SendPushNotificationToSeller(obj.getString("messageForSeller"),registarionidforseller);
							 		              }
							 		             
							 		          }
							 		          catch(Exception e){
							 		              e.printStackTrace();
							 		          }
							 		       	
							 		       	
							 		     
							 		       	//================================================================================================================//
					 		           	
					 		           	
					 		           // objResult.put(obj.getString("OrderId"), result);
					 		           // jsonResultArray.put(objResult);
					 		            }
				            	 }
				            	 else
				            	 {
				 		            result = "Something went wrong";
				 		           // objResult.put(obj.getString("OrderId"), result);
				 		           // jsonResultArray.put(objResult);
				 		           
				            	 }
			            		
			            	}
			            	
			            }
			            else
			            {
			            	result = "Item not found";
			            	//objResult.put(obj.getString("OrderId"), result);
			            	//jsonResultArray.put(objResult);
			            }
	
			        }
		        }
		        catch(Exception e){
		            e.printStackTrace();
		        }
		        
		        return result;
			
		
    
      
    	
    }
    
    @POST
    @Path("/GetWishList")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String GetWishList(@FormParam("mobile") String mobile){
		Connection con = null;
		JSONObject obj = null;
		JSONArray objarray = new JSONArray();
		ArrayList<String> objArraylist = new ArrayList<String>();
		
		String mobileno = null;
		Statement mystmt,mystmt1,mystmt2=null;
		
    	
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
	    	con = DriverManager.getConnection(url, user, pass);
	    	mystmt = con.createStatement();
	    	mystmt1 = con.createStatement();
	        ResultSet resultss = mystmt.executeQuery("Select * from wishlist where MobileNo ='"+mobile+"'");
	        if(resultss.next())
	        {
	        	resultss.previous();
	        	while(resultss.next())
	        	{
	        		
	        		
	        		
	        		ResultSet results = mystmt1.executeQuery("Select * from productlist where ProuctId ='"+resultss.getString("ProductId")+"'");
	        		System.out.println("Select * from productlist where ProuctId ='"+resultss.getString("ProductId")+"'");
	        		
	        		while(results.next())
	        		{
	        		obj = new JSONObject();
	        		obj.put("ProuctId", results.getString("ProuctId"));
	        		obj.put("ImagePath", results.getString("ImagePath"));
	        		obj.put("Categoryname", results.getString("Categoryname"));
	        		obj.put("MerchantSkuId", results.getString("MerchantSkuId"));
	        		obj.put("MRP", results.getString("MRP"));
	        		obj.put("SellingPrice", results.getString("SellingPrice"));
	        		obj.put("weight", results.getString("weight"));
	        		obj.put("shipindays", results.getString("shipindays"));
	        		obj.put("description", results.getString("description"));
	        		obj.put("email", results.getString("email"));
	        		obj.put("mobileno", results.getString("mobileno"));
	        		obj.put("ProductName", results.getString("ProductName"));
	        		obj.put("ImageName", results.getString("ImageName"));
	        		obj.put("Quantatity", results.getString("Quantatity"));
	        		obj.put("Status", results.getString("Status"));
	        		
	        		mystmt2 = con.createStatement();
	    	        ResultSet results1 = mystmt2.executeQuery("Select * from buisnessinformation where EmailId ='"+results.getString("email")+"' and MobileNo = '"+results.getString("mobileno")+"'");
	        		while(results1.next())
	        		{
	        			obj.put("BuisnessInformationID", results1.getString("BuisnessInformationID"));
	        			obj.put("MobileNo", results1.getString("MobileNo"));
	        			obj.put("EmailId", results1.getString("EmailId"));
	        			obj.put("BuisnessType", results1.getString("BuisnessType"));
	        			obj.put("RegisteredBuisnessName", results1.getString("RegisteredBuisnessName"));
	        			obj.put("RegisteredBuisnessAdress", results1.getString("RegisteredBuisnessAdress"));
	        			obj.put("PinCode", results1.getString("PinCode"));
	        			obj.put("State", results1.getString("State"));
	        			obj.put("City", results1.getString("City"));
	        			obj.put("DisplayName", results1.getString("DisplayName"));
	        		}
	    	        
	    	        objarray.put(obj);
	        		}
	        	}
	        }
	        else
	        {
	        	obj = new JSONObject();
	        	obj.put("status", "false");
	        	objarray.put(obj);
	        }
			
		} catch (JSONException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objarray+"";
    }
    
    @POST
    @Path("/OrderList")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String OrderList(@FormParam("mobilenumber") String mobile,@FormParam("limitstart") String limitstart,@FormParam("limitend") String limitend,@FormParam("flag") String flag,@FormParam("searchquery") String searchquery,@FormParam("Status") String status){
		Connection con = null;
		JSONObject obj = null;
		JSONArray objarray = new JSONArray();
		ArrayList<String> objArraylist = new ArrayList<String>();
		Statement mystmt,mystmt1 = null;
		String result = "false";
		int limitstart1 = Integer.parseInt(limitstart);
		int limitend1 = Integer.parseInt(limitend);
	    try {
			
				Class.forName("com.mysql.jdbc.Driver");
		    	con = DriverManager.getConnection(url, user, pass);
		    	mystmt = con.createStatement();
		    	mystmt1 = con.createStatement();
		    	ResultSet results1;
		    	ResultSet resultss = null;
		    	int count;
		    	if(flag.equalsIgnoreCase("Buyer"))
		    	{
		    		if(searchquery.length()<=0)
		    		{
				    	System.out.println("Select * from orderdetail where BuyerMobile ='"+mobile+"' Order BY Timestamp DESC  LIMIT "+limitstart1+","+limitend1);
				    	results1 = mystmt1.executeQuery("SELECT COUNT(*) AS rowcount from orderdetail where BuyerMobile ='"+mobile+"'");
				    	results1.next();
					    count = results1.getInt("rowcount");
					    System.out.println("row count ----------->"+count);
					    results1.close();
				    	resultss = mystmt.executeQuery("Select * from orderdetail where BuyerMobile ='"+mobile+"' Order BY Timestamp DESC  LIMIT "+limitstart1+","+limitend1);
			    	}
		    		else
		    		{
                        System.out.println("Select * from orderdetail where BuyerMobile ='"+mobile+"' and "+searchquery+" Order BY Timestamp DESC  LIMIT "+limitstart1+","+limitend1);
				    	results1 = mystmt1.executeQuery("SELECT COUNT(*) AS rowcount from orderdetail where BuyerMobile ='"+mobile+"' and "+searchquery);
				    	results1.next();
					    count = results1.getInt("rowcount");
					    System.out.println("row count ----------->"+count);
					    results1.close();
				    	resultss = mystmt.executeQuery("Select * from orderdetail where BuyerMobile ='"+mobile+"' and "+searchquery+" Order BY Timestamp DESC  LIMIT "+limitstart1+","+limitend1);
			    	
		    		}
			    	
		    	}
		    	else
		    	{
		    		if(searchquery.length()<=0)
			    	{
		    			System.out.println("Select * from orderdetail where SellerMobile ='"+mobile+"' and Status ='"+status+"' Order BY Timestamp DESC  LIMIT "+limitstart1+","+limitend1);
			    		results1 = mystmt1.executeQuery("SELECT COUNT(*) AS rowcount from orderdetail where SellerMobile ='"+mobile+"' and Status ='"+status+"'");
				    	results1.next();
					    count = results1.getInt("rowcount");
					    System.out.println("row count ----------->"+count);
					    results1.close();
				    	resultss = mystmt.executeQuery("Select * from orderdetail where SellerMobile ='"+mobile+"' and Status ='"+status+"' Order BY Timestamp DESC  LIMIT "+limitstart1+","+limitend1);
				    }
			    	else
			    	{
			    		results1 = mystmt1.executeQuery("SELECT COUNT(*) AS rowcount from orderdetail where SellerMobile ='"+mobile+"' and Status ='"+status+"' and "+searchquery);
				    	results1.next();
					    count = results1.getInt("rowcount");
					    System.out.println("row count ----------->"+count);
					    results1.close();
				    	resultss = mystmt.executeQuery("Select * from orderdetail where SellerMobile ='"+mobile+"'  and Status ='"+status+"' and "+searchquery+" Order BY Timestamp DESC  LIMIT "+limitstart1+","+limitend1);
				    	
			    	}
		    	}
		       
		        if(resultss.next())
		        {
		        	resultss.previous();
		        	while(resultss.next())
		        	{
		        		obj = new JSONObject();
		        		obj.put("Id", resultss.getString("Id"));
		        		obj.put("OrderId", resultss.getString("OrderId"));
		        		obj.put("ImagePath", resultss.getString("ImagePath"));
		        		obj.put("CatagoryName", resultss.getString("CatagoryName"));
		        		obj.put("MerchantSkuId", resultss.getString("MerchantSkuId"));
		        		obj.put("MRP", resultss.getString("MRP"));
		        		obj.put("SellingPrice", resultss.getString("SellingPrice"));
		        		obj.put("Weight", resultss.getString("Weight"));
		        		obj.put("Shipindays", resultss.getString("Shipindays"));
		        		obj.put("Description", resultss.getString("Description"));
		        		obj.put("SellerEmail", resultss.getString("SellerEmail"));
		        		obj.put("SellerMobile", resultss.getString("SellerMobile"));
		        		obj.put("ProductName", resultss.getString("ProductName"));
		        		obj.put("ImageName", resultss.getString("ImageName"));
		        		obj.put("Quantatity", resultss.getString("Quantatity"));
		        		obj.put("Status", resultss.getString("Status"));
		        		obj.put("Timestamp", resultss.getString("Timestamp"));
		        		obj.put("BuyerMobile", resultss.getString("BuyerMobile"));
		        		obj.put("ProductID", resultss.getString("ProductID"));
		        		obj.put("DeliveryAdress", resultss.getString("DeliveryAdress"));
		        		obj.put("UserName", resultss.getString("UserName"));
		        		obj.put("UserAdressMobileNumber", resultss.getString("UserAdressMobileNumber"));
		        		obj.put("rowcount", count+"");
		        		objarray.put(obj);
		        		
		        	}
		        }
		        else
		        {
		        	obj = new JSONObject();
		        	obj.put("status", "Record Not found");
		        	objarray.put(obj);
		        	
		        }
	       }
	    catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return objarray+"";
    }
    @POST
    @Path("/DashboardCounter")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String DashboardCounter(@FormParam("email") String email,@FormParam("mobile") String mobile){
    	 String result="false";
         boolean x;
         Statement mystmt1=null;
         Statement mystmt2=null;
         Statement mystmt3=null;
         JSONObject objcatalog;
         Statement mystmt4=null;
         Statement mystmt5=null;
         Statement mystmt6=null;
         JSONObject objorder;
         JSONArray objarray = new JSONArray();
         try{
             Class.forName("com.mysql.jdbc.Driver");
             Connection conn = DriverManager.getConnection(url, user, pass);
             mystmt1 = conn.createStatement();
             mystmt2 = conn.createStatement();
             mystmt3 = conn.createStatement();
             //String sql = "Select COUNT(*) AS rowcount from  productlist where mobileno = ? and email =?";
             ResultSet results1 = mystmt1.executeQuery("Select COUNT(*) AS rowcount from  productlist where mobileno = '"+mobile+"' and email = '"+email+"' and Status = 'In Stock'");
             ResultSet results2 = mystmt2.executeQuery("Select COUNT(*) AS rowcount from  productlist where mobileno = '"+mobile+"' and email = '"+email+"' and Status = 'Out Stock'");
             ResultSet results3 = mystmt3.executeQuery("Select COUNT(*) AS rowcount from  productlist where mobileno = '"+mobile+"' and email = '"+email+"' and Status = 'Non Live'");
             System.out.println("Select COUNT(*) AS rowcount from  productlist where mobileno = '"+mobile+"' and email = '"+email+"' and Status = 'In Stock'");
             mystmt4 = conn.createStatement();
             mystmt5 = conn.createStatement();
             mystmt6 = conn.createStatement();
             //String sql = "Select COUNT(*) AS rowcount from  productlist where mobileno = ? and email =?";
             ResultSet results4 = mystmt4.executeQuery("Select COUNT(*) AS rowcount from  orderdetail where SellerMobile = '"+mobile+"' and Status = 'Order Placed'");
             ResultSet results5 = mystmt5.executeQuery("Select COUNT(*) AS rowcount from  orderdetail where SellerMobile = '"+mobile+"' and Status = 'Shipped'");
             ResultSet results6 = mystmt6.executeQuery("Select COUNT(*) AS rowcount from  orderdetail where SellerMobile = '"+mobile+"' and Status = 'Delivered'");
           
             if(results1.next()&&results2.next()&&results3.next())
             {
            	objcatalog = new JSONObject();
            	objcatalog.put("In Stock", results1.getInt("rowcount")+"");
            	objcatalog.put("Out Stock", results2.getInt("rowcount")+"");
            	objcatalog.put("Non Live", results3.getInt("rowcount")+"");
            	objarray.put(objcatalog);
             	
             }
             else
             {
            	 objarray.put("false");
             }
             
             if(results4.next()&&results5.next()&&results6.next())
             {
            	objorder = new JSONObject();
            	objorder.put("Order Placed", results4.getInt("rowcount")+"");
            	objorder.put("Shipped", results5.getInt("rowcount")+"");
            	objorder.put("Delivered", results6.getInt("rowcount")+"");
            	objarray.put(objorder);
             	
             }
             else
             {
            	 objarray.put("false");
             }
             
           
         }
         catch(Exception e){
             e.printStackTrace();
         }
         
         return objarray+"";
    }
    
    @POST
    @Path("/PremiumPromotionProduct")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String PremiumPromotionProduct(@FormParam("type") String type){
        String result="false";
        boolean x;
        Statement mystmt,mystmt2,mystmt3=null;
        JSONObject obj;
        JSONObject rootObj = new JSONObject();
        JSONArray objarray = new JSONArray();
        JSONArray objpromotionArray;
        JSONArray objproductArray = new JSONArray();
        String productid = null;
        String discount="0";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            mystmt = conn.createStatement();
            String sql = "Select * from promotionalproduct where PromotionCategoryName = '"+type+"'";
            System.out.println("promotion sql------>"+sql);
            ResultSet results = mystmt.executeQuery("Select * from promotionalproduct where PromotionCategoryName = '"+type+"'");
            if(results.next())
            {
            	results.previous();
            	while(results.next())
            	{
            		//productid = results.getString("ProductId"); 
            		discount = results.getString("Discount");
            		if(type.equalsIgnoreCase("Silver"))
            		{
            			 productid =  results.getString("ProductId");
            			 String promotionname = results.getString("PromotionImageName");
            			 mystmt2 = conn.createStatement();
                     	 ResultSet results1 = mystmt2.executeQuery("Select * from productlist where ProuctId IN ("+productid+")");
                     	 if(results1.next())
                     	 {
                     		results1.previous();
                     		objpromotionArray = new JSONArray();
            	        	while(results1.next())
            	        	{
            	        		obj = new JSONObject();
            	        		obj.put("ProuctId", results1.getString("ProuctId"));
            	        		obj.put("ImagePath", results1.getString("ImagePath"));
            	        		obj.put("Categoryname", results1.getString("Categoryname"));
            	        		obj.put("MerchantSkuId", results1.getString("MerchantSkuId"));
            	        		obj.put("MRP", results1.getString("MRP"));
            	        		obj.put("SellingPrice", results1.getString("SellingPrice"));
            	        		obj.put("weight", results1.getString("weight"));
            	        		obj.put("shipindays", results1.getString("shipindays"));
            	        		obj.put("description", results1.getString("description"));
            	        		obj.put("email", results1.getString("email"));
            	        		obj.put("mobileno", results1.getString("mobileno"));
            	        		obj.put("ProductName", results1.getString("ProductName"));
            	        		obj.put("ImageName", results1.getString("ImageName"));
            	        		obj.put("Quantatity", results1.getString("Quantatity"));
            	        		obj.put("Status", results1.getString("Status"));
            	        		obj.put("discount", discount);
            	        		//obj.put("rowcount",rowcount+"");
            	        		
            	        		mystmt3 = conn.createStatement();
            	    	        ResultSet results2 = mystmt3.executeQuery("Select * from buisnessinformation where EmailId ='"+results1.getString("email")+"' and MobileNo = '"+results1.getString("mobileno")+"'");
            	        		while(results2.next())
            	        		{
            	        			obj.put("BuisnessInformationID", results2.getString("BuisnessInformationID"));
            	        			obj.put("MobileNo", results2.getString("MobileNo"));
            	        			obj.put("EmailId", results2.getString("EmailId"));
            	        			obj.put("BuisnessType", results2.getString("BuisnessType"));
            	        			obj.put("RegisteredBuisnessName", results2.getString("RegisteredBuisnessName"));
            	        			obj.put("RegisteredBuisnessAdress", results2.getString("RegisteredBuisnessAdress"));
            	        			obj.put("PinCode", results2.getString("PinCode"));
            	        			obj.put("State", results2.getString("State"));
            	        			obj.put("City", results2.getString("City"));
            	        			obj.put("DisplayName", results2.getString("DisplayName"));
            	        		}
            	    	        
            	        		objpromotionArray.put(obj);
            	    	        
            	        	}
            	        	rootObj.put(promotionname, objpromotionArray);
            	        	objarray.put(rootObj);
            	        
                     		
                     		
                     	 }
                     	 else
                     	 {
                     		result = "false";
                         	return result;
                     	 }
            			 
            			
            		}
            		else
            		{
	            		obj = new JSONObject();
	            		obj.put("id", results.getString("id"));
	            		obj.put("PromotionCategoryId", results.getString("PromotionCategoryId"));
	            		obj.put("ProductId", results.getString("ProductId"));
	            		obj.put("PromotionCategoryName", results.getString("PromotionCategoryName"));
	            		obj.put("PromotionImageName", results.getString("PromotionImageName"));
	            		obj.put("PromotionImageurl", results.getString("PromotionImageurl"));
	            		obj.put("Discount", results.getString("Discount"));
	            		objarray.put(obj);
            		}
            	}
            	/*mystmt2 = conn.createStatement();
            	ResultSet results1 = mystmt2.executeQuery("Select * from productlist where ProuctId IN ("+productid+")");
            	if(results1.next())
            	{
            		
            	}
            	else
            	{
            		result = "false";
                	return result;
            	}*/
            	
            }
            else
            {
            	result = "false";
            	return result;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return objarray+"";
    }
    @POST
    @Path("/RegistrationForFCM")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String RegistrationForFCM(@FormParam("macadress") String macadress, @FormParam("regid") String regid){
        String result="false";
        boolean x;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            String sql = "INSERT INTO RegistrationIdForFCM (macadress, RegistrationId) VALUES (?, ?)";
            
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, macadress);
            statement.setString(2, regid);
        
             
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
            	result = "true";
               
            }
          
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }
    @POST
    @Path("/RejectOrderFromSeller")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String RejectOrderFromSeller(@FormParam("orderid") String orderid,@FormParam("Status") String Status,@FormParam("ProductName") String productname,@FormParam("BuyerMobile") String buyermobilenumber,@FormParam("timestamp") String timestamp,@FormParam("timestampforsearch") String timestampforsearch){
    	 String result="false";
       
         try{
             Class.forName("com.mysql.jdbc.Driver");
             Connection con = DriverManager.getConnection(url, user, pass);
             System.out.println("-----------productid---------------->"+timestampforsearch);
            	 String sql = "UPDATE orderdetail SET Status=? ,Timestamp=?,TimestampforSearch=? WHERE OrderId=?";
            	 PreparedStatement pstatement = con.prepareStatement(sql);
            	 pstatement.setString(1, Status);
            	 pstatement.setString(2, timestamp);
            	 pstatement.setString(3, timestampforsearch);
            	 pstatement.setString(4, orderid);
            	 
            	 int rowsUpdated = pstatement.executeUpdate();
            	 if (rowsUpdated > 0) {
            	     System.out.println("Record updated successfully!");
            	    
            	     String message;
            	     if(Status.equalsIgnoreCase("Rejected"))
            	     {
            	    	 result = "Item Rejected";
            	    	 message = "Your Order of "+productname+" with order id:"+orderid+" is Rejected due to some reason, sorry for inconvenience";
            	     }
            	     else
            	     {
            	    	 result = "Item Shipped";
            	    	 message = "Your Order of "+productname+" with order id:"+orderid+" is Shipped";
            	     }
            	 	//===================================send message to buyer====================================================//
		 		       	String authkey = "204882AnH8usahCKvL5ab1f865";
		 		       	String sender = "GAMLAA";
		 		       	String mobilenumber = "91"+buyermobilenumber;
		 		       
		 		       	try {
		 		   			String res = "http://api.msg91.com/api/sendhttp.php?sender=GAMLAA&route=4&mobiles="+URLEncoder.encode(mobilenumber, "UTF-8")+"&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&country=91&message="+URLEncoder.encode(message, "UTF-8");
		 		   		
		 		       	System.out.println("message--------->"+res);
		 		       	} catch (UnsupportedEncodingException e1) {
		 		   			// TODO Auto-generated catch block
		 		   			e1.printStackTrace();
		 		   		}
		 		   		
		 		       	
		 		       	
		 		       	try {
		 		       	HttpResponse<String> response = Unirest.get("http://api.msg91.com/api/sendhttp.php?sender=GAMLAA&route=4&mobiles="+URLEncoder.encode(mobilenumber, "UTF-8")+"&authkey="+URLEncoder.encode(authkey, "UTF-8")+"&country=91&message="+URLEncoder.encode(message, "UTF-8"))
		 		       			  .asString();
		 		       	
		 		       	}
		 		       	catch (Exception e) {
		 		   			// TODO: handle exception
		 		   		}
		 		       	
		 		       	//================================================================================================================//
		 		       	
            	     
            	     String registarionidforbuyer;
	 		          
	 		          Statement mystmt1=null;
	 		          try{
	 		              Class.forName("com.mysql.jdbc.Driver");
	 		              Connection conn1 = DriverManager.getConnection(url, user, pass);
	 		              mystmt1 = conn1.createStatement();
	 		              String sql1 = "Select * from  registrationidforfcm where MobileNo = ?";
	 		              ResultSet results1 = mystmt1.executeQuery("Select * from registrationidforfcm where MobileNo = '"+buyermobilenumber+"'");
	 		              if(results1.next())
	 		              {
	 		            	
	 		            	 registarionidforbuyer = results1.getString("RegistrationId");
	 		            	 System.out.println("token------------------->"+registarionidforbuyer);
	 		            	 SendPushNotification sp = new SendPushNotification();  
		 		   		     sp.SendPushNotificationToBuyer(message,registarionidforbuyer);
	 		              }
	 		             
	 		          }
	 		          catch(Exception e){
	 		              e.printStackTrace();
	 		          }
            	     
            	    
            	     
            	
             }
             con.close();
                
         }
         catch (Exception e) {
			e.printStackTrace();
		}
         return result;
    
    
    }
    @POST
    @Path("/ProductType")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String ProductType(@FormParam("catagory") String catagory){
        String result="false";
        JSONArray objarray = new JSONArray();
        boolean x;
        Statement mystmt=null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            mystmt = conn.createStatement();
            //String sql = "Select DISTINCT ProductName from productlist where MobileNo = ? and productid =?";
            String Status = "In Stock";
            ResultSet results = mystmt.executeQuery("Select distinct ProductName from productlist where Categoryname = '"+catagory+"' and Status ='"+Status+"'");
            if(results.next())
            {
            	
            	JSONObject obj;
            	results.previous();
            	while(results.next())
            	{
            		obj = new JSONObject();
            		obj.put("name", results.getString("ProductName"));
            		objarray.put(obj);
            	}
            }
            else
            {
            	objarray.put("false");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return objarray+"";
    }
    @POST
    @Path("/GetMinAndMaxPrice")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String GetMinAndMaxPrice(@FormParam("catagory") String catagory){
        String result="false";
      
        boolean x;
        Statement mystmt=null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            mystmt = conn.createStatement();
            //String sql = "Select DISTINCT ProductName from productlist where MobileNo = ? and productid =?";
            String Status = "In Stock";
            ResultSet results = mystmt.executeQuery("Select MIN(SellingPrice) As MinPrice, MAX(SellingPrice) As MaxPrice from productlist where Categoryname = '"+catagory+"' and Status ='"+Status+"'");
            if(results.next())
            {
            	
            	
            	results.previous();
            	while(results.next())
            	{
            		result = results.getString("MinPrice")+","+results.getString("MaxPrice");
            	}
            }
            else
            {
            	result = "false";
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    @SuppressWarnings("unused")
	private void createFolderIfNotExists(String dirName)
			throws SecurityException {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
			theDir.mkdir();
		}
	}
}