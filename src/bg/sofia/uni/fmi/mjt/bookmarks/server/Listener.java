package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.server.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Listener implements Runnable {
    private final ServerSocket listener;
    private final int threadNumber = 10;
    private final ExecutorService pool = Executors.newFixedThreadPool(threadNumber);

    public Listener(ServerSocket socket) {
        this.listener = socket;
    }

    @Override
    public void run() {
        System.out.println("Server started. Listening for connections...");
        while (!Thread.interrupted()) {
            Socket client = null;
            try {
                client = listener.accept();
            } catch (IOException e) {
                Logger.log(e, "Listener could not accept connection");
                System.out.println("Could not accept connection. Shutting down");
            }
            System.out.println("Connection accepted...");
            ClientHandler cHandler = new ClientHandler(client);
            pool.execute(cHandler);
        }

        try {
            listener.close();
        } catch (IOException e) {
            Logger.log(e, "Listener could not terminate server socket");
            System.out.println("Listener could not terminate server socket");
        }
        pool.shutdown();
    }
}
