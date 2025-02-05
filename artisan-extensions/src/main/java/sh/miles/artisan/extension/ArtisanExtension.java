package sh.miles.artisan.extension;

import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Can be extended in order to easily setup extension related functions
 *
 * @since 1.0.0
 */
@NullMarked
public interface ArtisanExtension {

    /**
     * The name of this extension
     *
     * @return name string
     * @since 1.0.0
     */
    String name();

    /**
     * The version of this extension
     *
     * @return version string
     * @since 1.0.0
     */
    String version();

    /**
     * Gets a list of handlers
     *
     * @return a list of container handlers
     * @since 1.0.0
     */
    List<ContainerHandler> buildHandlers();
}
