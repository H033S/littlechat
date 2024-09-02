package com.nazmen_tech.littlechat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private static final ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    private Socket socket;
    private BufferedReader socketIn;
    private BufferedWriter socketOut;
    private String username;

    public ClientHandler(Socket socket) {
        performActionCatchingIOException(() -> {

            this.socket = socket;
            this.socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = socketIn.readLine();
            clients.add(this);

            notifyRoom(String.format("%s just entered to room!", username));
        });
    }

    @Override
    public void run() {

        performActionCatchingIOException(() -> {

            while (!socket.isClosed()) {

                var message = socketIn.readLine();
                System.out.println(message);
                notifyRoom(message);
            }
        });
    }

    private void notifyRoom(String message) {

        performActionCatchingIOException(() -> {

            for (ClientHandler client : clients) {

                if (!client.getSocket().isClosed() &&
                    !client.getUsername().equals(username)) {

                    client.getSocketOut().write(message);
                    client.getSocketOut().newLine();
                    client.getSocketOut().flush();
                }
            }
        });
    }

    private void performActionCatchingIOException(ClientAction action) {

        try {

            action.perform();

        } catch (IOException | NullPointerException e) {
            try {

                if (socket != null) {
                    socket.close();
                }
                if (socketIn != null) {
                    socketIn.close();
                }
                if (socketOut != null) {
                    socketOut.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getSocketIn() {
        return socketIn;
    }

    public BufferedWriter getSocketOut() {
        return socketOut;
    }

    public String getUsername() {
        return username;
    }
}
