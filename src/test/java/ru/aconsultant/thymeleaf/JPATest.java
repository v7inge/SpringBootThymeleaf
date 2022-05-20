package ru.aconsultant.thymeleaf;

import org.hibernate.Hibernate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.aconsultant.thymeleaf.model.Contact;
import ru.aconsultant.thymeleaf.model.UserAccount;
import ru.aconsultant.thymeleaf.service.UserAccountService;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JPATest {

    @Autowired
    UserAccountService userAccountService;

    @Test
    @Transactional
    public void findUserAccountTest() {
        UserAccount userAccount = userAccountService.findUserAccount("friend");
        //Hibernate.initialize(userAccount.getContactNames());
        //List<Contact> contacts = userAccountService.userContactList("friend");
        List<UserAccount> accounts = userAccountService.getCompanions(userAccount);
    }
}
