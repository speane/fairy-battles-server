package com.speanegames.fairybattles.server.authentication;

import java.sql.SQLException;

public class AuthenticationController {

    private UserService userService;

    public AuthenticationController() {
        userService = new UserService();
    }

    public void registerUser(String username, String password) throws SQLException {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userService.save(user);
    }

    public boolean checkCredentials(String username, String password) throws SQLException {
        User user = userService.get(username);
        return (user != null) && user.getPassword().equals(password);
    }

    public boolean userExists(String username) throws SQLException {
        return userService.get(username) != null;
    }
}
