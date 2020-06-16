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
	private static int attempts = 20;
	
	private FTPClient ftpClient;
	
	public boolean busy;
	
	private void connectToFTP() throws SocketException, IOException, InterruptedException {
		
		// If FTP is busy we wait
		int i = 0;
		while (busy) {
			
			if (i >= attempts) {
				System.out.println("Error connecting to FTP: it's busy for a long time.");
				disconnectFromFTP();
				break;
			}
			
			Thread.sleep(500);
			i++;
		}
		busy = true;
		
		// Connect if necessary
		if(ftpClient == null || !ftpClient.isConnected()) {
			try {
				//System.out.println("Connecting to FTP at " + System.currentTimeMillis());
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
	    } finally {
	    	busy = false;
	    }
	}
	
	
	public void saveUserBase64Image(Contact user, MultipartFile file) throws IOException, SQLException {
		
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
	
	
	public List<String> imageExtensions() {
		return Arrays.asList("jpg", "jpeg", "png");
	}
	
	
	public String getBase64String(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}
	
	
	public void saveUserBase64Image(Contact user, MultipartFile[] files) throws IOException, SQLException {
		
		MultipartFile file = files[0];
		saveUserBase64Image(user, file);
	}
	
	
	public static boolean filePassesFilter(MultipartFile file, List<String> extensions) {
		
		if (extensions == null || extensions.size() == 0) {
			return true;
		}
		
		String fileEx = FilenameUtils.getExtension(file.getOriginalFilename());
		
		return extensions.contains(fileEx);
	}
	
	
	private byte[] getBytesWhenConnected(String filename) throws IOException {
		
		if (filename == null || filename == "") { return null; }
		
		InputStream inputStream = ftpClient.retrieveFileStream(filename);
		byte[] bytes = inputStream.readAllBytes();
		
		return bytes;
	}
	
	
	public HashMap<String, Object> getMultipleFilesBase64(Set<String> filenames) throws IOException, InterruptedException {
		
        connectToFTP();
        HashMap<String, Object> result = new HashMap<String, Object>();
        
        try {
	        for (String filename : filenames) {
		    	InputStream inputStream = ftpClient.retrieveFileStream(filename);
		    	byte[] bytes = inputStream.readAllBytes();
		       	result.put(filename, getBase64String(bytes));
		       	ftpClient.completePendingCommand();
	        }
        } catch (IOException e) {
        	System.out.println(e);
        	disconnectFromFTP();
        }
        busy = false;
        return result;
	}
	
	
	public Object getFileBase64(String filename) throws IOException, InterruptedException {
		
		Set<String> filenames = new HashSet<String>();
		filenames.add(filename);
		return getMultipleFilesBase64(filenames).get(filename);
	}
	
	
	public byte[] getBytesFromFTP(String filename) throws IOException, InterruptedException {
		
		if (filename == null || filename == "") { return null; }
		
		if(ftpClient == null || !ftpClient.isConnected()) {
        	connectToFTP();
        }
		InputStream inputStream = ftpClient.retrieveFileStream(filename);
		byte[] bytes = inputStream.readAllBytes();
		busy = false;
		return bytes;
	}
	
	
	public boolean saveFile(MultipartFile file, String filename) throws IOException, InterruptedException {
		
        return saveFile(file, filename, null, false);
	}
	
	
	public boolean saveFile(MultipartFile file, String filename, List<String> extensions) throws IOException, InterruptedException {
		
        return saveFile(file, filename, extensions, false);
	}
	
	
	public boolean saveFile(MultipartFile file, String filename, List<String> extensions, boolean cropAvatar) throws IOException, InterruptedException {
		
		boolean complete = false;
		
        if (file.isEmpty()) {
	    	return false;
	    }
        
        if (!filePassesFilter(file, extensions)) {
        	System.out.println("File did not pass the filter");
        	return false;
        }
        
        connectToFTP();
		byte[] bytes = file.getBytes();
		
		try {
			OutputStream outputStream = ftpClient.storeFileStream(filename);
			
			if (cropAvatar) {
				String ext = FilenameUtils.getExtension(file.getOriginalFilename());
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
		
		busy = false;
		return complete;
	}
	
	
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
	
	
}
