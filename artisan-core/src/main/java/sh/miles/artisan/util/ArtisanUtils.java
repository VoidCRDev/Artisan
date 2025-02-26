package sh.miles.artisan.util;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Contains basic utilities for Artisan
 *
 * @since 1.0.0
 */
@NullMarked
public final class ArtisanUtils {

    private ArtisanUtils() {
        throw utilityClass(getClass());
    }

    /**
     * Throws predefined message with input class meant for utility class constructors
     *
     * @param clazz the class this is called in
     * @return the exception to throw
     * @since 1.0.0
     */
    public static UnsupportedOperationException utilityClass(Class<?> clazz) {
        return new UnsupportedOperationException("Invalid instantiation of utility class " + clazz.getName());
    }

    /**
     * Collects all read values until a terminating character is hit
     *
     * @param stream      the stream to read
     * @param terminators the terminators to stop reading at
     * @param collector   the collector to collect codepoints
     * @param collecting  function called while collecting codepoints
     * @param <C>         a collector type
     * @since 1.0.0
     */
    public static <C> void collectUntilAnyCode(final InputStream stream, final int[] terminators, C collector, BiConsumer<C, Integer> collecting) {
        boolean containsFileEnd = intArrayContains(terminators, -1);
        try {
            int next;
            while ((next = stream.read()) != -1 && !intArrayContains(terminators, next)) {
                if (next == '\r') continue; // obligatory fuck windows
                collecting.accept(collector, next);
            }
            if (next == -1 && containsFileEnd) {
                throw new IllegalStateException("collect until any code failed to collect a code form a specified terminator. Instead the file ended collector state as string %s".formatted(collector.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Collects all read values until a terminating character is hit
     *
     * @param stream     the stream to read
     * @param terminator the terminator to stop reading at
     * @param collector  the collector to collect codepoints
     * @param collecting function called while collecting codepoints
     * @param <C>        a collector type
     * @since 1.0.0
     */
    public static <C> void collectUntilCode(final InputStream stream, final int terminator, C collector, BiConsumer<C, Integer> collecting) {
        collectUntilAnyCode(stream, new int[]{terminator}, collector, collecting);
    }

    /**
     * Reads the input stream until any code is hit
     *
     * @param stream      the stream to read
     * @param terminators terminators to stop reading at
     * @return the terminating character, or -1 if the end of the file is hit
     * @since 1.0.0
     */
    public static int readUntilAnyCode(final InputStream stream, final int[] terminators) {
        try {
            int next;
            while ((next = stream.read()) != -1 && !intArrayContains(terminators, next)) {
                // nothing
            }
            return next;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the input stream until the given code is hit
     *
     * @param stream     the stream to read
     * @param terminator the terminating value to stop reading at
     * @return the terminating character, or -1 if the end of the file is hit
     * @since 1.0.0
     */
    public static int readUntilCode(final InputStream stream, final int terminator) {
        return readUntilAnyCode(stream, new int[]{terminator});
    }

    /**
     * Does a contain check on an int array
     *
     * @param array  the array to check
     * @param target the target to check for
     * @return true if contains, otherwise false
     * @since 1.0.0
     */
    public static boolean intArrayContains(int[] array, int target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) return true;
        }

        return false;
    }

    /**
     * Simple string split without regex overhead
     *
     * @param target            target to split
     * @param split             the char to split on
     * @param expectedSplitSize expected size of split for marginally improved performance
     * @return the split values
     * @since 1.0.0
     */
    public static List<String> simpleSplit(String target, char split, int expectedSplitSize) {
        final List<String> collection = new ArrayList<>(expectedSplitSize);
        char current;
        int position = 0;
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < target.length(); i++) {
            current = target.charAt(i);
            if (current == split) {
                position++;
                collection.add(builder.toString());
                builder.setLength(0);
                continue;
            }

            builder.append(current);
        }

        collection.add(builder.toString());
        return collection;
    }

    /**
     * Simple method to append chars to builder from integer value
     *
     * @param builder builder to append to
     * @param integer integer to convert
     * @since 1.0.0
     */
    public static void appendCharFromInteger(StringBuilder builder, Integer integer) {
        builder.append((char) integer.intValue());
    }
}
