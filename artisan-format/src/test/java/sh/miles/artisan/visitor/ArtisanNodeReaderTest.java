package sh.miles.artisan.visitor;

import org.junit.jupiter.api.Test;
import sh.miles.artisan.ArtisanFormat;
import sh.miles.artisan.util.TestUtil;

import static org.junit.jupiter.api.Assertions.*;

public class ArtisanNodeReaderTest {

    public static final String FILE = "parser/token/success-test.ajex";

    @Test
    public void testDoesNotThrow() {
        final var root = ArtisanFormat.asTree(TestUtil.readResource(FILE));
        final ArtisanNodeReader reader = assertDoesNotThrow(() -> new ArtisanNodeReader(root));
        assertDoesNotThrow(() -> reader.visit(new PrinterNodeVisitor(System.out)));
    }

    @Test
    public void testGetMetadataShallowAndDepp() {
        final var reader = ArtisanFormat.asReader(TestUtil.readResource(FILE));
        assertNotNull(reader.getMetaValue("Version", false));
        assertNotNull(reader.getMetaValue("Version", true));
        assertNotNull(reader.getMetaValue("LastUpdated", false));
        assertNull(reader.getMetaValue("Inheritable", false));
        assertNotNull(reader.getMetaValue("Inheritable", true));
        assertNull(reader.getMetaValue("ThisKeyDoesNotExistAndWillNotEverEverBeFound", true));
        assertNull(reader.getMetaValue("ThisKeyDoesNotExistAndWillNotEverEverBeFound", false));
    }

    @Test
    public void testGetAllLiterals() {
        final var reader = ArtisanFormat.asReader(TestUtil.readResource(FILE));
        assertFalse(reader.getLiterals("AT").isEmpty());
        assertTrue(reader.getLiterals("ThisFunctionContentDoesNotExist").isEmpty());
    }
}
