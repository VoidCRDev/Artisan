package sh.miles.artisan.parser.token;

import org.jspecify.annotations.NullMarked;

/**
 * A Token created when parsing which highlights important parts of parsed text
 *
 * @param id        the token id, should be ordered starting at 0 and ascending
 * @param segment   the token
 * @param tokenType the type of token
 * @since 1.0.0
 */
@NullMarked
public record ArtisanParseToken(int id, String segment, ArtisanTokenType tokenType) {

    /**
     * Represents types of tokens that can be tokenized
     *
     * @since 1.0.0
     */
    public enum ArtisanTokenType {
        /**
         * Denotes an opening token, which can also contain extra naming information
         *
         * @since 1.0.0
         */
        OPEN,
        /**
         * Denotes a closing token, which must only ever close an opening token
         *
         * @since 1.0.0
         */
        CLOSE,
        /**
         * Represents a block of metadata, which can be handled by the parser if needed
         *
         * @since 1.0.0
         */
        META,
        /**
         * Represents an entry, which is within {@link #OPEN} and {@link #CLOSE} tags. All lines that are not
         * {@link #COMMENT} are eligible to be entries. Unless the line is empty
         *
         * @since 1.0.0
         */
        ENTRY,
        /**
         * Denotes a comment, which can or can not be handled by the parser
         *
         * @since 1.0.0
         */
        COMMENT,
        ;
    }
}
