package sh.miles.artisan.visitor;

import org.jspecify.annotations.Nullable;
import sh.miles.artisan.parser.node.ArtisanSyntaxNode;
import sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode;

/**
 * Similar to a map entry Metadata results contain a key value pair of explicitly string values
 *
 * @param key   the metadata key
 * @param value the metadata value
 */
public record MetadataResult(String key, String value) {
}
