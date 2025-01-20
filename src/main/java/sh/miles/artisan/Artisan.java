package sh.miles.artisan;

import sh.miles.artisan.util.ArtisanUtils;

/**
 * Main access point for all artisan API. All you need in one easy to access class
 *
 * @since 1.0.0
 */
public final class Artisan {

    private Artisan() {
        throw ArtisanUtils.utilityClass(getClass());
    }

}
