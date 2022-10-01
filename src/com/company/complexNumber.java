package com.company;

public class complexNumber {
    private double realPart;
    private double imaginaryPart;

    public void setRealPart(double realPart) {
        this.realPart = realPart;
    }

    complexNumber(double realPart, double imaginaryPart) {
        this.realPart = realPart;
        this.imaginaryPart = imaginaryPart;
    }

    public  double abs() {
        return Math.hypot(realPart, imaginaryPart);
    }

    public complexNumber plus(complexNumber b) {
        complexNumber a = this;
        double real = a.realPart + b.realPart;
        double imag = a.imaginaryPart + b.imaginaryPart;
        return new complexNumber(real, imag);
    }

    public complexNumber minus(complexNumber b) {
        complexNumber a = this;
        double real = a.realPart - b.realPart;
        double imag = a.imaginaryPart - b.imaginaryPart;
        return new complexNumber(real, imag);
    }

    public complexNumber multiply(complexNumber b) {
        complexNumber a = this;
        double real = a.realPart * b.realPart - a.imaginaryPart * b.imaginaryPart;
        double imag = a.realPart * b.imaginaryPart + a.imaginaryPart * b.realPart;
        return new complexNumber(real, imag);
    }

    public complexNumber multiply(double alpha) {
        return new complexNumber(alpha * realPart, alpha * imaginaryPart);
    }

    public complexNumber conjugate() {
        return new complexNumber(realPart, -imaginaryPart);
    }

    public complexNumber exp() {
        return new complexNumber(Math.exp(realPart) * Math.cos(imaginaryPart), Math.exp(realPart) * Math.sin(imaginaryPart));
    }

    public complexNumber divides(double value) {
        complexNumber a = this;
        a.realPart /= value;
        a.imaginaryPart /= value;
        return a;
    }

    public double getRealPart() { return realPart; }

    public String toString() {
        if (imaginaryPart == 0) return realPart + "";
        if (realPart == 0) return imaginaryPart + "i";
        if (imaginaryPart <  0) return realPart + " - " + (-imaginaryPart) + "i";
        return realPart + " + " + imaginaryPart + "i";
    }
}
