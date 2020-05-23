package ru.aconsultant.thymeleaf.dao;
 
import javax.sql.DataSource;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.aconsultant.thymeleaf.beans.UserAccount;
import ru.aconsultant.thymeleaf.mapper.UserAccountMapper;
 
@Repository
@Transactional
public class UserAccountDAO extends JdbcDaoSupport {
 
    @Autowired
    public UserAccountDAO(DataSource dataSource) {
        this.setDataSource(dataSource);
    }
 
    public UserAccount findUserAccount(String userName) {
        // Select .. from App_User u Where u.User_Name = ?
        String sql = UserAccountMapper.BASE_SQL + " WHERE u.USER_NAME = ? ";
 
        Object[] params = new Object[] { userName };
        UserAccountMapper mapper = new UserAccountMapper();
        try {
        	UserAccount userInfo = this.getJdbcTemplate().queryForObject(sql, params, mapper);
            return userInfo;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
 
}