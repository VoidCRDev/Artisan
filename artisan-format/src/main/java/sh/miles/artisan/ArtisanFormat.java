package sh.miles.artisan;

import org.jspecify.annotations.NullMarked;
import sh.miles.artisan.parser.node.ArtisanSyntaxNode;
import sh.miles.artisan.parser.token.ArtisanParseToken;
import sh.miles.artisan.parser.token.ArtisanTokenizer;
import sh.miles.artisan.util.ArtisanUtils;
import sh.miles.artisan.visitor.ArtisanNodeReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Main access point for all artisan API. All you need in one easy to access class
 *
 * @since 1.0.0
 */
@NullMarked
public final class ArtisanFormat {

    /**
     * The file extension for the Artisan Java Extension Format
     *
     * @since 1.0.0
     */
    public static final String FILE_EXTENSION = ".ajex";

    private ArtisanFormat() {
        throw ArtisanUtils.utilityClass(getClass());
    }

    /**
     * Creates a reader from the provided stream
     * <p>
     * This method chains {@link #asReader(ArtisanSyntaxNode)}, {@link #asTree(List)}, and
     * {@link #tokenize(InputStream)}. This method is best used when no in depth configuration is needed with the
     * underlying AST. Seeing as the {@link ArtisanNodeReader} provided can retrieve all needed information.
     *
     * @param stream the stream to derive the reader from
     * @return the created reader
     * @throws IllegalArgumentException if any arguments are violated during the chaining method
     * @since 1.0.0
     */
    public static ArtisanNodeReader asReader(final InputStream stream) {
        return new ArtisanNodeReader(asTree(tokenize(stream)));
    }

    /**
     * Creates a reader from the provided {@link ArtisanSyntaxNode}
     *
     * @param node the node to create a reader from
     * @return the created node reader
     * @throws IllegalArgumentException if the provided node isn't a container
     * @since 1.0.0
     */
    public static ArtisanNodeReader asReader(final ArtisanSyntaxNode node) throws IllegalArgumentException {
        return new ArtisanNodeReader(node);
    }

    /**
     * Generates an ArtisanSyntaxTree from the provided stream
     * <p>
     * This method does thee equivalent of a chain of {@link #asTree(List)} and {@link #tokenize(InputStream)}
     *
     * @param stream the stream to use to generate the tree
     * @return the node tree
     * @since 1.0.0
     */
    public static ArtisanSyntaxNode asTree(final InputStream stream) {
        return ArtisanSyntaxNode.generate(tokenize(stream));
    }

    /**
     * Generates an ArtisanSyntaxNode tree from the provided tokens
     *
     * @param tokens the tokens to transform into a node tree
     * @return the node tree
     * @since 1.0.0
     */
    public static ArtisanSyntaxNode asTree(final List<ArtisanParseToken> tokens) {
        return ArtisanSyntaxNode.generate(tokens);
    }

    /**
     * Tokenizes a stream
     *
     * @param stream the stream to tokenize
     * @return an ordered list of tokens
     * @since 1.0.0
     */
    public static List<ArtisanParseToken> tokenize(final InputStream stream) {
        final List<ArtisanParseToken> collector = new ArrayList<>();
        final ArtisanTokenizer tokenizer = new ArtisanTokenizer(stream);
        while (tokenizer.hasNext()) {
            collector.add(tokenizer.next());
        }

        return collector;
    }
}
