package ru.aconsultant.thymeleaf.form;

public class AuthForm {
	
	private String login;
	private String password;
	private boolean remember;
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean getRemember() {
		return remember;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setRemember(Boolean remember) {
		this.remember = remember;
	}

}
