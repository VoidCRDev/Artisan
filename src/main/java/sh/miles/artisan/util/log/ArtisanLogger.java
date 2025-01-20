package sh.miles.artisan.util.log;

import org.jspecify.annotations.NullMarked;

/**
 * Represents a logger for this project, which can be used to provide output
 *
 * @since 1.0.0
 */
@NullMarked
public interface ArtisanLogger {
    /**
     * Prints a message to the info stream
     *
     * @param message message to print
     * @since 1.0.0
     */
    void info(String message);

    /**
     * Prints a message to the warn stream
     *
     * @param message message to print
     * @since 1.0.0
     */
    void warn(String message);

    /**
     * Prints a message to the debug stream
     *
     * @param message message to print
     * @since 1.0.0
     */
    void debug(String message);

    /**
     * Prints a message to the error stream
     *
     * @param message the message
     */
    void error(String message);

    /**
     * Prints a message to the throwing stream
     *
     * @param message   the message to print
     * @param throwable the exception to display
     */
    void throwing(String message, Throwable throwable);
}
