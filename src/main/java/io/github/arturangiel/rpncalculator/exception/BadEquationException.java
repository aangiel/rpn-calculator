package io.github.arturangiel.rpncalculator.exception;

import org.apfloat.Apfloat;

import java.util.Deque;

public class BadEquationException extends CalculatorException {
    public BadEquationException(Deque<Apfloat> stack) {
        super(String.format("Left on stack: %s", stack));
    }
}
