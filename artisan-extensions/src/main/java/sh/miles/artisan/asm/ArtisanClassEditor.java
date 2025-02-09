package sh.miles.artisan.asm;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import sh.miles.artisan.ArtisanExtensions;
import sh.miles.artisan.extension.ArtisanExtension;
import sh.miles.artisan.extension.ContainerHandler;
import sh.miles.artisan.util.JvmClasspath;
import sh.miles.artisan.util.log.ArtisanLogger;
import sh.miles.artisan.util.log.ArtisanVoidLogger;
import sh.miles.artisan.visitor.ArtisanNodeReader;
import sh.miles.artisan.visitor.LiteralResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Bulk class editor that provides run logic to extensions as well as other options to tweak the run time such as
 * logging and differing sources. Generally should be preferred to create an ArtisanClassEditor through the
 * {@link ArtisanExtensions} class
 *
 * @since 1.0.0
 */
@NullMarked
public final class ArtisanClassEditor {

    private final Map<String, ArtisanExtension> extensions = new HashMap<>();
    @Nullable
    private ArtisanNodeReader syntaxTreeReader;
    private ArtisanLogger logger = new ArtisanVoidLogger();
    @Nullable
    private Path clazzPath = null;
    private byte @Nullable [] clazzBytes = null;

    private final Set<String> containers = new HashSet<>();
    private boolean hasPreRun = false;

    /**
     * Creates a class new editor. Should generally be accessed through {@link ArtisanExtensions#newEditor()} or
     * {@link ArtisanExtensions#newDefaultEditor()}
     *
     * @since 1.0.0
     */
    public ArtisanClassEditor() {
    }

    /**
     * Adds an extension with a unique name to this builder
     *
     * @param extension the extension to add
     * @return this editor
     * @throws IllegalArgumentException thrown if an extension with a conflicting name was registered
     * @since 1.0.0
     */
    public ArtisanClassEditor extension(ArtisanExtension extension) throws IllegalArgumentException {
        if (this.extensions.containsKey(extension.name())) {
            throw new IllegalArgumentException("Extension with conflicting named registered");
        }

        this.extensions.put(extension.name(), extension);
        return this;
    }

    /**
     * Adds a class file to this builder, can not use if {@link #classBytes(byte[])} was already set
     *
     * @param path the path to the ".class" file
     * @return this editor
     * @since 1.0.0
     */
    public ArtisanClassEditor classFile(Path path) {
        if (this.clazzBytes != null) {
            throw new IllegalArgumentException("Class Bytes are already set as input, can not accept class file");
        }

        this.clazzPath = path;
        return this;
    }

    /**
     * Adds class bytes to this builder, can not use if {@link #classFile(Path)} was already set
     *
     * @param bytes the class bytes to add
     * @return this editor
     * @since 1.0.0
     */
    public ArtisanClassEditor classBytes(byte[] bytes) {
        if (this.clazzPath != null) {
            throw new IllegalArgumentException("Class path is already set as input, can not accept class file input");
        }

        this.clazzBytes = bytes;
        return this;
    }

    /**
     * Clears all bytes or paths assigned to this builder from {@link #classBytes(byte[])} or {@link #classFile(Path)}
     *
     * @return this editor
     * @since 1.0.0
     */
    public ArtisanClassEditor clearClassProvider() {
        this.clazzBytes = null;
        this.clazzPath = null;
        return this;
    }

    /**
     * Adds a logger to this builder
     *
     * @param logger the logger to add
     * @return this editor
     * @since 1.0.0
     */
    public ArtisanClassEditor logger(ArtisanLogger logger) {
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
    public ArtisanClassEditor syntaxTreeReader(ArtisanNodeReader syntaxTreeReader) {
        this.syntaxTreeReader = syntaxTreeReader;
        return this;
    }

    /**
     * Runs this class editor configuration
     *
     * @return the byte output, empty if there was an early return
     * @throws IllegalArgumentException thrown if no bytes were given
     * @since 1.0.0
     */
    public byte[] run() throws IllegalArgumentException {
        if (this.extensions.isEmpty()) {
            logger.warn("No extensions were provided so no transformations occurred, was this intended?");
            return new byte[0];
        }

        if (this.syntaxTreeReader == null) {
            throw new IllegalArgumentException("No valid syntax tree reader was provided");
        }

        preRun();

        byte[] classBytes = clazzBytes;
        if (classBytes == null && this.clazzPath != null) {
            try {
                classBytes = Files.readAllBytes(this.clazzPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (classBytes == null) {
            throw new IllegalArgumentException("No file or bytes were given for this editor. So it can not be run");
        }

        final ClassReader reader = new ClassReader(classBytes);
        final ClassNode node = new ClassNode();
        reader.accept(node, 0);

        boolean modified = false;
        final JvmClasspath classpath = new JvmClasspath(JvmClasspath.CLASS, node.name, null, null);
        final ClassWriter writer = new ClassWriter(reader, 0);
        for (final ArtisanExtension extension : this.extensions.values()) {
            logger.info("Applying extension " + extension.name());
            for (final ContainerHandler handler : extension.buildHandlers()) {
                final List<LiteralResult> literals = this.syntaxTreeReader.getLiterals(handler.containerName());
                for (final LiteralResult literal : literals) {
                    handler.parse(literal, this.logger);
                }

                if (!containers.contains(handler.containerName())) {
                    logger.debug("Skipping container %s from %s".formatted(handler.containerName(), extension.name()));
                    continue;
                }

                try {
                    if (handler.doesModify(classpath)) {
                        handler.visit(node, classpath, this.logger);
                        logger.info("Finished applying visitor for class %s within container %s".formatted(classpath.dotpath(), handler.containerName()));
                    }
                } catch (Exception e) {
                    logger.throwing("Unable to apply handler with exception", e);
                }
            }
        }

        node.accept(writer);
        return writer.toByteArray();
    }

    private void preRun() {
        if (hasPreRun) {
            return;
        }

        this.containers.addAll(syntaxTreeReader.getContainers());
        hasPreRun = true;
    }
}
