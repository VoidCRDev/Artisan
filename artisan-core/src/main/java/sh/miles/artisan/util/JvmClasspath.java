package sh.miles.artisan.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a JVM classpath
 *
 * @param type       the type of target this classpath ends up at. See static fields of this class for options
 * @param name       the name of the target, null if of the type {@link #CLASS}
 * @param path       the path of this classpath, e.g. the package in the case of this class it'd be
 *                   sh/miles/artisan/uti/JvmClasspath
 * @param descriptor the method descriptor if one is present
 * @since 1.0.0
 */
@NullMarked
public record JvmClasspath(byte type, String path, @Nullable String name, @Nullable String descriptor) {
    public static final byte FIELD = 0x0001;
    public static final byte METHOD = 0x0002;
    public static final byte CLASS = 0x00003;

    public JvmClasspath {
        if (type != FIELD && type != METHOD && type != CLASS) {
            throw new IllegalArgumentException("The provided type is not a valid selection");
        }

        if (name == null && type != CLASS) {
            throw new IllegalArgumentException("A null name is only valid for the class type");
        }

        if (path.contains(".")) {
            throw new IllegalArgumentException("path should be done with '/' char not '.'");
        }
    }

    /**
     * Returns the path as a dotpath. A dotpath is similar to sh/miles/artisan/util/JvmClasspath, but replaces the
     * common file separator with '.' instead e.g. sh.miles.artisan.util.JvmClasspath
     *
     * @return the dotpath
     * @since 1.0.0
     */
    public String dotpath() {
        return path.replaceAll("/", ".");
    }

    /**
     * Checks whether or not the provided type aligns with the type of this Jvm Classpath
     *
     * @param type the type
     * @return true if the types align, otherwise false
     * @since 1.0.0
     */
    boolean is(byte type) {
        return this.type == type;
    }

    /**
     * Changes the name of this jvm classpath
     *
     * @param newType the new type of the classpath
     * @param name    the name to set
     * @return a new {@link JvmClasspath} with the updated name
     * @since 1.0.0
     */
    public JvmClasspath withName(final byte newType, @Nullable final String name) {
        if (name == null) return new JvmClasspath(CLASS, this.path, null, null);
        return new JvmClasspath(newType, this.path, name, this.descriptor);
    }

    /**
     * Changes the descriptor of this jvm classpath
     *
     * @param descriptor the descriptor to set
     * @return a new {@link JvmClasspath} with the updated descriptor
     * @throws IllegalArgumentException thrown if the name is null and the provided descriptor is not null
     * @since 1.0.0
     */
    public JvmClasspath withDescriptor(@Nullable final String descriptor) {
        if (this.name == null && descriptor != null)
            throw new IllegalArgumentException("Can not add descriptor if name is null");
        if (descriptor == null) return new JvmClasspath(FIELD, this.path, this.name, null);
        return new JvmClasspath(METHOD, this.path, this.name, descriptor);
    }
}
