package ru.aconsultant.thymeleaf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.aconsultant.thymeleaf.conn.UserAccountRepositoryInterface;
import ru.aconsultant.thymeleaf.model.Contact;
import ru.aconsultant.thymeleaf.model.UserAccount;

import java.util.ArrayList;
import java.util.List;

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

    public List<Contact> userContactList(String username) {
        List<Object[]> objects = Util.getAsList(userAccountRepositoryInterface.getContacts(username));
        return asContactList(objects);
    }

    private List<Contact> asContactList(List<Object[]> objects) {
        List<Contact> contacts = new ArrayList<>();
        if (objects != null) {
            for (Object[] object : objects) {
                contacts.add(new Contact(object));
            }
        }
        return contacts;
    }
}
