package sh.miles.artisan.util;

import java.io.InputStream;
import java.util.Random;

public final class TestUtil {

    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public static InputStream readResource(String path) {
        return TestUtil.class.getResourceAsStream("/" + path);
    }

    public static String nextString(int minLength, int maxLength) {
        final StringBuilder builder = new StringBuilder();
        final int size;
        if (minLength == maxLength) {
            size = minLength;
        } else {
            size = RANDOM.nextInt(minLength, maxLength);
        }

        for (int i = 0; i < size; i++) {
            builder.append((char) RANDOM.nextInt(Character.MIN_VALUE, Character.MAX_VALUE));
        }

        return builder.toString();
    }


}
