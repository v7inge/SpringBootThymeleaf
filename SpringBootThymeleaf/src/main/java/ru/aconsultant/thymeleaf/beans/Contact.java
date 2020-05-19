package ru.aconsultant.thymeleaf.beans;

public class Contact {

	public String login;
	public Integer unreadCount;
	
	
	public Contact() {
	
	}
	
	public Contact(String login) {
		this.login = login;
	}
	
	public Contact(String login, Integer unreadCount) {
		this.login = login;
		this.unreadCount = unreadCount;
	}
	

	public void setlogin(String login) {
		this.login = login;
	}
	
	public void setUnreadCount(Integer unreadCount) {
		this.unreadCount = unreadCount;
	}
	
	public String getlogin() {
		return login;
	}
	
	public Integer getUnreadCount() {
		return unreadCount;
	}
	
}
