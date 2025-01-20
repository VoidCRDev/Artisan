package sh.miles.artisan.util.stream;

import org.junit.jupiter.api.Test;
import sh.miles.artisan.test.TestUtil;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class StringInputStreamTest {

    private static final Random RANDOM = new Random();

    @Test
    public void testCreateStringInputStream() {
        assertDoesNotThrow(() -> new StringInputStream(TestUtil.nextString(1000, 2000)).close());
    }

    @Test
    public void testAvailable() {
        final int size = RANDOM.nextInt(1000, 2000);
        final StringBuilder builder = new StringBuilder();
        try (final StringInputStream stream = new StringInputStream(TestUtil.nextString(size, size))) {
            while (stream.available() != 0) {
                builder.append((char) stream.read());
            }

            assertEquals(size, builder.length());
        }
    }

    @Test
    public void readStringInputStream() {
        final String content = TestUtil.nextString(1000, 2000);
        final StringBuilder read = new StringBuilder();

        try (final StringInputStream stream = new StringInputStream(content)) {
            int next;
            while ((next = stream.read()) != -1) {
                read.append((char) next);
            }
        }

        assertEquals(content, read.toString());
    }
}
