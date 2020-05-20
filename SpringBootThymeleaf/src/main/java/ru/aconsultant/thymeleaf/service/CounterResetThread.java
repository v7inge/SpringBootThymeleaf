package ru.aconsultant.thymeleaf.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import ru.aconsultant.thymeleaf.conn.DatabaseAccess;

public class CounterResetThread extends Thread {
	
	private String userName;
	private String contactName;
	private DatabaseAccess databaseAccess;
	
	public CounterResetThread(String userName, String contactName, DatabaseAccess databaseAccess) {
		this.userName = userName;
		this.contactName = contactName;
		this.databaseAccess = databaseAccess;
	}
	
	@Override
    public void run() {
		
		System.out.println("Start time: " + System.currentTimeMillis());
		try {
			this.databaseAccess.resetCounter(this.userName, this.contactName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("End time: " + System.currentTimeMillis());
    }

}
