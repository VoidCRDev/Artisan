package sh.miles.artisan.extension.builtin;

import org.jspecify.annotations.NullMarked;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import sh.miles.artisan.ArtisanExtensions;
import sh.miles.artisan.asm.ArtisanAccessUtil;
import sh.miles.artisan.extension.ArtisanExtension;
import sh.miles.artisan.extension.ContainerHandler;
import sh.miles.artisan.util.ArtisanUtils;
import sh.miles.artisan.util.JvmClasspath;
import sh.miles.artisan.util.log.ArtisanLogger;
import sh.miles.artisan.visitor.LiteralResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * A Built in plugin for simple access transformations.
 * <p>
 * This plugin is automatically registered via {@link ArtisanExtensions#registerDefaultExtension(ArtisanExtension)}
 *
 * @since 1.0.0
 */
@NullMarked
public class ArtisanAccessTransformationExtension implements ArtisanExtension {

    /**
     * Creates a new extension
     *
     * @since 1.0.0
     */
    public ArtisanAccessTransformationExtension() {
    }

    @Override
    public String name() {
        return "Artisan Access Transformation Extensions";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public List<ContainerHandler> buildHandlers() {
        return List.of(new ArtisanAccessTransformationHandler());
    }

    private static class ArtisanAccessTransformationHandler implements ContainerHandler {

        private final Map<JvmClasspath, Map<String, AccessTransformer>> transformations = new HashMap<>();
        private final Set<JvmClasspath> classes = new HashSet<>();

        @Override
        public void parse(final LiteralResult literal, final ArtisanLogger logger) {
            final List<String> split = ArtisanUtils.simpleSplit(literal.literal, ' ', 3);

            final int access = ArtisanAccessUtil.scopeToOpcode(split.getFirst());
            if (access == -1) {
                throw new IllegalArgumentException("Unexpected scope definition" + split.getFirst());
            }

            final JvmClasspath classpath = new JvmClasspath(JvmClasspath.CLASS, split.get(1), null, null);
            this.classes.add(classpath);
            final JvmClasspath target;
            final String descriptorString = split.get(2);

            final List<String> descriptorSplit = ArtisanUtils.simpleSplit(descriptorString, '(', 2);
            target = new JvmClasspath(descriptorSplit.size() > 1 ? JvmClasspath.METHOD : JvmClasspath.FIELD, classpath.path(), descriptorSplit.getFirst(), descriptorSplit.size() >= 2 ? "(" + descriptorSplit.get(1) : null);
            final Map<String, AccessTransformer> transformations = this.transformations.computeIfAbsent(classpath, (k) -> new HashMap<>());

            // if not a method append field name otherwise full description dog()V method vs dog field
            final String name = target.descriptor() == null ? target.name() : target.name() + target.descriptor();
            transformations.put(name, new AccessTransformer(access, target));
            logger.info("Found AT Transformation %s".formatted(name));
        }

        @Override
        public boolean doesModify(final JvmClasspath path) {
            return this.classes.contains(path);
        }

        @Override
        public void visit(final ClassNode node, final JvmClasspath path, final ArtisanLogger logger) {
            final Map<String, AccessTransformer> transforms = fetchTransformers(node, path);
            if (transforms.isEmpty()) {
                logger.info("No ATs found for " + path.path());
                return;
            }

            for (final MethodNode method : node.methods) {
                final AccessTransformer transformer = transforms.get(method.name + method.desc);
                if (transformer == null) continue;
                method.access = transformer.merge(method.access);
                logger.info("Applying transformer to " + transformer.classpath.path() + " " + transformer.classpath.name() + transformer.classpath.descriptor());
            }

            for (final FieldNode field : node.fields) {
                final AccessTransformer transformer = transforms.get(field.name);
                if (transformer == null) continue;
                field.access = transformer.merge(field.access);
                logger.info("Applying transformer to " + transformer.classpath.path() + " " + transformer.classpath.name());
            }
        }

        @Override
        public String containerName() {
            return "AT";
        }

        private Map<String, AccessTransformer> fetchTransformers(ClassNode node, JvmClasspath path) {
            final Map<String, AccessTransformer> combination = new HashMap<>();
            if (this.transformations.containsKey(path)) {
                combination.putAll(transformations.get(path));
            }

            if (node.superName != null && this.transformations.containsKey(new JvmClasspath(JvmClasspath.CLASS, node.superName, null, null))) {
                combination.putAll(transformations.get(path));
            }

            for (final String anInterface : node.interfaces) {
                final JvmClasspath clazz = new JvmClasspath(JvmClasspath.CLASS, anInterface, null, null);
                if (this.transformations.containsKey(clazz)) {
                    combination.putAll(transformations.get(clazz));
                }
            }

            return combination;
        }
    }

    private record AccessTransformer(int access, JvmClasspath classpath) {

        public int merge(int otherAccess) {
            int combinedAccess = otherAccess;
            if ((otherAccess & ACC_PUBLIC) != 0) combinedAccess -= ACC_PUBLIC;
            if ((otherAccess & ACC_PRIVATE) != 0) combinedAccess -= ACC_PRIVATE;
            if ((otherAccess & ACC_PROTECTED) != 0) combinedAccess -= ACC_PROTECTED;
            // inverted
//            if ((otherAccess & ACC_STATIC) != 0) combinedAccess += ACC_STATIC;
//            if ((otherAccess & ACC_FINAL) != 0) combinedAccess += ACC_FINAL;
//            if ((otherAccess & ACC_TRANSIENT) != 0) combinedAccess += ACC_TRANSIENT;
//            if ((otherAccess & ACC_TRANSITIVE) != 0) combinedAccess += ACC_TRANSITIVE;
//            if ((otherAccess & ACC_VOLATILE) != 0) combinedAccess += ACC_VOLATILE;
//            if ((otherAccess & ACC_SYNCHRONIZED) != 0) combinedAccess += ACC_SYNCHRONIZED;
//            if ((otherAccess & ACC_INTERFACE) != 0) combinedAccess += ACC_INTERFACE;
//            if ((otherAccess & ACC_ABSTRACT) != 0) combinedAccess += ACC_ABSTRACT;
//            if ((otherAccess & ACC_STRICT) != 0) combinedAccess += ACC_STRICT;
//            if ((otherAccess & ACC_SYNTHETIC) != 0) combinedAccess += ACC_SYNTHETIC;
//            if ((otherAccess & ACC_RECORD) != 0) combinedAccess += ACC_RECORD;
            return combinedAccess + access;
        }
    }
}
