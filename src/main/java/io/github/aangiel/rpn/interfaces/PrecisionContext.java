package io.github.aangiel.rpn.interfaces;

public interface PrecisionContext {
    /**
     * Returns precision used with {@link org.apfloat.Apfloat Apfloat} which is currently implemented.
     *
     * @return precision for {@link org.apfloat.Apfloat Apfloat} type.
     */
    long getPrecision();
}
