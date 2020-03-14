# rpn-calculator
Java library to calculate Reverse Polish Notation equations

Theory: [Wikipedia](https://en.wikipedia.org/wiki/Reverse_Polish_notation)

Library for numbers: [Apfloat](http://www.apfloat.org/apfloat_java/)

#Usage

##Default usage
```
Calculator calc = CalculatorProvider.getDefaultCalculator();
Apfloat result = calc.calculate("5 1 2 + 4 * + 3 -");
System.out.println(result); // should display 1.4e1
```

##Usage with ApfloatMath static, one-parameter (Apfloat) methods
```
Calculator calc = CalculatorProvider.getMathFunctionsCalculator();
Apfloat result = calc.calculate("2 sinh");
System.out.println(result); // should display 3.626860407
```

##Usage with custom function
```
CalculatorContext context = CalculatorContext.getDefaultContext();
context.addCustomFunction("fun", 3, (array) -> array[0].multiply(array[1]).multiply(array[2]));
Calculator calc = CalculatorProvider.getCalculatorWithCustomContext(context);
Apfloat result = calc.calculate("5 1 4 3 2 fun * 4 * + 3 -");
System.out.println(result); // should display 9.8e1
```

