package com.speanegames.fairybattles.server.lobby;

import com.speanegames.fairybattles.server.config.AppConfig;
import com.speanegames.fairybattles.server.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private String id;
    private Player creator;
    private Player[] sunTeamPlayers;
    private Player[] moonTeamPlayers;
    private List<Player> connectedPlayers;
    private boolean battle;

    public Lobby(String id, Player creator) {
        this.id = id;
        this.creator = creator;
        sunTeamPlayers = new Player[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        moonTeamPlayers = new Player[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        connectedPlayers = new ArrayList<>();
    }

    public int joinSunTeam(Player player) {
        for (int i = 0; i < sunTeamPlayers.length; i++) {
            if (sunTeamPlayers[i] == null) {
                sunTeamPlayers[i] = player;
                return i;
            }
        }

        return -1;
    }

    public int joinMoonTeam(Player player) {
        for (int i = 0; i < moonTeamPlayers.length; i++) {
            if (moonTeamPlayers[i] == null) {
                moonTeamPlayers[i] = player;
                return i;
            }
        }

        return -1;
    }

    public boolean canJoinSunTeam() {
        for (Player sunTeamPlayer : sunTeamPlayers) {
            if (sunTeamPlayer == null) {
                return true;
            }
        }

        return false;
    }

    public int getSunTeamPosition(Player player) {
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (sunTeamPlayers[i] == player) {
                return i;
            }
        }

        return -1;
    }

    public int getMoonTeamPosition(Player player) {
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (moonTeamPlayers[i] == player) {
                return i;
            }
        }

        return -1;
    }

    public boolean canJoinMoonTeam() {
        for (Player moonTeamPlayer : moonTeamPlayers) {
            if (moonTeamPlayer == null) {
                return true;
            }
        }

        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public Player[] getSunTeamPlayers() {
        return sunTeamPlayers;
    }

    public void setSunTeamPlayers(Player[] sunTeamPlayers) {
        this.sunTeamPlayers = sunTeamPlayers;
    }

    public Player[] getMoonTeamPlayers() {
        return moonTeamPlayers;
    }

    public void setMoonTeamPlayers(Player[] moonTeamPlayers) {
        this.moonTeamPlayers = moonTeamPlayers;
    }

    public void connectPlayer(Player player) {
        connectedPlayers.add(player);
        player.setLobbyID(id);
    }

    public void disconnectPlayer(Player player) {
        connectedPlayers.remove(player);
        /*int position;
        if ((position = getSunTeamPosition(player)) != -1) {
            sunTeamPlayers[position] = null;
        } else if ((position = getMoonTeamPosition(player)) != -1) {
            moonTeamPlayers[position] = null;
        }*/
        player.setLobbyID(null);
    }

    public List<Player> getConnectedPlayers() {
        return connectedPlayers;
    }

    public void setConnectedPlayers(List<Player> connectedPlayers) {
        this.connectedPlayers = connectedPlayers;
    }

    public boolean isBattle() {
        return battle;
    }

    public void setBattle(boolean battle) {
        this.battle = battle;
    }
}
