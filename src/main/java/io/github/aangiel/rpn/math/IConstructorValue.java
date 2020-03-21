package io.github.aangiel.rpn.math;

import java.util.List;

@FunctionalInterface
public interface IConstructorValue<T extends Number> {

    T apply(List<Object> args);
}
