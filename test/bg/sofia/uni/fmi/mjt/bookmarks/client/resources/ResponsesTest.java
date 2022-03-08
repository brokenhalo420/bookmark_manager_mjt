package bg.sofia.uni.fmi.mjt.bookmarks.client.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponsesTest {

    @Test
    void get() {
        int responseCode = 0;
        assertEquals(responseCode,Responses.SUCCESS.get());
    }
}