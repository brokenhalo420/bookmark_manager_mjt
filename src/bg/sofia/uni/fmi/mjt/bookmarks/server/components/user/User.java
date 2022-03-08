package bg.sofia.uni.fmi.mjt.bookmarks.server.components.user;

import bg.sofia.uni.fmi.mjt.bookmarks.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.components.bookmark.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.components.group.Group;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class User {
    private final String username;
    private final String password;
    private final Set<Group> groups;
    private final Map<String, Set<Bookmark>> keywords;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        groups = new HashSet<>();
        keywords = new HashMap<>();
    }

    public String createGroup(String title) {
        if (title == null || title.isEmpty()) {
            RuntimeException thrown = new IllegalArgumentException("Could not create group. Title is null!");
            Logger.log(thrown, "Group class found and error in method createGroup");
            return "Invalid group title";
        }

        if (groups.stream().anyMatch(s -> s.getTitle().equals(title))) {
            return "Could not create group. It already exists";
        }

        groups.add(new Group(title));
        return "Group created successfully";
    }

    public String addBookmark(String groupName, String url) {
        if (groupName == null || groupName.isEmpty()) {
            RuntimeException thrown = new IllegalArgumentException("Could not add bookmark. Group name is null!");
            Logger.log(thrown, "Group class found and error in method addBookmark");
            return "Could not add bookmark. Group name is null or empty.";
        }

        if (url == null || url.isEmpty()) {
            RuntimeException thrown = new IllegalArgumentException("Could not add bookmark. URL provided is null!");
            Logger.log(thrown, "Group class found and error in method addBookmark");
            return "Could not add bookmark. Url is null or empty.";
        }

        if (groups.stream().noneMatch(s -> s.getTitle().equals(groupName))) {
            return "Could not add bookmark. No such group exists.";
        }

        Bookmark item = new Bookmark(url);
        groups.forEach(s -> {
            if (s.getTitle().equals(groupName)) {
                s.add(item);
                return;
            }
        });

        item.getKeywords().forEach(s -> {
            keywords.put(s, keywords.getOrDefault(s, new HashSet<>()));
            keywords.get(s).add(item);
        });

        return "Bookmark successfully added to group";
    }

    public String removeBookmark(String groupName, String url) {
        if (groupName == null || groupName.isEmpty()) {
            RuntimeException thrown = new IllegalArgumentException("Could not remove bookmark. Group name is null");
            Logger.log(thrown, "Group class found and error in method removeBookmark");
            return "Could not remove bookmark. Group name is null or empty";
        }

        if (url == null || url.isEmpty()) {
            RuntimeException thrown = new IllegalArgumentException("Could not remove bookmark. URL provided is null!");
            Logger.log(thrown, "Group class found and error in method removeBookmark");
            return "Could not remove bookmark. URL is null or empty";
        }

        if (groups.stream().noneMatch(s -> s.getTitle().equals(groupName))) {
            return "Could not remove bookmark. No such group exists.";
        }

        for (Group g : groups) {
            if (g.getTitle().equals(groupName)) {
                Bookmark item = g.remove(url);

                if (item == null) {
                    return "Cannot remove bookmark. No such bookmark exists.";
                } else {
                    item.getKeywords().forEach(keyword -> {
                        keywords.get(keyword).removeIf(s -> s.getUrl().equals(url));
                    });
                    break;
                }

            }
        }
        return "Bookmark removed successfully";
    }

    public Set<Bookmark> getAllBookmarks(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            RuntimeException thrown =
                new IllegalArgumentException("Could not retrieve all bookmarks. Group name is null!");
            Logger.log(thrown, "Group class found and error in method getAllBookmarks");
            return null;
        }

        if (groups.stream().noneMatch(s -> s.getTitle().equals(groupName))) {
            return null;
        }

        Set<Bookmark> result = null;

        for (Group g : groups) {
            if (g.getTitle().equals(groupName)) {
                result = g.getBookmarks();
            }
        }

        return result;
    }

    public Set<Bookmark> getAllBookmarks() {
        if (groups.size() == 0) {
            return null;
        }

        Set<Bookmark> result = new HashSet<>();
        for (Group g : groups) {
            result.addAll(g.getBookmarks());
        }

        return result;
    }

    public Set<Bookmark> findBookmarksByTitle(String title) {
        if (title == null || title.isEmpty()) {
            RuntimeException thrown = new IllegalArgumentException("Could not find bookmarks by title. Title is null!");
            Logger.log(thrown, "Group class found and error in method findBookmarksByTitle");
            return new HashSet<>();
        }

        Set<Bookmark> result = new HashSet<>();
        for (Group g : groups) {
            result.addAll(
                g.getBookmarks().stream().takeWhile(s -> s.getTitle().equals(title)).collect(Collectors.toSet()));
        }

        return result;
    }

    public Set<Bookmark> findBookmarksByTags(String... tags) {
        if (tags == null || tags.length == 0) {
            RuntimeException thrown =
                new IllegalArgumentException("Could not find bookmarks by tags. Tags is null or no tags are provided!");
            Logger.log(thrown, "Group class found and error in method findBookmarksByTags");
            return new HashSet<>();
        }

        if (Arrays.stream(tags).anyMatch(String::isEmpty)) {
            RuntimeException thrown =
                new IllegalArgumentException("Could not find bookmarks by tags. Tags contains and empty tag!");
            Logger.log(thrown, "Group class found and error in method findBookmarksByTags");
            return new HashSet<>();
        }

        Map<Bookmark, Integer> bookmarksByTag = new HashMap<>();

        Arrays.stream(tags).forEach(keyword -> {
            if (keywords.containsKey(keyword)) {
                keywords.get(keyword).forEach(bookmark -> {
                    bookmarksByTag.put(bookmark, bookmarksByTag.getOrDefault(bookmark, 0) + 1);
                });
            }
        });

        if (bookmarksByTag.isEmpty()) {
            return new HashSet<>();
        }

        return bookmarksByTag.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public void cleanup() {
        final int errorCode = 404;
        HttpClient client = HttpClient.newBuilder().build();
        for (Group g : groups) {
            g.getBookmarks().removeIf(s -> {
                var request =
                    HttpRequest.newBuilder().GET().uri(URI.create(s.getUrl())).version(HttpClient.Version.HTTP_2)
                        .build();
                HttpResponse<String> response = null;
                try {
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException e) {
                    Logger.log(e, "User class encountered and error when sending a http request in method cleanup");
                    return false;
                }

                if (response.statusCode() == errorCode) {
                    s.getKeywords().forEach(
                        keyword -> keywords.get(keyword).removeIf(bookmark -> bookmark.getUrl().equals(s.getUrl())));
                }
                return response.statusCode() == errorCode;
            });
        }
    }

    public boolean importFromChrome() {
        final Path path = switch (System.getProperty("os.name")) {
            case "Windows 10" -> Paths.get(System.getProperty("user.home"), "AppData", "Local", "Google", "Chrome",
                "User Data", "Default", "Bookmarks");
            case "Linux" -> Paths.get("~/.config/google-chrome/Default/Bookmarks");
            case "MacOS" -> Paths.get(String.format("/Users/%s/Library/Application\\ Support/Google/Chrome/Bookmarks",
                System.getProperty("user.name")));
            default -> Paths.get("no file");
        };

        if (Files.notExists(path)) {
            Logger.log(new FileNotFoundException("Could not find file"),
                "User encountered and error when importing from Chrome in method importFromChrome");
            System.out.println("No file to import from");
            return false;
        }

        Set<String> urlsToBeImported = new HashSet<>();
        Gson gson = new Gson();
        Map<?, ?> parsedJson = null;

        try (FileReader in = new FileReader(path.toString())) {
            parsedJson = gson.fromJson(in, Map.class);
        } catch (IOException e) {
            Logger.log(e, "Something went wrong when reading chrome file.");
            System.out.println("Error reading chrome file");
            return false;
        }

        if (parsedJson.isEmpty()) {
            return false;
        }

        ((List<Map<?, ?>>) ((Map<?, ?>) ((Map<?, ?>) parsedJson.get("roots")).get("bookmark_bar")).get("children"))
            .forEach(bookmark -> {
                urlsToBeImported.add(((String) bookmark.get("url")));
            });
        ((List<Map<?, ?>>) ((Map<?, ?>) ((Map<?, ?>) parsedJson.get("roots")).get("other")).get("children"))
            .forEach(bookmark -> {
                urlsToBeImported.add(((String) bookmark.get("url")));
            });

        if (urlsToBeImported.isEmpty()) {
            return false;
        }

        if (groups.stream().noneMatch(s -> s.getTitle().equals("imported"))) {
            String success = this.createGroup("imported");
            if (!success.equals("Group created successfully")) {
                System.out.println(success);
                return false;
            }

        }

        urlsToBeImported.forEach(url -> this.addBookmark("imported", url));
        System.out.println("Successfully imported urls");
        return true;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public Map<String, Set<Bookmark>> getKeywords() {
        return keywords;
    }
}
