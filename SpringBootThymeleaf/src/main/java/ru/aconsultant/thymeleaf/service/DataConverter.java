package ru.aconsultant.thymeleaf.service;

import java.util.Date;

public abstract class DataConverter {

	
	public static Date stringToDate(String string) {
		
		String[] parts = string.split("_");
		
		Date date = new Date();
		//date.setYear(2020);
		return date;
	}
}
