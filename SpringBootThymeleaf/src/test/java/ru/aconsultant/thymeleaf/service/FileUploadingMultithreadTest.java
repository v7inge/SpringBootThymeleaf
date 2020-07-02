package ru.aconsultant.thymeleaf.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.runner.RunWith;
import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;


@RunWith(ConcurrentTestRunner.class)
public class FileUploadingMultithreadTest {

	private static FileProcessor fileProcessor;

	
	@BeforeAll
    public static void beforeAll() {
        System.out.println("Concurrent file uploading test started...");
        fileProcessor = new FileProcessor();
    }
	
	
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void thread1() throws InterruptedException, SocketException, IOException {
		
		createUpload("Junit file 1.txt");
	}
	
	
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void thread2() throws InterruptedException, SocketException, IOException {
		
		createUpload("Junit file 2.txt");
	}
	
	
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void thread3() throws InterruptedException, SocketException, IOException {
		
		createUpload("Junit file 3.txt");
	}
	
	
	private void createUpload(String filename) throws IOException, InterruptedException {
		
		byte[] bytes = createNewFile(filename);
		fileProcessor.saveBytes(bytes, filename, false);
	}
	
	
	private byte[] createNewFile(String filename) {
		
		File file = new File(filename);
		
        try {
        	
            file.createNewFile();
            
            // Add some content
            FileWriter writer = new FileWriter(file);
            writer.write(filename + " test data");
            writer.close();
            
            byte[] bytes = fileToBytes(file);
            file.delete();
            return bytes;
            
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
        System.out.println("Concurrent file uploading test finished.");
    }
	
}

