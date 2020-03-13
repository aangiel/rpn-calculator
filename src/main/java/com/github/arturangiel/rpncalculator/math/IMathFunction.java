package com.github.arturangiel.rpncalculator.math;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface IMathFunction<T> {

    T apply(T[] t) throws InvocationTargetException, IllegalAccessException;
}
