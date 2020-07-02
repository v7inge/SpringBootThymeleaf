package ru.aconsultant.thymeleaf.service;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses( { FileProcessorConsequentTest.class, FileUploadingMultithreadTest.class, 
	FileDownloadingMultithreadTest.class, FileDeletingMultithreadTest.class } )
public class FileProcessorRunTests {

}
