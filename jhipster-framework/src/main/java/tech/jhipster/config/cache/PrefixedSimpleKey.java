package tech.jhipster.config.cache;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <p>PrefixedSimpleKey class.</p>
 */
public class PrefixedSimpleKey implements Serializable {

    private final String prefix;
    private final Object[] params;
    private final String methodName;
    private int hashCode;

    /**
     * <p>Constructor for PrefixedSimpleKey.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param methodName a {@link java.lang.String} object.
     * @param elements a {@link java.lang.Object} object.
     */
    public PrefixedSimpleKey(String prefix, String methodName, Object... elements) {
        Assert.notNull(prefix, "Prefix must not be null");
        Assert.notNull(elements, "Elements must not be null");
        this.prefix = prefix;
        this.methodName = methodName;
        params = new Object[elements.length];
        System.arraycopy(elements, 0, params, 0, elements.length);
        hashCode = prefix.hashCode();
        hashCode = 31 * hashCode + methodName.hashCode();
        hashCode = 31 * hashCode + Arrays.deepHashCode(params);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        return (this == other ||
            (other instanceof PrefixedSimpleKey && prefix.equals(((PrefixedSimpleKey) other).prefix) &&
                methodName.equals(((PrefixedSimpleKey) other).methodName) &&
                Arrays.deepEquals(params, ((PrefixedSimpleKey) other).params)));
    }

    /** {@inheritDoc} */
    @Override
    public final int hashCode() {
        return hashCode;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return prefix + " " + getClass().getSimpleName() + methodName + " [" + StringUtils.arrayToCommaDelimitedString(
            params) + "]";
    }
}
