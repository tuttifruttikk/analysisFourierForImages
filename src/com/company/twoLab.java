package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class twoLab {
    private final ArrayList<Double> u1 = new ArrayList<>();
    private final ArrayList<Double> u2 = new ArrayList<>();
    private final ArrayList<Double> time = new ArrayList<>();
    private final ArrayList<Double> uShift = new ArrayList<>();

    private final double f1 = 2000;
    private final double f2 = 1000;
    private final double T = 1d / 500;
    private final double dt = T / 1000;
    private final double a = -T / 2;
    private final double b = T / 2;

    private final double shift = 200;



    private final ArrayList<ArrayList<complexNumber>> F = new ArrayList<>();
    private final ArrayList<ArrayList<complexNumber>> ermF = new ArrayList<>();

    public double createFunction(double f, double t) {
        return Math.sin(2 * Math.PI * f * t);
    }

    public void createTime() {
        for (double t = a; t < b; t += dt) {
            time.add(t);
        }
    }

    public void createFile(ArrayList<Double> signal, String name) throws IOException {
        FileWriter file = new FileWriter(name + ".txt");
        for (int i = 0; i < signal.size(); ++i) {
            String tmp = signal.get(i).toString();
            file.write(time.get(i) + "\t\t" + tmp + "\n");
            file.flush();
        }
    }

    public void createDirectConversionFile(ArrayList<ArrayList<complexNumber>> signal, String name) throws IOException {
        FileWriter file = new FileWriter(name + ".txt");
        for (int i = 0; i < signal.size(); ++i) {
            for (int j = 0; j < signal.get(0).size(); ++j) {
                complexNumber tmp = signal.get(i).get(j);
                file.write(i + "\t\t" + tmp.abs() + "\n");
                file.flush();
            }
        }
    }

    public void createInvertConversionFile(ArrayList<ArrayList<complexNumber>> signal, String name) throws IOException {
        FileWriter file = new FileWriter(name + ".txt");
        for (int i = 0; i < signal.size(); ++i) {
            for (int j = 0; j < signal.get(0).size(); ++j) {
                complexNumber tmp = signal.get(i).get(j);
                file.write(time.get(i) + "\t\t" + tmp + "\n");
                file.flush();
            }
        }
    }

    public void createSignal(ArrayList<Double> signal, double f) throws IOException {
        for (double t = a; t < b; t += dt) {
            signal.add(createFunction(f, t));
        }
    }

    public ArrayList<ArrayList<complexNumber>> multiplyDoubleToComplex(ArrayList<Double> originalSignal, ArrayList<ArrayList<complexNumber>> directionSignal) {
        return multiplyDoubleComplex(originalSignal, directionSignal);
    }

    static ArrayList<ArrayList<complexNumber>> multiplyDoubleComplex(ArrayList<Double> originalSignal, ArrayList<ArrayList<complexNumber>> directionSignal) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();
        for (int i = 0; i < originalSignal.size(); ++i) {
            result.add(new ArrayList<>());
        }
        for (int i = 0; i < directionSignal.get(0).size(); ++i) {
            complexNumber tmp = new complexNumber(0, 0);
            for (int j = 0; j < originalSignal.size(); ++j) {
                complexNumber complexElement = directionSignal.get(j).get(i);
                double doubleElement = originalSignal.get(j);
                tmp = tmp.plus(complexElement.multiply(doubleElement));
            }
            result.get(i).add(tmp);
        }
        return result;
    }

    public ArrayList<ArrayList<complexNumber>> multiplyComplexToComplex(ArrayList<ArrayList<complexNumber>> firstMatrix, ArrayList<ArrayList<complexNumber>> secondMatrix) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();
        for (int i = 0; i < firstMatrix.size(); ++i) {
            result.add(new ArrayList<>());
        }

        for (int j = 0; j < secondMatrix.size(); ++j) {
            complexNumber tmp = new complexNumber(0, 0);
            for (int k = 0; k < firstMatrix.size(); ++k) {
                complexNumber firstElement = secondMatrix.get(k).get(j);
                complexNumber secondElement = firstMatrix.get(k).get(0);
                tmp = tmp.plus(firstElement.multiply(secondElement));
            }
            result.get(j).add(tmp);
        }
        return result;
    }

    public ArrayList<ArrayList<complexNumber>> multiplyNumberToComplex(ArrayList<ArrayList<complexNumber>> matrix, double value) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();
        for (int i = 0; i < matrix.size(); ++i) {
            result.add(new ArrayList<>());
        }

        for (int i = 0; i < matrix.size(); ++i) {
            for (int j = 0; j < matrix.get(0).size(); ++j) {
                complexNumber tmp = matrix.get(i).get(j);
                result.get(i).add(tmp.multiply(value));
            }
        }
        return result;
    }


    public void createMatrix(ArrayList<Double> u1, ArrayList<ArrayList<complexNumber>> f, ArrayList<ArrayList<complexNumber>> ermF) {
        double N = u1.size();
        for (int i = 0; i < N; ++i) {
            f.add(new ArrayList<>());
            ermF.add(new ArrayList<>());
        }
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                complexNumber tmp = new complexNumber(0, 2 * Math.PI * j / N * i);
                tmp = tmp.exp();
                f.get(i).add(tmp);
                ermF.get(j).add(tmp.conjugate());
            }
        }
    }

    public ArrayList<Double> multiplyNumberToDouble(ArrayList<Double> vector, double value) {
        ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < vector.size(); ++i) {
            result.add(vector.get(i) * value);
        }
        return result;
    }

    public ArrayList<ArrayList<complexNumber>> sumComplexMatrix(ArrayList<ArrayList<complexNumber>> firstMatrix, ArrayList<ArrayList<complexNumber>> secondMatrix) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();
        for (int i = 0; i < firstMatrix.size(); ++i) {
            result.add(new ArrayList<>());
        }

        for (int i = 0; i < firstMatrix.size(); ++i) {
            for (int j = 0; j < secondMatrix.get(0).size(); ++j) {
                complexNumber first = firstMatrix.get(i).get(j);
                complexNumber second = secondMatrix.get(i).get(j);
                result.get(i).add(first.plus(second));
            }
        }

        return result;
    }

    public void linearityProperties() throws IOException {
        int a1 = 10;
        int a2 = 15;
        ArrayList<Double> u = multiplyNumberToDouble(u1, a1);
        ArrayList<Double> tmp = multiplyNumberToDouble(u2, a2);
        for (int i = 0; i < u2.size(); ++i) {
            u.set(i, tmp.get(i) + u.get(i));
        }

        ArrayList<ArrayList<complexNumber>> un1 = multiplyDoubleToComplex(u1, ermF);
        ArrayList<ArrayList<complexNumber>> un2 = multiplyDoubleToComplex(u2, ermF);

        un1 = multiplyNumberToComplex(un1, a1);
        un2 = multiplyNumberToComplex(un2, a2);

        double N = u.size();

        ArrayList<ArrayList<complexNumber>> unPlus = sumComplexMatrix(un1, un2);
        createDirectConversionFile(unPlus, "uPlusDirect");
        ArrayList<ArrayList<complexNumber>> uPlusInverseConversion = multiplyComplexToComplex(multiplyNumberToComplex(unPlus, 1 / N), F);

        createFile(u, "uPlus");
        createInvertConversionFile(uPlusInverseConversion, "uPlusInversion");
    }

    public void signalShift() throws IOException {
        for (double t = a; t < b; t += dt) {
            double tmp = Math.sin(2 * Math.PI * f1 * t - shift);
            uShift.add(tmp);
        }

        createFile(uShift, "shiftSignal");
        ArrayList<ArrayList<complexNumber>> shiftDirection = multiplyDoubleToComplex(uShift, ermF);
        createDirectConversionFile(shiftDirection, "shiftDirection");

    }

    public void equalityPars(ArrayList<ArrayList<complexNumber>> directConversion) {
        double sumOriginal = 0;
        for (int i = 0; i < u1.size(); ++i) {
            sumOriginal += u1.get(i) * u1.get(i);
        }

        double sumDirect = 0;
        for (int i = 0; i < directConversion.size(); ++i) {
            for (int j = 0; j < directConversion.get(0).size(); ++j) {
                complexNumber tmp = directConversion.get(i).get(j).multiply(directConversion.get(i).get(j));
                sumDirect += tmp.abs();
            }
        }

        double N = u1.size();
        sumDirect *= 1 / N;
        System.out.println("Direct conversion u: " + sumDirect + "\nOriginal u: " + sumOriginal);
    }

    public static void main(String[] args) throws IOException {
        twoLab exercise = new twoLab();
        exercise.createTime();
        exercise.createSignal(exercise.u1, exercise.f1);
        exercise.createFile(exercise.u1, "originalSignal");
        exercise.createMatrix(exercise.u1, exercise.F, exercise.ermF);
        ArrayList<ArrayList<complexNumber>> direction = exercise.multiplyDoubleToComplex(exercise.u1, exercise.ermF);
        double N = exercise.u1.size();
        ArrayList<ArrayList<complexNumber>> inversion = exercise.multiplyComplexToComplex(exercise.multiplyNumberToComplex(direction, 1 / N), exercise.F);
        exercise.createDirectConversionFile(direction, "directionConversion");
        exercise.createInvertConversionFile(inversion, "inversionConversion");

        /* 2.2 */
        exercise.createSignal(exercise.u2, exercise.f2);
        exercise.linearityProperties();
        exercise.signalShift();
        exercise.equalityPars(direction);
    }
}
