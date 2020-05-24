package ru.aconsultant.thymeleaf.controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.Principal;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
import ru.aconsultant.thymeleaf.beans.Contact;
import ru.aconsultant.thymeleaf.beans.Message;
import ru.aconsultant.thymeleaf.conn.DatabaseAccess;
import ru.aconsultant.thymeleaf.service.CounterResetThread;
import ru.aconsultant.thymeleaf.security.PasswordEncoder;
import ru.aconsultant.thymeleaf.security.UserDetailsServiceImpl;

@Controller
@SessionAttributes({"loginedUser","testUser"})
public class MainController {
	
	@Autowired
    private DatabaseAccess databaseAccess;
	
	@Autowired
    private UserDetailsServiceImpl userService;
	
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
	public String chatGet(Model model, Principal principal) throws SQLException {
		
		String userName = "no user";
		if (principal != null) {
			userName = principal.getName();
		}
		
		/*String userName = principal.getName();
		 
        System.out.println("User Name: " + userName);
 
        User loginedUser = (User) ((Authentication) principal).getPrincipal();*/
 
        //model.addAttribute("userInfo", userInfo);
        model.addAttribute("loginedUser", userName);
		
		//UserAccount yy = (UserAccount) model.getAttribute("testUser");
		
		// Fill contact list
        ArrayList<Contact> contactList = this.databaseAccess.contactList(userName);
		model.addAttribute("contactList", contactList);

		return "chat";
	}
	
	@RequestMapping(value = { "/message-sent" }, method = RequestMethod.POST)
	public String messageSent(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws SQLException, IOException {
		
		String contact = "";
        StringBuffer sb = new StringBuffer();
        String line = null;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null)
            sb.append(line);

        try {
            
        	String jsonString = sb.toString();
        	
        	StringReader stringReader = new StringReader(jsonString);
        	ObjectMapper mapper = new ObjectMapper();
        	Message message = mapper.readValue(stringReader, Message.class);
        			
        	try {
        		this.databaseAccess.saveMessage(message);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) { }
        
        return null;
	}
	
	@RequestMapping(value = { "/contact-clicked" }, method = RequestMethod.POST)
	public String contactClick(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws SQLException, IOException {
		
		String contact = "";
        StringBuffer sb = new StringBuffer();
        String line = null;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null)
            sb.append(line);
        
        try {
            
        	String jsonString = sb.toString();
        	JSONObject jsonObject =  new JSONObject(jsonString);		
        	JSONObject jsonEnt = new JSONObject();
        	
        	contact = jsonObject.getString("contact");
        	request.setAttribute("contact", contact);
        	String userName = model.getAttribute("loginedUser").toString();
        	List<Message> history = this.databaseAccess.getHistory(userName, contact);
            jsonEnt.put("contactHistory", history);
            PrintWriter out = response.getWriter();
            out.write(jsonEnt.toString());
        
            // Reset message counter with minor thread priority
            if (jsonObject.getBoolean("needToResetCounter")) {
            	CounterResetThread counterResetThread = new CounterResetThread(userName, contact, this.databaseAccess);
            	counterResetThread.setPriority(3);
            	counterResetThread.start();
            }
            
        } catch (JSONException e) { }
        
        return null;
	}
	
	@RequestMapping(value = { "/auth" }, method = RequestMethod.GET)
	public String authGet(Model model, Principal principal) {
		
		String userName = "no user";
		if (principal != null) {
			userName = principal.getName();
		}
 
        //User loginedUser = (User) ((Authentication) principal).getPrincipal();
        model.addAttribute("message", userName);
 

		model.addAttribute("authForm", new AuthForm());

		return "auth";
	}
	
	@RequestMapping(value = { "/about" }, method = RequestMethod.GET)
	public String aboutGet(Model model) throws SQLException {

		return "about";
	}
	
	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
	public String registerPost(Model model, @ModelAttribute("authForm") AuthForm authForm, Principal principal) throws SQLException {
 
        String username = authForm.getUsername();
        String password = authForm.getPassword();
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        String encryptedPassword = PasswordEncoder.encryptPassword(password);
        UserAccount user = new UserAccount(username, encryptedPassword);
        
        String errorString = this.databaseAccess.addUserAccount(user);
        if (errorString == "") {
        	
        	Authentication authentication = new UsernamePasswordAuthenticationToken(userService.loadUserByUsername(username), null, userService.createAuthorityList("ROLE_USER"));
        	SecurityContextHolder.getContext().setAuthentication(authentication);
        	return "chat";
        	
        } else {
        	System.out.println(errorString);
        }
 
        
        return "auth";
    }
	
}