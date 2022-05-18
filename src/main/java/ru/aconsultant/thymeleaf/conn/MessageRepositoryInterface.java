package ru.aconsultant.thymeleaf.conn;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.aconsultant.thymeleaf.model.Message;

public interface MessageRepositoryInterface extends CrudRepository<Message, String> {

    @Query("select message from Message message where message.sender = :sender and message.receiver = :receiver" +
            " or message.sender = :receiver and message.receiver = :sender")
    Iterable<Message> getHistory(@Param("sender") String sender, @Param("receiver") String receiver);

}
