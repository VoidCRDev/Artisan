package sh.miles.artisan.parser.node;

import org.junit.jupiter.api.Test;
import sh.miles.artisan.parser.token.ArtisanParseToken;
import sh.miles.artisan.parser.token.ArtisanTokenizer;
import sh.miles.artisan.util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArtisanSyntaxNodeTest {

    @Test
    public void testShouldParse() {
        final var tokens = tokenize("parser/token/success-test.ajex");
        final var tree = assertDoesNotThrow(() -> ArtisanSyntaxNode.generate(tokens));
         System.out.println(tree);
    }

    @Test
    public void testShouldFailParse() {
        final var tokens = tokenize("parser/node/fail-ast-test.ajex");
        assertThrows(IllegalArgumentException.class, () -> ArtisanSyntaxNode.generate(tokens));
    }

    private static List<ArtisanParseToken> tokenize(String file) {
        final List<ArtisanParseToken> tokens = new ArrayList<>();
        final var tokenizer = new ArtisanTokenizer(TestUtil.readResource(file));
        while (tokenizer.hasNext()) {
            tokens.add(tokenizer.next());
        }

        return tokens;
    }

}
