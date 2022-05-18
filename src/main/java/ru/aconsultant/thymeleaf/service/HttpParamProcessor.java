package ru.aconsultant.thymeleaf.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class HttpParamProcessor {
	
	
	public HashMap<String, Object> getRequestParameters(HttpServletRequest request) throws IOException {
		
		HashMap<String, Object> params = new HashMap<>();
		
		StringBuffer sb = new StringBuffer();
        BufferedReader reader = request.getReader();
        String line = null;
    
        while ((line = reader.readLine()) != null)
            sb.append(line);
        
        try {
            
        	String jsonString = sb.toString();
        	JSONObject jsonObject =  new JSONObject(jsonString);		
        	
        	String[] keys = JSONObject.getNames(jsonObject);
        	
        	for (String key : keys) {
        		params.put(key, jsonObject.get(key));
            }
            
        } catch (JSONException e) { }
        
        return params;
	}
	
	
	public void translateResponseParameters(HttpServletResponse response, HashMap<String, Object> map) throws IOException {
		
		JSONObject jsonEnt = new JSONObject();
		
		for(Map.Entry<String, Object> item : map.entrySet()) {
			jsonEnt.put(item.getKey(), item.getValue());
	    }
		
        PrintWriter out = response.getWriter();
        out.write(jsonEnt.toString());
	}
	
}
