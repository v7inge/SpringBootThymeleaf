package ru.aconsultant.thymeleaf.conn;

import org.springframework.data.repository.CrudRepository;
import ru.aconsultant.thymeleaf.model.Message;

public interface MessageRepositoryInterface extends CrudRepository<Message, String> {

    Iterable<Message> findBySenderAndReceiver(String sender, String receiver);
}
