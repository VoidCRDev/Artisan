package sh.miles.artisan.parser.node;

import org.jspecify.annotations.NullMarked;
import sh.miles.artisan.parser.token.ArtisanParseToken;
import sh.miles.artisan.visitor.ArtisanNodeReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode.NodeContainerType.FUNCTION_CONTENT;
import static sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode.NodeContainerType.METADATA;
import static sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode.NodeContainerType.ROOT;

/**
 * Represents a "node" of data that acts when visited
 *
 * @since 1.0.0
 */
@NullMarked
public abstract class ArtisanSyntaxNode implements Iterable<ArtisanSyntaxNode> {

    protected final List<ArtisanSyntaxNode> children = new ArrayList<>();

    /**
     * Adds a child to this node
     *
     * @param child the child to add
     * @since 1.0.0
     */
    public void addChild(ArtisanSyntaxNode child) {
        this.children.add(child);
    }

    /**
     * Replaces the provided original with the replaced value
     *
     * @param original the original value
     * @param replace  the value to replace the original
     * @since 1.0.0
     */
    public void replaceChild(ArtisanSyntaxNode original, ArtisanSyntaxNode replace) {
        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i) == original) {
                this.children.set(i, replace);
                break;
            }
        }
    }

    /**
     * Removes a direct child from this node
     * <p>
     * This method does not remove children's children
     *
     * @param child the direct child to remove
     * @since 1.0.0
     */
    public void removeChild(ArtisanSyntaxNode child) {
        this.children.remove(child);
    }

    /**
     * Determines whether or not this node has children
     *
     * @return true if this node has children, otherwise false
     * @since 1.0.0
     */
    public final boolean hasChildren() {
        return !this.children.isEmpty();
    }

    /**
     * Checks if a given node tree contains a type of node
     *
     * @param clazz the type of node to check for
     * @return true if the node is found, otherwise false
     * @since 1.0.0
     */
    public final boolean containsType(Class<? extends ArtisanSyntaxNode> clazz) {
        for (final ArtisanSyntaxNode child : this.children) {
            if (child.getClass().isAssignableFrom(clazz)) {
                return true;
            } else {
                if (child.containsType(clazz)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Logic for printing this node to a string
     *
     * @param builder the cumulative string builder
     * @param depth   the depth of the builder thus far
     * @return the resulting builder after this node is visited
     * @since 1.0.0
     */
    protected abstract StringBuilder asString(StringBuilder builder, int depth);

    @Override
    public Iterator<ArtisanSyntaxNode> iterator() {
        return this.children.iterator();
    }

    @Override
    public String toString() {
        return asString(new StringBuilder(), 0).toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final ArtisanSyntaxNode that)) return false;
        return Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(children);
    }

    /**
     * Generates an ArtisanSyntaxNode tree from the provided tokens
     *
     * @param tokens the tokens to transform into a node tree
     * @return the node tree
     */
    public static ArtisanSyntaxNode generate(List<ArtisanParseToken> tokens) {
        final var root = ContainerArtisanSyntaxNode.create(ROOT, (ArtisanParseToken) null);
        final var meta = ContainerArtisanSyntaxNode.create(METADATA, (ArtisanParseToken) null);
        ContainerArtisanSyntaxNode head = root;
        Stack<ArtisanSyntaxNode> metaCache = new Stack<>();
        boolean hasOpenedAtAll = false;
        boolean open = false;
        for (final ArtisanParseToken token : tokens) {
            switch (token.tokenType()) {
                case META -> {
                    if (!hasOpenedAtAll) {
                        meta.addChild(MetadataArtisanSyntaxNode.create(token));
                    } else {
                        metaCache.add(MetadataArtisanSyntaxNode.create(token));
                    }
                }

                case OPEN -> {
                    if (open) {
                        throw new IllegalStateException("Can not open multiple function bodies at once invalid tree structure");
                    }

                    hasOpenedAtAll = true;
                    open = true;

                    if (!metaCache.isEmpty()) {
                        throw new IllegalStateException("can not annotate function content declarations with metadata");
                    }

                    final var temp = head;
                    head = ContainerArtisanSyntaxNode.create(FUNCTION_CONTENT, token);
                    root.addChild(head);
                }

                case CLOSE -> {
                    if (!open) {
                        throw new IllegalStateException("Can not close unopened function invalid tree structure");
                    }

                    head = root; // can only go one deep for now, can use a stack structure later
                }

                case ENTRY -> {
                    if (!open) {
                        throw new IllegalStateException("Entries can not be detached from an open function content body");
                    }
                    final var entry = LiteralArtisanSyntaxNode.create(token);
                    while (!metaCache.isEmpty()) {
                        entry.addChild(metaCache.pop());
                    }

                    head.addChild(entry);
                }

                case COMMENT -> {
                    // comments are always ignored
                }

                case null, default -> {
                    throw new IllegalStateException("Illegal tokens with null or no defined token (%s)".formatted(token.tokenType()));
                }
            }
        }

        root.addChild(meta);
        return root;
    }
}
