package sh.miles.artisan.parser.node;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sh.miles.artisan.parser.token.ArtisanParseToken;
import sh.miles.artisan.parser.token.ArtisanParseToken.ArtisanTokenType;

/**
 * An implementation of {@link  ArtisanSyntaxNode} that represents a "Container" like node which contains other nodes to
 * divide them. Into syntactical structures more cleanly
 *
 * @since 1.0.0
 */
@NullMarked
public final class ContainerArtisanSyntaxNode extends ArtisanSyntaxNode {

    public final NodeContainerType containerType;
    @Nullable
    public final String name;

    ContainerArtisanSyntaxNode(final NodeContainerType containerType, @Nullable final String name) {
        this.containerType = containerType;
        this.name = name;
    }

    @Override
    protected StringBuilder asString(final StringBuilder builder, final int depth) {
        builder.append("\t".repeat(depth)).append("ContainerNode(").append(containerType.name());
        if (name != null) builder.append(",").append(name);
        builder.append(")");

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

    public enum NodeContainerType {
        METADATA, FUNCTION_CONTENT, ROOT,
    }

    /**
     * Creates an {@link ContainerArtisanSyntaxNode}
     *
     * @param type  the type of container node to create
     * @param token the token provided, which is only required for {@link NodeContainerType#FUNCTION_CONTENT}.
     * @return the created node
     * @throws IllegalArgumentException if the input is malformed or invalid
     * @since 1.0.0
     */
    public static ContainerArtisanSyntaxNode create(NodeContainerType type, @Nullable ArtisanParseToken token) throws IllegalArgumentException {
        switch (type) {
            case ROOT -> {
                if (token != null) {
                    throw new IllegalArgumentException("No token can be provided when creating a ContainerArtisanSyntaxNode of the type ROOT");
                }

                return new ContainerArtisanSyntaxNode(type, null);
            }
            case METADATA -> {
                if (token != null) {
                    throw new IllegalArgumentException("No token can be provided when creating a ContainerArtisanSyntaxNode of the type METADATA");
                }

                return new ContainerArtisanSyntaxNode(type, null);
            }

            case FUNCTION_CONTENT -> {
                if (token == null) {
                    throw new IllegalArgumentException("A token must be provided when creating a ContainerArtisanSyntaxNode of the type FUNCTION_CONTENT");
                }

                if (token.tokenType() != ArtisanTokenType.OPEN) {
                    throw new IllegalArgumentException("A FUNCTION_CONTENT type can only be created by an open token");
                }

                return new ContainerArtisanSyntaxNode(type, token.segment());
            }

            case null, default -> {
                throw new IllegalArgumentException("The provided type was null or not yet implemented (%s)".formatted(type));
            }
        }
    }
}
