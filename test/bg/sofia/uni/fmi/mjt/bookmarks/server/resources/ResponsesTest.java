package bg.sofia.uni.fmi.mjt.bookmarks.server.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponsesTest {

    @Test
    void get() {
        int responseCode = 1;
        assertEquals(responseCode,Responses.FAIL.get());
    }
}