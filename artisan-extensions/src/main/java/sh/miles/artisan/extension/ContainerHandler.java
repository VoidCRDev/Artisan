package sh.miles.artisan.extension;

import org.jspecify.annotations.NullMarked;
import org.objectweb.asm.tree.ClassNode;
import sh.miles.artisan.util.JvmClasspath;
import sh.miles.artisan.util.log.ArtisanLogger;
import sh.miles.artisan.visitor.LiteralResult;

/**
 * Handles more advanced incremental parsing steps that can not be assumed by the artisan-format module.
 *
 * @since 1.0.0
 */
@NullMarked
public interface ContainerHandler {

    /**
     * Should handle literal results into materials that can be used later. How these materials are stored depends on
     * the implementation
     *
     * @param literal the literal to use
     * @param logger  the logger
     * @since 1.0.0
     */
    void parse(LiteralResult literal, ArtisanLogger logger);

    /**
     * Gets the class visitor for this given handler
     *
     * @param node   the parent node to apply changes too
     * @param path   the path "name" of the class being edited
     * @param logger the logger
     * @since 1.0.0
     */
    void visit(ClassNode node, JvmClasspath path, ArtisanLogger logger);

    /**
     * The name of the container this ContainerHandler applies to
     *
     * @return the name of the container
     * @since 1.0.0
     */
    String containerName();
}
