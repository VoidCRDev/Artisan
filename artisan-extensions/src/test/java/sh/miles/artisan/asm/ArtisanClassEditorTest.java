package sh.miles.artisan.asm;

import org.junit.jupiter.api.Test;
import sh.miles.artisan.ArtisanFormat;
import sh.miles.artisan.mock.MockArtisanExtension;
import sh.miles.artisan.util.TestUtil;
import sh.miles.artisan.util.log.ArtisanPrintStreamLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ArtisanClassEditorTest {

    @Test
    public void testRunArtisanClassEditorEmpty() {
        assertDoesNotThrow(() -> new ArtisanClassEditor()
                .logger(new ArtisanPrintStreamLogger(System.out))
                .run());
    }

    @Test
    public void testRunArtisanClassEditor() throws IOException {
        final byte[] output = assertDoesNotThrow(() -> new ArtisanClassEditor()
                .logger(new ArtisanPrintStreamLogger(System.out))
                .syntaxTreeReader(ArtisanFormat.asReader(TestUtil.readResource("test.ajex")))
                .extension(new MockArtisanExtension())
                .classBytes(TestUtil.readResource("Example.class").readAllBytes())
                .run());
        assertTrue(output.length > 0);
        System.out.println(Path.of("").toAbsolutePath());
        final Path path = Path.of("src/test/resources/Example-Transformed.class");
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        Files.write(path, output);
    }
}
