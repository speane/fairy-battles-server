package com.speanegames.fairybattles.server.application;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.speanegames.fairybattles.server.authentication.AuthenticationController;
import com.speanegames.fairybattles.server.config.AppConfig;
import com.speanegames.fairybattles.server.lobby.Lobby;
import com.speanegames.fairybattles.server.player.Player;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.finish.BattleFinishedEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.hit.HitFortressEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.hit.HitHeroEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.kill.DestroyFortressEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.kill.KillHeroEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.kill.RespawnHeroEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.move.HeroMoveRequest;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.move.HeroMovedEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.shoot.HeroShootEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.shoot.HeroShootRequest;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.start.BattleStartedEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.start.StartBattleRequest;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.battle.start.StartBattleResponse;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.cleanplace.LobbySlotCleanedEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.connect.ConnectToLobbyRequest;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.connect.ConnectToLobbyResponse;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.create.CreateLobbyRequest;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.create.CreateLobbyResponse;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.dissolve.DissolveLobbyRequest;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.dissolve.LobbyDissolvedEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.jointeam.JoinTeamRequest;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.jointeam.JoinTeamResponse;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.jointeam.PlayerJoinedTeamEvent;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.leave.LeaveLobbyRequest;
import com.speanegames.fairybattles.server.transfers.transfers.lobby.leave.LeaveLobbyResponse;
import com.speanegames.fairybattles.server.transfers.transfers.signin.SignInRequest;
import com.speanegames.fairybattles.server.transfers.transfers.signin.SignInResponse;
import com.speanegames.fairybattles.server.transfers.transfers.signout.SignOutRequest;
import com.speanegames.fairybattles.server.transfers.transfers.signup.SignUpRequest;
import com.speanegames.fairybattles.server.transfers.transfers.signup.SignUpResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PlayServer {

    private HashMap<Integer, Player> players;
    private HashMap<String, Lobby> lobbies;

    private AuthenticationController authenticationController;

    public PlayServer() {
        players = new LinkedHashMap<>();
        lobbies = new LinkedHashMap<>();
        authenticationController = new AuthenticationController();
    }

    private Server server;

    public void start() throws IOException {
        initServer();
        registerTransferClasses();
        initListener();
    }

    private void initServer() throws IOException {
        server = new Server();
        server.start();
        server.bind(AppConfig.PLAY_PORT, AppConfig.PLAY_PORT + 1);
    }

    private void registerTransferClasses() {
        Kryo kryo = server.getKryo();

        kryo.register(SignInRequest.class);
        kryo.register(SignInResponse.class);
        kryo.register(SignUpRequest.class);
        kryo.register(SignUpResponse.class);
        kryo.register(CreateLobbyRequest.class);
        kryo.register(CreateLobbyResponse.class);
        kryo.register(ConnectToLobbyRequest.class);
        kryo.register(ConnectToLobbyResponse.class);
        kryo.register(JoinTeamRequest.class);
        kryo.register(JoinTeamResponse.class);
        kryo.register(DissolveLobbyRequest.class);
        kryo.register(LobbyDissolvedEvent.class);
        kryo.register(LeaveLobbyRequest.class);
        kryo.register(LeaveLobbyResponse.class);
        kryo.register(PlayerJoinedTeamEvent.class);
        kryo.register(LobbySlotCleanedEvent.class);
        kryo.register(StartBattleRequest.class);
        kryo.register(StartBattleResponse.class);
        kryo.register(BattleStartedEvent.class);
        kryo.register(HeroMoveRequest.class);
        kryo.register(HeroMovedEvent.class);
        kryo.register(HeroShootRequest.class);
        kryo.register(HeroShootEvent.class);
        kryo.register(HitHeroEvent.class);
        kryo.register(HitFortressEvent.class);
        kryo.register(KillHeroEvent.class);
        kryo.register(RespawnHeroEvent.class);
        kryo.register(DestroyFortressEvent.class);
        kryo.register(BattleFinishedEvent.class);
        kryo.register(SignOutRequest.class);
    }

    private void initListener() {
        server.addListener(new Listener() {

            @Override
            public void received(Connection c, Object o) {
                try {
                    if (o instanceof SignInRequest) {
                        signInRequestHandler(c, (SignInRequest) o);
                    } else if (o instanceof ConnectToLobbyRequest) {
                        connectToLobbyRequestHandler(c, (ConnectToLobbyRequest) o);
                    } else if (o instanceof CreateLobbyRequest) {
                        createLobbyRequestHandler(c, (CreateLobbyRequest) o);
                    } else if (o instanceof JoinTeamRequest) {
                        joinTeamRequestHandler(c, (JoinTeamRequest) o);
                    } else if (o instanceof DissolveLobbyRequest) {
                        dissolveLobbyRequestHandler(c, (DissolveLobbyRequest) o);
                    } else if (o instanceof LeaveLobbyRequest) {
                        leaveLobbyRequestHandler(c, (LeaveLobbyRequest) o);
                    } else if (o instanceof StartBattleRequest) {
                        startBattleRequestHandler(c, (StartBattleRequest) o);
                    } else if (o instanceof HeroMoveRequest) {
                        heroMoveRequestHandler(c, (HeroMoveRequest) o);
                    } else if (o instanceof HeroShootRequest) {
                        heroShootRequestHandler(c, (HeroShootRequest) o);
                    } else if (o instanceof HitHeroEvent) {
                        hitHeroEventHandler(c, (HitHeroEvent) o);
                    } else if (o instanceof HitFortressEvent) {
                        hitFortressEventHandler(c, (HitFortressEvent) o);
                    } else if (o instanceof KillHeroEvent) {
                        killHeroEventHandler(c, (KillHeroEvent) o);
                    } else if (o instanceof RespawnHeroEvent) {
                        respawnHeroEventHandler(c, (RespawnHeroEvent) o);
                    } else if (o instanceof DestroyFortressEvent) {
                        destroyFortressEventHandler(c, (DestroyFortressEvent) o);
                    } else if (o instanceof BattleFinishedEvent) {
                        finishBattleEventHandler(c, (BattleFinishedEvent) o  );
                    } else if (o instanceof SignOutRequest) {
                        signOutRequestHandler(c, (SignOutRequest) o);
                    } else if (o instanceof SignUpRequest) {
                        signUpRequestHandler(c, (SignUpRequest) o);
                    }
                } catch (Exception exception) {
                    System.err.println(exception.getMessage());
                }
            }

            @Override
            public void disconnected(Connection c) {
                if (players.containsKey(c.getID())) {
                    Player player = players.get(c.getID());
                    String lobbyID = player.getLobbyID();
                    Lobby lobby = lobbies.get(lobbyID);
                    if (lobby != null) {
                        if (lobby.getCreator() == player) {
                            LobbyDissolvedEvent lobbyDissolvedEvent = new LobbyDissolvedEvent();
                            for (Player connectedPlayer : lobby.getConnectedPlayers()) {
                                server.sendToTCP(connectedPlayer.getConnectionID(), lobbyDissolvedEvent);
                                connectedPlayer.setLobbyID(null);
                            }

                            lobbies.remove(lobbyID);
                        } else {
                            sendSlotCleanedEvents(lobby, player);
                        }
                    }
                }
            }
        });
    }

    private void signUpRequestHandler(Connection connection, SignUpRequest request) throws SQLException {
        SignUpResponse response = new SignUpResponse();

        if (!authenticationController.userExists(request.login)) {
            response.success = true;
            authenticationController.registerUser(request.login, request.password);
        } else {
            response.success = false;
            response.errorMessage = "Such username is already registered";
        }

        connection.sendTCP(response);
    }

    private void signInRequestHandler(Connection connection, SignInRequest request) throws SQLException {
        SignInResponse response = new SignInResponse();

        if (authenticationController.checkCredentials(request.login, request.password)) {
            Player player = new Player();

            player.setLogin(request.login);
            player.setConnectionID(connection.getID());

            players.put(connection.getID(), player);
            response.success = true;
            response.login = player.getLogin();
        } else {
            response.errorMessage = "Wrong password or login";
            response.success = false;
        }

        connection.sendTCP(response);
    }

    private void connectToLobbyRequestHandler(Connection connection, ConnectToLobbyRequest request) {
        Player player = players.get(connection.getID());
        ConnectToLobbyResponse response = new ConnectToLobbyResponse();

        if (lobbies.containsKey(request.lobbyId)) {
            Lobby lobby = lobbies.get(request.lobbyId);
            response.success = true;
            response.lobbyId = lobby.getId();
            lobby.connectPlayer(player);

            connection.sendTCP(response);

            for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
                if (lobby.getSunTeamPlayers()[i] != null) {
                    PlayerJoinedTeamEvent playerJoinedTeamEvent = new PlayerJoinedTeamEvent();
                    Player sunTeamPlayer = lobby.getSunTeamPlayers()[i];
                    playerJoinedTeamEvent.login = sunTeamPlayer.getLogin();
                    playerJoinedTeamEvent.team = "SUN";
                    playerJoinedTeamEvent.position = i;

                    connection.sendTCP(playerJoinedTeamEvent);
                }

                if (lobby.getMoonTeamPlayers()[i] != null) {
                    PlayerJoinedTeamEvent playerJoinedTeamEvent = new PlayerJoinedTeamEvent();
                    Player moonTeamPlayer = lobby.getMoonTeamPlayers()[i];
                    playerJoinedTeamEvent.login = moonTeamPlayer.getLogin();
                    playerJoinedTeamEvent.team = "MOON";
                    playerJoinedTeamEvent.position = i;

                    connection.sendTCP(playerJoinedTeamEvent);
                }
            }
        } else {
            response.success = false;
            response.errorMessage = "Lobby not found";

            connection.sendTCP(response);
        }
    }

    private void createLobbyRequestHandler(Connection connection, CreateLobbyRequest request) {
        Player creator = players.get(connection.getID());
        String lobbyId = RandomStringUtils.random(AppConfig.LOBBY_ID_LENGTH, false, true);
        Lobby lobby = new Lobby(lobbyId, creator);
        lobbies.put(lobbyId, lobby);

        CreateLobbyResponse response = new CreateLobbyResponse();
        response.success = true;
        response.lobbyId = lobbyId;

        creator.setLobbyID(lobbyId);

        connection.sendTCP(response);
    }

    private void joinTeamRequestHandler(Connection connection, JoinTeamRequest request) {
        JoinTeamResponse response = new JoinTeamResponse();

        Player player = players.get(connection.getID());
        String lobbyID = player.getLobbyID();
        Lobby lobby = lobbies.get(lobbyID);

        synchronized (lobbies.get(lobbyID)) {
            int sunTeamPosition = lobby.getSunTeamPosition(player);
            int moonTeamPosition = lobby.getMoonTeamPosition(player);

            if (StringUtils.equals(request.team, "SUN")) {
                if (sunTeamPosition == -1) {
                    if (lobby.canJoinSunTeam()) {

                        if (moonTeamPosition != -1) {
                            sendSlotCleanedEvents(lobby, player);
                        }

                        int position = lobby.joinSunTeam(player);
                        response.success = true;
                        response.team = "SUN";
                        response.position = position;

                        connection.sendTCP(response);

                        PlayerJoinedTeamEvent playerJoinedTeamEvent = new PlayerJoinedTeamEvent();
                        playerJoinedTeamEvent.login = player.getLogin();
                        playerJoinedTeamEvent.team = response.team;
                        playerJoinedTeamEvent.position = response.position;

                        for (Player connectedPlayer : lobby.getConnectedPlayers()) {
                            if (!connectedPlayer.getLogin().equals(player.getLogin())) {
                                server.sendToTCP(connectedPlayer.getConnectionID(), playerJoinedTeamEvent);
                            }
                        }
                        if (!lobby.getCreator().getLogin().equals(player.getLogin())) {
                            server.sendToTCP(lobby.getCreator().getConnectionID(), playerJoinedTeamEvent);
                        }
                    } else {
                        response.success = false;
                        response.errorMessage = "Sun team is already full";

                        connection.sendTCP(response);
                    }
                } else {
                    response.success = false;
                    response.errorMessage = "Already in sun team";

                    connection.sendTCP(response);
                }

            } else {
                if (moonTeamPosition == -1) {
                    if (lobby.canJoinMoonTeam()) {

                        if (sunTeamPosition != -1) {
                            sendSlotCleanedEvents(lobby, player);
                        }

                        int position = lobby.joinMoonTeam(player);
                        response.success = true;
                        response.team = "MOON";
                        response.position = position;

                        connection.sendTCP(response);

                        PlayerJoinedTeamEvent playerJoinedTeamEvent = new PlayerJoinedTeamEvent();
                        playerJoinedTeamEvent.login = player.getLogin();
                        playerJoinedTeamEvent.team = response.team;
                        playerJoinedTeamEvent.position = response.position;

                        for (Player connectedPlayer : lobby.getConnectedPlayers()) {
                            if (!connectedPlayer.getLogin().equals(player.getLogin())) {
                                server.sendToTCP(connectedPlayer.getConnectionID(), playerJoinedTeamEvent);
                            }
                        }
                        if (!lobby.getCreator().getLogin().equals(player.getLogin())) {
                            server.sendToTCP(lobby.getCreator().getConnectionID(), playerJoinedTeamEvent);
                        }
                    } else {
                        response.success = false;
                        response.errorMessage = "Moon team is already full";

                        connection.sendTCP(response);
                    }
                } else {
                    response.success = false;
                    response.errorMessage = "Already in moon team";

                    connection.sendTCP(response);
                }
            }
        }
    }

    private void dissolveLobbyRequestHandler(Connection connection, DissolveLobbyRequest request) {
        Player player = players.get(connection.getID());
        LobbyDissolvedEvent response = new LobbyDissolvedEvent();

        synchronized (lobbies.get(player.getLobbyID())) {
            String lobbyID = player.getLobbyID();
            Lobby lobby = lobbies.get(lobbyID);
            for (Player connectedPlayer : lobby.getConnectedPlayers()) {
                server.sendToTCP(connectedPlayer.getConnectionID(), response);
            }
            server.sendToTCP(lobby.getCreator().getConnectionID(), response);
            lobbies.remove(lobbyID);
        }
    }

    private void leaveLobbyRequestHandler(Connection connection, LeaveLobbyRequest request) {
        Player player = players.get(connection.getID());
        LeaveLobbyResponse response = new LeaveLobbyResponse();

        synchronized (lobbies.get(player.getLobbyID())) {
            Lobby lobby = lobbies.get(player.getLobbyID());
            lobby.disconnectPlayer(player);
            sendSlotCleanedEvents(lobby, player);
        }

        connection.sendTCP(response);
    }

    private void sendSlotCleanedEvents(Lobby lobby, Player player) {
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            Player sunTeamPlayer = lobby.getSunTeamPlayers()[i];
            if ((sunTeamPlayer != null) && (sunTeamPlayer == player)) {
                lobby.getSunTeamPlayers()[i] = null;

                LobbySlotCleanedEvent lobbySlotCleanedEvent = new LobbySlotCleanedEvent();
                lobbySlotCleanedEvent.team = "SUN";
                lobbySlotCleanedEvent.position = i;

                for (Player connectedPlayer : lobby.getConnectedPlayers()) {
                    server.sendToTCP(connectedPlayer.getConnectionID(), lobbySlotCleanedEvent);
                }

                server.sendToTCP(lobby.getCreator().getConnectionID(), lobbySlotCleanedEvent);

                break;
            }
            Player moonTeamPlayer = lobby.getMoonTeamPlayers()[i];
            if ((moonTeamPlayer != null) && (moonTeamPlayer == player)) {
                lobby.getMoonTeamPlayers()[i] = null;

                LobbySlotCleanedEvent lobbySlotCleanedEvent = new LobbySlotCleanedEvent();
                lobbySlotCleanedEvent.team = "MOON";
                lobbySlotCleanedEvent.position = i;

                for (Player connectedPlayer : lobby.getConnectedPlayers()) {
                    server.sendToTCP(connectedPlayer.getConnectionID(), lobbySlotCleanedEvent);
                }

                server.sendToTCP(lobby.getCreator().getConnectionID(), lobbySlotCleanedEvent);

                break;
            }
        }
    }

    private void startBattleRequestHandler(Connection connection, StartBattleRequest request) {
        Player player = players.get(connection.getID());
        Lobby lobby = lobbies.get(player.getLobbyID());
        StartBattleResponse response = new StartBattleResponse();
        response.success = true;
        connection.sendTCP(response);

        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            Player sunTeamPlayer = lobby.getSunTeamPlayers()[i];
            if (sunTeamPlayer != null) {
                BattleStartedEvent event = new BattleStartedEvent();
                event.position = i;
                event.team = "SUN";
                server.sendToTCP(sunTeamPlayer.getConnectionID(), event);
            }

            Player moonTeamPlayer = lobby.getMoonTeamPlayers()[i];
            if (moonTeamPlayer != null) {
                BattleStartedEvent event = new BattleStartedEvent();
                event.position = i;
                event.team = "MOON";
                server.sendToTCP(moonTeamPlayer.getConnectionID(), event);
            }
        }
    }

    private void heroMoveRequestHandler(Connection connection, HeroMoveRequest request) {
        Player player = players.get(connection.getID());
        Lobby lobby = lobbies.get(player.getLobbyID());
        HeroMovedEvent event = new HeroMovedEvent();
        event.x = request.x;
        event.y = request.y;
        event.rotation = request.rotation;
        int position;
        if ((position = lobby.getSunTeamPosition(player)) != -1) {
            event.team = "SUN";
            event.position = position;
        } else {
            event.team = "MOON";
            event.position = lobby.getMoonTeamPosition(player);
        }

        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (lobby.getSunTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getSunTeamPlayers()[i].getConnectionID(), event);
            }
            if (lobby.getMoonTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getMoonTeamPlayers()[i].getConnectionID(), event);
            }
        }

    }

    private void heroShootRequestHandler(Connection connection, HeroShootRequest request) {
        Player player = players.get(connection.getID());
        Lobby lobby = lobbies.get(player.getLobbyID());
        HeroShootEvent event = new HeroShootEvent();
        event.x = request.x;
        event.y = request.y;
        event.rotation = request.rotation;
        int position;
        if ((position = lobby.getSunTeamPosition(player)) != -1) {
            event.team = "SUN";
            event.position = position;
        } else {
            event.team = "MOON";
            event.position = lobby.getMoonTeamPosition(player);
        }

        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (lobby.getSunTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getSunTeamPlayers()[i].getConnectionID(), event);
            }
            if (lobby.getMoonTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getMoonTeamPlayers()[i].getConnectionID(), event);
            }
        }
    }

    private void hitHeroEventHandler(Connection connection, HitHeroEvent event) {
        Player player = players.get(connection.getID());
        Lobby lobby = lobbies.get(player.getLobbyID());
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (lobby.getSunTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getSunTeamPlayers()[i].getConnectionID(), event);
            }
            if (lobby.getMoonTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getMoonTeamPlayers()[i].getConnectionID(), event);
            }
        }
    }

    private void hitFortressEventHandler(Connection connection, HitFortressEvent event) {
        Player player = players.get(connection.getID());
        Lobby lobby = lobbies.get(player.getLobbyID());
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (lobby.getSunTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getSunTeamPlayers()[i].getConnectionID(), event);
            }
            if (lobby.getMoonTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getMoonTeamPlayers()[i].getConnectionID(), event);
            }
        }
    }

    private void killHeroEventHandler(Connection connection, KillHeroEvent event) {
        Player player = players.get(connection.getID());
        Lobby lobby = lobbies.get(player.getLobbyID());
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (lobby.getSunTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getSunTeamPlayers()[i].getConnectionID(), event);
            }
            if (lobby.getMoonTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getMoonTeamPlayers()[i].getConnectionID(), event);
            }
        }
    }

    private void respawnHeroEventHandler(Connection connection, RespawnHeroEvent event) {
        Player player = players.get(connection.getID());
        Lobby lobby = lobbies.get(player.getLobbyID());
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (lobby.getSunTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getSunTeamPlayers()[i].getConnectionID(), event);
            }
            if (lobby.getMoonTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getMoonTeamPlayers()[i].getConnectionID(), event);
            }
        }
    }

    private void destroyFortressEventHandler(Connection connection, DestroyFortressEvent event) {
        Player player = players.get(connection.getID());
        Lobby lobby = lobbies.get(player.getLobbyID());
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (lobby.getSunTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getSunTeamPlayers()[i].getConnectionID(), event);
            }
            if (lobby.getMoonTeamPlayers()[i] != null) {
                server.sendToTCP(lobby.getMoonTeamPlayers()[i].getConnectionID(), event);
            }
        }
    }

    private void finishBattleEventHandler(Connection connection, BattleFinishedEvent event) {
        Player player = players.get(connection.getID());
        Lobby lobby = lobbies.get(player.getLobbyID());
        for (Player connectedPlayer : lobby.getConnectedPlayers()) {
            server.sendToTCP(connectedPlayer.getConnectionID(), event);
        }
        server.sendToTCP(lobby.getCreator().getConnectionID(), event);
        lobby.setBattle(false);
    }

    private void signOutRequestHandler(Connection connection, SignOutRequest request) {
        if (players.containsKey(connection.getID())) {
            players.remove(connection.getID());
        }
    }
}
