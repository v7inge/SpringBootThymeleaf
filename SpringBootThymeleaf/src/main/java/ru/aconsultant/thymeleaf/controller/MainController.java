package ru.aconsultant.thymeleaf.controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.aconsultant.thymeleaf.form.AuthForm;
import ru.aconsultant.thymeleaf.service.Serializer;
import ru.aconsultant.thymeleaf.beans.UserAccount;
import ru.aconsultant.thymeleaf.utils.DBUtils;
import ru.aconsultant.thymeleaf.utils.MyUtils;
import ru.aconsultant.thymeleaf.beans.Message;
import ru.aconsultant.thymeleaf.conn.DatabaseAccess;

@Controller
@SessionAttributes("loginedUser")
public class MainController {

	private String loginedUser;
	
	@Autowired
    private DatabaseAccess databaseAccess;
	
	@MessageMapping("/message")
	@SendTo("/chat/messages")
	public Message getMessages(Message message, SimpMessageHeaderAccessor headerAccessor) {
		
		return message;
	}
	
	@MessageMapping("/direct/{sender}/to/{reciever}")
	@SendTo("/queue/{reciever}")
	public Message direct(@Payload Message message, @DestinationVariable String sender, @DestinationVariable String reciever) {
		
		return message;
	}
	
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public String chatGet(Model model, HttpServletRequest request) throws SQLException {
		
		// Fill contact list
        ArrayList<String> contactList = this.databaseAccess.contactList(model.getAttribute("loginedUser").toString());
		model.addAttribute("contactList", contactList);

		return "chat";
	}
	
	@RequestMapping(value = { "/" }, method = RequestMethod.POST)
	public String chatPost(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws SQLException, IOException {
		
		String contact = "";
        StringBuffer sb = new StringBuffer();
        String line = null;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null)
            sb.append(line);

        try {
            
        	String jsonString = sb.toString();
        	JSONObject jsonObject =  new JSONObject(jsonString);
        	
        	switch (jsonObject.getString("queryType")) {
        		case "contact_clicked":
        			
        			JSONObject jsonEnt = new JSONObject();
        			contact = jsonObject.getString("contact");
        			request.setAttribute("contact", contact);
        			List<Message> history = this.databaseAccess.getHistory(model.getAttribute("loginedUser").toString(), contact);
                    jsonEnt.put("contactHistory", history);
                    PrintWriter out = response.getWriter();
                	out.write(jsonEnt.toString());
        	        break;
        		case "message_sent":
        			String messageString = jsonString.replace("\"queryType\":\"message_sent\",", "");
        			jsonObject.remove("queryType");
        			StringReader stringReader = new StringReader(messageString);
        			ObjectMapper mapper = new ObjectMapper();
        			Message message = mapper.readValue(stringReader, Message.class);
        			try {
        				this.databaseAccess.saveMessage(message);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
        			break;
        	    default:
        	    	return "chat";
        	}
        } catch (JSONException e) {
        }
        
        return null;
	}
	
	@RequestMapping(value = { "/auth" }, method = RequestMethod.GET)
	public String authGet(Model model) { //, @ModelAttribute("loginedUser") UserAccount loginedUser) {
		
		model.addAttribute("authForm", new AuthForm());
		model.addAttribute("message", "message from controller");

		return "auth";
	}
	
	@RequestMapping(value = { "/auth" }, method = RequestMethod.POST)
	public String authPost(Model model, @ModelAttribute("authForm") AuthForm authForm, HttpServletRequest request, HttpSession session) throws JsonGenerationException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
 
        String login = authForm.getLogin();
        String password = authForm.getPassword();
        boolean remember = authForm.getRemember();
 
        UserAccount user = null;
        boolean hasError = false;
        String errorString = null;
 
        if (login == null || password == null || login.length() == 0 || password.length() == 0) {
            hasError = true;
            errorString = "Please enter login and password";
        } else {
        	user = this.databaseAccess.findUser(login, password);
        	if (user == null) {
        		hasError = true;
                errorString = "Invalid pair login-passwod";
        	} else {
        		
        		model.addAttribute("loginedUser", user.getUserName());
        		/*session.setAttribute("testUser", Serializer.serializeObject(new UserAccount("test login", "test password")));
        		UserAccount testUser = Serializer.deSerializeUserAccount(session.getAttribute("testUser").toString());*/
        		return "redirect:/";
        	}
        }
 
        model.addAttribute("errorString", errorString);
        return "auth";
    }
	
}