package ru.aconsultant.thymeleaf.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Random;

@Entity
@Table(name = "APP_USER")
public class UserAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "ENCRYPTED_PASSWORD")
	private String encryptedPassword;

	@Column(name = "LETTER")
	private String letter;
	
	public UserAccount() { }
	
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
