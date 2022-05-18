package ru.aconsultant.thymeleaf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ru.aconsultant.thymeleaf.model.Contact;

public class ContactMapper implements RowMapper<Contact> {

	@Override
	public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Contact contact = new Contact(rs.getString("USER_NAME"));
		contact.setBase64Image(rs.getString("BASE64IMAGE"));
		contact.setLetter(rs.getString("LETTER"));
		
		return contact;
	}
	
}
