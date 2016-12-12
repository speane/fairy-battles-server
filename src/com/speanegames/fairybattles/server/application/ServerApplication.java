package com.speanegames.fairybattles.server.application;

public class ServerApplication {

    public void start() {
        new Thread(() -> {
            try {
                new PlayServer().start();
            } catch (Exception ex) {
                // TODO log exception
            }
        }).start();
    }
}
