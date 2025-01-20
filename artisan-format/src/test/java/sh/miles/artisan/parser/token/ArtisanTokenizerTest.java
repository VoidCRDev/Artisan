package sh.miles.artisan.parser.token;

import org.junit.jupiter.api.Test;
import sh.miles.artisan.util.TestUtil;

import static org.junit.jupiter.api.Assertions.*;

public class ArtisanTokenizerTest {

    @Test
    public void testCreateTokenizer() {
        assertDoesNotThrow(() -> new ArtisanTokenizer(TestUtil.readResource("parser/token/success-test.ajex")));
    }

    @Test
    public void testTokenizeSucceedsWithFile() {
        final ArtisanTokenizer tokenizer = new ArtisanTokenizer(TestUtil.readResource("parser/token/success-test.ajex"));
        int lastToken = 0;
        while (assertDoesNotThrow(tokenizer::hasNext, "Tokenizer has next is throwing unexpectedly")) {
            final ArtisanParseToken token = assertDoesNotThrow(tokenizer::next);
            assertEquals(lastToken++, token.id());
            // System.out.println(token);
        }
    }

    @Test
    public void testTokenizeFailsWithFile() {
        final ArtisanTokenizer tokenizer = new ArtisanTokenizer(TestUtil.readResource("parser/token/fail-tokenizer-test.ajex"));
        int lastToken = 0;
        while (assertDoesNotThrow(tokenizer::hasNext, "Tokenizer has next is throwing unexpectedly")) {
            final ArtisanParseToken token = tokenizer.next();
            if (token.id() == 5) {
                break;
            }
        }

        assertThrows(IllegalStateException.class, tokenizer::next, "Tokenizer should fail on invalid line");
    }

    @Test
    public void testTokenizeRemoveThrow() {
        final ArtisanTokenizer tokenizer = new ArtisanTokenizer(TestUtil.readResource("parser/token/success-test.ajax"));
        assertThrows(UnsupportedOperationException.class, tokenizer::remove);
    }
}
