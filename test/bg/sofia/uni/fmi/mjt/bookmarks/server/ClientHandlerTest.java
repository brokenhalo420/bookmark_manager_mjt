package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.server.resources.Responses;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(MockitoJUnitRunner.class)
class ClientHandlerTest {

    /***
     * Fields
     ***/
    private final Path logFile =
        Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "logs", "log.txt");
    private final Path testOutputFile =
        Paths.get("test", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "output.txt");
    private final Path usersFile =
        Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "resources", "users");

    /***
     * Mocks
     ***/
    @Mock
    private Socket socket;

    /***
     * Helper methods
     ***/
    @BeforeAll
    void preparations() {
        try {
            Files.deleteIfExists(testOutputFile);
            Files.deleteIfExists(logFile);
        } catch (IOException e) {
            System.out.println("Test could not delete log file and output test file");
        }
    }

    @AfterAll
    void cleanup() {
        try {
            Files.deleteIfExists(testOutputFile);
            Files.deleteIfExists(logFile);
        } catch (IOException e) {
            System.out.println("Test could not delete log file and output file");
        }
    }

    @BeforeEach
    void set() {
        deleteOutputFile();
        createOutputFile();
        deleteAllUserFiles();
    }

    void deleteOutputFile() {
        try {
            Files.deleteIfExists(testOutputFile);
        } catch (IOException e) {
            System.out.println("Test could not delete output file");
        }
    }

    void createOutputFile() {
        if (Files.notExists(testOutputFile)) {
            try {
                Files.createFile(testOutputFile);
            } catch (IOException e) {
                System.out.println("Could not create output file");
            }
        }
    }

    String executeTest(String input) {
        setInputOutputStreams(input);
        ClientHandler client = new ClientHandler(socket);
        client.run();
        return readFromOutputFile();
    }

    private void setInputOutputStreams(String input) {
        socket = Mockito.mock(Socket.class);
        InputStream in = new BufferedInputStream(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        BufferedOutputStream out;
        try {
            out = new BufferedOutputStream(new FileOutputStream(testOutputFile.toString()));
        } catch (IOException e) {
            System.out.println("Could not open stream");
            return;
        }
        try {
            Mockito.when(socket.getOutputStream()).thenReturn(out);
            Mockito.when(socket.getInputStream()).thenReturn(in);
            Mockito.when(socket.isClosed()).thenReturn(false);
        } catch (IOException e) {
            System.out.println("Could not set mock");
        }
    }

    String readFromOutputFile() {
        try {
            return Files.lines(testOutputFile).collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            return "";
        }
    }

    private void deleteAllUserFiles() {
        try {
            Files.newDirectoryStream(usersFile).forEach(s -> {
                try {
                    Files.deleteIfExists(s);
                } catch (IOException e) {
                    System.out.println("Cant delete user file");
                }
            });
        } catch (IOException e) {
            System.out.println("Cant delete user files;");
        }
    }

    /***
     * Tests
     ***/

    @Test
    void testLoginUnknownUser() {
        String output = executeTest(TestRequests.LOGIN_FROM_UNKNOWN_USER_REQUEST);
        String errorMessage = "Could not login user because they aren't registered";
        assert output.contains(errorMessage);
    }

    @Test
    void testLogoutUnlogged() {
        String output = executeTest(TestRequests.LOGOUT_UNLOGGED_REQUEST);
        String errorMessage = "Could not complete \"logout\". Not logged in. Please login and try again";
        assert output.contains(errorMessage);
    }

    @Test
    void testCreateGroupUnlogged() {
        String output = executeTest(TestRequests.CREATE_GROUP_UNLOGGED_REQUEST);
        String errorMessage =
            "Could not complete \"new-group <group-name>\". Not logged in. Please login and try again";
        assert output.contains(errorMessage);
    }

    @Test
    void testAddToGroupUnlogged() {
        String output = executeTest(TestRequests.ADD_TO_GROUP_UNLOGGED_REQUEST);
        String errorMessage =
            "Could not complete \"add-to <group-name> <bookmark>\". Not logged in. Please login and try again";
        assert output.contains(errorMessage);
    }

    @Test
    void testRemoveFromUnlogged() {
        String output = executeTest(TestRequests.REMOVE_FROM_GROUP_UNLOGGED_REQUEST);
        String errorMessage =
            "Could not complete \"remove-from <group-name>\". Not logged in. Please login and try again";
        assert output.contains(errorMessage);
    }

    @Test
    void testListUnlogged() {
        String output = executeTest(TestRequests.LIST_ALL_AND_GROUP_UNLOGGED_REQUEST);
        String errorMessageList =
            "Could not complete \"list\". Not logged in. Please login and try again";
        String errorMessageListGroup =
            "Could not complete \"list --group-name <group-name>\". Not logged in. Please login and try again";
        assert output.contains(errorMessageList) && output.contains(errorMessageListGroup);
    }

    @Test
    void testSearchUnlogged() {
        String output = executeTest(TestRequests.SEARCH_UNLOGGED_REQUEST);
        String errorMessageTitle =
            "Could not complete \"search --title <title>\". Not logged in. Please login and try again";
        String errorMessageTags =
            "Could not complete \"search --tags <tag> [<tag>...]\". Not logged in. Please login and try again";
        assert output.contains(errorMessageTitle) && output.contains(errorMessageTags);
    }

    @Test
    void testCleanupUnlogged() {
        String output = executeTest(TestRequests.CLEANUP_UNLOGGED_REQUEST);
        String errorMessage = "Could not complete \"cleanup\". Not logged in. Please login and try again";
        assert output.contains(errorMessage);
    }

    @Test
    void testImportUnlogged() {
        String output = executeTest(TestRequests.IMPORT_FROM_CHROME_UNLOGGED_REQUEST);
        String errorMessage = "Could not complete \"import-from-chrome\". Not logged in. Please login and try again";
        assert output.contains(errorMessage);
    }

    @Test
    void testQuit() {
        String output = executeTest(TestRequests.QUIT_REQUEST);
        String disconnectCode = String.valueOf(Responses.DISCONNECT.get());
        assert output.contains(disconnectCode);
    }

    @Test
    void testRegisterFirstTime() {
        String output = executeTest(TestRequests.REGISTER_REQUEST);
        String errorMessage = "User administrator has been registered successfully";
        assert output.contains(errorMessage);
    }

    @Test
    void testRegisterSameUser() {
        String output = executeTest(TestRequests.REGISTER_SAME_USER_REQUEST);
        String errorMessage = "Could not register user because they are already registered";
        assert output.contains(errorMessage);
    }

    @Test
    void testLogin() {
        String output = executeTest(TestRequests.LOGIN_REQUEST);
        String errorMessage = "Successfully logged in";
        assert output.contains(errorMessage);
    }

    @Test
    void testRegisterWhenLoggedIn() {
        String output = executeTest(TestRequests.LOGGED_IN_REGISTER_REQUEST);
        String errorMessage = "Successfully logged in";
        assert output.contains(errorMessage);
    }

    @Test
    void testLoginWhenLoggedIn() {
        String output = executeTest(TestRequests.LOGIN_WHEN_LOGGED_REQUEST);
        String errorMessage = "Could not complete login <username> <password>";
        assert output.contains(errorMessage);
    }

    @Test
    void testCreateGroup() {
        String output = executeTest(TestRequests.CREATE_GROUP_REQUEST);
        String errorMessage = "Group created successfully";
        assert output.contains(errorMessage);
    }

    @Test
    void testAddToGroup() {
        String output = executeTest(TestRequests.ADD_TO_GROUP_REQUEST);
        String errorMessage = "0" + System.lineSeparator() + "Bookmark successfully added to group";
        assert output.contains(errorMessage + System.lineSeparator() + errorMessage);
    }

    @Test
    void testRemoveFromGroup() {
        String output = executeTest(TestRequests.REMOVE_FROM_GROUP_REQUEST);
        String errorMessage = "Bookmark removed successfully";
        assert output.contains(errorMessage);
    }

    @Test
    void testList() {
        String output = executeTest(TestRequests.LIST_ALL_AND_GROUP_REQUEST);
        String errorMessage = "0" + System.lineSeparator() + "[Facebook | https://facebook.com/]";
        assert output.contains(errorMessage + System.lineSeparator() + errorMessage);
    }

    @Test
    void testSearchByTitle() {
        String output = executeTest(TestRequests.SEARCH_BY_TITLE_REQUEST);
        String errorMessage = "[Facebook | https://facebook.com/]";
        assert output.contains(errorMessage);
    }

    @Test
    void testSearchByTags() {
        String output = executeTest(TestRequests.SEARCH_BY_TAGS_REQUEST);
        String errorMessage = "[Facebook | https://facebook.com/]";
        assert output.contains(errorMessage);
    }

    @Test
    void testCleanup() {
        String output = executeTest(TestRequests.CLEANUP_REQUEST);
        String errorMessage = "Successfully cleaned up invalid links";
        assert output.contains(errorMessage);
    }

    @Test
    void testImportFromChrome() {
        String output = executeTest(TestRequests.IMPORT_FROM_CHROME_REQUEST);
        String errorMessage = "[]";
        assert !output.contains(errorMessage);
    }
}