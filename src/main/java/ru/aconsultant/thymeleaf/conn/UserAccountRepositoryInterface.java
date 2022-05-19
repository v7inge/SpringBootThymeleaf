package ru.aconsultant.thymeleaf.conn;

import org.springframework.data.repository.CrudRepository;
import ru.aconsultant.thymeleaf.model.UserAccount;

public interface UserAccountRepositoryInterface extends CrudRepository<UserAccount, String> {

    UserAccount findByUserName(String username);
}
