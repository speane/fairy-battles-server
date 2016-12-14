package com.speanegames.fairybattles.server.authentication;


import com.speanegames.fairybattles.server.database.DatabaseConnection;

import java.sql.SQLException;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class AuthenticationManager {
    private DatabaseConnection databaseConnection;

    public AuthenticationManager() throws SQLException {
        String HOST = "localhost";
        String SCHEME_NAME = "fairy_battles";
        String LOGIN = "speane";
        String PASSWORD = "123456QWERTY";
        databaseConnection = new DatabaseConnection(HOST, SCHEME_NAME, LOGIN, PASSWORD);
    }

    public UserInfo getUserInfo(String userName, String password) throws SQLException {
        return databaseConnection.getUser(userName, password);
    }

    public UserInfo register(String userName, String password) throws SQLException {
        databaseConnection.execute("INSERT INTO users (login, password) VALUES ('"
                + userName + "', '" + password + "');");
        return getUserInfo(userName, password);
    }
}
