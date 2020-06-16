package ru.aconsultant.thymeleaf.beans;

import java.io.IOException;
import java.sql.SQLException;

public class Contact {
	
	public String username;
	public Integer unreadCount;
	public boolean current;
	public String base64Image;
	public String letter;
	
	public Contact() {
	
	}
	
	
	public Contact(String username) {
		this.username = username;
	}
	
	
	public Contact(String username, Integer unreadCount) {
		this.username = username;
		this.unreadCount = unreadCount;
	}
	
	
	public Contact(String username, Integer unreadCount, boolean current, String base64Image, String letter) throws SQLException, IOException {
		this.username = username;
		this.unreadCount = unreadCount;
		this.current = current;
		this.base64Image = base64Image;
		this.letter = letter;
	}
	

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setUnreadCount(Integer unreadCount) {
		this.unreadCount = unreadCount;
	}
	
	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}
	
	public void setCurrent(boolean current) {
		this.current = current;
	}
	
	public void setLetter(String letter) {
		this.letter = letter;
	}
	
	public String getUsername() {
		return username;
	}
	
	public Integer getUnreadCount() {
		return unreadCount;
	}
	
	public boolean getCurrent() {
		return current;
	}
	
	public String getBase64Image() {
		return base64Image;
	}
	
	public String getLetter() {
		return letter;
	}
	
}
