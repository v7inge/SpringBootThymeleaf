package ru.aconsultant.thymeleaf.beans;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class Message {

	private String sender;
	private String reciever;
	//private Calendar date;
	private String text;
	private String filePath;
	private boolean image;
	private long milliseconds;

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
	
	/*public Calendar getDate() {
		return date;
	}*/
	
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
	
	// --- SET --- //
	
	public void setSender(String st) {
		this.sender = st;
	}
	
	public void setReciever(String st) {
		this.reciever = st;
	}
	
	/*public void setDate(Calendar date) {
		this.date = date;
	}*/
	
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
	
	@Override
	public String toString() {
		return text;
	}
	
}