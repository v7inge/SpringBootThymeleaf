package ru.aconsultant.thymeleaf.conn;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.aconsultant.thymeleaf.model.Contact;
import ru.aconsultant.thymeleaf.model.UserAccount;

public interface UserAccountRepositoryInterface extends CrudRepository<UserAccount, String> {

    UserAccount findByUserName(String username);

    @Query(value = "SELECT AppUser.USER_NAME AS Username, SUM(m.New) AS UnreadCount, AppUser.BASE64IMAGE AS Base64Image, AppUser.LETTER AS Letter, 0 AS Current FROM\r\n" +
                    "(SELECT chats.User, chats.Contact FROM PERSONAL_CHATS chats WHERE chats.User = :username) AS UserChats\r\n" +
                    "LEFT JOIN message m ON UserChats.Contact = m.Sender AND m.Receiver = :username AND m.New = 1\r\n" +
                    "LEFT JOIN APP_USER AppUser ON UserChats.Contact = AppUser.USER_NAME\r\n" +
                    "GROUP BY UserChats.Contact\r\n" +
                    "UNION\r\n" +
                    "SELECT AppUser.USER_NAME, 0, AppUser.BASE64IMAGE, AppUser.LETTER, 1 FROM\r\n" +
                    "APP_USER AppUser WHERE AppUser.USER_NAME = :username",
            nativeQuery = true)
    Iterable<Object[]> getContacts(@Param("username") String username);

}
