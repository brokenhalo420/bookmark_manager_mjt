package bg.sofia.uni.fmi.mjt.bookmarks.server.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OperationsTest {

    @Test
    void get() {
        int operationCode = 1;
        assertEquals(operationCode,Operations.HELP.get());
    }
}