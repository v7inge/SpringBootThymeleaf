package ru.aconsultant.thymeleaf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.aconsultant.thymeleaf.conn.MessageRepositoryInterface;
import ru.aconsultant.thymeleaf.model.Message;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepositoryInterface messageRepositoryInterface;

    public List<Message> getHistory(String sender, String receiver) {
        return Util.getAsList(messageRepositoryInterface.getHistory(sender, receiver));
    }
}
