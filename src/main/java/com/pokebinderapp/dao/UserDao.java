package com.pokebinderapp.dao;

import com.pokebinderapp.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    User createUser(User user);

    List<User> getUsers();

    User getUserByUsername(String username);

    User getUserById(int userId);

    int findIdByUsername(String username);

    BigDecimal getMoney(int userId);

    boolean updateMoney(int userId, BigDecimal newAmount);

    boolean deleteUser(int userId);
}
