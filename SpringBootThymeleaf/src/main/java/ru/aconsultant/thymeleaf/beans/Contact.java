package ru.aconsultant.thymeleaf.beans;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import ru.aconsultant.thymeleaf.conn.DatabaseAccess;
import ru.aconsultant.thymeleaf.service.FileProcessor;

public class Contact {

	/*@Autowired
	private FileProcessor fileProcessor;*/
	
	public String username;
	public Integer unreadCount;
	public byte[] avatar;
	public String avatarPath;
	public boolean current;
	public String base64Image;
	
	public Contact() {
	
	}
	
	
	public Contact(String username) {
		this.username = username;
	}
	
	
	public Contact(String username, Integer unreadCount, byte[] avatar) {
		this.username = username;
		this.unreadCount = unreadCount;
		this.avatar = avatar;
	}
	
	
	public Contact(String username, Integer unreadCount, String avatarPath, boolean current) throws SQLException, IOException {
		this.username = username;
		this.unreadCount = unreadCount;
		this.avatarPath = avatarPath;
		this.current = current;
		//this.avatar = fileProcessor.getBytesFromFTP(avatarPath);
	}
	

	public void setUsername(String username) {
		this.username = username;
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
	
	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}
	
	public void setCurrent(boolean current) {
		this.current = current;
	}
	
	public String getUsername() {
		return username;
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
	
	public boolean getCurrent() {
		return current;
	}
	
	public String getBase64Image() {
		return base64Image;
	}
	
}
