package io.github.arturangiel.rpncalculator;

import io.github.arturangiel.rpncalculator.math.FunctionValue;
import io.github.arturangiel.rpncalculator.math.IMathFunction;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class CalculatorContext {

    private Map<String, FunctionValue> functions;
    private long precision;

    private CalculatorContext() {
        functions = new HashMap<>();
    }

    public static CalculatorContext getDefaultContext() {
        CalculatorContext context = new CalculatorContext();
        context.populateDefaultOperations();
        context.setPrecision(10);
        return context;
    }

    public static CalculatorContext getMathFunctionsContext() {
        CalculatorContext context = getDefaultContext();
        context.populateDefaultOneParameterMathFunctions();
        return context;
    }

    public static CalculatorContext getEmptyContext() {
        CalculatorContext context = new CalculatorContext();
        return context;
    }

    private void populateDefaultOneParameterMathFunctions() {
        Stream.of(ApfloatMath.class.getMethods())
                .filter(m -> Apfloat.class.equals(m.getReturnType()))
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> Arrays.equals(m.getParameterTypes(), new Class[]{Apfloat.class}))
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .forEach(m -> functions.put(m.getName(), FunctionValue.forFunction(1, a -> (Apfloat) m.invoke(null, a))));
    }

    private void populateDefaultOperations() {
        functions.put("+", FunctionValue.forFunction(2, a -> a[0].add(a[1])));
        functions.put("-", FunctionValue.forFunction(2, a -> a[0].subtract(a[1])));
        functions.put("*", FunctionValue.forFunction(2, a -> a[0].multiply(a[1])));
        functions.put("/", FunctionValue.forFunction(2, a -> a[0].divide(a[1])));
    }

    public Map<String, FunctionValue> getFunctions() {
        return functions;
    }

    public void setFunctions(Map<String, FunctionValue> functions) {
        this.functions = functions;
    }

    public long getPrecision() {
        return precision;
    }

    public void setPrecision(long precision) {
        this.precision = precision;
    }

    public CalculatorContext addCustomFunction(String name, int parametersCount, IMathFunction<Apfloat> function) {
        functions.put(name, FunctionValue.forFunction(parametersCount, function));
        return this;
    }

    public Set<String> getAvailableFunctions() {
        return functions.keySet();
    }
}
