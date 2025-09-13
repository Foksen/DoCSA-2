package ru.mirea.task1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ArrayGenerator {

    private static final int N = 10000;

    private static final String filename = "tmp/array.txt";

    public static void main(String[] args) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < N; ++i) {
                writer.write(String.valueOf((int) (Math.random() * 10000)));
                writer.newLine();
            }
        }
    }
}
