package sh.miles.artisan;

import org.jspecify.annotations.NullMarked;
import sh.miles.artisan.asm.ArtisanClassEditor;
import sh.miles.artisan.extension.ArtisanExtension;
import sh.miles.artisan.extension.ContainerHandler;
import sh.miles.artisan.extension.SimpleArtisanExtension;
import sh.miles.artisan.extension.builtin.ArtisanAccessTransformationExtension;
import sh.miles.artisan.util.ArtisanUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Main access point for artisan extension
 *
 * @since 1.0.0
 */
@NullMarked
public final class ArtisanExtensions {

    private static final Map<String, ArtisanExtension> defaultExtensions = new HashMap<>();

    static {
        registerDefaultExtension(new ArtisanAccessTransformationExtension());
    }

    private ArtisanExtensions() {
        throw ArtisanUtils.utilityClass(getClass());
    }

    /**
     * Creates a new ArtisanClassEditor
     *
     * @return the class editor
     * @since 1.0.0
     */
    public static ArtisanClassEditor newEditor() {
        return new ArtisanClassEditor();
    }

    /**
     * Creates a default editor packaged with the extensions registered through
     * {@link #registerDefaultExtension(ArtisanExtension)}
     *
     * @return a new class editor with defaults
     * @since 1.0.0
     */
    public static ArtisanClassEditor newDefaultEditor() {
        final var editor = new ArtisanClassEditor();
        for (final ArtisanExtension extension : defaultExtensions.values()) {
            editor.extension(extension);
        }

        return editor;
    }

    /**
     * Registers an extension to the default extensions, which can be utilized via {@link #newDefaultEditor()}
     *
     * @param extension the extension to register
     * @since 1.0.0
     */
    public static void registerDefaultExtension(ArtisanExtension extension) {
        defaultExtensions.put(extension.name(), extension);
    }

    /**
     * Creates a new artisan extension
     *
     * @param extensionName    the name of the extension
     * @param extensionVersion the version of the extension
     * @param handlers         all handlers with the extension
     * @return the newly created extension
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static ArtisanExtension createExtension(String extensionName, String extensionVersion, Supplier<ContainerHandler>... handlers) {
        if (extensionName == null) {
            throw new IllegalArgumentException("The extension name must not be null");
        }

        if (extensionVersion == null) {
            throw new IllegalArgumentException("The extension version must not be null");
        }

        if (handlers == null) {
            throw new IllegalArgumentException("The handlers must not be null");
        }

        if (handlers.length == 0) {
            throw new IllegalArgumentException("A valid ArtisanExtension must have atleast one ContainerHandler to be created");
        }

        return new SimpleArtisanExtension(extensionName, extensionVersion, Arrays.asList(handlers));
    }
}
