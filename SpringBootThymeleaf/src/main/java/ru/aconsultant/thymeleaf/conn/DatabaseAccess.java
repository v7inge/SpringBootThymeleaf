package ru.aconsultant.thymeleaf.conn;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.aconsultant.thymeleaf.beans.*;
import ru.aconsultant.thymeleaf.mapper.MessageMapper;
import ru.aconsultant.thymeleaf.mapper.UserAccountMapper;

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
 
    
    public UserAccount findUser(String login, String password) {
    	
    	String sql = "Select a.USER_NAME, a.PASSWORD from USER_ACCOUNT a where a.USER_NAME = ? and a.PASSWORD = ?";
        Object[] args = new Object[] { login, password };
        UserAccountMapper mapper = new UserAccountMapper();
        try {
        	UserAccount userInfo = this.getJdbcTemplate().queryForObject(sql, args, mapper);
            return userInfo;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    
    public UserAccount findUser(String login) {
    	
    	String sql = "Select a.USER_NAME, a.PASSWORD from USER_ACCOUNT a where a.USER_NAME = ?";
        Object[] args = new Object[] { login };
        UserAccountMapper mapper = new UserAccountMapper();
        try {
        	UserAccount userInfo = this.getJdbcTemplate().queryForObject(sql, args, mapper);
            return userInfo;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    
    public ArrayList<String> contactNameList(String excludeName) throws SQLException {
        
		String sql = "Select a.USER_NAME from USER_ACCOUNT a WHERE a.USER_NAME <> ?";
		Object[] args = new Object[] { excludeName };
		int[] argTypes = new int[] { Types.VARCHAR };
		
		try {
			SqlRowSet rs = this.getJdbcTemplate().queryForRowSet(sql, args, argTypes);
			
			ArrayList<String> list = new ArrayList<String>();
		    while (rs.next()) {
		        list.add(rs.getString("USER_NAME"));
		    }
		    return list;
			
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    
    public List<Contact> userContactList(String username) throws InvalidResultSetAccessException, SQLException, IOException {
    	
    	String sql =
    			"SELECT AppUser.USER_NAME AS Username, SUM(m.New) AS UnreadCount, AppUser.AVATAR AS Avatar, AppUser.BASE64IMAGE AS Base64Image, AppUser.LETTER AS Letter, 0 AS Current FROM\r\n" + 
    			"(SELECT chats.User, chats.Contact FROM PERSONAL_CHATS chats WHERE chats.User = ?) AS UserChats\r\n" + 
    			"LEFT JOIN MESSAGES m ON UserChats.Contact = m.Sender AND m.New = 1\r\n" + 
    			"LEFT JOIN APP_USER AppUser ON UserChats.Contact = AppUser.USER_NAME\r\n" + 
    			"GROUP BY UserChats.Contact, AppUser.AVATAR\r\n" + 
    			"UNION\r\n" + 
    			"SELECT AppUser.USER_NAME, 0, AppUser.AVATAR, AppUser.BASE64IMAGE, AppUser.LETTER, 1 FROM\r\n" + 
    			"APP_USER AppUser WHERE AppUser.USER_NAME = ?";
    	
    	Object[] args = new Object[] { username, username };
		int[] argTypes = new int[] { Types.VARCHAR, Types.VARCHAR };
		
		try {
			SqlRowSet rs = this.getJdbcTemplate().queryForRowSet(sql, args, argTypes);
			
			ArrayList<Contact> list = new ArrayList<Contact>();
		    while (rs.next()) {   	
		    	Contact contact = new Contact(rs.getString("Username"), rs.getInt("UnreadCount"), rs.getString("Avatar"), rs.getInt("Current")==1, rs.getString("Base64Image"), rs.getString("Letter"));
		    	list.add(contact);
		    }
		    return list;
			
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    	
    }
    
    
    public List<Message> getHistory(String sender, String receiver) throws SQLException {
    	
    	String sql = "Select * from MESSAGES m where m.Sender = ? and m.Receiver = ? or m.Sender = ? and m.Receiver = ?";
    	Object[] args = new Object[] { sender, receiver, receiver, sender };
		MessageMapper mapper = new MessageMapper();
    	
		try {
			List<Message> history = this.getJdbcTemplate().query(sql, args, mapper);
            return history;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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
    
    
    public UserAccount findUserAccount(String userName) {

        String sql = "SELECT u.USER_NAME, u.ENCRYPTED_PASSWORD From APP_USER u WHERE u.USER_NAME = ? ";
 
        Object[] params = new Object[] { userName };
        UserAccountMapper mapper = new UserAccountMapper();
        try {
        	UserAccount userInfo = this.getJdbcTemplate().queryForObject(sql, params, mapper);
            return userInfo;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    
    public String addUserAccount(UserAccount user) throws SQLException {
    	
    	if (findUserAccount(user.getUserName()) != null) {
    		return "error";
    	}
    	
		String sql = "INSERT INTO APP_USER (USER_NAME, ENCRYPTED_PASSWORD, ENABLED, LETTER) VALUES (?, ?, 1, ?)";
        this.getJdbcTemplate().update(sql, user.getUserName(), user.getEncryptedPassword(), user.getLetter());
        return "";
    }
    
    
    public ArrayList<String> searchForUsers(String input, String username) throws SQLException {
        
    	String sql = 
				"SELECT u.USER_NAME FROM APP_USER u \n" +
				"WHERE u.USER_NAME LIKE CONCAT(?, \"%\") \n" +
				"AND u.USER_NAME NOT IN \n" +
				"(SELECT chats.Contact FROM PERSONAL_CHATS chats \n" +
				"WHERE chats.User = ?)";
    	
		Object[] args = new Object[] { input, username };
		int[] argTypes = new int[] { Types.VARCHAR, Types.VARCHAR };
		
		try {
			SqlRowSet rs = this.getJdbcTemplate().queryForRowSet(sql, args, argTypes);
			ArrayList<String> list = new ArrayList<String>();
		    while (rs.next()) {
		        list.add(rs.getString("USER_NAME"));
		    }
		    return list;
			
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    
    public void addContact(String username, String contact) throws SQLException {
    	
    	String sql = "SELECT ch.User, ch.Contact FROM PERSONAL_CHATS ch \n" + 
    			"WHERE ch.User = ? AND ch.Contact = ?";
    	
    	Object[] args = new Object[] { username, contact };
		int[] argTypes = new int[] { Types.VARCHAR, Types.VARCHAR };
    	
		try {
			SqlRowSet rs = this.getJdbcTemplate().queryForRowSet(sql, args, argTypes);
		    if (rs.next()) { } else {
		    	sql = "INSERT INTO PERSONAL_CHATS (User, Contact, New) VALUES (?, ?, NULL)";
		    	this.getJdbcTemplate().update(sql, username, contact);
		    }
			
        } catch (EmptyResultDataAccessException e) { }    
    }
    
    
    public void setUserAvatar(String username, String avatar) throws SQLException {
        String sql = "UPDATE APP_USER SET AVATAR=? WHERE USER_NAME=?";
        this.getJdbcTemplate().update(sql, avatar, username);
    }
    
    
    public void saveUserBase64Image(String username, String base63image) throws SQLException {
        String sql = "UPDATE APP_USER SET BASE64IMAGE=? WHERE USER_NAME=?";
        this.getJdbcTemplate().update(sql, base63image, username);
    }
    
    
    public String getUserAvatarPath(String username) throws SQLException {
        
		String sql = "SELECT AVATAR FROM APP_USER WHERE USER_NAME = ?";
				
		Object[] args = new Object[] { username };
		int[] argTypes = new int[] { Types.VARCHAR };
		
		try {
			SqlRowSet rs = this.getJdbcTemplate().queryForRowSet(sql, args, argTypes);
		    if (rs.next()) {
		        return rs.getString("AVATAR");
		    }
			
        } catch (EmptyResultDataAccessException e) { }
		return null;
    }
    
    
    public void fillAvatarPath(Contact contact) throws SQLException {
    	String avatarPath = getUserAvatarPath(contact.getUsername());
    	contact.setAvatarPath(avatarPath);
    }

    
}