package ru.aconsultant.thymeleaf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.aconsultant.thymeleaf.model.UserAccount;
import ru.aconsultant.thymeleaf.service.UserAccountService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JPATest {

    @Autowired
    UserAccountService userAccountService;

    @Test
    public void findUserAccountTest() {
        UserAccount userAccount = userAccountService.findUserAccount("friend");
    }
}
