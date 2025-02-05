package sh.miles.artisan.util.log;

import org.jspecify.annotations.NullMarked;

import java.io.PrintStream;

/**
 * A simple implementation of {@link ArtisanLogger} backed by a {@link PrintStream}
 *
 * @since 1.0.0
 */
@NullMarked
public class ArtisanPrintStreamLogger implements ArtisanLogger {

    private final PrintStream stream;

    /**
     * Creates a new PrintStream logger
     *
     * @param stream the stream to use for printing
     */
    public ArtisanPrintStreamLogger(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void info(final String message) {
        stream.println(message);
    }

    @Override
    public void warn(final String message) {
        stream.println("[WARN] " + message);
    }

    @Override
    public void debug(final String message) {
        stream.println("[DEBUG] " + message);
    }

    @Override
    public void error(final String message) {
        stream.println("[ERROR] " + message);
    }

    @Override
    public void throwing(final String message, final Throwable throwable) {
        stream.println(message);
        throwable.printStackTrace(this.stream);
    }
}
