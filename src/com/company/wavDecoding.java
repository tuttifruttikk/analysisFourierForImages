package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class wavDecoding {
    private final int N = 800;
    private ArrayList<ArrayList<Double>> u = new ArrayList<>();
    private ArrayList<ArrayList<complexNumber>> F = new ArrayList<>();
    private ArrayList<ArrayList<complexNumber>> ermF = new ArrayList<>();
    private ArrayList<Double> f1 = new ArrayList<>();
    private ArrayList<Double> f2 = new ArrayList<>();

    public void decode() {
        double []first = {710, 780, 860, 950};
        double []second = {1220, 1350, 1490, 1650};
        char [][]symbols = {
                {'1', '2', '3', 'A'},
                {'4', '5', '6', 'B'},
                {'7', '8', '9', 'C'},
                {'*', '0', '#', 'D'}
        };
        for (int i = 0; i < f1.size(); ++i) {
            double F1 = f1.get(i);
            for (int j = 0; j < 4; ++j) {
                if (F1 - first[j] == 0) {
                    double F2 = f2.get(i);
                    for (int k = 0; k < 4; ++k) {
                        if (F2 - second[k] == 0) {
                            System.out.print(symbols[j][k] + "\t");
                        }
                    }
                }
            }
        }
    }

    public void createFile(double[] array, String name) throws IOException {
        File file = new File(name + ".txt");
        FileWriter fileWriter = new FileWriter(file);
        for (int i = 0; i < array.length; ++i) {
            fileWriter.write(i + "     " + array[i] + "\n");
            fileWriter.flush();
        }
    }

    public void createComplexFile(ArrayList<ArrayList<complexNumber>> signal, String name) throws IOException {
        FileWriter file = new FileWriter(name + ".txt");
        for (int i = 0; i < signal.size(); ++i) {
            for (int j = 0; j < signal.get(0).size(); ++j) {
                complexNumber tmp = signal.get(i).get(j);
                file.write(j + "\t\t" + tmp.abs() + "\n");
                file.flush();
            }
        }
    }

    public void createMatrix() {
        for (int i = 0; i < N; ++i) {
            F.add(new ArrayList<>());
            ermF.add(new ArrayList<>());
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                complexNumber tmp = new complexNumber(0, 2 * Math.PI * j / N * i);
                tmp = tmp.exp();
                F.get(i).add(tmp);
                ermF.get(j).add(tmp.conjugate());
            }
        }
    }

    public int findMax(ArrayList<ArrayList<complexNumber>> u, int start, int finish) {
        double A = 0;
        int result = 0;

        for (int i = start; i < finish; ++i) {
            if (u.get(0).get(i).getRealPart() >= A) {
                A = u.get(0).get(i).getRealPart();
                result = i + 1;
            }
        }

        return result;
    }

    public ArrayList<ArrayList<complexNumber>> multiplyDoubleToComplex(ArrayList<ArrayList<Double>> firstMatrix, ArrayList<ArrayList<complexNumber>> secondMatrix) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();

        for (int i = 0; i < firstMatrix.size(); ++i) {
            result.add(new ArrayList<>());
        }

        for (int i = 0; i < secondMatrix.get(0).size(); ++i) {
            result.get(0).add(new complexNumber(0, 0));
        }

        for (int i = 0; i < firstMatrix.size(); i++) {
            for (int k = 0; k < firstMatrix.get(0).size(); k++) {
                for (int j = 0; j < secondMatrix.get(0).size(); j++) {
                    complexNumber tmp = secondMatrix.get(k).get(j);
                    tmp = tmp.multiply(firstMatrix.get(i).get(k));
                    result.get(i).set(j, result.get(i).get(j).plus(tmp));
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        wavDecoding wavDecoding = new wavDecoding();
        WavFile wavFile = WavFile.openWavFile(new File("6.wav"));

        ArrayList<ArrayList<complexNumber>> U1;

        long size = wavFile.getFramesRemaining();
        double[] buffer = new double[(int) size];

        wavFile.readFrames(buffer, (int) size);
        wavDecoding.createMatrix();

        for (int t = 0; t < size / wavDecoding.N; ++t) {
            if (t % 2 == 1)
                continue;
            wavDecoding.u.add(new ArrayList<>());
            for (int i = 0; i < wavDecoding.N; ++i) {
                wavDecoding.u.get(0).add(buffer[i + t * wavDecoding.N]);
            }

            U1 = wavDecoding.multiplyDoubleToComplex(wavDecoding.u, wavDecoding.ermF);
            for (int i = 0; i < wavDecoding.u.get(0).size(); i++) {
                complexNumber tmp = U1.get(0).get(i);
                tmp.setRealPart(tmp.abs());
                U1.get(0).set(i, tmp);
            }
            wavDecoding.createComplexFile(U1, "spectral");
            double F1 = wavDecoding.findMax(U1, 0, 100) * 10;
            double F2 = wavDecoding.findMax(U1, 100, 200) * 10;
            System.out.print("f1 = " + F1 + "\t");
            System.out.println("f2 = " + F2);
            wavDecoding.f1.add(F1);
            wavDecoding.f2.add(F2);
            wavDecoding.u.clear();
        }
        wavDecoding.decode();
        wavDecoding.createFile(buffer, "audio");
    }
}
