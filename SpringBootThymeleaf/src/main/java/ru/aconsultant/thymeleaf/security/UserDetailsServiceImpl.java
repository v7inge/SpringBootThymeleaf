package ru.aconsultant.thymeleaf.security;
 
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.aconsultant.thymeleaf.conn.DatabaseAccess;
import ru.aconsultant.thymeleaf.model.UserAccount;
 
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
 
    @Autowired
    private DatabaseAccess databaseAccess;
    
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserAccount appUser = this.databaseAccess.findUserAccount(userName);
 
        if (appUser == null) {
            System.out.println("User not found! " + userName);
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }
 
        UserDetails userDetails = (UserDetails) new User(appUser.getUserName(), appUser.getEncryptedPassword(), createAuthorityList("ROLE_USER"));
 
        return userDetails;
    }
    
    public List<GrantedAuthority> createAuthorityList(String role) {
    	
    	 List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
         GrantedAuthority authority = new SimpleGrantedAuthority(role);
         grantList.add(authority);
         return grantList;
    }
 
}