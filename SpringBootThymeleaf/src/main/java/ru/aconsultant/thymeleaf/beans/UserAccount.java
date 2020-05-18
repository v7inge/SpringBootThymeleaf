package ru.aconsultant.thymeleaf.beans;

public class UserAccount {

	private String login;
	private String password;
	
	
	public UserAccount() {
	
	}
	
	public UserAccount(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	public String getUserName() {
	return login;
	}
	
	public void setlogin(String login) {
	this.login = login;
	}
	
	public String getPassword() {
	return password;
	}
	
	public void setPassword(String password) {
	this.password = password;
	}
	
	@Override
    public String toString() {
        return this.login + "/" + this.password;
    }
	
}
