package sh.miles.artisan.visitor;

import org.jspecify.annotations.NullMarked;
import sh.miles.artisan.parser.node.ArtisanSyntaxNode;
import sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode;
import sh.miles.artisan.parser.node.LiteralArtisanSyntaxNode;
import sh.miles.artisan.parser.node.MetadataArtisanSyntaxNode;

/**
 * A base node visitor outline that allows logic to occur when a node type is visited
 *
 * @since 1.0.0
 */
@NullMarked
public interface ArtisanNodeVisitor {

    /**
     * Triggered when visitation initially starts
     *
     * @param root the root
     * @since 1.0.0
     */
    default void startVisit(ContainerArtisanSyntaxNode root) {
    }

    /**
     * Triggered when some meta data is visited
     *
     * @param parent the parent of the meta node
     * @param meta   the meta node
     * @since 1.0.0
     */
    default void visitMeta(ArtisanSyntaxNode parent, MetadataArtisanSyntaxNode meta) {
    }

    /**
     * Triggered when some literal is visited
     *
     * @param parent  the parent container of the literal node
     * @param literal the literal node itself
     * @since 1.0.0
     */
    default void visitLiteral(ContainerArtisanSyntaxNode parent, LiteralArtisanSyntaxNode literal) {
    }

    /**
     * Triggered when some container is visited
     *
     * @param parent    the parent container
     * @param container the container
     * @since 1.0.0
     */
    default void visitContainer(ContainerArtisanSyntaxNode parent, ContainerArtisanSyntaxNode container) {
    }

    /**
     * Triggered when visitation ends completely
     *
     * @param root the root node
     * @since 1.0.0
     */
    default void endVisit(ContainerArtisanSyntaxNode root) {
    }
}
