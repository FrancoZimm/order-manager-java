import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.model.Calculator;

public class CalculatorTestCase {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Multiply two positive integers")
    void testMultiply() {
        int result = calculator.multiply(4, 5);
        assertEquals(result, 20);
    }
    
    @Test
    @DisplayName("Test: Multiply by zero")
    void testMultiplyByZero() {
        int result = calculator.multiply(4, 0);
        assertEquals(result, 0);
    }

    @Test
    @DisplayName("Test: Multiply negative integer")
    void testMultiplyNegative() {
        int result = calculator.multiply(-4, 5);
        assertEquals(result, -20);
    }

    @Test
    @DisplayName("Test: Concatenate two non-null strings")
    void testConcat() {
        String result = calculator.concat("Hello, ", "World!");
        assertEquals(result, "Hello, World!");
    }

    @Test
    @DisplayName("Test: Concatenate with null string") 
    void testConcatWithNull() {
        String result = calculator.concat("Hello, ", null);
        assertEquals(result, Calculator.EMPTY);
    }

    @Test
    @DisplayName("Test: Sum two positive numbers")
    void testSum() {
        double result = calculator.sum(5.5, 4.5);
        assertEquals(result, 10.0);
    }

    @Test
    @DisplayName("Test: Sum negative numbers")
    void testSumNegative() {
        double result = calculator.sum(-5.0, -3.0);
        assertEquals(result, -8.0);
    }

    @Test
    @DisplayName("Test: Apply valid discount")  
    void testDiscount() {
        double result = calculator.discount(200.0, 15.0);
        assertEquals(result, 170.0);
    }

    @Test
    @DisplayName("Test: Calculate valid discount")
    void testCalculateTotal() {
        List<Double> amounts = List.of(50.0, 75.0, 25.0);
        double result = calculator.calculateTotal(amounts);
        assertEquals(result, 150.0);
    }

    @Test
    @DisplayName("Test: Discounts with zero percent and one hundred percent")
    void testEdgeCaseDiscounts() {
        double noDiscount = calculator.discount(100.0, 0.0);
        assertEquals(noDiscount, 100.0);

        double fullDiscount = calculator.discount(100.0, 100.0);
        assertEquals(fullDiscount, 0.0);
    }

    @Test
    @DisplayName("Test: Invalid discount percentage")
    void testInvalidDiscount() {
        try {
            calculator.discount(100.0, 150.0);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Percentage must be between 0 and 100");
        }
    }

    @Test
    @DisplayName("Test: Calculate total with empty list")
    void testCalculateTotalEmptyList() {
        List<Double> amounts = List.of();
        double result = calculator.calculateTotal(amounts);
        assertEquals(result, 0.0);
    }

    @Test
    @DisplayName("Test: List of amounts returns correct total")
    void testCalculateTotalWithNegativeAmounts() {
        List<Double> amounts = List.of(100.0, -20.0, 30.0);
        double result = calculator.calculateTotal(amounts);
        assertEquals(result, 110.0);
    }

    @Test
    @DisplayName("Test: Empty list returns zero total")
    void testCalculateTotalWithEmptyList() {
        List<Double> amounts = List.of();
        double result = calculator.calculateTotal(amounts);
        assertEquals(result, 0.0);
    }

    @Test
    @DisplayName("Test: Discount with negative percentage throws exception")
    void testNegativeDiscount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.discount(100.0, -10.0);
        });
        assertEquals("Percentage must be between 0 and 100", exception.getMessage());
    }

    @Test
    @DisplayName("Test: Concatenate with empty string")
    void testConcatWithEmptyString() {
        String result = calculator.concat("", "test");
        assertEquals("test", result);
    }

    @Test
    @DisplayName("Test: Concatenate two null strings returns EMPTY")
    void testConcatBothNull() {
        Calculator calculator = new Calculator();
        String result = calculator.concat(null, null);
        assertEquals(Calculator.EMPTY, result);
    }
    
    @Test
    @DisplayName("Test: Concatenate null and non-null returns EMPTY")
    void testConcatFirstNullSecondNotNull() {
        String result = calculator.concat(null, "World");
        assertEquals(Calculator.EMPTY, result);
    }
}
