package sh.miles.artisan.extension.builtin;

import org.junit.jupiter.api.Test;
import sh.miles.artisan.ArtisanFormat;
import sh.miles.artisan.asm.ArtisanClassEditor;
import sh.miles.artisan.util.TestUtil;
import sh.miles.artisan.util.log.ArtisanPrintStreamLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TestArtisanAccessTransformationExtension {

    private static ArtisanClassEditor editor() {
        try {
            return new ArtisanClassEditor()
                    .extension(new ArtisanAccessTransformationExtension())
                    .syntaxTreeReader(ArtisanFormat.asReader(TestUtil.readResource("test.ajex")))
                    .classBytes(TestUtil.readResource("Example.class").readAllBytes())
                    .logger(new ArtisanPrintStreamLogger(System.out));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(final byte[] bytes) {
        final Path path = Path.of("src/test/resources/Example-AT-Test.class");

        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testExtensionDoesNotThrow() {
        final byte[] result = assertDoesNotThrow(() -> editor().run());
        write(result);
    }
}
