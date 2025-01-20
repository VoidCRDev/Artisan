package sh.miles.artisan.parser.token;

import org.jspecify.annotations.NullMarked;
import sh.miles.artisan.util.ArtisanUtils;

/**
 * Contains token constants that Artisan uses
 *
 * @since 1.0.0
 */
@NullMarked
public final class ArtisanTokenConstants {

    private ArtisanTokenConstants() {
        throw ArtisanUtils.utilityClass(getClass());
    }

    /**
     * The open and close char
     */
    static final int OPEN_CLOSE = '~';

    /**
     * The comment char
     */
    static final int COMMENT = '#';

    /**
     * The Meta char
     */
    static final int META = '@';

    /**
     * The New Line char
     */
    static final int NEW_LINE = '\n';
}
