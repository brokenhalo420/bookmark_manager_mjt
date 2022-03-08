package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class Server {
    private static final int PORT = 9090;

    public static void main(String[] args) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            Logger.log(e, "Server socket could not start");
            System.out.println("Server socket could not start");
            return;
        }
        Thread listener = new Thread(new Listener(server));
        listener.setName("Listener thread");
        listener.start();
        boolean running = true;

        while (running) {
            String s = null;
            try {
                s = new BufferedReader(new InputStreamReader(System.in)).readLine();
            } catch (IOException e) {
                Logger.log(e, "Could not read from keyboard");
                System.out.println("Could not read from keyboard");
                break;
            }

            if (s.equals("quit")) {
                running = false;
            }
        }
        listener.interrupt();
        System.exit(0);
    }
}
