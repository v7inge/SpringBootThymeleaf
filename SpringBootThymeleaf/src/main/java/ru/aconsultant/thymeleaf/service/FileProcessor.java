package ru.aconsultant.thymeleaf.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ru.aconsultant.thymeleaf.conn.DatabaseAccess;
import ru.aconsultant.thymeleaf.model.Contact;

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
	
	// This method is not used as we use FileProcessor as Autowired. But we need it if create it manually (to put into static variable).
	public void setDatabaseAccess(DatabaseAccess databaseAccess) {
		this.databaseAccess = databaseAccess;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// ---------------------------------- CONNECTION ---------------------------------- //
	
	
	private void connectToFTP() throws SocketException, IOException, InterruptedException {
		
		// Connect if necessary
		if(ftpClient == null || !ftpClient.isConnected()) {
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
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// ---------------------------------- DOWNLOADING ---------------------------------- //
	
	
	private synchronized byte[] getBytesWhenConnected(String filename) throws IOException {
		
		if (filename == null || filename == "") { return null; }
		
		InputStream inputStream = ftpClient.retrieveFileStream(filename);
		byte[] bytes = inputStream.readAllBytes();
		
		return bytes;
	}
	
	
	public synchronized HashMap<String, Object> getMultipleFilesBase64(Set<String> filenames) throws IOException, InterruptedException {
		
        connectToFTP();
        HashMap<String, Object> result = new HashMap<String, Object>();
        
        for (String filename : filenames) {
        	try {
		    	byte[] bytes = getBytesWhenConnected(filename);
	        	result.put(filename, getBase64String(bytes));
		       	ftpClient.completePendingCommand();
        	} catch (IOException e) {
        		System.out.println("Could not connect to FTP server");
        	} catch (NullPointerException n) {
        		String exceptionMsg = "No file \"" + filename + "\" found on FTP server";
        		System.err.println(exceptionMsg);
        	}
        }
        
        return result;
	}
	
	
	public Object getFileBase64(String filename) throws IOException, InterruptedException {
		
		Set<String> filenames = new HashSet<String>();
		filenames.add(filename);
		return getMultipleFilesBase64(filenames).get(filename);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// ---------------------------------- UPLOADING ---------------------------------- //
	
	
	public void saveUserBase64Image(Contact user, MultipartFile[] files) throws IOException, SQLException {
		
		MultipartFile file = files[0];
		saveUserBase64Image(user, file);
	}
	
	
	public synchronized void saveUserBase64Image(Contact user, MultipartFile file) throws IOException, SQLException {
		
		// Check if file is empty
		if (file.isEmpty()) {
	    	user.setBase64Image(null);
	    	return;
	    }
		
		// Check if it's an image
		if (!filePassesFilter(file, imageExtensions())) {
        	System.out.println("File did not pass the filter");
        	return;
        }
		
		// Crop it
		byte[] bytes = file.getBytes();
		String ext = FilenameUtils.getExtension(file.getOriginalFilename());
		bytes = cropImageSquare(bytes, ext);
		
		String base63image = Base64.encodeBase64String(bytes);
		user.setBase64Image(base63image);
		databaseAccess.saveUserBase64Image(user.getUsername(), base63image);	
	}
	
	
	public boolean saveFile(MultipartFile file, String filename) throws IOException, InterruptedException {
		
		return saveBytes(fileToBytes(file), filename, false);
	}
	
	
	public boolean saveFile(File file, String filename) throws IOException, InterruptedException {
		
		return saveBytes(fileToBytes(file), filename, false);
	}
	
	
	public boolean saveFile(MultipartFile file, String filename, List<String> extensions) throws IOException, InterruptedException {
		
		if (!filePassesFilter(file, extensions)) {
        	System.out.println("File did not pass the filter");
        	return false;
        }
		
		return saveBytes(fileToBytes(file), filename, false);
	}
	
	
	public synchronized boolean saveBytes(byte[] bytes, String filename, boolean cropAvatar) throws IOException, InterruptedException {
		
		boolean complete = false;
		
        if (bytes.length == 0) {
	    	return false;
	    }
        
        connectToFTP();
		
		try {
			OutputStream outputStream = ftpClient.storeFileStream(filename);
			
			if (cropAvatar) {
				String ext = FilenameUtils.getExtension(filename);
				bytes = cropImageSquare(bytes, ext);
			}
			
			outputStream.write(bytes);
			outputStream.close();
			complete = ftpClient.completePendingCommand();
			
		} catch (IOException e) {
			
			System.out.println(e);
        	disconnectFromFTP();
        	complete = false;
		}
		
		return complete;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// ---------------------------------- DELETING ---------------------------------- //
	
	
	public synchronized boolean deleteFile(String filename) throws SocketException, IOException, InterruptedException {
		
		boolean complete;
		connectToFTP();
		
		try {
			ftpClient.deleteFile(filename);
			complete = true;
		} catch (SocketException e) {
			System.out.println("Error deleting file: " + filename + ". Root error: " + e.getMessage());
			complete = false;
		}
		
		return complete;
	}
	
	
	public void clearAllConversationContents(String user1, String user2) throws SocketException, IOException, InterruptedException {
		
		// Delete files
		List<String> filePaths = databaseAccess.getFilePaths(user1, user2);
		for (String filePath : filePaths) {
			deleteFile(filePath);
		}
		
		// Clear message history
		databaseAccess.clearMessageHistory(user1, user2);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// ---------------------------------- GRAPHICS ---------------------------------- //
	
	
	private byte[] cropImageSquare(byte[] image, String ext) throws IOException {        
        
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
        ImageIO.write(croppedImage, ext, baos);
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
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// ---------------------------------- SERVICE ---------------------------------- //
	
	
	private byte[] fileToBytes(File file) throws IOException {
		
		byte[] bytes = new byte[(int) file.length()];

		FileInputStream fis = new FileInputStream(file);
		fis.read(bytes);
		fis.close();
		return bytes;
	}
	
	
	private byte[] fileToBytes(MultipartFile file) throws IOException {
		
		byte[] bytes = file.getBytes();
		return bytes;
	}
	
	
	public String getBase64String(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}
	
	
	public static boolean filePassesFilter(MultipartFile file, List<String> extensions) {
		
		if (extensions == null || extensions.size() == 0) {
			return true;
		}
		
		String fileEx = FilenameUtils.getExtension(file.getOriginalFilename());
		
		return extensions.contains(fileEx);
	}
	
	
	public List<String> imageExtensions() {
		return Arrays.asList("jpg", "jpeg", "png");
	}
	
	
}
