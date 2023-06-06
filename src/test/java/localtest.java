


import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class localtest {
    @Test
    void test() {
        assertEquals(1, 1);
    }

    @Test
    public int fibonacci(int i) {
        int number = 1;
        if (number == 0 || number == 1) return number;
        return fibonacci(number - 2) + fibonacci(number - 1);
    }

    //metodo de grados fahrenheit a celsius
    @Test
    public double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }

    //encontrar el radio de un circulo
    @Test
    public double circleArea(double radius) {
        return Math.PI * Math.pow(radius, 2);
    }
}

