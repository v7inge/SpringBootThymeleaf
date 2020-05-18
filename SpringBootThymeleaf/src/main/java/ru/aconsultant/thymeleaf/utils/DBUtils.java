package ru.aconsultant.thymeleaf.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;

import ru.aconsultant.thymeleaf.beans.*;
 
public class DBUtils {
 
    /*public static UserAccount findUser(Connection conn, String userName, String password) throws SQLException {
 
    	if (conn == null) {
    		return null;
    	}
    	
        String sql = "Select a.USER_NAME, a.PASSWORD, a.GENDER from USER_ACCOUNT a where a.USER_NAME = ? and a.PASSWORD= ?";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, userName);
        pstm.setString(2, password);
        ResultSet rs = pstm.executeQuery();
 
        if (rs.next()) {
            String gender = rs.getString("Gender");
            UserAccount user = new UserAccount();
            user.setUserName(userName);
            user.setPassword(password);
            user.setGender(gender);
            return user;
        }
        return null;
    }
 
    public static UserAccount findUser(Connection conn, String userName) throws SQLException {
 
        String sql = "Select a.USER_NAME, a.PASSWORD, a.GENDER from USER_ACCOUNT a where a.USER_NAME = ? ";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, userName);
 
        ResultSet rs = pstm.executeQuery();
 
        if (rs.next()) {
            String password = rs.getString("Password");
            String gender = rs.getString("Gender");
            UserAccount user = new UserAccount();
            user.setUserName(userName);
            user.setPassword(password);
            user.setGender(gender);
            return user;
        }
        return null;
    }
 
    public static ArrayList<UserAccount> userList(Connection conn) throws SQLException {
        
    	String sql = "Select a.USER_NAME from USER_ACCOUNT a ";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
 
        ResultSet rs = pstm.executeQuery();
        ArrayList<UserAccount> list = new ArrayList<UserAccount>();
        while (rs.next()) {

            UserAccount user = new UserAccount();
            user.setUserName(rs.getString("USER_NAME"));
            list.add(user);
        }
        return list;
    }
    
public static ArrayList<UserAccount> userList(Connection conn, String excludeName) throws SQLException {
        
		String sql = "Select a.USER_NAME from USER_ACCOUNT a WHERE a.USER_NAME <> ?";
		
	    PreparedStatement pstm = conn.prepareStatement(sql);
	    pstm.setString(1, excludeName);
	    
	    ResultSet rs = pstm.executeQuery();
	    ArrayList<UserAccount> list = new ArrayList<UserAccount>();
	    while (rs.next()) {
	
	        UserAccount user = new UserAccount();
	        user.setUserName(rs.getString("USER_NAME"));
	        list.add(user);
	    }
	    return list;
    }*/
    
    public static ArrayList<Message> getHistory(Connection conn, String sender, String reciever) throws SQLException {
    	
    	String sql = "Select * from MESSAGES m where m.Sender = ? and m.Reciever = ? or m.Sender = ? and m.Reciever = ?";
    	PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, sender);
        pstm.setString(2, reciever);
        pstm.setString(3, reciever);
        pstm.setString(4, sender);
        ResultSet rs = pstm.executeQuery();
    	
        ArrayList<Message> history = new ArrayList <Message>();
        
        while (rs.next()) {
        	
        	Message message = new Message(rs.getString("Sender"), rs.getString("Reciever"), rs.getTimestamp("DateTime"), rs.getString("Text"));
        	history.add(message);
        	
        }
        
    	return history;	
    }
    
    public static void saveMessage(Connection conn, Message message) throws SQLException {
        String sql = "INSERT INTO MESSAGES (Sender, Reciever, DateTime, Text) VALUES (?, ?, ?, ?)";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
 
        pstm.setString(1, message.getSender());
        pstm.setString(2, message.getReciever());
        pstm.setDate(3, new java.sql.Date(message.getDate().getTime()));
        pstm.setString(4, message.getText());
 
        pstm.executeUpdate();
    }
    
    public static List<Product> queryProduct(Connection conn) throws SQLException {
        String sql = "Select a.Code, a.Name, a.Price from Product a ";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
 
        ResultSet rs = pstm.executeQuery();
        List<Product> list = new ArrayList<Product>();
        while (rs.next()) {
            String code = rs.getString("Code");
            String name = rs.getString("Name");
            float price = rs.getFloat("Price");
            Product product = new Product();
            product.setCode(code);
            product.setName(name);
            product.setPrice(price);
            list.add(product);
        }
        return list;
    }
 
    public static Product findProduct(Connection conn, String code) throws SQLException {
        String sql = "Select a.Code, a.Name, a.Price from Product a where a.Code=?";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, code);
 
        ResultSet rs = pstm.executeQuery();
 
        while (rs.next()) {
            String name = rs.getString("Name");
            float price = rs.getFloat("Price");
            Product product = new Product(code, name, price);
            return product;
        }
        return null;
    }
 
    public static void updateProduct(Connection conn, Product product) throws SQLException {
        String sql = "Update Product set Name =?, Price=? where Code=? ";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
 
        pstm.setString(1, product.getName());
        pstm.setFloat(2, product.getPrice());
        pstm.setString(3, product.getCode());
        pstm.executeUpdate();
    }
 
    public static void insertProduct(Connection conn, Product product) throws SQLException {
        String sql = "Insert into Product(Code, Name,Price) values (?,?,?)";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
 
        pstm.setString(1, product.getCode());
        pstm.setString(2, product.getName());
        pstm.setFloat(3, product.getPrice());
 
        pstm.executeUpdate();
    }
 
    public static void deleteProduct(Connection conn, String code) throws SQLException {
        String sql = "Delete From Product where Code= ?";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
 
        pstm.setString(1, code);
 
        pstm.executeUpdate();
    }
 
}

class MessageSavingTread extends Thread    {
    
	private Message message;

    MessageSavingTread(Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        
    }
}