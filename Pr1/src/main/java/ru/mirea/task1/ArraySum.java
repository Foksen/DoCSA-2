package ru.mirea.task1;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.Math.ceilDiv;

@Slf4j
public class ArraySum {

    private static final String filename = "tmp/array.txt";

    private static final String DELIMITER = "-".repeat(70);

    private static final int MIN_CHUNK_CAPACITY = 500;

    public static void main(String[] args) throws IOException {
        int[] arr = readFile();

        log.info(DELIMITER);
        executeAndMeasure("sequential sum", ArraySum::sequentialSum, arr);
        executeAndMeasure("thread sum", ArraySum::threadSum, arr);
        executeAndMeasure("fork join sum", ArraySum::forkJoinSum, arr);
    }

    private static int[] readFile() throws IOException {
        try (var reader = new BufferedReader(new FileReader(filename))) {
            return reader.lines().mapToInt(Integer::parseInt).toArray();
        }
    }

    private static long sequentialSum(int[] arr) {
        return Arrays.stream(arr).map(ArraySum::waitArrayElement).sum();
    }

    private static long threadSum(int[] arr) {
        AtomicLong totalSum = new AtomicLong(0);
        int threadsCount = calcOptimalThreadsCount(arr.length);
        int chunkSize = Math.ceilDiv(arr.length, threadsCount);

        List<Thread> threads = IntStream.range(0, threadsCount)
                .mapToObj(i -> {
                    int indexL = i * chunkSize;
                    int indexR = Math.min(arr.length, (i + 1) * chunkSize);
                    return new Thread(() -> {
                        long sum = Arrays.stream(arr, indexL, indexR).map(ArraySum::waitArrayElement).sum();
                        totalSum.addAndGet(sum);
                    });
                })
                .peek(Thread::start)
                .toList();

        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return totalSum.get();
    }

    private static long forkJoinSum(int[] arr) {
        int threadsCount = calcOptimalThreadsCount(arr.length);
        int chunkSize = Math.ceilDiv(arr.length, threadsCount);

        ForkJoinPool pool = new ForkJoinPool(threadsCount);

        List<ForkJoinTask<Long>> tasks = IntStream.range(0, threadsCount)
                .mapToObj(i -> {
                    int indexL = i * chunkSize;
                    int indexR = Math.min(arr.length, (i + 1) * chunkSize);
                    return pool.submit(() -> Arrays.stream(arr, indexL, indexR)
                            .map(ArraySum::waitArrayElement)
                            .asLongStream()
                            .sum());
                })
                .toList();

        long totalSum = tasks.stream()
                .mapToLong(ForkJoinTask::join)
                .sum();

        pool.shutdown();
        return totalSum;
    }

    private static int calcOptimalThreadsCount(int len) {
        int maxThreadsCount = Runtime.getRuntime().availableProcessors();
        int requiredThreadsCount = Math.max(1, ceilDiv(len, MIN_CHUNK_CAPACITY));
        int optimalThreadsCount = Math.min(requiredThreadsCount, maxThreadsCount);
        log.info("Threads were used: {}", optimalThreadsCount);
        return optimalThreadsCount;
    }

    private static int waitArrayElement(int a) {
        try {
            Thread.sleep(1);
            return a;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, R> void executeAndMeasure(String funcName, Function<T, R> func, T arg) {
        long startTime = System.currentTimeMillis();
        Runtime.getRuntime().gc();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        R result = func.apply(arg);

        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long endTime = System.currentTimeMillis();

        double durationSec = (endTime - startTime) / 1000.;
        long memoryConsumptionKb = (endMemory - startMemory) / 1024;

        log.info("Result '{}': {}", funcName, result);
        log.info("Time '{}': {} s", funcName, durationSec);
        log.info("Memory '{}': {} KB", funcName, memoryConsumptionKb);
        log.info(DELIMITER);
    }
}
