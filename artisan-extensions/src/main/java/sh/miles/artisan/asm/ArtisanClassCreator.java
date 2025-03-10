package sh.miles.artisan.asm;

import org.jspecify.annotations.NullMarked;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import sh.miles.artisan.extension.ArtisanExtension;
import sh.miles.artisan.extension.ContainerHandler;
import sh.miles.artisan.util.JvmClasspath;
import sh.miles.artisan.util.log.ArtisanLogger;
import sh.miles.artisan.visitor.ArtisanNodeReader;
import sh.miles.artisan.visitor.LiteralResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Bulk class creator that runs extensions on some class you intend to create
 * <p>
 * Class creators can not edit current classes for that see {@link ArtisanClassEditor}
 *
 * @since 1.1.0
 */
@NullMarked
public final class ArtisanClassCreator {

    private final Map<String, ArtisanExtension> extensions = new HashMap<>();
    private ClassNode node = new ClassNode();
    private Path output;
    private ArtisanLogger logger;
    private ArtisanNodeReader syntaxTreeReader;
    private Set<ContainerHandler> containers = new HashSet<>();
    private boolean hasPreRun = false;

    /**
     * Creates a new class creator
     *
     * @since 1.1.0
     */
    public ArtisanClassCreator() {
    }

    /**
     * Overrides the current ClassNode and provides a new one
     *
     * @param node the node to start over with
     * @return this creator
     * @since 1.1.0
     */
    public ArtisanClassCreator withExisting(ClassNode node) {
        if (node == null) throw new IllegalArgumentException("The provided node must not be null");
        this.node = node;
        return this;
    }

    /**
     * Edits the current node
     *
     * @param editor the node to edit
     * @return this creator
     * @since 1.1.0
     */
    public ArtisanClassCreator edit(Consumer<ClassNode> editor) {
        if (editor == null) {
            throw new IllegalArgumentException("The provided editor must not be null");
        }
        editor.accept(this.node);
        return this;
    }

    /**
     * Sets the class name of this node. The name must be set in order to write or generate the class
     *
     * @param name the name of the class
     * @return this creator
     * @since 1.1.0
     */
    public ArtisanClassCreator className(String name) {
        this.node.name = name;
        return this;
    }

    /**
     * Sets the java version of this node. Number versions are specified in {@link Opcodes}
     *
     * @param version the version to set
     * @return this creator
     * @since 1.1.0
     */
    public ArtisanClassCreator javaVersion(int version) {
        this.node.version = version;
        return this;
    }

    /**
     * Sets the super class of this node. The super class internal name
     *
     * @param superClass the super class
     * @return this creator
     * @since 1.1.0
     */
    public ArtisanClassCreator superClass(String superClass) {
        this.node.superName = superClass;
        return this;
    }

    /**
     * Adds an extension with a unique name to this builder
     *
     * @param extension the extension to add
     * @return this creator
     * @throws IllegalArgumentException thrown if an extension with a conflicting name was registered
     * @since 1.1.0
     */
    public ArtisanClassCreator extension(ArtisanExtension extension) throws IllegalArgumentException {
        if (this.extensions.containsKey(extension.name())) {
            throw new IllegalArgumentException("Extension with conflicting named registered");
        }

        this.extensions.put(extension.name(), extension);
        return this;
    }

    /**
     * Defines the path to output the class file at
     *
     * @param path the path
     * @return this creator
     * @since 1.1.0
     */
    public ArtisanClassCreator outputClassFile(Path path) {
        this.output = path.toAbsolutePath();
        return this;
    }

    /**
     * Adds a logger to this builder
     *
     * @param logger the logger to add
     * @return this editor
     * @since 1.0.0
     */
    public ArtisanClassCreator logger(ArtisanLogger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * Sets the syntax tree for this class editor
     *
     * @param syntaxTreeReader the syntax tree
     * @return this editor
     * @since 1.0.0
     */
    public ArtisanClassCreator syntaxTreeReader(ArtisanNodeReader syntaxTreeReader) {
        this.syntaxTreeReader = syntaxTreeReader;
        this.hasPreRun = false;
        return this;
    }

    /**
     * Generates a class to bytes
     *
     * @return the bytes generated
     * @throws IllegalArgumentException thrown if no syntax reader is provided
     * @since 1.1.0
     */
    public byte[] generate() throws IllegalArgumentException {
        if (this.extensions.isEmpty()) {
            logger.warn("No extensions were provided so no transformations occurred, was this intended?");
        }

        if (this.syntaxTreeReader == null) {
            throw new IllegalArgumentException("Must provide a non null syntax reader");
        }

        preRun();

        ArtisanClassUtil.simpleValidate(this.node);
        final JvmClasspath classpath = new JvmClasspath(JvmClasspath.CLASS, node.name, null, null);
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        for (final ContainerHandler handler : this.containers) {
            try {
                if (handler.doesModify(classpath)) {
                    handler.visit(node, classpath, this.logger);
                    logger.info("Finished applying visitor for class %s within container %s".formatted(classpath.dotpath(), handler.containerName()));
                }
            } catch (Exception e) {
                logger.throwing("Unable to apply handler with exception", e);
            }
        }

        node.accept(writer);
        return writer.toByteArray();
    }

    /**
     * Generates a class like in {@link #generate()} but also writes them to the file desginated by
     * {@link #outputClassFile(Path)}
     *
     * @throws IllegalArgumentException if no syntax reader is provided
     * @since 1.1.0
     */
    public void generateAndWrite() {
        final byte[] bytes = generate();
        try {
            if (Files.notExists(this.output.getParent())) {
                Files.createDirectories(this.output.getParent());
            }
            final var stream = Files.newOutputStream(this.output, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void preRun() {
        if (hasPreRun) {
            return;
        }

        final Set<String> contaierNames = new HashSet<>(syntaxTreeReader.getContainers());
        for (final ArtisanExtension extension : this.extensions.values()) {
            logger.debug("Gathering Containers from extension %s".formatted(extension.name()));
            for (final ContainerHandler handler : extension.buildHandlers()) {
                if (!contaierNames.contains(handler.containerName())) {
                    logger.debug("Skipping container %s from %s".formatted(handler.containerName(), extension.name()));
                }

                final List<LiteralResult> literals = this.syntaxTreeReader.getLiterals(handler.containerName());
                for (final LiteralResult literal : literals) {
                    handler.parse(literal, this.logger);
                }

                this.containers.add(handler);
            }
        }

        hasPreRun = true;
    }
}
