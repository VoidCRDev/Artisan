package sh.miles.artisan.util.stream;

import org.jspecify.annotations.NullMarked;

import java.io.InputStream;

/**
 * A InputStream that wraps around a single string of content
 *
 * @since 1.0.0
 */
@NullMarked
public final class StringInputStream extends InputStream {

    private final String content;
    private int pointer;

    public StringInputStream(String content) {
        this.content = content;
        this.pointer = 0;
    }

    @Override
    public int available() {
        // naive possibly dangerous implementation, should revisit
        return pointer != content.length() ? 1 : 0;
    }

    @Override
    public int read() {
        if (pointer >= content.length()) {
            return -1;
        }

        return content.charAt(pointer++);
    }

    @Override
    public void close() {
    }
}
