package bg.sofia.uni.fmi.mjt.bookmarks.client;

import bg.sofia.uni.fmi.mjt.bookmarks.client.resources.Operations;
import bg.sofia.uni.fmi.mjt.bookmarks.client.resources.Responses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

public class Client {
    public static final String IP = "localhost";
    public static final int PORT = 9090;

    public static void main(String[] args) {
        Logger.log(new IllegalArgumentException("test"), "test");
        try (Socket client = new Socket(IP, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true);
             BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Connected to server...");
            while (true) {
                if (client.getInputStream() == null) {
                    break;
                }

                String command = null;
                int responseCode = -1;
                String response = null;

                try {
                    command = keyboard.readLine();
                } catch (IOException e) {
                    Logger.log(e, "Could not read from keyboard");
                    System.out.println("Cannot read from input.");
                    System.out.println("Press key to continue");
                    terminate(client, in, out, keyboard);
                }

                int commandCode = getCommand(command).get();

                if (commandCode == Operations.NONE.get()) {
                    System.out.println(
                        "Invalid command! Please enter a valid command or type \"help\" for a list of all commands.");
                } else if (commandCode == Operations.HELP.get()) {
                    help();
                } else if (commandCode == Operations.QUIT.get()) {
                    out.println(commandCode);
                    terminate(client, in, out, keyboard);
                    System.out.println("Client has disconnected");
                } else {
                    out.println(commandCode);
                    out.println(command);
                    try {
                        responseCode = Integer.parseInt(in.readLine());
                        if (responseCode == Responses.SUCCESS.get()) {
                            response = in.readLine();
                            System.out.println(response);
                        }

                        if (responseCode == Responses.FAIL.get()) {
                            response = in.readLine();
                            System.out.println(response);
                        }

                        if (responseCode == Responses.DISCONNECT.get()) {
                            System.out.println("Client has been disconnected");
                            System.out.println("Press key to continue...");
                            terminate(client, in, out, keyboard);
                        }
                    } catch (IOException e) {
                        System.out.println("Could not receive information from server");
                        System.out.println("Press key to continue...");
                        terminate(client, in, out, keyboard);
                    }

                }
            }
            terminate(client, in, out, keyboard);

        } catch (IOException e) {
            Logger.log(e, "Socket couldn't connect to server");
            System.out.println("Could not connect to server. Please try again later.");
            System.exit(0);
        }
    }

    private static Operations getCommand(String input) {
        if (input == null || input.isEmpty()) {
            return Operations.NONE;
        }

        if (input.matches("^help$")) {
            return Operations.HELP;
        }

        if (input.matches("^register [a-zA-Z0-9]{6,20} [a-zA-Z0-9]{6,20}$")) {
            return Operations.REGISTER;
        }

        if (input.matches("login [a-zA-Z0-9]{6,20} [a-zA-Z0-9]{6,20}$")) {
            return Operations.LOGIN;
        }
        if (input.matches("^logout$")) {
            return Operations.LOGOUT;
        }

        if (input.matches("^new-group [a-zA-Z0-9_-]{6,20}$")) {
            return Operations.CREATE_GROUP;
        }

        if (input.matches("^add-to --shorten [a-zA-Z0-9_-]{6,20} [^ \t]+$")) {
            return Operations.ADD_TO_GROUP_SHORT;
        }

        if (input.matches("^add-to [a-zA-Z0-9_-]{6,20} [^ \t]+$")) {
            return Operations.ADD_TO_GROUP;
        }

        if (input.matches("^remove-from [a-zA-Z0-9_-]{6,20} [^ \t]+$")) {
            return Operations.REMOVE_FROM;
        }

        if (input.matches("^list$")) {
            return Operations.LIST;
        }

        if (input.matches("^list --group-name [a-zA-Z0-9-_]{6,20}$")) {
            return Operations.LIST_GROUP;
        }

        if (input.matches("^search --title .+$")) {
            return Operations.SEARCH_BY_TITLE;
        }

        if (input.matches("^search --tags (.+)+$")) {
            return Operations.SEARCH_BY_TAGS;
        }

        if (input.matches("^cleanup$")) {
            return Operations.CLEANUP;
        }

        if (input.matches("^import-from-chrome$")) {
            return Operations.IMPORT_FROM_CHROME;
        }

        if (input.matches("^quit$")) {
            return Operations.QUIT;
        }

        return Operations.NONE;
    }

    private static void help() {
        String[] commandList =
            {"register <username> <password>", "login <username> <password>", "logout", "new-group <group-name>",
                "add-to {--shorten} <group-name> <bookmark>", "remove-from <group-name> <bookmark>", "list",
                "list --group-name <group-name>", "search --title <title>", "search --tags <tag> [<tag>,...]",
                "cleanup", "import-from-chrome", "clear"};

        String output = String.join(System.lineSeparator(), commandList);
        System.out.println(output);
    }

    private static void terminate(Socket client, Reader in, Writer out, Reader keyboard) {
        try {
            in.close();
            out.close();
            client.close();
            keyboard.close();
        } catch (IOException e) {
            Logger.log(e, "Cannot close communication channels. They are already closed");
        }
        System.exit(0);
    }
}
