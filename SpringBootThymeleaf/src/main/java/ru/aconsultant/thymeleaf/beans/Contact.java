package ru.aconsultant.thymeleaf.beans;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import ru.aconsultant.thymeleaf.conn.DatabaseAccess;
import ru.aconsultant.thymeleaf.service.FileProcessor;

public class Contact {

	/*@Autowired
	private FileProcessor fileProcessor;*/
	
	public String login;
	public Integer unreadCount;
	public byte[] avatar;
	public String avatarPath;
	
	public Contact() {
	
	}
	
	
	public Contact(String login) {
		this.login = login;
	}
	
	
	public Contact(String login, Integer unreadCount, byte[] avatar) {
		this.login = login;
		this.unreadCount = unreadCount;
		this.avatar = avatar;
	}
	
	
	public Contact(String login, Integer unreadCount, String avatarPath) throws SQLException, IOException {
		this.login = login;
		this.unreadCount = unreadCount;
		this.avatarPath = avatarPath; 
		//this.avatar = fileProcessor.getBytesFromFTP(avatarPath);
	}
	

	public void setlogin(String login) {
		this.login = login;
	}
	
	public void setUnreadCount(Integer unreadCount) {
		this.unreadCount = unreadCount;
	}
	
	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}
	
	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}
	
	public String getlogin() {
		return login;
	}
	
	public Integer getUnreadCount() {
		return unreadCount;
	}
	
	public byte[] getAvatar() {
		return avatar;
	}
	
	public String getAvatarPath() {
		return avatarPath;
	}
	
}
