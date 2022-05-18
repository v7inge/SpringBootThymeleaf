package ru.aconsultant.thymeleaf.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FileProcessorConsequentTest {

	private static FileProcessor fileProcessor;
	
	private final static ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final static PrintStream originalErr = System.err;
	
	@BeforeAll
    public static void beforeAll() {
        System.out.println("Consequent test started...");
        fileProcessor = new FileProcessor();
        System.setErr(new PrintStream(errContent));
    }
	
	
	@Test
	public void fileDoesNotExist() throws IOException, InterruptedException {
		
		System.out.println("Testing nonexistent file downloading...");
		
		String filename = "" + System.currentTimeMillis() + " nonexistent file";
		
		fileProcessor.getFileBase64(filename);
		assertThat(errContent.toString(), containsString("No file \"" + filename + "\" found on FTP server"));
	}
	
	
	@AfterAll
    public static void afterAll() {
        System.out.println("Consequent test finished.");
        System.setErr(originalErr);
    }
	
}