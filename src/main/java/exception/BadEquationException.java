package exception;

import org.apfloat.Apfloat;

import java.util.Deque;

public class BadEquationException extends CalculatorException {
    public BadEquationException(Deque<Apfloat> stack) {
        super("Left on stack: " + stack.toString());
    }
}
