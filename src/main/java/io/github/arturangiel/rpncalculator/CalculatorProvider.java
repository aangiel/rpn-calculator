package io.github.arturangiel.rpncalculator;

public class CalculatorProvider {
    private static Calculator defaultCalculator;
    private static Calculator mathFunctionsCalculator;
    private static Calculator mathFunctionsAndConstantsCalculator;
    private static Calculator emptyCalculator;


    public static Calculator getDefaultCalculator() {
        if (defaultCalculator == null)
            defaultCalculator = new CalculatorImpl();
        return defaultCalculator;
    }

    public static Calculator getMathFunctionsCalculator() {
        if (mathFunctionsCalculator == null)
            mathFunctionsCalculator = new CalculatorImpl(CalculatorContext.getMathFunctionsContext());
        return mathFunctionsCalculator;
    }

    public static Calculator getMathFunctionsAndConstantsCalculator() {
        if (mathFunctionsAndConstantsCalculator == null)
            mathFunctionsAndConstantsCalculator = new CalculatorImpl(CalculatorContext.getMathFunctionsAndConstantsContext());
        return mathFunctionsAndConstantsCalculator;
    }

    public static Calculator getEmptyCalculator() {
        if (emptyCalculator == null)
            emptyCalculator = new CalculatorImpl(CalculatorContext.getEmptyContext());
        return emptyCalculator;
    }

    public static Calculator getCalculatorWithCustomContext(CalculatorContext context) {
        return new CalculatorImpl(context);
    }
}
