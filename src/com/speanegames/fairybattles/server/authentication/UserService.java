package com.speanegames.fairybattles.server.authentication;

import java.sql.SQLException;

public class UserService {

    private UserDao userDao;

    public UserService() {
        userDao = new UserDao();
    }

    public void save(User user) throws SQLException {
        userDao.save(user);
    }

    public User get(String username) throws SQLException {
        return userDao.find(username);
    }
}
