package sh.miles.artisan.visitor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Represents a literal result that bundles any meta data attached
 *
 * @since 1.0.0
 */
@NullMarked
public final class LiteralResult {

    public final String literal;
    private final Map<String, String> metadata;

    LiteralResult(final String literal, final Map<String, String> metadata) {
        this.literal = literal;
        this.metadata = metadata;
    }

    /**
     * Tries to fetch a meta key attached to this literal result
     *
     * @param metaKey the meta key
     * @return the result from the query, could be null
     * @since 1.0.0
     */
    @Nullable
    public String getMetaValue(String metaKey) {
        return this.metadata.get(metaKey);
    }

    /**
     * Gets all the meta keys
     *
     * @return all the meta keys
     * @since 1.0.0
     */
    public Set<String> getMetaKeys() {
        return this.metadata.keySet();
    }

}
