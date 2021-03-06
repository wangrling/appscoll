package com.android.home.threelibs.testing.junitrunner;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A simple calculator with a basic set of operations.
 */


public class Calculator {

    public enum Operator {ADD, SUB, DIV, MUL};

    /**
     * Addition operation.
     */
    public double add(double firstOperand, double secondOperand) {
        return firstOperand + secondOperand;
    }

    /**
     * Subtract operation
     */
    public double sub(double firstOperand, double secondOperand) {
        return firstOperand - secondOperand;
    }

    /**
     * Divide operation.
     */
    public double div(double firstOperand, double secondOperand) {
        checkArgument(secondOperand != 0, "secondOperand must be != 0," +
                " you cannot divide by zero.");

        return firstOperand / secondOperand;
    }

    /**
     * Multiply operation
     */
    public double mul(double firstOperand, double secondOperand) {

        return firstOperand * secondOperand;
    }
}
