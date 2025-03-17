package com.pokebinderapp.dao;

import com.pokebinderapp.model.User;
import com.pokebinderapp.exception.DaoException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcUserDao implements UserDao {


    // todo: consolidate null check logic
    // todo: consolidate SQL statements


    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, userId);
        } catch (Exception e) {
            throw new DaoException("Error getting user by ID", e);
        }
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, username);
        } catch (Exception e) {
            throw new DaoException("Error finding user ID by username", e);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            try {
                return jdbcTemplate.queryForObject(sql, this::mapRowToUser, username);
            } catch (EmptyResultDataAccessException e) {
                return null;  // Return null if no user is found
            }
        } catch (Exception e) {
            throw new DaoException("Error getting user by username", e);
        }
    }

    @Override
    public BigDecimal getMoney(int userId) {
        String sql = "SELECT money FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
        } catch (Exception e) {
            throw new DaoException("Error getting money for user", e);
        }
    }

    @Override
    public boolean updateMoney(int userId, BigDecimal newAmount) {
        String sql = "UPDATE users SET money = ? WHERE user_id = ?";
        try {
            int rows = jdbcTemplate.update(sql, newAmount, userId);
            return rows > 0;
        } catch (Exception e) {
            throw new DaoException("Error updating money", e);
        }
    }

    @Override
    public User createUser(User newUser) {
        // Instantiate the encoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Get the raw password and then hash it
        String rawPassword = newUser.getHashedPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        newUser.setHashedPassword(hashedPassword);

        String sql;
        Object[] params;

        if (newUser.getMoney() != null) {  // If money is explicitly set
            sql = "INSERT INTO users (username, password_hash, role, money) VALUES (?, ?, ?, ?) RETURNING user_id";
            params = new Object[]{newUser.getUsername(), newUser.getHashedPassword(), newUser.getRole(), newUser.getMoney()};
        } else {  // Use default value for money
            sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?) RETURNING user_id";
            params = new Object[]{newUser.getUsername(), newUser.getHashedPassword(), newUser.getRole()};
        }

        try {
            int newId = jdbcTemplate.queryForObject(sql, Integer.class, params);
            newUser.setId(newId);
            return newUser;
        } catch (Exception e) {
            throw new DaoException("Error creating user", e);
        }
    }



    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM users";
        try {
            return jdbcTemplate.query(sql, this::mapRowToUser);
        } catch (Exception e) {
            throw new DaoException("Error finding all users", e);
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId);
        return rowsAffected > 0;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setHashedPassword(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setMoney(rs.getBigDecimal("money"));
        return user;
    }
}
