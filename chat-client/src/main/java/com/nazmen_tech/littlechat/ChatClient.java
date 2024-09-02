package com.nazmen_tech.littlechat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient implements Runnable {

    private Socket socket;
    private BufferedReader socketIn;
    private BufferedWriter socketOut;
    private String username;

    public ChatClient(String host, int port) throws UnknownHostException, IOException {

        performActionCatchingIOException(() -> {

            this.socket = new Socket(host, port);
            this.socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            sendUsernameInfoToServer();
            new Thread(this).start();
            startChatting();
        });
    }

    private void sendUsernameInfoToServer() throws IOException {

        Console console = System.console();
        username = console.readLine("Enter Username: ", username);
        writeMessage(username);
    }

    private void startChatting() throws IOException {

        Console console = System.console();
        while (!socket.isClosed()) {

            String message = console.readLine("%s: ", username);
            writeMessage(String.format("%s: %s", username, message));
        }
    }

    private void writeMessage(String message) throws IOException {

        this.socketOut.write(message);
        this.socketOut.newLine();
        this.socketOut.flush();

    }

    @Override
    public void run() {

        performActionCatchingIOException(() -> {

            while (!socket.isClosed()) {

                String message = socketIn.readLine();

                System.out.print("\033[2K\r"); // Clear the line
                System.out.println(message);
                System.out.printf("%s: ", username);
                System.out.flush();
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

    public static void main(String[] args) throws UnknownHostException, IOException {

        assert args.length >= 2;

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        new ChatClient(host, port);
    }
}
