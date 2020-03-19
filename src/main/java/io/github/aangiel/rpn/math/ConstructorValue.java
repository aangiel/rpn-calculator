package io.github.aangiel.rpn.math;

import java.util.List;

@FunctionalInterface
public interface ConstructorValue<T extends Number> {

    T apply(List<Object> args);
}
