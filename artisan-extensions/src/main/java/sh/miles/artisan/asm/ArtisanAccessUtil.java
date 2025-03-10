package sh.miles.artisan.asm;

import sh.miles.artisan.util.ArtisanUtils;

import java.util.Locale;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SYNCHRONIZED;
import static org.objectweb.asm.Opcodes.ACC_TRANSIENT;
import static org.objectweb.asm.Opcodes.ACC_TRANSITIVE;

/**
 * Provides a simple utility to convert from an "access" to the opcode variant
 *
 * @since 1.0.0
 */
public final class ArtisanAccessUtil {

    private static final Map<String, Integer> TRANSLATOR = Map.of("public", ACC_PUBLIC, "private", ACC_PRIVATE, "protected", ACC_PROTECTED, "static", ACC_STATIC, "final", ACC_FINAL, "synchronized", ACC_SYNCHRONIZED, "transitive", ACC_TRANSITIVE, "transient", ACC_TRANSIENT);

    private ArtisanAccessUtil() {
        throw ArtisanUtils.utilityClass(getClass());
    }

    /**
     * Translates the provided scope into an ASM opcode
     * <p>
     * Supports: public, private, protected, static, final, synchronized, transitive, transient
     *
     * @param scope the scope to translate, will return -1 for null input
     * @return the opcode, or -1
     * @since 1.0.0
     */
    public static int scopeToOpcode(String scope) {
        if (scope == null) return -1;
        return TRANSLATOR.getOrDefault(scope.toLowerCase(Locale.ROOT), -1);
    }
}
