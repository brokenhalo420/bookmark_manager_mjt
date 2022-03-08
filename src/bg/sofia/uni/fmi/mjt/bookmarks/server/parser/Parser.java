package bg.sofia.uni.fmi.mjt.bookmarks.server.parser;

import bg.sofia.uni.fmi.mjt.bookmarks.server.logger.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Parser {

    private static final Path PATH_TO_STOPWORDS_FILE =
        Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "resources", "stopwords.txt");
    private static final String WORD_SEPARATION_REGEX = "[\\p{IsPunctuation}\\p{IsWhite_Space}]+";
    private static Set<String> stopwords = new HashSet<>();

    static {
        try (BufferedReader in = new BufferedReader(new FileReader(PATH_TO_STOPWORDS_FILE.toString()));) {
            stopwords = in.lines().collect(Collectors.toSet());
        } catch (IOException e) {
            Logger.log(e, "Parser couldn't open stopwords file");
            System.exit(0);
        }
    }

    public static List<String> getPageData(String url) {
        if (url == null || url.isEmpty()) {
            RuntimeException thrown = new IllegalArgumentException("Url is empty. Cannot parse page");
            Logger.log(thrown, "Error occured in Parser class with url:" + url);
            return null;
        }
        Document doc = new Document("");
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            Logger.log(e, "Parsing failed. Url does not respond to request");
            return null;
        } catch (IllegalArgumentException ex) {
            Logger.log(ex, "Parsing failed. Url does not respond to request");
            return null;
        }

        List<String> data = new ArrayList<>();
        data.add(doc.title());

        Map<String, Integer> wordsInDocument = new HashMap<>();
        Arrays.stream(doc.body().text().split(WORD_SEPARATION_REGEX)).forEach((String s) -> {
                String word = s.toLowerCase(Locale.ROOT);
                final int suffixLength = 3;
                if (word.endsWith("ed") || word.endsWith("ly")) {
                    word = word.substring(0, word.length() - 2);
                } else if (word.endsWith("ing")) {
                    word = word.substring(0, word.length() - suffixLength);
                }

                if (word.isEmpty() || word.isBlank() || !word.matches("[a-zа-я]+")) {
                    return;
                }
                wordsInDocument.put(word, wordsInDocument.getOrDefault(word, 0) + 1);
            }
        );

        Map<String, Integer> sortedWords =
            (wordsInDocument.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new)));

        stopwords.forEach(sortedWords::remove);

        final int tagsLimit = 5;
        data.addAll(sortedWords.keySet().stream().limit(tagsLimit).toList());
        return data;
    }
}
