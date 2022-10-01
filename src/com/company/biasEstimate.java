package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class biasEstimate {
    public static void main(String[] args) {
        MyImage firstImage = new MyImage("tank_first.bmp");
        MyImage secondImage = new MyImage("tank_second.bmp");

        ArrayList<ArrayList<Double>> image1 = new ArrayList<>();
        ArrayList<ArrayList<Double>> image2 = new ArrayList<>();

        biasEstimate.initImages(firstImage, secondImage, image1, image2);

        ArrayList<ArrayList<complexNumber>> F1 = new ArrayList<>();
        ArrayList<ArrayList<complexNumber>> ermF1 = new ArrayList<>();
        biasEstimate.createF(F1, ermF1, firstImage.width);

        ArrayList<ArrayList<complexNumber>> F2 = new ArrayList<>();
        ArrayList<ArrayList<complexNumber>> ermF2 = new ArrayList<>();
        biasEstimate.createF(F2, ermF2, firstImage.height);

        ArrayList<ArrayList<complexNumber>> U1 = biasEstimate.multiplyDoubleMToComplexM(image1, ermF1);
        ArrayList<ArrayList<complexNumber>> U2 = biasEstimate.multiplyDoubleMToComplexM(image2, ermF1);

        U1 = biasEstimate.multiplyComplexMToComplexM(biasEstimate.trancpMatrix(U1), ermF2);
        U2 = biasEstimate.multiplyComplexMToComplexM(biasEstimate.trancpMatrix(U2), ermF2);

        ArrayList<ArrayList<complexNumber>> G = biasEstimate.multiplyElem(biasEstimate.matrix(U1), biasEstimate.trancpMatrix(U2));
        G = divElem(G);

        ArrayList<ArrayList<complexNumber>> g = multiplyComplexMToComplexM(biasEstimate.multiplyNumberToComplexM(G, 1 / (double) firstImage.width), F1);
        g = biasEstimate.matrix(g);
        g = biasEstimate.multiplyComplexMToComplexM(biasEstimate.multiplyNumberToComplexM(g, 1 / (double) firstImage.height), F2);

        ArrayList<Integer> result = biasEstimate.findMaxIndex(biasEstimate.matrix(g));
        int x = firstImage.width - result.get(0);
        int y = result.get(1);
        biasEstimate.test(firstImage.bufferedImage, x, y);
    }

    public static void test(BufferedImage image, int x, int y) {
        System.out.println("X = " + x + "\nY = " + y);
        image.setRGB(x, y, new Color(0, 255 , 0).getRGB());
        try {
            ImageIO.write(image, "bmp", new File("imageWithPoint.bmp"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static ArrayList<Integer> findMaxIndex(ArrayList<ArrayList<complexNumber>> matrix) {
        ArrayList<Integer> result = new ArrayList<>();
        int x = 0, y = 0;
        double max = 0;
        for (int i = 0; i < matrix.size(); ++i) {
            for (int j = 0; j < matrix.get(0).size(); ++j) {
                if (matrix.get(i).get(j).abs() > max) {
                    max = matrix.get(i).get(j).abs();
                    x = j;
                    y = i;
                }
            }
        }
        result.add(x);
        result.add(y);
        return result;
    }

    public static void createF(ArrayList<ArrayList<complexNumber>> F, ArrayList<ArrayList<complexNumber>> ermCongF, int value) {
        for (int i = 0; i < value; ++i) {
            F.add(new ArrayList<>());
            ermCongF.add(new ArrayList<>());
        }
        for (int i = 0; i < value; ++i) {
            for (int j = 0; j < value; ++j) {
                complexNumber complex = new complexNumber(0, 2 * Math.PI * (double) j / (double) value * i);
                complex = complex.exp();
                F.get(i).add(complex);
                ermCongF.get(j).add(complex.conjugate());
            }
        }
    }

    public static void initImages(MyImage firstImage, MyImage secondImage, ArrayList<ArrayList<Double>> image1, ArrayList<ArrayList<Double>> image2) {
        double[][] colorImage1 = firstImage.getBlue();
        double[][] colorImage2 = secondImage.getBlue();
        for (int i = 0; i < firstImage.height; ++i) {
            image1.add(new ArrayList<>());
        }
        for (int i = 0; i < secondImage.height; ++i) {
            image2.add(new ArrayList<>());
        }
        for (int i = 0; i < firstImage.height; ++i) {
            for (int j = 0; j < firstImage.width; ++j) {
                image1.get(i).add(colorImage1[i][j]);
            }
        }
        for (int i = 0; i < secondImage.height; ++i) {
            for (int j = 0; j < secondImage.width; ++j) {
                image2.get(i).add(colorImage2[i][j]);
            }
        }
    }

    public static ArrayList<ArrayList<complexNumber>> matrix(ArrayList<ArrayList<complexNumber>> matrix) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();
        for (int i = 0; i < matrix.get(0).size(); ++i) {
            result.add(new ArrayList<>());
        }
        for (int i = 0; i < matrix.size(); ++i) {
            for (int j = 0; j < matrix.get(0).size(); ++j) {
                result.get(j).add(matrix.get(i).get(j));
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<complexNumber>> divElem(ArrayList<ArrayList<complexNumber>> matrix) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();
        for (int i = 0; i < matrix.size(); ++i) {
            result.add(new ArrayList<>());
        }
        for (int i = 0; i < matrix.size(); ++i) {
            for (int j = 0; j < matrix.get(0).size(); ++j) {
                double tmp = matrix.get(i).get(j).abs();
                complexNumber complex = matrix.get(i).get(j);
                result.get(i).add(complex.divides(tmp));
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<complexNumber>> multiplyElem(ArrayList<ArrayList<complexNumber>> firstMatrix, ArrayList<ArrayList<complexNumber>> secondMatrix) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();
        for (int i = 0; i < firstMatrix.size(); ++i) {
            result.add(new ArrayList<>());
        }
        for (int i = 0; i < firstMatrix.size(); ++i) {
            for (int j = 0; j < firstMatrix.get(0).size(); ++j) {
                result.get(i).add(firstMatrix.get(i).get(j).multiply(secondMatrix.get(i).get(j)));
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<complexNumber>> multiplyComplexMToComplexM(ArrayList<ArrayList<complexNumber>> firstMatrix, ArrayList<ArrayList<complexNumber>> secondMatrix) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();
        for (int i = 0; i < firstMatrix.size(); ++i) {
            result.add( new ArrayList<>());
        }
        for (int i = 0; i < firstMatrix.size(); ++i) {
            for (int j = 0; j < secondMatrix.get(0).size(); ++j) {
                complexNumber tmp = new complexNumber(0, 0);
                for (int k = 0; k < firstMatrix.get(0).size(); ++k) {
                    complexNumber firstElem = secondMatrix.get(k).get(j);
                    complexNumber secondElem = firstMatrix.get(i).get(k);
                    tmp = tmp.plus(firstElem.multiply(secondElem));
                }
                result.get(i).add(tmp);
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<complexNumber>> trancpMatrix(ArrayList<ArrayList<complexNumber>> matrix) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();
        for (int i = 0; i < matrix.get(0).size(); ++i) {
            result.add(new ArrayList<>());
        }
        for (int i = 0; i < matrix.size(); ++i) {
            for (int j = 0; j < matrix.get(0).size(); ++j) {
                complexNumber complex = matrix.get(i).get(j);
                result.get(j).add(complex.conjugate());
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<complexNumber>> multiplyDoubleMToComplexM(ArrayList<ArrayList<Double>> firstMatrix, ArrayList<ArrayList<complexNumber>> secondMatrix) {
        ArrayList<ArrayList<complexNumber>> result = new ArrayList<>();

        for (int i = 0; i < firstMatrix.size(); ++i) {
            result.add(new ArrayList<>());
        }
        for (int i = 0; i < firstMatrix.size(); ++i) {
            for (int j = 0; j < secondMatrix.get(0).size(); ++j) {
                result.get(i).add(new complexNumber(0, 0));
            }
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

    public static ArrayList<ArrayList<complexNumber>> multiplyNumberToComplexM(ArrayList<ArrayList<complexNumber>> matrix, double value) {
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

    public static class MyImage {
        public File file;
        public int width, height;
        public BufferedImage bufferedImage;
        public double[][] red, green, blue;

        public MyImage(String name) {
            file = new File(name);
            try {
                bufferedImage = ImageIO.read(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();

            red = new double[height][];
            green = new double[height][];
            blue = new double[height][];
            for (int i = 0; i < height; ++i) {
                red[i] = new double[width];
                green[i] = new double[width];
                blue[i] = new double[width];
            }

            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    Color color = new Color(bufferedImage.getRGB(j, i));
                    red[i][j] = color.getRed();
                    green[i][j] = color.getGreen();
                    blue[i][j] = color.getBlue();
                }
            }
        }

        public double[][] getRed() {
            return red;
        }

        public double[][] getGreen() {
            return green;
        }

        public double[][] getBlue() {
            return blue;
        }
    }
}