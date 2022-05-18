package ru.aconsultant.thymeleaf.security;
 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 
public class PasswordEncoder {
 
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
 
}