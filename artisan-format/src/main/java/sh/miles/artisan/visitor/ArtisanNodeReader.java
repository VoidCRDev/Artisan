package sh.miles.artisan.visitor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sh.miles.artisan.parser.node.ArtisanSyntaxNode;
import sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode;
import sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode.NodeContainerType;
import sh.miles.artisan.parser.node.LiteralArtisanSyntaxNode;
import sh.miles.artisan.parser.node.MetadataArtisanSyntaxNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

/**
 * Represents a reader class that can read artisan nodes and do visitation via an implemented
 * {@link ArtisanNodeVisitor}
 *
 * @since 1.0.0
 */
@NullMarked
public final class ArtisanNodeReader {

    private final ContainerArtisanSyntaxNode root;

    /**
     * Creates a new artisan node reader
     *
     * @param root the root to read from
     * @throws IllegalArgumentException thrown if the reader is not provided a container
     */
    public ArtisanNodeReader(ArtisanSyntaxNode root) throws IllegalArgumentException {
        if (!(root instanceof ContainerArtisanSyntaxNode)) {
            throw new IllegalArgumentException("Reader can only read starting at container syntax nodes");
        }

        this.root = (ContainerArtisanSyntaxNode) root;
    }

    /**
     * Gets a list of all container names provided to the reader
     * <p>
     * Note this method does not return "root" or "metadata" containers. These are meant for internal or more advanced
     * use. If you need metadata from the metadata container use {@link #getMetaValue(String, boolean)}
     *
     * @return a list of container names
     * @since 1.0.0
     */
    public List<String> getContainers() {
        final List<String> collector = new ArrayList<>();
        for (final ArtisanSyntaxNode node : root) {
            if (node instanceof ContainerArtisanSyntaxNode container) {
                collector.add(container.name);
            }
        }

        return collector;
    }

    /**
     * Gets all literals inside the provided container
     *
     * @param containerName the container to retrieve the literals from
     * @return the literals
     * @since 1.0.0
     */
    public List<LiteralResult> getLiterals(String containerName) {
        final ContainerArtisanSyntaxNode container = getContainer(NodeContainerType.FUNCTION_CONTENT, containerName);
        if (container == null) return new ArrayList<>();
        final List<LiteralResult> literals = new ArrayList<>();
        for (final ArtisanSyntaxNode child : container) {
            if (child instanceof LiteralArtisanSyntaxNode literal) {
                final Map<String, String> meta = new HashMap<>();
                for (final ArtisanSyntaxNode metaNode : literal) { // meta node assumption is safe because of AST requirements
                    final var metaMeta = (MetadataArtisanSyntaxNode) metaNode;
                    meta.put(metaMeta.key, metaMeta.value);
                }
                literals.add(new LiteralResult(literal.literal, meta));
            }
        }

        return literals;
    }

    /**
     * Gets all metadata values from within the root node in this reader
     * <p>
     * General this method with deep true should be used sparingly and instead any calls to deep should usually be
     * replaced with the results of {@link #getLiterals(String)}, which also provides metadata bundled with the
     * {@link LiteralResult}.
     *
     * @param deep whether or not to scan the entire tree. Note this is far more expensive
     * @return all non repeated meta data values
     * @since 1.0.0
     */
    public Set<MetadataResult> getAllMetadataValues(boolean deep) {
        final Set<MetadataResult> collector = new HashSet<>();
        final ContainerArtisanSyntaxNode metaContainer = getContainer(NodeContainerType.METADATA, null);
        MetadataArtisanSyntaxNode metaNode;
        for (final ArtisanSyntaxNode node : metaContainer) {
            metaNode = (MetadataArtisanSyntaxNode) node;
            collector.add(new MetadataResult(metaNode.key, metaNode.value));
        }

        if (!deep) return collector;

        Stack<ArtisanSyntaxNode> toVisit = new Stack<>();
        toVisit.add(this.root);
        ArtisanSyntaxNode next;
        while (!toVisit.isEmpty()) {
            next = toVisit.pop();
            if (next == metaContainer) continue;
            if (next instanceof MetadataArtisanSyntaxNode meta) {
                collector.add(new MetadataResult(meta.key, meta.value));
            }

            for (final ArtisanSyntaxNode child : next) {
                toVisit.add(child);
            }
        }

        return collector;
    }

    /**
     * Gets a meta value from the root node in this reader
     * <p>
     * Generally this method with deep true should be used sparingly and instead any calls to deep calls should be
     * delegated to the results of {@link #getLiterals(String)}, which also provides meta data bundled with the
     * {@link LiteralResult}.
     *
     * @param metaKey the meta key to retrieve
     * @param deep    whether or not to scan the entire tree. Note this is far more expensive
     * @return the value of that meta key
     * @since 1.0.0
     */
    public String getMetaValue(String metaKey, boolean deep) {
        final ContainerArtisanSyntaxNode metaContainer = getContainer(NodeContainerType.METADATA, null);
        MetadataArtisanSyntaxNode metaNode;
        for (final ArtisanSyntaxNode node : metaContainer) {
            metaNode = (MetadataArtisanSyntaxNode) node;
            if (metaNode.key.equals(metaKey)) {
                return metaNode.value;
            }
        }

        if (!deep) return null;

        Stack<ArtisanSyntaxNode> toVisit = new Stack<>();
        toVisit.add(this.root);
        ArtisanSyntaxNode next;
        while (!toVisit.isEmpty()) {
            next = toVisit.pop();
            if (next == metaContainer) continue;
            if (next instanceof MetadataArtisanSyntaxNode meta) {
                if (meta.key.equals(metaKey)) {
                    return meta.value;
                }
            }

            for (final ArtisanSyntaxNode child : next) {
                toVisit.add(child);
            }
        }

        return null;
    }

    /**
     * uses the visitor to visit all nodes
     *
     * @param visitor the visitor
     * @since 1.0.0
     */
    public void visit(ArtisanNodeVisitor visitor) {
        visitor.startVisit(this.root);
        for (final ArtisanSyntaxNode child : this.root) {
            traverseNode(this.root, child, visitor);
        }
        visitor.endVisit(this.root);
    }

    @Nullable
    private ContainerArtisanSyntaxNode getContainer(NodeContainerType type, @Nullable String name) {
        for (final ArtisanSyntaxNode node : this.root) {
            if (node instanceof ContainerArtisanSyntaxNode container && container.containerType == type && Objects.equals(container.name, name)) {
                return container;
            }
        }

        return null;
    }

    private void traverseNode(ArtisanSyntaxNode parent, ArtisanSyntaxNode node, ArtisanNodeVisitor visitor) {
        switch (node) {
            case ContainerArtisanSyntaxNode container -> {
                visitor.visitContainer((ContainerArtisanSyntaxNode) parent, container);
                for (final ArtisanSyntaxNode child : container) {
                    traverseNode(container, child, visitor);
                }
            }

            case LiteralArtisanSyntaxNode literal -> {
                visitor.visitLiteral((ContainerArtisanSyntaxNode) parent, literal);
                for (final ArtisanSyntaxNode child : literal) {
                    traverseNode(literal, child, visitor);
                }
            }

            case MetadataArtisanSyntaxNode metadata -> visitor.visitMeta(parent, metadata);
            default -> throw new IllegalStateException("Unexpected Node Value: " + node.getClass().getName());
        }
    }
}
