package sh.miles.artisan.extension;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a simple artisan extension
 *
 * @param name     the name of the extension
 * @param version  the extension version
 * @param handlers the version handler
 * @since 1.0.0
 */
@NullMarked
public record SimpleArtisanExtension(String name, String version,
                                     List<Supplier<ContainerHandler>> handlers) implements ArtisanExtension {
    public SimpleArtisanExtension {
        handlers = List.copyOf(handlers);
    }

    @Override
    public List<ContainerHandler> buildHandlers() {
        final List<ContainerHandler> collector = new ArrayList<>();
        for (final Supplier<ContainerHandler> handler : this.handlers) {
            collector.add(handler.get());
        }

        return collector;
    }
}
