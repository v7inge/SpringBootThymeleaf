package ru.aconsultant.thymeleaf.beans;
import java.io.Serializable;
import java.util.Random;

public class UserAccount implements Serializable {

	private static final long serialVersionUID = 1L;
	private String userName;
	private String encryptedPassword;
	private String letter;
	
	public UserAccount() {
	
	}
	
	public UserAccount(String userName, String encryptedPassword) {
		this.userName = userName;
		this.encryptedPassword = encryptedPassword;
		setRandomLetter();
	}
	
	
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
	
	public void setLetter(String letter) {
		this.letter = letter;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
	
	public String getLetter() {
		return letter;
	}
	
	@Override
    public String toString() {
        return this.userName + "/" + this.encryptedPassword;
    }
	
	private void setRandomLetter() {
		Random random = new Random();
		this.letter = "" + (char) (random.nextInt(26) + 'a'); 
	}
	
}
