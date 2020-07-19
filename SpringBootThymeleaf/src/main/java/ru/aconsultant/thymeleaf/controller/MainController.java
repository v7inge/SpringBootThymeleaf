package ru.aconsultant.thymeleaf.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ru.aconsultant.thymeleaf.form.AuthForm;
import ru.aconsultant.thymeleaf.conn.DatabaseAccess;
import ru.aconsultant.thymeleaf.service.HttpParamProcessor;
import ru.aconsultant.thymeleaf.security.PasswordEncoder;
import ru.aconsultant.thymeleaf.security.UserDetailsServiceImpl;
import ru.aconsultant.thymeleaf.service.FileProcessor;
import ru.aconsultant.thymeleaf.form.UploadForm;
import ru.aconsultant.thymeleaf.model.Contact;
import ru.aconsultant.thymeleaf.model.Message;
import ru.aconsultant.thymeleaf.model.UserAccount;

import org.apache.commons.net.util.Base64;

@Controller
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
	
	private HashMap<String, String> fileCache = new HashMap<String, String>();
	private static int fileCacheSize = 10;
	
	@Autowired
	FindByIndexNameSessionRepository<? extends Session> sessions;
	
	// How to get principal as a user:
	//User loginedUser = (User) ((Authentication) principal).getPrincipal();
	
	
	@MessageMapping("/message-flow")
    public void broadcast(@Payload Message message, Principal principal) throws SQLException {
		
		String sender = principal.getName();
		String receiver = message.getReceiver();
		
		// Send message to the receiver
        messagingTemplate.convertAndSendToUser(receiver, "/queue/reply", message);
		
		// Notify user himself
        message.setCode(3);
		messagingTemplate.convertAndSendToUser(sender, "/queue/reply", message);
		
        // Save message to database
		message.setCode(0);
		message.setNewOne( !receiverIsFocusedOnTheSender(sender, receiver) );
		databaseAccess.saveMessage(message);
    }
	
	
	// For android developing
	@MessageMapping("/echo")
    public void echo(@Payload Message message) {
		
		message.setText("Server returns: " + message.getText());
		messagingTemplate.convertAndSend("/queue/echo-reply", message);
    }
	
	
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public String chatGet(Model model, Principal principal, HttpServletRequest request) throws SQLException, InvalidResultSetAccessException, IOException {
 		
		setSessionAttribute(request, "currentContact", null);
		
		// Fill contact list
        List<Contact> contactList = databaseAccess.userContactList(principal.getName());
        int indexLast = contactList.size() - 1;
        Contact user = contactList.get(indexLast);
        contactList.remove(indexLast);
        
		model.addAttribute("contactList", contactList);
		model.addAttribute("user", user);

		return "chat";
	}
	
	
	/*@RequestMapping(value = { "/contact-clicked" }, method = RequestMethod.POST)
	public void contactClick(Model model, HttpServletRequest request, HttpServletResponse response, Principal principal) throws SQLException, IOException {
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		
		String currentContact = (String) requestParameters.get("contact");
		setSessionAttribute(request, "currentContact", currentContact);
		
		String userName = principal.getName();
		
		List<Message> history = new ArrayList<Message>();
		if ((boolean) requestParameters.get("needToLoadMessages")) {
			history = databaseAccess.getHistory(userName, currentContact);
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("contactHistory", history);
		httpParamProcessor.translateResponseParameters(response, map);
		
		// Reset message counter
        if ((boolean) requestParameters.get("needToResetCounter")) {	
        	databaseAccess.resetCounter(userName, currentContact);
        }
	}*/
	
	
	@PostMapping("/get-history")
	public void getHistory(Model model, HttpServletRequest request, HttpServletResponse response, Principal principal) throws SQLException, IOException {
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		
		String currentContact = (String) requestParameters.get("contact");
		setSessionAttribute(request, "currentContact", currentContact);
		
		String userName = principal.getName();
		
		List<Message> history = databaseAccess.getHistory(userName, currentContact);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("contactHistory", history);
		httpParamProcessor.translateResponseParameters(response, map);
	}
	
	
	@PostMapping("/reset-counter")
	public void resetCounter(Model model, HttpServletRequest request, HttpServletResponse response, Principal principal) throws SQLException, IOException {
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		String currentContact = (String) requestParameters.get("contact");
		String userName = principal.getName();
		databaseAccess.resetCounter(userName, currentContact);
	}
	
	
	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String authGet(Model model, Principal principal) {
		
		if (principal != null) {
			return "redirect:/";
		}
		
		model.addAttribute("authForm", new AuthForm());

		return "login";
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
		List<Contact> users = databaseAccess.searchForUsers(input, userName);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("users", users);
		httpParamProcessor.translateResponseParameters(response, map);
	}
	
	
	@RequestMapping(value = { "/contact-add" }, method = RequestMethod.POST)
	public void contactAdd(Model model, HttpServletRequest request, HttpServletResponse response, Principal principal) throws SQLException, IOException {
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		String addedContact = (String) requestParameters.get("input");
        String userName = principal.getName();
        
        // Add dialogues to the database
        databaseAccess.addContact(userName, addedContact);
        databaseAccess.addContact(addedContact, userName);
        
        // Notify the user he was added. We use "text" and "filePath" fields to store data of the user who added a contact.
        Message message = new Message();
        Contact user = databaseAccess.getContact(userName);
        message.setSender(userName);
        message.setText(user.getBase64Image());
        message.setFilePath(user.getLetter());
        message.setReceiver(addedContact);
        message.setCode(6);
        messagingTemplate.convertAndSendToUser(addedContact, "/queue/reply", message);
	}
	
	
	// Unused yet, left as an example
	@GetMapping("/picture/{path}")
	public @ResponseBody byte[] getPicture(@PathVariable String path) throws IOException, SQLException, InterruptedException {
		
		return null;
		//return fileProcessor.getBytesFromFTP(path);
	}
	
	
	@PostMapping("/get-files")
	public void getImages(HttpServletResponse response, HttpServletRequest request, Principal principal) throws IOException, InterruptedException {
		
		HashMap<String, Object> requestParameters = httpParamProcessor.getRequestParameters(request);
		
		// Prepare variables
		HashMap<String, Object> responseParameters = new HashMap<String, Object>();
		String base64 = "";
		Set<String> filenamesToDownload = new HashSet<String>();
		
		// Try to get files from cache
		for (String key: requestParameters.keySet()) {
			
			base64 = fileCache.get(key);
			if (base64 == null) {
				filenamesToDownload.add(key);
			} else {
				responseParameters.put(key, base64);
			}
		}
		
		// Get other files from FTP
		HashMap<String, Object> filesFromFTP = fileProcessor.getMultipleFilesBase64(filenamesToDownload);
		for (Map.Entry<String, Object> entry : filesFromFTP.entrySet()) {
			
			responseParameters.put(entry.getKey(), entry.getValue());
		}
		
		// Response
		httpParamProcessor.translateResponseParameters(response, responseParameters);
		
		// Save to the cache downloaded files
		for (Map.Entry<String, Object> entry : filesFromFTP.entrySet()) {
			
			storeFileInCache(entry.getKey(), entry.getValue().toString());
		}	
	}
	
	
	@PostMapping("/set-profile-picture")
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
	
	
	@PostMapping("/send-file")
	public void sendImage(HttpServletResponse response, Principal principal, MultipartHttpServletRequest request) throws SQLException, IOException, InterruptedException {
		
		long milliseconds = Long.parseLong(request.getParameter("milliseconds"));
		String id = request.getParameter("id");
		String username = principal.getName();
		String contact = request.getParameter("contact");
		MultipartFile file = request.getFile("file");
		String fileName = file.getOriginalFilename();
		String filePath = id + " " + fileName;
		int code = Integer.parseInt(request.getParameter("code"));
		int readyCode = (code == 1) ? 2 : 5;
		
		// Build message
		Message message = new Message(username, contact, milliseconds, "", filePath, fileName, code, id);
		
		// Firstly notify that there's a file uploading
		messagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
		messagingTemplate.convertAndSendToUser(contact, "/queue/reply", message);
		
		// Store file in cache
		storeFileInCache(filePath, file);
		
		// Notify that the file is ready
		message.setCode(readyCode);
		messagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
		messagingTemplate.convertAndSendToUser(contact, "/queue/reply", message);
		
		// Save uploaded picture
		fileProcessor.saveFile(file, filePath);
		
		// Save massage to database
		message.setCode(code);
		message.setNewOne( !receiverIsFocusedOnTheSender(username, contact) );
		databaseAccess.saveMessage(message);
	}
	
	
	// Unused now, example from personal.html
	@PostMapping("/avatar-upload")
	public void avatarUpload(@ModelAttribute UploadForm form, HttpServletResponse response, Principal principal) throws SQLException, IOException {
		
		Contact bufferContact = new Contact(principal.getName());
		fileProcessor.saveUserBase64Image(bufferContact, form.getFiles());
	}
	
	
	// Service method for debugging
	@RequestMapping(value = { "/test" }, method = RequestMethod.POST)
	public void test(HttpServletResponse response, Principal principal, HttpServletRequest request) throws SQLException, IOException, InterruptedException {
        
		
		fileProcessor.clearAllConversationContents("victor", "jerry");
		System.out.println("Content cleared");
		
		/*long currentMillis = System.currentTimeMillis();
		String filename = "Test file created at" + currentMillis + ".txt";
		
		byte[] bytes = FileTest.createNewFile(filename);
		fileProcessor.saveBytes(bytes, filename, false);
		fileProcessor.deleteFile(filename);*/
	}
	
	
	private void checkCacheSize() {
		
		if (fileCache.size() >= fileCacheSize) {
			
			String firstKey = fileCache.keySet().stream().findFirst().get();
			fileCache.remove(firstKey);
		}
	}
	
	
	private void storeFileInCache(String filename, MultipartFile file) throws IOException {
		
		checkCacheSize();
		byte[] bytes = file.getBytes();
		String base64 = Base64.encodeBase64String(bytes);
		fileCache.put(filename, base64);
	}
	
	
	private void storeFileInCache(String filename, String base64) throws IOException {
		
		checkCacheSize();
		fileCache.put(filename, base64);
	}
	
	
	private void setSessionAttribute(HttpServletRequest request, String name, String value) {
		
		HttpSession session = request.getSession(true);
	    session.setAttribute(name, value);
	}
	
	
	private boolean receiverIsFocusedOnTheSender(String sender, String receiver) {
		
		boolean focused = false;
		
		Collection<? extends Session> usersSessions = this.sessions.findByPrincipalName(receiver).values();
		for (Session s : usersSessions) {
			
			if (s.getAttribute("currentContact") != null && s.getAttribute("currentContact").equals(sender)) {
				focused = true;
				break;
			}
		}
		
		return focused;
	}
	
}