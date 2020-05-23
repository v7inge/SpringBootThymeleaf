package ru.aconsultant.thymeleaf.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ru.aconsultant.thymeleaf.beans.UserAccount;
import org.springframework.jdbc.core.RowMapper;

public class UserAccountMapper implements RowMapper<UserAccount> {

	public static final String BASE_SQL = "SELECT u.USER_ID, u.USER_NAME, u.ENCRYPTED_PASSWORD From APP_USER u ";
	
	@Override
	public UserAccount mapRow(ResultSet rs, int rowNum) throws SQLException {

		String login = rs.getString("USER_NAME");
		//String password = rs.getString("PASSWORD"); //#refactor
		String encryptedPassword = rs.getString("ENCRYPTED_PASSWORD");
		return new UserAccount(login, encryptedPassword);
	}
}
