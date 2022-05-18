package ru.aconsultant.thymeleaf.service;

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
public class FileDeletingMultithreadTest {

	
	private static FileProcessor fileProcessor;
	
	
	@BeforeAll
    public static void beforeAll() {
        System.out.println("Concurrent file deleting test started...");
        fileProcessor = new FileProcessor();
    }
	
	
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void thread1() throws InterruptedException, SocketException, IOException {
		
		fileProcessor.deleteFile("Junit file 1.txt");
	}
	
	
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void thread2() throws InterruptedException, SocketException, IOException {
		
		fileProcessor.deleteFile("Junit file 2.txt");
	}
	
	
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void thread3() throws InterruptedException, SocketException, IOException {
		
		fileProcessor.deleteFile("Junit file 3.txt");
	}
	
	
	@AfterAll
    public static void afterAll() {
        System.out.println("Concurrent file deleting test finished.");
    }
}
