package com.speanegames.fairybattles.server.authentication;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class RegistrationInfo {
    public String login;
    public String password;
    public String email;

    public RegistrationInfo() {
        String EMPTY_STRING = "";

        login = EMPTY_STRING;
        password = EMPTY_STRING;
        email = EMPTY_STRING;
    }
    public RegistrationInfo(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }
}
