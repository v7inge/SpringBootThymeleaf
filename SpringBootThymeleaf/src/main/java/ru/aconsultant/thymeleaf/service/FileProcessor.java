package ru.aconsultant.thymeleaf.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ru.aconsultant.thymeleaf.beans.Contact;
import ru.aconsultant.thymeleaf.conn.DatabaseAccess;

@Component
public class FileProcessor {

	@Autowired
    private DatabaseAccess databaseAccess;
	
	private static final String server = "freshnoon.beget.tech";
	private static final int port = 21;
	private static final String user = "freshnoon_chatbot";
	private static final String pass = "hgy7fD531cvZ";
	private static int avaWidth = 150;
	private static int avaHeight = 150;
	
	private FTPClient ftpClient;
	
	
	private void connectToFTP() throws SocketException, IOException {
		
		try {
			ftpClient = new FTPClient();
	        ftpClient.setControlEncoding("UTF-8");
	        ftpClient.connect(server, port);
	        ftpClient.login(user, pass);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } 
	}
	
	
	private void disconnectFromFTP() {

		try {
	        if (ftpClient.isConnected()) {
	            ftpClient.logout();
	            ftpClient.disconnect();
	        }
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
	
	
	public void saveUserBase64Image(Contact user, MultipartFile file) throws IOException, SQLException {
		
		// Check if file is empty
		if (file.isEmpty()) {
	    	user.setBase64Image(null);
	    	return;
	    }
		
		// Check if it's an image
		List<String> extensions = Arrays.asList("jpg", "jpeg", "png");
		if (!filePassesFilter(file, extensions)) {
        	System.out.println("File did not pass the filter");
        	return;
        }
		
		// Crop it
		byte[] bytes = file.getBytes();        
		bytes = cropImageSquare(bytes);
		
		String base63image = Base64.encodeBase64String(bytes);
		user.setBase64Image(base63image);
		databaseAccess.saveUserBase64Image(user.getUsername(), base63image);	
	}
	
	
	public String getBase64String(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}
	
	
	public void saveUserBase64Image(Contact user, MultipartFile[] files) throws IOException, SQLException {
		
		MultipartFile file = files[0];
		saveUserBase64Image(user, file);
	}
	
	
	public void saveUserAvatar(String username, MultipartFile[] files) throws IOException, SQLException {
		
		List<String> extensions = Arrays.asList("jpg", "jpeg", "png");
		
		String fileName = saveUploadedFile(files[0], extensions, true);
		if (fileName == "") {
			System.out.println("Error occured during file uploading to FTP");
		} else {
			databaseAccess.setUserAvatar(username, fileName);
		}
	}
	
	
	public byte[] getUserAvatar(String username) throws SQLException, IOException {
		
		String filename = databaseAccess.getUserAvatarPath(username);
		if (filename == "" || filename == null) {
			return null;
		} else {
			return getBytesFromFTP(filename);
		}
	}
	
	
	public void fillContactsBase64Images(List<Contact> contacts) throws SocketException, IOException {
		
		if(ftpClient==null || !ftpClient.isConnected()) {
        	connectToFTP();
        }
		
		for (Contact contact : contacts) {
			byte[] img = getBytesWhenConnected(contact.getAvatarPath());
			if (img == null) {
				contact.setBase64Image(null);
			} else {
				contact.setBase64Image(Base64.encodeBase64String(img));
			}
		}
		disconnectFromFTP();
		
	}
	
	
	public static boolean filePassesFilter(MultipartFile file, List<String> extensions) {
		
		if (extensions.size() == 0) {
			return true;
		}
		
		String fileEx = FilenameUtils.getExtension(file.getOriginalFilename());
		
		return extensions.contains(fileEx);
	}
	
	
	private byte[] getBytesWhenConnected(String filename) throws IOException {
		
		if (filename == null || filename == "") { return null; }
		
		InputStream inputStream = ftpClient.retrieveFileStream(filename);
		byte[] bytes = inputStream.readAllBytes();
		
		System.out.println("Complete: " + ftpClient.completePendingCommand());
		
		return bytes;
	}
	
	
	// Testing method
	public byte[] getMultipleFilesFromFTP() throws IOException {
		
		if(ftpClient == null || !ftpClient.isConnected()) {
        	connectToFTP();
        }
		
		InputStream inputStream = ftpClient.retrieveFileStream("1590680116034 Люк.png");
		byte[] bytes1 = inputStream.readAllBytes();
		
		System.out.println("Complete: " + ftpClient.completePendingCommand());
		//inputStream.close();
		//inputStream.
		
		inputStream = ftpClient.retrieveFileStream("1590680245489 Коллекторы Неглини.png");
		byte[] bytes2 = inputStream.readAllBytes();
		System.out.println("Complete: " + ftpClient.completePendingCommand());
		
		inputStream = ftpClient.retrieveFileStream("1590917884069 8_3.jpg");
		byte[] bytes3 = inputStream.readAllBytes();
		System.out.println("Complete: " + ftpClient.completePendingCommand());
		
		disconnectFromFTP();
		return bytes3;
	}
	
	
	public String getFileAsBase64FromFTP(String filename) throws IOException {
		
		byte[] bytes = getBytesFromFTP(filename);
		String base64String = getBase64String(bytes);
		return base64String;
	}
	
	
	public byte[] getBytesFromFTP(String filename) throws IOException {
		
		if (filename == null || filename == "") { return null; }
		
		if(ftpClient == null || !ftpClient.isConnected()) {
        	connectToFTP();
        }
		InputStream inputStream = ftpClient.retrieveFileStream(filename);
		byte[] bytes = inputStream.readAllBytes();
		disconnectFromFTP();
		return bytes;
		
		
		/*if(ftpClient == null || !ftpClient.isConnected()) {
        	connectToFTP();
        }
		
		try {
		
			InputStream inputStream = ftpClient.retrieveFileStream(filename);
			byte[] bytes = inputStream.readAllBytes();
			return bytes;
		
		} catch (FTPConnectionClosedException e) {
			
			connectToFTP();
			InputStream inputStream = ftpClient.retrieveFileStream(filename);
			byte[] bytes = inputStream.readAllBytes();
			return bytes;
			
		}*/
	}
	
	
	public String saveUploadedFile(MultipartFile file, List<String> extensions, boolean cropAvatar) throws IOException {
		
		String fileName = "";
        if (file.isEmpty()) {
	    	return fileName;
	    }
        
        if (!filePassesFilter(file, extensions)) {
        	System.out.println("File did not pass the filter");
        	return fileName;
        }
 
        connectToFTP();
		byte[] bytes = file.getBytes();        
		fileName = System.currentTimeMillis() + " " + file.getOriginalFilename();
		OutputStream outputStream = ftpClient.storeFileStream(fileName);
		
		if (cropAvatar) {
			bytes = cropImageSquare(bytes);
		}
		
		outputStream.write(bytes);
		outputStream.close();
		disconnectFromFTP();
        
        return fileName;
    }
	
	
	private byte[] cropImageSquare(byte[] image) throws IOException {        
        
		// Convert from byte array to buffered image
		InputStream in = new ByteArrayInputStream(image);
        BufferedImage originalImage = ImageIO.read(in);

        // Crop image
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int x, y = 0, width, height;
        if (originalHeight > originalWidth) {
        	x = 0;
        	width = originalWidth;
        	height = originalWidth;
        } else {
        	x = (originalWidth - originalHeight) / 2;
        	width = originalHeight;
        	height = originalHeight;
        }
                  
        BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);
        croppedImage = resizeImage(croppedImage);

        // Convert from buffered image to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(croppedImage, "jpg", baos);
        byte[] bytes = baos.toByteArray();

        return bytes;
    }
	
	
	private static BufferedImage resizeImage(BufferedImage originalImage) {
		
		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		
		BufferedImage resizedImage = new BufferedImage(avaWidth, avaHeight, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, avaWidth, avaHeight, null);
		g.dispose();
		return resizedImage;
	}
	
	
}
