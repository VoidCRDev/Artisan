package sh.miles.artisan.parser.node;

import org.jspecify.annotations.NullMarked;
import sh.miles.artisan.parser.token.ArtisanParseToken;
import sh.miles.artisan.parser.token.ArtisanParseToken.ArtisanTokenType;

/**
 * An implementation of {@link ArtisanSyntaxNode} that wraps some text value that can be parsed later or left as is
 *
 * @since 1.0.0
 */
@NullMarked
public final class LiteralArtisanSyntaxNode extends ArtisanSyntaxNode {

    public final String literal;

    LiteralArtisanSyntaxNode(final String literal) {
        this.literal = literal;
    }

    @Override
    public void addChild(final ArtisanSyntaxNode child) {
        if (!(child instanceof MetadataArtisanSyntaxNode)) {
            throw new IllegalArgumentException("Currently Literal nodes only allow MetadataArtisanSyntaxNodes as children");
        }

        super.addChild(child);
    }

    @Override
    protected StringBuilder asString(final StringBuilder builder, final int depth) {
        builder.append("\t".repeat(depth)).append("LiteralNode(").append(literal).append(")");
        if (this.hasChildren()) {
            builder.append(" {\n");
            for (final ArtisanSyntaxNode child : children) {
                child.asString(builder, depth + 1);
            }
            builder.append("\t".repeat(depth)).append("}\n");
        } else {
            builder.append("\n");
        }
        return builder;
    }

    /**
     * Creates a {@link LiteralArtisanSyntaxNode}
     *
     * @param token the token to use to create this literal node
     * @return the literal node
     * @throws IllegalArgumentException thrown if the token is not the ENTRY token type
     * @since 1.0.0
     */
    public static LiteralArtisanSyntaxNode create(ArtisanParseToken token) throws IllegalArgumentException {
        if (token.tokenType() != ArtisanTokenType.ENTRY) {
            throw new IllegalArgumentException("Can only create a LiteralArtisanSyntaxNode from a ENTRY token type");
        }

        return new LiteralArtisanSyntaxNode(token.segment());
    }
}
