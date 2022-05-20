package ru.aconsultant.thymeleaf.model;

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
	private String username;

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

	@Transient
	private Integer unreadCount;

	@Column(name = "BASE64IMAGE")
	private String base64Image;

	@Transient
	private boolean current;

	public UserAccount() { }
	
	public UserAccount(String username, String encryptedPassword) {
		this.username = username;
		this.encryptedPassword = encryptedPassword;
		setRandomLetter();
	}

	public UserAccount(String username, String letter, Long unreadCount, String base64Image) {
		this.username = username;
		this.letter = letter;
		this.unreadCount = unreadCount == null ? 0 : unreadCount.intValue();
		this.base64Image = base64Image;
	}

	public void setUsername(String userName) {
		this.username = userName;
	}
	
	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
	
	public void setLetter(String letter) {
		this.letter = letter;
	}
	
	public String getUsername() {
		return username;
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

	public Integer getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(Integer unreadCount) {
		this.unreadCount = unreadCount;
	}

	public String getBase64Image() {
		return base64Image;
	}

	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	@Override
    public String toString() {
        return this.username + "/" + this.encryptedPassword;
    }
	
	private void setRandomLetter() {
		Random random = new Random();
		this.letter = "" + (char) (random.nextInt(26) + 'a'); 
	}
	
}
