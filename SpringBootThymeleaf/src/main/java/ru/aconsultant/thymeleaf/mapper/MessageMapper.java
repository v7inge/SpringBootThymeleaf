package ru.aconsultant.thymeleaf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ru.aconsultant.thymeleaf.beans.Message;
import org.springframework.jdbc.core.RowMapper;

public class MessageMapper implements RowMapper<Message> {

	@Override
	public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Message message = new Message(rs.getString("Sender"), rs.getString("Reciever"), rs.getTimestamp("DateTime"), rs.getString("Text"));
		message.setFilePath(rs.getString("FilePath"));
		message.setImage(rs.getInt("Image")==1);
		return message;
	}
	
}
