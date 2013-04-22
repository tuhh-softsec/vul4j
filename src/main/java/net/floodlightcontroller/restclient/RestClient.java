package net.floodlightcontroller.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;


public class RestClient {

	public static void get (String str) {
		
		if (str == null)
			return;
	
		try {
	 
			URL url = new URL(str);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
	 
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	 
			if (conn.getContentType().equals("application/json")) 
			{	}else{
				System.out.print("The content received is not json format!");				
			}		
			
		 BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()))); 
		 StringBuffer res = new StringBuffer();
		 String line;
		 while ((line = br.readLine()) != null) {
			 	res.append(line);
		 	}	   
		 
		 String res2=res.toString().replaceAll("\"", "'");
		 JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(res2);  
		 JSONArray rib_json_array = jsonObj.getJSONArray("rib");
		 String router_id = jsonObj.getString("router-id");
		       
		 int size = rib_json_array.size();
		 System.out.print("size:"+size+"\n");
		 for (int j = 0; j < size; j++) {
        JSONObject second_json_object = rib_json_array.getJSONObject(j);
        String prefix = second_json_object.getString("prefix");
        String nexthop = second_json_object.getString("nexthop");
              			
     		//insert each rib entry into the local rib;
        RestClient.post("http://127.0.0.1:8090/wm/bgp/"+router_id+"/"+prefix+"/"+nexthop);
          
        
        
		 }
		 br.close();
		 conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}
	
public static void post (String str) {
		
		if (str == null)
			return;
	
		try {
	 
			URL url = new URL(str);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");		
	 
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
					
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}
	

public static void delete (String str) {
	
	if (str == null)
		return;

	try {
 
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("DELETE");
		conn.setRequestProperty("Accept", "application/json");
		
 
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
 
		conn.disconnect();

	} catch (MalformedURLException e) {

		e.printStackTrace();

	} catch (IOException e) {

		e.printStackTrace();

	}
}
	
	
}
