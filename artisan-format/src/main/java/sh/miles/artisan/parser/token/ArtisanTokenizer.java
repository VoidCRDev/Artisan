package sh.miles.artisan.parser.token;

import org.jspecify.annotations.NullMarked;
import sh.miles.artisan.ArtisanFormat;
import sh.miles.artisan.parser.token.ArtisanParseToken.ArtisanTokenType;
import sh.miles.artisan.util.ArtisanUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static sh.miles.artisan.parser.token.ArtisanTokenConstants.*;

/**
 * A Tokenizer class through Artisan, which turns a stream into a set of tokens
 *
 * @since 1.0.0
 */
@NullMarked
public final class ArtisanTokenizer implements Iterator<ArtisanParseToken> {

    private final InputStream stream;
    private int id = 0;
    private boolean hasOpenedTag = false;

    /**
     * Creates a new ArtisanTokenizer
     * <p>
     * Generally calls to the ArtisanTokenizer should be delegated to {@link ArtisanFormat#tokenize(InputStream)}
     *
     * @param stream the stream to use to create the tokenizer
     * @since 1.0.0
     */
    public ArtisanTokenizer(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public boolean hasNext() {
        try { // this is heavy should check if this can be optimized
            return stream.available() > 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArtisanParseToken next() throws NoSuchElementException {
        try {
            if (stream.available() <= 0) throw new NoSuchElementException("No next token to read");
            int next;

            while ((next = stream.read()) != -1) {
                if (next == NEW_LINE) continue;

                switch (next) {
                    case META -> {
                        final StringBuilder result = new StringBuilder();
                        ArtisanUtils.collectUntilCode(stream, NEW_LINE, result, ArtisanUtils::appendCharFromInteger);
                        return new ArtisanParseToken(id++, result.toString(), ArtisanTokenType.META);
                    }

                    case COMMENT -> {
                        final StringBuilder result = new StringBuilder();
                        ArtisanUtils.collectUntilCode(stream, NEW_LINE, result, ArtisanUtils::appendCharFromInteger);
                        return new ArtisanParseToken(id++, result.toString(), ArtisanTokenType.COMMENT);
                    }

                    case OPEN_CLOSE -> {
                        final StringBuilder result = new StringBuilder();
                        ArtisanUtils.collectUntilCode(stream, NEW_LINE, result, ArtisanUtils::appendCharFromInteger);
                        this.hasOpenedTag = !hasOpenedTag;
                        return new ArtisanParseToken(id++, result.toString(), this.hasOpenedTag ? ArtisanTokenType.OPEN : ArtisanTokenType.CLOSE);
                    }

                    default -> {
                        if (!hasOpenedTag) {
                            final StringBuilder result = new StringBuilder();
                            ArtisanUtils.collectUntilCode(stream, NEW_LINE, result, ArtisanUtils::appendCharFromInteger);
                            throw new IllegalStateException("Unknown token start %s, for line '%s' unable to continue parsing because of invalid token".formatted((char) next, result.insert(0, (char) next)));
                        }
                        final StringBuilder result = new StringBuilder().append((char) next);
                        ArtisanUtils.collectUntilCode(stream, NEW_LINE, result, ArtisanUtils::appendCharFromInteger);
                        return new ArtisanParseToken(id++, result.toString(), ArtisanTokenType.ENTRY);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        throw new NoSuchElementException("There is no valid next element");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not valid for an ArtisanTokenizer iterator");
    }
}
