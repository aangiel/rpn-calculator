package io.github.arturangiel.rpncalculator;

public class CalculatorProvider {
    private static Calculator defaultCalculator;
    private static Calculator mathFunctionsCalculator;
    private static Calculator emptyCalculator;
    private static Calculator customCalculator;


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

    public static Calculator getEmptyCalculator() {
        if (emptyCalculator == null)
            emptyCalculator = new CalculatorImpl(CalculatorContext.getEmptyContext());
        return emptyCalculator;
    }

    public static Calculator getCalculatorWithCustomContext(CalculatorContext context) {
        if (customCalculator == null)
            customCalculator = new CalculatorImpl(context);
        return customCalculator;
    }

    public static Calculator getNewCalculatorWithCustomContext(CalculatorContext context) {
        return new CalculatorImpl(context);
    }
}
