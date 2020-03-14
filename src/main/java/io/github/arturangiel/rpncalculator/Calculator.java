package io.github.arturangiel.rpncalculator;

import io.github.arturangiel.rpncalculator.exception.CalculatorException;
import org.apfloat.Apfloat;

public interface Calculator {

    Apfloat calculate(String equation) throws CalculatorException;

    CalculatorContext getContext();
}
