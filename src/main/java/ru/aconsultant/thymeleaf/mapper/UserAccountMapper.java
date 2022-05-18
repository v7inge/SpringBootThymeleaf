package ru.aconsultant.thymeleaf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ru.aconsultant.thymeleaf.model.UserAccount;

public class UserAccountMapper implements RowMapper<UserAccount> {
	
	@Override
	public UserAccount mapRow(ResultSet rs, int rowNum) throws SQLException {

		String login = rs.getString("USER_NAME");
		String encryptedPassword = rs.getString("ENCRYPTED_PASSWORD");
		return new UserAccount(login, encryptedPassword);
	}
}
