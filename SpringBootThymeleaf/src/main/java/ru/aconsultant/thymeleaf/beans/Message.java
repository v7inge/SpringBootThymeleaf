package ru.aconsultant.thymeleaf.beans;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class Message {

	private String sender;
	private String reciever;
	private String text;
	private String filePath;
	private boolean image;
	private long milliseconds;
	
	private int code;
	// null or 0: Plain text message
	// 1: Notify the client that there's an image uploading for him
	// 2: Notify the client that image is uploaded and should be downloaded
	// 3: Notify the client that his plain text is successfully sent
	
	private String id;
	
	// --- CONSTRUCTORS --- //
	
	public Message() {}
	
	public Message(String sender, String reciever, Calendar date, String text) {
		this.sender = sender;
		this.reciever = reciever;
		this.text = text;
		this.milliseconds = date.getTimeInMillis();
	}
	
	public Message(String sender, String reciever, long milliseconds, String text) {
		this.sender = sender;
		this.reciever = reciever;
		this.text = text;
		this.milliseconds = milliseconds;
	}
	
	public Message(String sender, String reciever, Calendar date, String text, String filePath, boolean image) {
		this.sender = sender;
		this.reciever = reciever;
		this.text = text;
		this.filePath = filePath;
		this.image = image;
		this.milliseconds = date.getTimeInMillis();
	}
	
	public Message(String sender, String reciever, long milliseconds, String text, String filePath, boolean image) {
		this.sender = sender;
		this.reciever = reciever;
		this.text = text;
		this.filePath = filePath;
		this.image = image;
		this.milliseconds = milliseconds;
	}

	// --- GET --- //
	
	public String getText() {
		return text;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getReciever() {
		return reciever;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public boolean getImage() {
		return image;
	}
	
	public long getMilliseconds() {
		
		return milliseconds;	
	}
	
	public Date getDateTime() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.getMilliseconds());
		return calendar.getTime();
	}
	
	public int getCode() {
		return code;
	}
	
	public String getId() {
		return id;
	}
	
	// --- SET --- //
	
	public void setSender(String st) {
		this.sender = st;
	}
	
	public void setReciever(String st) {
		this.reciever = st;
	}
	
	public void setText(String st) {
		this.text = st;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public void setImage(boolean image) {
		this.image = image;
	}
	
	public void setMilliseconds(long milliseconds) {
		this.milliseconds = milliseconds;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	// --- OTHER --- //
	
	@Override
	public String toString() {
		return text;
	}
	
}