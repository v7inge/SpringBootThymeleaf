package ru.aconsultant.thymeleaf.conn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    	
    	String sql = "Select a.USER_NAME, a.PASSWORD, a.GENDER from USER_ACCOUNT a where a.USER_NAME = ? and a.PASSWORD = ?";
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
    	
    	String sql = "Select a.USER_NAME, a.PASSWORD, a.GENDER from USER_ACCOUNT a where a.USER_NAME = ?";
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
    
    public ArrayList<Contact> contactList(String userName) throws SQLException {
        
		String sql = 
				"SELECT UserChats.Contact, SUM(m.New) AS UnreadCount FROM \n" +
				"(SELECT chats.User, chats.Contact FROM PERSONAL_CHATS chats WHERE chats.User = ?) AS UserChats \n" +
				"LEFT JOIN MESSAGES m ON UserChats.Contact = m.Sender AND m.New = 1 \n" +
				"GROUP BY UserChats.Contact";
				
		Object[] args = new Object[] { userName };
		int[] argTypes = new int[] { Types.VARCHAR };
		
		try {
			SqlRowSet rs = this.getJdbcTemplate().queryForRowSet(sql, args, argTypes);
			
			ArrayList<Contact> list = new ArrayList<Contact>();
		    while (rs.next()) {
		        list.add(new Contact(rs.getString("Contact"), rs.getInt("UnreadCount")));
		    }
		    return list;
			
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    
    public List<Message> getHistory(String sender, String reciever) throws SQLException {
    	
    	String sql = "Select * from MESSAGES m where m.Sender = ? and m.Reciever = ? or m.Sender = ? and m.Reciever = ?";
    	Object[] args = new Object[] { sender, reciever, reciever, sender };
		MessageMapper mapper = new MessageMapper();
    	
		try {
			List<Message> history = this.getJdbcTemplate().query(sql, args, mapper);
            return history;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    public void saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO MESSAGES (Sender, Reciever, DateTime, Text) VALUES (?, ?, ?, ?)";
        this.getJdbcTemplate().update(sql, message.getSender(), message.getReciever(), new java.sql.Date(message.getDate().getTime()), message.getText());
    }
    
}