package ru.aconsultant.thymeleaf.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;

import org.junit.Rule;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileProcessorConsequentTest {

	private static FileProcessor fileProcessor;
	
	private final static ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final static PrintStream originalErr = System.err;
	
	@BeforeAll
    public static void beforeAll() {
        System.out.println("FileProcessor testing started...");
        fileProcessor = new FileProcessor();
        System.setErr(new PrintStream(errContent));
    }
	
	
	@Test
	public void createDelete() throws SocketException, IOException, InterruptedException {
		
		String filename = "" + System.currentTimeMillis() + " junit test file.txt";
		boolean result;
		
		System.out.println("Testing file saving...");
		MultipartFile multipartFile = createNewTextFile(filename);
		result = fileProcessor.saveFile(multipartFile, filename);
		
		if (result) {
		
			System.out.println("Testing file deletion...");
			result = fileProcessor.deleteFile(filename);
			if (!result) {
				System.out.println("Error deleting file \""+ filename + "\".");
			}
		
		} else {
			System.out.println("Error uploading file \""+ filename + "\".");
		}
	}
	
	
	@Test
	public void fileDoesNotExist() throws IOException, InterruptedException {
		
		System.out.println("Testing nonexistent file downloading...");
		
		String filename = "" + System.currentTimeMillis() + " nonexistent file";
		
		fileProcessor.getFileBase64(filename);
		
		// Wait until file processor gets an exception
		while (fileProcessor.busy) {
			Thread.sleep(250);
		}
		
		assertThat(errContent.toString(), containsString("No file \"" + filename + "\" found on FTP server"));
	}
	
	
	private MultipartFile createNewTextFile(String filename) {
		
		byte[] bytes = createNewFile(filename);
		MultipartFile multipartFile = new MockMultipartFile(filename, filename, "text/plain", bytes);
		return multipartFile;
	}
	
	
	private byte[] createNewFile(String filename) {
		
		File file = new File(filename);
		
        try {
        	
            file.createNewFile();
            
            // Add some content
            FileWriter writer = new FileWriter(file);
            writer.write(filename + " test data");
            writer.close();
            
            return fileToBytes(file);
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	
	private byte[] fileToBytes(File file) throws IOException {
		
		byte[] bytes = new byte[(int) file.length()];

		FileInputStream fis = new FileInputStream(file);
		fis.read(bytes);
		fis.close();
		return bytes;
	}
	
	
	@AfterAll
    public static void afterAll() {
        System.out.println("FileProcessor testing finished.");
        System.setErr(originalErr);
    }
	
}


class FileSavingThread extends Thread {
	
	private String filename;
	private FileProcessor fileProcessor;
	private MultipartFile multipartFile;
	
	
	public FileSavingThread(String filename, FileProcessor fileProcessor, MultipartFile multipartFile) {
		this.filename = filename;
		this.fileProcessor = fileProcessor;
		this.multipartFile = multipartFile;
	}
	
	
	@Override
	public void run() {
		
		try {
			
			boolean result = fileProcessor.saveFile(multipartFile, filename);
			if (result) {
				System.out.println("File \"" + filename + "\" successfully uploaded.");
			} else {
				System.out.println("Error uploading file \""+ filename + "\".");
			}
		
		} catch (IOException | InterruptedException e) {
			System.out.println("Error uploading file \""+ filename + "\":");
			e.printStackTrace();
		}
	}
}