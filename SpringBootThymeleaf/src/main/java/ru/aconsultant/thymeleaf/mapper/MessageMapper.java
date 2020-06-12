package ru.aconsultant.thymeleaf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import ru.aconsultant.thymeleaf.beans.Message;
import org.springframework.jdbc.core.RowMapper;

public class MessageMapper implements RowMapper<Message> {

	@Override
	public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(rs.getTimestamp("DateTime").getTime());
		
		String sender = rs.getString("Sender");
		String receiver = rs.getString("Receiver");
		String text = rs.getString("Text");
		String filePath = rs.getString("FilePath");
		String fileName = rs.getString("FileName");
		int code = rs.getInt("Code");
		String id = rs.getString("id");
		
		Message message = new Message(sender, receiver, date, text, filePath, fileName, code, id);
		
		return message;
	}
	
}
