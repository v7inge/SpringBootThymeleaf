package ru.aconsultant.thymeleaf.service;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ru.aconsultant.thymeleaf.conn.DatabaseAccess;

@Component
public class FileProcessor {

	@Autowired
    private DatabaseAccess databaseAccess;
	
	public static final String server = "freshnoon.beget.tech";
	public static final int port = 21;
	public static final String user = "freshnoon_chatbot";
	public static final String pass = "hgy7fD531cvZ";
	
	
	public void saveUserAvatar(String username, MultipartFile[] files) throws IOException, SQLException {
		
		List<String> extensions = Arrays.asList("jpg", "jpeg", "png");
		
		String fileName = saveUploadedFile(files[0], extensions);
		if (fileName == "") {
			System.out.println("Error occured during file uploading to FTP");
		} else {
			databaseAccess.setUserAvatar(username, fileName);
		}
	}
	
	
	public static boolean filePassesFilter(MultipartFile file, List<String> extensions) {
		
		if (extensions.size() == 0) {
			return true;
		}
		
		String fileEx = FilenameUtils.getExtension(file.getOriginalFilename());
		
		return extensions.contains(fileEx);
	}
	
	
	public static String saveUploadedFile(MultipartFile file, List<String> extensions) throws IOException {
		
		String fileName = "";
        if (file.isEmpty()) {
	    	return fileName;
	    }
        
        if (!filePassesFilter(file, extensions)) {
        	System.out.println("File did not pass the filter");
        	return fileName;
        }
        
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
 
        try {
            
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		    
		    byte[] bytes = file.getBytes();
		            
		    fileName = System.currentTimeMillis() + " " + file.getOriginalFilename();
		    OutputStream outputStream = ftpClient.storeFileStream(fileName);
		    outputStream.write(bytes);
		    outputStream.close();
	             
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return fileName;
    }
	
}
