package ru.aconsultant.thymeleaf.form;

public class AuthForm {
	
	private String username;
	private String password;
	private boolean remember;
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean getRemember() {
		return remember;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setRemember(Boolean remember) {
		this.remember = remember;
	}

}
