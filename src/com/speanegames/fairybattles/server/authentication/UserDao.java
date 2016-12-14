package com.speanegames.fairybattles.server.authentication;

import java.sql.*;

public class UserDao {

    private Connection connection;

    public UserDao() {
        String url = "jdbc:mysql://localhost/fairy_battles?useSSL=true";
        try {
            connection = DriverManager.getConnection(url, "speane", "123456QWERTY");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void save(User user) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(String.format(
            "INSERT INTO `fairy_battles`.`users` (`username`, `password`) VALUES ('%s', '%s');",
            user.getUsername(),
            user.getPassword()
        ));
    }

    public User find(String username) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format(
                "SELECT username, password FROM users WHERE username = '%s';",
                username
        ));
        if (resultSet.next()) {
            User user = new User();

            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));

            return user;
        } else {
            return null;
        }
    }
}
