package sh.miles.artisan.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import sh.miles.artisan.util.ArtisanUtils;

/**
 * Provides utilities for building on nodes with the {@link ArtisanClassEditor} and {@link ArtisanClassEditor}
 *
 * @since 1.1.0
 */
public class ArtisanClassUtil {

    private ArtisanClassUtil() {
        throw ArtisanUtils.utilityClass(getClass());
    }

    /**
     * Validates that a class node has a name, java version, and super class
     *
     * @param node the node
     * @throws IllegalArgumentException if the given node is null
     * @throws IllegalStateException    thrown if no class name is set via {@link ClassNode#name}
     * @since 1.1.0
     */
    public static void simpleValidate(ClassNode node) throws IllegalArgumentException, IllegalStateException {
        if (node == null)
            throw new IllegalArgumentException("Null node must not be given to ArtisanClassUtil#simpleValid(ClassNode)");
        if (node.version == 0) node.version = Opcodes.V21;
        if (node.superName == null) node.superName = "java/lang/Object";
        if (node.name == null) throw new IllegalStateException("No class name set");
    }

}
