package com.nazmen_tech.littlechat;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private ChatServer(String... args) {

        assert args.length >= 1;
        int portToListening = getPortToListening(args);

        try (
                ServerSocket serverSocket = new ServerSocket(portToListening);) {

            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();

                ClientHandler cHandler = new ClientHandler(socket);

                Thread thread = new Thread(cHandler);
                thread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getPortToListening(String[] args) {

        return Integer.parseInt(args[0]);
    }

    public static void main(String[] args) {
        new ChatServer(args);
    }
}
