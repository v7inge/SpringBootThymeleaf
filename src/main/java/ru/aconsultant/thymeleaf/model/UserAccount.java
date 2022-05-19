package ru.aconsultant.thymeleaf.model;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
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

	@ElementCollection
	@Column(name = "Contact")
	@CollectionTable(name = "PERSONAL_CHATS", joinColumns = @JoinColumn(name = "User"))
	private List<String> contactNames;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "PERSONAL_CHATS", joinColumns = @JoinColumn(name = "User"), inverseJoinColumns = @JoinColumn(name = "Contact"))
	private List<UserAccount> companions;

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

	public List<String> getContactNames() {
		return contactNames;
	}

	public void setContactNames(List<String> contactNames) {
		this.contactNames = contactNames;
	}

	public List<UserAccount> getCompanions() {
		return companions;
	}

	public void setCompanions(List<UserAccount> companions) {
		this.companions = companions;
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
