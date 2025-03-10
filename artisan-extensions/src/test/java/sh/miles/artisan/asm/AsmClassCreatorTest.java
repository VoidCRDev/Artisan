package sh.miles.artisan.asm;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import sh.miles.artisan.ArtisanFormat;
import sh.miles.artisan.util.TestUtil;
import sh.miles.artisan.util.log.ArtisanPrintStreamLogger;

import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

public class AsmClassCreatorTest {

    public static int WOW = TestInterface.get();

    @Test
    public void testWrite() {
        final var classCreator = new ArtisanClassCreator();
        classCreator.className("Main")
                .logger(new ArtisanPrintStreamLogger(System.out))
                .syntaxTreeReader(ArtisanFormat.asReader(TestUtil.readResource("test.ajex")))
                .outputClassFile(Path.of("Main.class"))
                .edit((node) -> {
                    node.name = "Main";
                    node.superName = "java/lang/Object";
                    node.version = Opcodes.V21;


                }).generateAndWrite();
    }

    interface TestInterface {
        static int get() {
            return ThreadLocalRandom.current().nextInt();
        }
    }
}
