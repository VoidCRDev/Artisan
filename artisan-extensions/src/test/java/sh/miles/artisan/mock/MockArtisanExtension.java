package sh.miles.artisan.mock;

import org.jspecify.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import sh.miles.artisan.extension.ArtisanExtension;
import sh.miles.artisan.extension.ContainerHandler;
import sh.miles.artisan.util.JvmClasspath;
import sh.miles.artisan.util.log.ArtisanLogger;
import sh.miles.artisan.visitor.LiteralResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockArtisanExtension implements ArtisanExtension {

    private final List<ContainerHandler> handlers = List.of(
            new ProbeHandler()
    );

    @Override
    public String name() {
        return "MockExtension";
    }

    @Override
    public String version() {
        return "MOCK-SNAPSHOT";
    }

    @Override
    public List<ContainerHandler> buildHandlers() {
        return this.handlers;
    }

    static class ProbeHandler implements ContainerHandler {

        private final List<AccessTransformation> transformations = new ArrayList<>();

        @Override
        public void parse(final LiteralResult literal, ArtisanLogger logger) {
            final String[] split = literal.literal.split(" ");
            final JvmClasspath classpath;
            if (split[2].contains("(")) {
                classpath = new JvmClasspath(JvmClasspath.METHOD, split[1], split[2].substring(0, split[2].indexOf("(")), split[2].substring(split[2].indexOf("(")));
            } else {
                classpath = new JvmClasspath(JvmClasspath.FIELD, split[1], split[2], null);
            }

            transformations.add(new AccessTransformation(Opcodes.ACC_PUBLIC, classpath));
        }

        @Override
        public void visit(final ClassNode node, final JvmClasspath path, ArtisanLogger logger) {
            // yolo for mock it'll always be what we want
            final Map<JvmClasspath, Object> nodes = new HashMap<>();
            for (final FieldNode field : node.fields) {
                nodes.put(new JvmClasspath(JvmClasspath.FIELD, path.path(), field.name, null), field);
            }

            for(final MethodNode method: node.methods) {
                nodes.put(new JvmClasspath(JvmClasspath.METHOD, path.path(), method.name, method.desc), method);
            }

            for (final AccessTransformation transformation : transformations) {
                switch (transformation.path.type()) {
                    case JvmClasspath.FIELD -> {
                        final var field = (FieldNode) nodes.get(transformation.path);
                        if (field != null) {
                            field.access = transformation.access;
                        }
                    }

                    case JvmClasspath.METHOD -> {
                        final var method = (MethodNode) nodes.get(transformation.path);
                        if (method != null) {
                            method.access = transformation.access;
                        }
                    }
                }
            }
        }

        @Override
        public boolean doesModify(final JvmClasspath path) {
            return true;
        }

        @Override
        public String containerName() {
            return "NAIVE_TEST_AT";
        }
    }

    protected record AccessTransformation(int access, JvmClasspath path) {
    }
}
