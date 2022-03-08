package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.client.resources.Operations;
import bg.sofia.uni.fmi.mjt.bookmarks.client.resources.Responses;
import bg.sofia.uni.fmi.mjt.bookmarks.server.components.bookmark.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.components.user.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.FailedUserLoadException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.shortener.UrlShortener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private User user;
    private final Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isLoggedIn;

    public ClientHandler(Socket socket) {
        this.client = socket;
        user = null;
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            Logger.log(e, "ClientHandler couldn't open communication channels with client");
            System.out.println("Error! Could not connect to client properly");
            try {
                client.close();
            } catch (IOException ex) {
                Logger.log(e, "Could not close connection with client. It is already closed");
                System.out.println("Client already logged out");
            }
        }
        isLoggedIn = false;
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            int commandCode = -1;
            String command = "";
            try {
                commandCode = Integer.parseInt(in.readLine());
            } catch (IOException e) {
                Logger.log(e, "Could not read from client");
                System.out.println("Could not read from client");
                break;
            }

            switch (getCommand(commandCode)) {
                case REGISTER -> register();
                case LOGIN -> login();
                case LOGOUT -> logout();
                case CREATE_GROUP -> createGroup();
                case ADD_TO_GROUP -> addToGroup(false);
                case ADD_TO_GROUP_SHORT -> addToGroup(true);
                case REMOVE_FROM -> removeFrom();
                case LIST -> list(false);
                case LIST_GROUP -> list(true);
                case SEARCH_BY_TITLE -> searchByTitle();
                case SEARCH_BY_TAGS -> searchByTags();
                case CLEANUP -> cleanup();
                case IMPORT_FROM_CHROME -> importFromChrome();
                case QUIT -> {
                    running = false;
                }
                default -> System.out.println("Invalid command received");
            }
        }
        try {
            out.println(Responses.DISCONNECT.get());
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            Logger.log(e, "Could not end connection with client. It is already closed");
            System.out.println("Problem ending connection with client");
        }
        terminate();
        System.out.println("Client disconnected...");
    }

    private Operations getCommand(int commandCode) {
        if (commandCode == Operations.REGISTER.get()) {
            return Operations.REGISTER;
        }
        if (commandCode == Operations.LOGIN.get()) {
            return Operations.LOGIN;
        }
        if (commandCode == Operations.LOGOUT.get()) {
            return Operations.LOGOUT;
        }
        if (commandCode == Operations.CREATE_GROUP.get()) {
            return Operations.CREATE_GROUP;
        }
        if (commandCode == Operations.ADD_TO_GROUP.get()) {
            return Operations.ADD_TO_GROUP;
        }
        if (commandCode == Operations.ADD_TO_GROUP_SHORT.get()) {
            return Operations.ADD_TO_GROUP_SHORT;
        }
        if (commandCode == Operations.REMOVE_FROM.get()) {
            return Operations.REMOVE_FROM;
        }
        if (commandCode == Operations.LIST.get()) {
            return Operations.LIST;
        }
        if (commandCode == Operations.LIST_GROUP.get()) {
            return Operations.LIST_GROUP;
        }
        if (commandCode == Operations.SEARCH_BY_TITLE.get()) {
            return Operations.SEARCH_BY_TITLE;
        }
        if (commandCode == Operations.SEARCH_BY_TAGS.get()) {
            return Operations.SEARCH_BY_TAGS;
        }
        if (commandCode == Operations.CLEANUP.get()) {
            return Operations.CLEANUP;
        }
        if (commandCode == Operations.IMPORT_FROM_CHROME.get()) {
            return Operations.IMPORT_FROM_CHROME;
        }
        if (commandCode == Operations.QUIT.get()) {
            return Operations.QUIT;
        }
        return Operations.NONE;
    }

    private void register() {
        String command = readLineFromClient("register <username> <password>");
        if (command == null) {
            return;
        }

        String username = command.split(" ")[1];
        String password = Base64.getEncoder().encodeToString(command.split(" ")[2].getBytes(StandardCharsets.UTF_8));

        if (userFileExists(username)) {
            out.println(Responses.FAIL.get());
            out.println("Could not register user because they are already registered");
            return;
        }


        if (!createUserFile(username)) {
            out.println(Responses.FAIL.get());
            out.println("Unable to register user. Try again later.");
            return;
        }
        user = new User(username, password);
        if (!writeUserToFile(username)) {
            deleteUserFile(username);
            out.println(Responses.FAIL.get());
            out.println("Unable to register user. Try again later.");
            return;
        }
        user = null;
        out.println(Responses.SUCCESS.get());
        out.println(String.format("User %s has been registered successfully", username));
    }

    private void login() {
        String command = readLineFromClient("login <username> <password>");
        if (command == null) {
            return;
        }

        String username = command.split(" ")[1];
        String password = Base64.getEncoder().encodeToString(command.split(" ")[2].getBytes(StandardCharsets.UTF_8));

        if (!userFileExists(username)) {
            out.println(Responses.FAIL.get());
            out.println("Could not login user because they aren't registered");
            return;
        }

        user = readUserFromFile(username);

        if (user == null) {
            Logger.log(new FailedUserLoadException(username + " couldn't be loaded"),
                "Could not load user from user file");
            out.println(Responses.FAIL.get());
            out.println("Could not login. No such user exists");
            return;
        }

        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
            isLoggedIn = true;
            out.println(Responses.SUCCESS.get());
            out.println("Successfully logged in");
            return;
        }

        isLoggedIn = false;
        user = null;
        out.println(Responses.FAIL.get());
        out.println("Could not login. Username or password incorrect");
    }

    private void logout() {
        String command = readLineFromClient("logout");
        if (command == null) {
            return;
        }

        if (!writeUserToFile(user.getUsername())) {
            out.println(Responses.FAIL.get());
            out.println("Problem logging out");
            return;
        }
        user = null;
        isLoggedIn = false;
        out.println(Responses.SUCCESS.get());
        out.println("Successfully logged out");
    }

    private void createGroup() {
        String command = readLineFromClient("new-group <group-name>");
        if (command == null) {
            return;
        }

        String groupName = command.split(" ")[1];
        String message = user.createGroup(groupName);
        if (message.equals("Group created successfully")) {
            out.println(Responses.SUCCESS.get());
            out.println(message);
            return;
        }

        out.println(Responses.FAIL.get());
        out.println(message);

    }

    private void addToGroup(boolean shorten) {
        String command =
            readLineFromClient(shorten ? "add-to --shorten <group-name> <bookmark>" : "add-to <group-name> <bookmark>");
        if (command == null) {
            return;
        }

        String bookmarkUrl = "";
        String url;
        String groupName;
        if (shorten) {
            final int urlIndex = 3;
            groupName = command.split(" ")[2];
            url = command.split(" ")[urlIndex];
            bookmarkUrl = UrlShortener.shorten(url);
        } else {
            groupName = command.split(" ")[1];
            url = command.split(" ")[2];
            bookmarkUrl = url;
        }

        String success = user.addBookmark(groupName, bookmarkUrl);
        if (success.equals("Bookmark successfully added to group")) {
            out.println(Responses.SUCCESS.get());
        } else {
            out.println(Responses.FAIL.get());
        }
        out.println(success);
    }

    private void removeFrom() {
        String command = readLineFromClient("remove-from <group-name>");
        if (command == null) {
            return;
        }

        String groupName = command.split(" ")[1];
        String bookmark = command.split(" ")[2];

        String success = user.removeBookmark(groupName, bookmark);

        if (success.equals("Bookmark removed successfully")) {
            out.println(Responses.SUCCESS.get());
        } else {
            out.println(Responses.FAIL.get());
        }
        out.println(success);

    }

    private void list(boolean byGroup) {
        String command = readLineFromClient(byGroup ? "list --group-name <group-name>" : "list");
        if (command == null) {
            return;
        }


        if (byGroup) {
            String groupName = command.split(" ")[2];
            Set<Bookmark> allBookmarks = user.getAllBookmarks(groupName);
            if (allBookmarks == null) {
                out.println(Responses.FAIL.get());
                out.println("No such group exists");
            } else {
                out.println(Responses.SUCCESS.get());
                out.println(allBookmarks.toString());
            }
        } else {
            Set<Bookmark> allBookmarks = user.getAllBookmarks();
            if (allBookmarks == null) {
                out.println(Responses.FAIL.get());
                out.println("No groups exist");
            } else {
                out.println(Responses.SUCCESS.get());
                out.println(allBookmarks.toString());
            }
        }
    }

    private void searchByTitle() {
        String command = readLineFromClient("search --title <title>");
        if (command == null) {
            return;
        }

        String title = Arrays.stream(command.split(" ")).skip(2).collect(Collectors.joining(" "));
        Set<Bookmark> bookmarks = user.findBookmarksByTitle(title);
        out.println(Responses.SUCCESS.get());
        out.println(bookmarks.toString());
    }

    private void searchByTags() {
        String command = readLineFromClient("search --tags <tag> [<tag>...]");
        if (command == null) {
            return;
        }

        String[] tags = Arrays.stream(command.split(" ")).skip(2).toArray(String[]::new);
        Set<Bookmark> bookmarks = user.findBookmarksByTags(tags);
        out.println(Responses.SUCCESS.get());
        out.println(bookmarks.toString());
    }

    private void cleanup() {
        String command = readLineFromClient("cleanup");
        if (command == null) {
            return;
        }

        user.cleanup();
        out.println(Responses.SUCCESS.get());
        out.println("Successfully cleaned up invalid links");
    }

    private void importFromChrome() {
        String command = readLineFromClient("import-from-chrome");
        if (command == null) {
            return;
        }

        boolean success = user.importFromChrome();
        if (success) {
            out.println(Responses.SUCCESS.get());
            out.println("Successfully imported bookmarks from chrome");
        } else {
            out.println(Responses.FAIL.get());
            out.println("Could not import bookmarks from chrome");
        }
    }

    private void terminate() {

        if (!isLoggedIn()) {
            return;
        }

        if (!createUserFile(user.getUsername())) {
            return;
        }

        writeUserToFile(user.getUsername());
        user = null;
        isLoggedIn = false;

    }

    private boolean isLoggedIn() {
        return isLoggedIn && user != null;
    }

    private boolean isClientClosed() {
        return (in == null || out == null || client.isClosed());
    }

    private boolean createUserFile(String username) {
        Path userFile =
            Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "resources", "users",
                username + ".json");
        if (Files.notExists(userFile)) {
            try {
                Files.createFile(userFile);
            } catch (IOException e) {
                Logger.log(e, "Could not create user file for user " + username);
                System.out.println("Could not create user file for user " + username);
                return false;
            }
        }
        return true;
    }

    private boolean deleteUserFile(String username) {
        Path userFile =
            Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "resources", "users",
                username + ".json");

        try {
            Files.delete(userFile);
        } catch (IOException e) {
            Logger.log(e, "Unable to delete user file");
            System.out.println("Unable to delete user file for: " + username);
            return false;
        }
        return true;
    }

    private boolean userFileExists(String username) {
        Path userFile =
            Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "resources", "users",
                username + ".json");
        return Files.exists(userFile);
    }

    private String readLineFromClient(String operation) {
        if (isClientClosed()) {
            return null;
        }
        String command;
        try {
            command = in.readLine();
        } catch (IOException e) {
            Logger.log(e, "Could not read message from client");
            return null;
        }

        if ((operation.contains("login") || operation.contains("register")) && isLoggedIn()) {
            out.println(Responses.FAIL.get());
            out.println("Could not complete " + operation);
            return null;
        }

        if (!(operation.contains("login") || operation.contains("register")) && !isLoggedIn()) {
            out.println(Responses.FAIL.get());
            out.println("Could not complete \"" + operation + "\". Not logged in. Please login and try again");
            return null;
        }

        return command;
    }

    private boolean writeUserToFile(String username) {
        Path userFile =
            Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "resources", "users",
                username + ".json");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile.toString()))) {
            writer.write(new Gson().toJson(user));
            writer.flush();
        } catch (IOException e) {
            Logger.log(e, "Could not open user file for user" + user.getUsername());
            return false;
        }

        return true;
    }

    private User readUserFromFile(String username) {
        Path userFile =
            Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "resources", "users",
                username + ".json");

        Gson gson = new Gson();
        User result = null;
        try (BufferedReader in = new BufferedReader(new FileReader(userFile.toString()))) {
            result = gson.fromJson(in, User.class);
        } catch (IOException e) {
            Logger.log(e, "Could not open user file for user" + username);
            return null;
        }

        return result;
    }
}
