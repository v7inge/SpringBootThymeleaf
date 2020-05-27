package ru.aconsultant.thymeleaf.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import ru.aconsultant.thymeleaf.beans.Message;
import ru.aconsultant.thymeleaf.conn.DatabaseAccess;

public class MessageSaveThread extends Thread {

	@Autowired
	private Message message;
	
	@Autowired
    private DatabaseAccess databaseAccess;
	
	public MessageSaveThread(Message message, DatabaseAccess databaseAccess) {
		this.message = message;
		this.databaseAccess = databaseAccess;
	}
	
	@Override
    public void run() {
		
		try {
			this.databaseAccess.saveMessage(this.message);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
}
