package iface;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface IMathFunction<T> {

    T apply(T[] t) throws InvocationTargetException, IllegalAccessException;
}
