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
        if (userAccount.getUsername() != null && findUserAccount(userAccount.getUsername()) == null) {
            return userAccountRepositoryInterface.save(userAccount);
        } else {
            return null;
        }
    }

    public UserAccount findUserAccount(String username) {
        return userAccountRepositoryInterface.findByUsername(username);
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

    public List<UserAccount> getCompanions(UserAccount userAccount) {
        return Util.getAsList(userAccountRepositoryInterface.getContactz(userAccount.getUsername()));
        //return Util.getAsList(userAccountRepositoryInterface.getContactAccounts(userAccount.getUserName(), userAccount.getContactNames()));
    }

    public List<UserAccount> getContactAccounts(UserAccount userAccount) {
        return Util.getAsList(userAccountRepositoryInterface.getContactAccounts(userAccount.getUsername(), userAccount.getContactNames()));
    }
}
