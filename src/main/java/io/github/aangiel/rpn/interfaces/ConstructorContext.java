package io.github.aangiel.rpn.interfaces;

import java.util.function.Function;

/**
 * @param <T> extends {@link Number}
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public interface ConstructorContext<T extends Number> {
    /**
     * Lambda for returning new Object of type <pre>&#60;T extends Number&#62;</pre><br><br>
     * Should be implemented as e.g. {@code return args -> Double.valueOf(args.get(0));}
     *
     * @return {@link Function Function&#60;String, T extends Number&#62;}
     * which is used during parsing of equation while trying to parse number
     */
    Function<String, T> getNumberConstructor();
}
