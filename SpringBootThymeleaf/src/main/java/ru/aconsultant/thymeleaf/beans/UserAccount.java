package ru.aconsultant.thymeleaf.beans;
import java.io.Serializable;

public class UserAccount implements Serializable {

	private String userName;
	//private String password; // #refactor
	private String encryptedPassword;
	
	
	public UserAccount() {
	
	}
	
	public UserAccount(String userName, String encryptedPassword) {
		this.userName = userName;
		this.encryptedPassword = encryptedPassword;
	}
	
	public String getUserName() {
	return userName;
	}
	
	public void setUserName(String userName) {
	this.userName = userName;
	}
	
	/*public String getPassword() {
	return password;
	}
	
	public void setPassword(String password) {
	this.password = password;
	}*/
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
		
	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
	
	@Override
    public String toString() {
        return this.userName + "/" + this.encryptedPassword;
    }
	
}
