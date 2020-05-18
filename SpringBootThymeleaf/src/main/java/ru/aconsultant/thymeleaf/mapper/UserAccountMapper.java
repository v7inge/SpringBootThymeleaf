package ru.aconsultant.thymeleaf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ru.aconsultant.thymeleaf.beans.UserAccount;
import org.springframework.jdbc.core.RowMapper;

public class UserAccountMapper implements RowMapper<UserAccount> {

	@Override
	public UserAccount mapRow(ResultSet rs, int rowNum) throws SQLException {

		String login = rs.getString("USER_NAME");
		String password = rs.getString("PASSWORD");
		return new UserAccount(login, password);
	}
}
