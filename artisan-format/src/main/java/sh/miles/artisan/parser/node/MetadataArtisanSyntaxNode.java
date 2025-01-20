package sh.miles.artisan.parser.node;

import sh.miles.artisan.parser.token.ArtisanParseToken;

import java.util.Arrays;

import static sh.miles.artisan.parser.token.ArtisanParseToken.ArtisanTokenType.META;

/**
 * An implementation of {@link ArtisanSyntaxNode} that contains parse time and tokenized metadata. This is largely
 * useless. And can be filtered out, however, it can be used by the visitation API.
 *
 * @since 1.0.0
 */
public final class MetadataArtisanSyntaxNode extends ArtisanSyntaxNode {

    public final String key;
    public final String value;

    MetadataArtisanSyntaxNode(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void addChild(final ArtisanSyntaxNode child) {
        throw new UnsupportedOperationException("The node type MetadataArtisanSyntaxNode can not have children");
    }

    @Override
    protected StringBuilder asString(final StringBuilder builder, final int depth) {
        builder.append("\t".repeat(depth)).append("MetadataNode(").append(this.key).append(",").append(this.value).append(")\n");
        return builder;
    }

    /**
     * Creates a {@link MetadataArtisanSyntaxNode}
     *
     * @param token the token to use for creation
     * @return the resulting node
     * @throws IllegalArgumentException thrown if the argument is not standardized to expected standards
     * @since 1.0.0
     */
    public static MetadataArtisanSyntaxNode create(final ArtisanParseToken token) throws IllegalArgumentException {
        if (token.tokenType() != META) {
            throw new IllegalArgumentException("The provided token is not of the meta token type");
        }

        final String meta = token.segment();
        final String[] kvSplit = meta.split(":");
        if (kvSplit.length != 2) {
            throw new IllegalArgumentException("Illegal size of key value pair split. Key Value pair must be denoted by ':' and ':' can not be used inside of metadata illegal split of content %s".formatted(Arrays.toString(kvSplit)));
        }

        final String key = kvSplit[0];
        final String value = kvSplit[1].stripLeading().stripTrailing();

        return new MetadataArtisanSyntaxNode(key, value);
    }
}
