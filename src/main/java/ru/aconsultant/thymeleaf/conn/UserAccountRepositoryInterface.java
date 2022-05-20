package ru.aconsultant.thymeleaf.conn;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.aconsultant.thymeleaf.model.UserAccount;

import java.util.Set;

public interface UserAccountRepositoryInterface extends CrudRepository<UserAccount, String> {

    UserAccount findByUsername(String username);

    @Query(value = "SELECT AppUser.USER_NAME AS Username, SUM(m.New) AS UnreadCount, AppUser.BASE64IMAGE AS Base64Image, AppUser.LETTER AS Letter, 0 AS Current FROM\r\n" +
                    "(SELECT chats.User, chats.Contact FROM PERSONAL_CHATS chats WHERE chats.User = :username) AS UserChats\r\n" +
                    "LEFT JOIN MESSAGES m ON UserChats.Contact = m.Sender AND m.Receiver = :username AND m.New = 1\r\n" +
                    "LEFT JOIN APP_USER AppUser ON UserChats.Contact = AppUser.USER_NAME\r\n" +
                    "GROUP BY UserChats.Contact",
            nativeQuery = true)
    Iterable<Object[]> getContacts(@Param("username") String username);

    @Query("select new UserAccount(u.username, u.letter, count(m), u.base64Image) from UserAccount u " +
            " left join Message m on m.receiver = :username and m.sender = u.username" +
            " and m.newOne = true where u.username in (:contactNames) group by u.username")
    Iterable<UserAccount> getContactAccounts(@Param("username") String username, @Param("contactNames") Set<String> contactNames);

    @Query(value = "SELECT AppUser.USER_NAME AS userName, COUNT(m.New) AS newCount, AppUser.BASE64IMAGE AS base64Image, AppUser.LETTER AS letter FROM\r\n" +
            "(SELECT chats.User, chats.Contact FROM PERSONAL_CHATS chats WHERE chats.User = :username) AS UserChats\r\n" +
            "LEFT JOIN MESSAGES m ON UserChats.Contact = m.Sender AND m.Receiver = :username AND m.New = 1\r\n" +
            "LEFT JOIN APP_USER AppUser ON UserChats.Contact = AppUser.USER_NAME\r\n" +
            "GROUP BY UserChats.Contact",
            nativeQuery = true)
    Iterable<UserAccount> getContactz(@Param("username") String username);

}
