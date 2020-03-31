package io.github.aangiel.rpn.context.interfaces;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public interface PrecisionContext {
    /**
     * Returns precision used with {@link org.apfloat.Apfloat Apfloat} which is currently implemented.
     *
     * @return precision for {@link org.apfloat.Apfloat Apfloat} type.
     */
    long getPrecision();
}
