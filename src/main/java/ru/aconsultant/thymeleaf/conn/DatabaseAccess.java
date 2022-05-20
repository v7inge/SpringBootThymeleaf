package ru.aconsultant.thymeleaf.conn;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.aconsultant.thymeleaf.mapper.ContactMapper;
import ru.aconsultant.thymeleaf.model.*;

@Repository
@Transactional
public class DatabaseAccess extends JdbcDaoSupport {
 
	// query for group chats:
	// SELECT * FROM GROUP_CHATS WHERE Members LIKE CONCAT("%", "victor", "$%")
	
	@Autowired 
	private DataSource dataSource;
	
    @Autowired
    public DatabaseAccess(DataSource dataSource) {
        this.setDataSource(dataSource);
    }
    
    
    public void saveMessage(Message message) throws SQLException {
    	
        String sql = "INSERT INTO MESSAGES (Sender, Receiver, DateTime, Text, FilePath, Code, ID, FileName, New) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        this.getJdbcTemplate().update(sql, message.getSender(), message.getReceiver(), message.getDateTime(), message.getText(), 
        		message.getFilePath(), message.getCode(), message.getId(), message.getFileName(), message.getNewOne());
    }
    
    
    public void resetCounter(String userName, String contactName) throws SQLException {
        String sql = "UPDATE MESSAGES SET New=0 WHERE Sender=? AND Receiver=?";
        this.getJdbcTemplate().update(sql, contactName, userName);
    }
    
    
    public List<Contact> searchForUsers(String input, String username) throws SQLException {
        
    	String sql = 
				"SELECT u.USER_NAME, u.BASE64IMAGE, u.LETTER FROM APP_USER u \n" +
				"WHERE u.USER_NAME LIKE CONCAT(?, \"%\") \n" +
				"AND u.USER_NAME NOT IN \n" +
				"(SELECT chats.Contact FROM PERSONAL_CHATS chats \n" +
				"WHERE chats.User = ?)";
    	
		Object[] args = new Object[] { input, username };
		ContactMapper mapper = new ContactMapper();
    	
		try {
			List<Contact> users = this.getJdbcTemplate().query(sql, args, mapper);
            return users;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    
    public void saveUserBase64Image(String username, String base63image) throws SQLException {
        String sql = "UPDATE APP_USER SET BASE64IMAGE=? WHERE USER_NAME=?";
        this.getJdbcTemplate().update(sql, base63image, username);
    }
    
    
    public String getUserBase64Image(String username) {
    	
    	String sql = "SELECT BASE64IMAGE FROM APP_USER WHERE USER_NAME = ?";
		
		Object[] args = new Object[] { username };
		int[] argTypes = new int[] { Types.VARCHAR };
		
		try {
			SqlRowSet rs = this.getJdbcTemplate().queryForRowSet(sql, args, argTypes);
		    if (rs.next()) {
		        return rs.getString("BASE64IMAGE");
		    }
			
        } catch (EmptyResultDataAccessException e) { }
		return null;
    }
    
    
    public List<String> getFilePaths(String user1, String user2) {
    	
    	String sql = "SELECT m.FilePath FROM MESSAGES m \r\n" +
    			"WHERE (m.Sender = ? AND m.Receiver = ? OR m.Sender = ? AND m.Receiver = ?) \r\n" + 
    			"AND m.FilePath IS NOT NULL";
    	
    	Object[] args = new Object[] { user1, user2, user2, user1 };
    	int[] argTypes = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
    	List<String> filePaths = new ArrayList<String>();
    	
		try {
			SqlRowSet rs = this.getJdbcTemplate().queryForRowSet(sql, args, argTypes);
			while (rs.next()) {
				filePaths.add(rs.getString("FilePath"));
			}
            return filePaths;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    
    public void clearMessageHistory(String user1, String user2) {
    	
    	String sql = "DELETE FROM MESSAGES WHERE Sender = ? AND Receiver = ? OR Sender = ? AND Receiver = ?";
        this.getJdbcTemplate().update(sql, user1, user2, user2, user1);
    }

    
}