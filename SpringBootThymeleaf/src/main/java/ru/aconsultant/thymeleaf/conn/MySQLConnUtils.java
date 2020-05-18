package ru.aconsultant.thymeleaf.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
  
public class MySQLConnUtils {
  
 public static Connection getMySQLConnection()
         throws ClassNotFoundException, SQLException {
     
     /*String hostName = "localhost";
     String dbName = "chat";
     String userName = "root";
     String password = "1111";*/
     
     String hostName = "freshnoon.beget.tech";
     String dbName = "freshnoon_chat";
     String userName = "freshnoon_chat";
     String password = "hgy78fdXVb0";
     
     return getMySQLConnection(hostName, dbName, userName, password);
 }
  
 public static Connection getMySQLConnection(String hostName, String dbName,
         String userName, String password) throws SQLException,
         ClassNotFoundException {
    
     Class.forName("com.mysql.jdbc.Driver");
  
     // URL Connection for MySQL:
     // Example: 
     // jdbc:mysql://localhost:3306/simplehr
     String connectionURL = "jdbc:mysql://" + hostName + ":3306/" + dbName + "?useUnicode=true&amp;characterEncoding=utf8";
  
     Connection conn = DriverManager.getConnection(connectionURL, userName,
             password);
     return conn;
 }
}