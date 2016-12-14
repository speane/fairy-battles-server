package com.speanegames.fairybattles.server.authentication;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class UserInfo {
    public String name;
    public int battlesPlayed;
    public int bestScore;

    public UserInfo() {

    }

    public UserInfo(String name, int battlesPlayed, int bestScore) {
        this.name = name;
        this.battlesPlayed = battlesPlayed;
        this.bestScore = bestScore;
    }
}
