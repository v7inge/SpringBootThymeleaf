package ru.aconsultant.thymeleaf.beans;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ru.aconsultant.thymeleaf.model.Message;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.GregorianCalendar;

public class MessageTest {
	
	@BeforeAll
    public static void beforeAll() {
        System.out.println("Message testing started...");
    }
	
	@Test
	public void creation() {
		
		System.out.println("Testing message millis...");
		
		// Test message dates
		long currentMillis = System.currentTimeMillis();
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(currentMillis);
		
		Message m = new Message("", "", date, "");
		long messageMillis = m.getMilliseconds();
		assertThat(messageMillis, is(currentMillis));
	}
	
	@AfterAll
    public static void afterAll() {
        System.out.println("Message testing finished.");
    }
	
}
