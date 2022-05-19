package ru.aconsultant.thymeleaf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.aconsultant.thymeleaf.conn.UserAccountRepositoryInterface;
import ru.aconsultant.thymeleaf.model.UserAccount;

@Service
public class UserAccountService {

    @Autowired
    UserAccountRepositoryInterface userAccountRepositoryInterface;

    public UserAccount save(UserAccount userAccount) {
        if (userAccount.getUserName() != null && findUserAccount(userAccount.getUserName()) == null) {
            return userAccountRepositoryInterface.save(userAccount);
        } else {
            return null;
        }
    }

    public UserAccount findUserAccount(String username) {
        return userAccountRepositoryInterface.findByUserName(username);
    }
}
