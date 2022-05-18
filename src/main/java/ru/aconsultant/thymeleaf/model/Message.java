package ru.aconsultant.thymeleaf.model;

import ru.aconsultant.thymeleaf.service.DateConverter;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "message")
public class Message {

	@Column(name = "Sender")
	private String sender;

	@Column(name = "Receiver")
	private String receiver;

	@Column(name = "Text")
	private String text;

	@Column(name = "FilePath")
	private String filePath;

	@Column(name = "DateTime")
	@Convert(converter = DateConverter.class)
	private long milliseconds;

	@Column(name = "FileName")
	private String fileName;

	@Column(name = "Code")
	private int code;
	// null or 0: Plain text message
	// 1: Image uploading
	// 2: Image is ready
	// 3: Plain text is successfully sent
	// 4: File uploading
	// 5: File is ready
	// 6: User added as a contact

	@Id
	private String id;

	@Column(name = "New")
	private boolean newOne;
	
	// --- CONSTRUCTORS --- //
	
	public Message() {}
	
	public Message(String sender, String receiver, Calendar date, String text) {
		this.sender = sender;
		this.receiver = receiver;
		this.text = text;
		this.milliseconds = date.getTimeInMillis();
	}
	
	public Message(String sender, String receiver, long milliseconds, String text) {
		this.sender = sender;
		this.receiver = receiver;
		this.text = text;
		this.milliseconds = milliseconds;
	}
	
	public Message(String sender, String receiver, Calendar date, String text, String filePath, String fileName, int code, String id) {
		this.sender = sender;
		this.receiver = receiver;
		this.text = text;
		this.filePath = filePath;
		this.milliseconds = date.getTimeInMillis();
		this.fileName = fileName;
		this.code = code;
		this.id = id;
	}
	
	public Message(String sender, String receiver, long milliseconds, String text, String filePath, String fileName, int code, String id) {
		this.sender = sender;
		this.receiver = receiver;
		this.text = text;
		this.filePath = filePath;
		this.milliseconds = milliseconds;
		this.fileName = fileName;
		this.code = code;
		this.id = id;
	}

	// --- GET --- //
	
	public String getText() {
		return text;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getReceiver() {
		return receiver;
	}
	
	public String getFilePath() {
		return filePath;
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
	
	public String getFileName() {
		return fileName;
	}
	
	public boolean getNewOne() {
		return newOne;
	}
	
	// --- SET --- //
	
	public void setSender(String st) {
		this.sender = st;
	}
	
	public void setReceiver(String st) {
		this.receiver = st;
	}
	
	public void setText(String st) {
		this.text = st;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setNewOne(boolean newOne) {
		this.newOne = newOne;
	}
	
	// --- OTHER --- //
	
	@Override
	public String toString() {
		return text;
	}
	
}