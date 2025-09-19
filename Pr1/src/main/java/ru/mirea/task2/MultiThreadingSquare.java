package ru.mirea.task2;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadingSquare {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.println("Input a number (or 'exit'): ");

        while (true) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit")) break;
            try {
                int number = Integer.parseInt(line);
                executor.submit(() -> {
                    int delaySec = 1 + random.nextInt(5);
                    Thread.sleep(delaySec * 1000L);
                    int result = number * number;
                    System.out.println(number + " * " + number + " = " + result + " (delay " + delaySec + " s)");
                    return result;
                });
            } catch (NumberFormatException e) {
                System.out.println("Input a correct number (or 'exit'): ");
            }
        }

        executor.shutdown();
    }
}
