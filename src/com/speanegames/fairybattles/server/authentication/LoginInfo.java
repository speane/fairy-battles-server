package com.speanegames.fairybattles.server.authentication;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class LoginInfo {
    public String userName;
    public String password;

    public LoginInfo() {
        String EMPTY_STRING = "";

        userName = EMPTY_STRING;
        password = EMPTY_STRING;
    }

    public LoginInfo(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
