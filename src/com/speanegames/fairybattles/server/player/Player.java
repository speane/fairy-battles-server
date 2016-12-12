package com.speanegames.fairybattles.server.player;

public class Player {

    private Integer connectionID;
    private String login;
    private String lobbyID;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLobbyID() {
        return lobbyID;
    }

    public void setLobbyID(String lobbyID) {
        this.lobbyID = lobbyID;
    }

    public Integer getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(Integer connectionID) {
        this.connectionID = connectionID;
    }
}
