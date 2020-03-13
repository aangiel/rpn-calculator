package exception;

public class LackOfArgumentsException extends CalculatorException {

    public LackOfArgumentsException(String item, int position) {
        super("Lack of arguments for: " + item + " at position: " + (position + 1));
    }
}
