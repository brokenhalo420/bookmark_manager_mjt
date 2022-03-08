package bg.sofia.uni.fmi.mjt.bookmarks.server.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Logger {

    private static final Path PATH_TO_FILE =
        Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "logs", "log.txt");
    private static final Object IS_BEING_USED = new Object();

    public static void log(Exception e, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PATH_TO_FILE.toString(), true))) {

            synchronized (IS_BEING_USED) {
                writer.write(message + ": ");
                writer.flush();
                e.printStackTrace(writer);
            }
        } catch (IOException ex) {
            System.out.println("Logger has encountered an error");
            System.exit(0);
        }
    }
}
