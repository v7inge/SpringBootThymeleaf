package ru.aconsultant.thymeleaf.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.aconsultant.thymeleaf.beans.UserAccount;

public class Serializer {

	static public String serializeObject(Object obj) throws JsonGenerationException, JsonMappingException, IOException {

		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(writer, obj);
		String result = writer.toString();
		
		return result;
	}
	
	static public UserAccount deSerializeUserAccount(String st) throws JsonGenerationException, JsonMappingException, IOException {

		UserAccount obj = new UserAccount();
		StringReader reader = new StringReader(st);
		ObjectMapper mapper = new ObjectMapper();
		obj = mapper.readValue(reader, obj.getClass());
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	static public Object deSerialize(String st, Class<?> cl) throws JsonGenerationException, JsonMappingException, IOException, InstantiationException, IllegalAccessException {

		@SuppressWarnings("deprecation")
		Object obj = cl.newInstance();
		
		StringReader reader = new StringReader(st);
		ObjectMapper mapper = new ObjectMapper();
		obj = mapper.readValue(reader, cl);
		return obj;
	}
	
}
