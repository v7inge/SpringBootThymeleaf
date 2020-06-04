package ru.aconsultant.thymeleaf.beans;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class Message {

	private String sender;
	private String reciever;
	private Date date;
	private String text;
	private String filePath;
	private boolean image;

	public Message() {}
	
	public Message(String sender, String reciever, Date date, String text) {
		this.sender = sender;
		this.reciever = reciever;
		this.date = date;
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getReciever() {
		return reciever;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public boolean getImage() {
		return image;
	}
	
	public void setSender(String st) {
		this.sender = st;
	}
	
	public void setReciever(String st) {
		this.reciever = st;
	}
	
	public void setDate(Date date) {
		this.date = date;
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
	
	@Override
	public String toString() {
		return text;
	}
	
}