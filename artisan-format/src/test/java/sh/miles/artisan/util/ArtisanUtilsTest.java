package sh.miles.artisan.util;

import org.junit.jupiter.api.Test;
import sh.miles.artisan.test.TestUtil;
import sh.miles.artisan.util.stream.StringInputStream;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArtisanUtilsTest {

    @Test
    public void testCollectUntilAnyCode() {
        final StringBuilder targetBuilder = new StringBuilder(TestUtil.nextString(1000, 2000));
        targetBuilder.insert(TestUtil.RANDOM.nextInt(0, targetBuilder.length()), '-');
        final String target = targetBuilder.toString();
        final int index = target.indexOf('-');

        final String expected = target.substring(0, index);
        final StringBuilder collector = new StringBuilder();
        try (final StringInputStream stream = new StringInputStream(target)) {
            ArtisanUtils.collectUntilAnyCode(stream, new int[]{'-'}, collector, (c, i) -> c.append((char) i.intValue()));
        }

        assertEquals(expected, collector.toString());
    }

    @Test
    public void testReadUntilAnyCode() {
        final StringBuilder targetBuilder = new StringBuilder(TestUtil.nextString(1000, 2000));
        targetBuilder.insert(TestUtil.RANDOM.nextInt(0, targetBuilder.length()), '-'); // guarantee at least one char of this
        final String target = targetBuilder.toString();
        final int index = target.indexOf("-");

        final String expected = target.substring(index);
        final StringBuilder collector = new StringBuilder();
        try (final StringInputStream stream = new StringInputStream(target)) {
            collector.append((char) ArtisanUtils.readUntilAnyCode(stream, new int[]{'-'}));
            while (stream.available() != 0) {
                collector.append((char) stream.read());
            }
        }

        assertEquals(expected, collector.toString());
    }

    @Test
    public void testSimpleSplit() {
        final String string = "asdfasdf-qwertqwert-fdsafdsa";
        final List<String> result = ArtisanUtils.simpleSplit(string, '-', 3);
        assertEquals(3, result.size());
        assertEquals("asdfasdf", result.get(0));
        assertEquals("qwertqwert", result.get(1));
        assertEquals("fdsafdsa", result.get(2));
    }
}
