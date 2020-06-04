package ru.aconsultant.thymeleaf.controller;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.aconsultant.thymeleaf.form.AuthForm;
import ru.aconsultant.thymeleaf.beans.UserAccount;
import ru.aconsultant.thymeleaf.beans.Contact;
import ru.aconsultant.thymeleaf.beans.Message;
import ru.aconsultant.thymeleaf.conn.DatabaseAccess;
import ru.aconsultant.thymeleaf.service.CounterResetThread;
import ru.aconsultant.thymeleaf.service.HttpParamProcessor;
import ru.aconsultant.thymeleaf.service.MessageSaveThread;
import ru.aconsultant.thymeleaf.security.PasswordEncoder;
import ru.aconsultant.thymeleaf.security.UserDetailsServiceImpl;
import ru.aconsultant.thymeleaf.service.FileProcessor;
import ru.aconsultant.thymeleaf.form.UploadForm;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.util.Base64;

@Controller
@SessionAttributes({"loginedUser","testUser"})
public class MainController {
	
	@Autowired
    private DatabaseAccess databaseAccess;
	
	@Autowired
    private UserDetailsServiceImpl userService;
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private HttpParamProcessor httpParamProcessor;
	
	@Autowired
	private FileProcessor fileProcessor;
	
	@Autowired
    private ServletContext servletContext;
	
	
	// How to get principal as a user:
	//User loginedUser = (User) ((Authentication) principal).getPrincipal();
	
	
	@MessageMapping("/message-flow")
    public void broadcast(@Payload Message message, Principal principal) {

        messagingTemplate.convertAndSendToUser(message.getReciever(), "/queue/reply", message);
        
        // Save massage to database with minor thread priority
        MessageSaveThread messageSaveThread = new MessageSaveThread(message, this.databaseAccess);
        messageSaveThread.setPriority(3);
        messageSaveThread.start(); 
    }
	
	
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public String chatGet(Model model, Principal principal) throws SQLException, InvalidResultSetAccessException, IOException {
 		
		// Fill contact list
        List<Contact> contactList = databaseAccess.userContactList(principal.getName());
        int indexLast = contactList.size() - 1;
        Contact user = contactList.get(indexLast);
        contactList.remove(indexLast);
        
		model.addAttribute("contactList", contactList);
		model.addAttribute("user", user);

		return "chat";
	}
	
	
	@RequestMapping(value = { "/contact-clicked" }, method = RequestMethod.POST)
	public void contactClick(Model model, HttpServletRequest request, HttpServletResponse response, Principal principal) throws SQLException, IOException {
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		String contact = (String) requestParameters.get("contact");
		String userName = principal.getName();
		
		List<Message> history = databaseAccess.getHistory(userName, contact);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("contactHistory", history);
		httpParamProcessor.translateResponseParameters(response, map);
		
		// Reset message counter with minor thread priority
        if ((boolean) requestParameters.get("needToResetCounter")) {
        	CounterResetThread counterResetThread = new CounterResetThread(userName, contact, this.databaseAccess);
        	counterResetThread.setPriority(3);
        	counterResetThread.start();
        }
	}
	
	
	@RequestMapping(value = { "/auth" }, method = RequestMethod.GET)
	public String authGet(Model model, Principal principal) {
		
		String userName = "no user";
		if (principal != null) {
			userName = principal.getName();
		}
 
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
        String encryptedPassword = PasswordEncoder.encryptPassword(password);
        UserAccount user = new UserAccount(username, encryptedPassword);
        
        String errorString = databaseAccess.addUserAccount(user);
        if (errorString == "") {
        	
        	Authentication authentication = new UsernamePasswordAuthenticationToken(userService.loadUserByUsername(username), null, userService.createAuthorityList("ROLE_USER"));
        	SecurityContextHolder.getContext().setAuthentication(authentication);
        	return "redirect:/";
        	
        } else {
        	return "redirect:/auth?regerror";
        }
    }
	
	
	@RequestMapping(value = { "/username-check" }, method = RequestMethod.POST)
	public void usernameInput(Model model, HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		String username = (String) requestParameters.get("username");
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("free", databaseAccess.findUserAccount(username) == null);
		httpParamProcessor.translateResponseParameters(response, map);
	}
	
	
	@RequestMapping(value = { "/contact-search" }, method = RequestMethod.POST)
	public void contactSearch(Model model, HttpServletRequest request, HttpServletResponse response, Principal principal) throws SQLException, IOException {
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		String input = (String) requestParameters.get("input");
		String userName = principal.getName();
		List<String> users = databaseAccess.searchForUsers(input, userName);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("users", users);
		httpParamProcessor.translateResponseParameters(response, map);
	}
	
	
	@RequestMapping(value = { "/contact-add" }, method = RequestMethod.POST)
	public void contactAdd(Model model, HttpServletRequest request, HttpServletResponse response, Principal principal) throws SQLException, IOException {
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		String input = (String) requestParameters.get("input");
        String userName = principal.getName();
        	
        databaseAccess.addContact(userName, input);
        databaseAccess.addContact(input, userName);    
	}
	
	
	@RequestMapping(value = { "/personal" }, method = RequestMethod.GET)
	public String personalGet(Model model, Principal principal) {
        
		
		
		model.addAttribute("imageSource", "https://aconsultant.ru/wp-content/uploads/2017/10/development.desktop-512.png");
		
		
		model.addAttribute("username", principal.getName());
		return "personal";
	}
	
	
	// Unused yet, left as an example
	@GetMapping("/picture/{path}")
	public @ResponseBody byte[] getPicture(@PathVariable String path) throws IOException, SQLException {
		
		return fileProcessor.getBytesFromFTP(path);
	}
	
	
	// Unused in product, used as testing method
	@GetMapping("/user-avatar")
	public @ResponseBody byte[] getUserAvatar() throws IOException, SQLException {
		
		return fileProcessor.getMultipleFilesFromFTP();
	}
	
	
	// Unused in product, used as testing method
	@PostMapping("/testload")
	public void testLoad(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		
		byte[] bytes = fileProcessor.getMultipleFilesFromFTP();
		String base64String = fileProcessor.getBase64String(bytes);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("base64String", base64String);
		httpParamProcessor.translateResponseParameters(response, map);
		
		
		//return fileProcessor.getMultipleFilesFromFTP();
	}
	
	
	@PostMapping("/get-images")
	public void getImages(HttpServletResponse response, HttpServletRequest request) throws IOException {
		
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		HashMap<String, Object> responseParameters = new HashMap<String, Object>();
		
		for (Map.Entry<String, Object> entry: requestParameters.entrySet()) {
			responseParameters.put(entry.getKey(), fileProcessor.getFileAsBase64FromFTP(entry.getKey()));
		}
		
		httpParamProcessor.translateResponseParameters(response, responseParameters);
	}
	
	
	@PostMapping("/profile-picture")
	public void profilePictureUpload(HttpServletResponse response, Principal principal, MultipartHttpServletRequest request) throws SQLException, IOException {
		
		// Saving uploaded picture
		MultipartFile file = request.getFile("file");
		Contact bufferContact = new Contact(principal.getName());
		fileProcessor.saveUserBase64Image(bufferContact, file);
		
		// Sending it back
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("base64String", bufferContact.getBase64Image());
		httpParamProcessor.translateResponseParameters(response, map);
	}
	
	
	// Unused now, example from personal.html
	@PostMapping("/avatar-upload")
	public void avatarUpload(@ModelAttribute UploadForm form, HttpServletResponse response, Principal principal) throws SQLException, IOException {
		
		Contact bufferContact = new Contact(principal.getName());
		fileProcessor.saveUserBase64Image(bufferContact, form.getFiles());
	}
	
	
	// Service method for debugging
	@RequestMapping(value = { "/test" }, method = RequestMethod.POST)
	public void securityCheck(Model model, HttpServletResponse response, Principal principal) throws SQLException, IOException {
        
		System.out.println("testing");
	}
	
	
}