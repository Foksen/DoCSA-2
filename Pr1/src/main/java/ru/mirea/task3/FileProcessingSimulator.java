package ru.mirea.task3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileProcessingSimulator {

    private static final long SIMULATION_DURATION_SEC = 120;

    public static void main(String[] args) throws InterruptedException {
        final BlockingQueue<File> queue = new LinkedBlockingQueue<>(5);
        Thread generator = new Thread(new FileGenerator(queue), "GENERATOR");
        List<Thread> processors = new ArrayList<>();
        for (FileType type : FileType.values()) {
            processors.add(new Thread(new FileProcessor(type, queue), "PROCESSOR-" + type.name()));
        }

        generator.start();
        processors.forEach(Thread::start);

        Thread.sleep(SIMULATION_DURATION_SEC * 1000);

        generator.interrupt();
        for (Thread p : processors) p.interrupt();
        generator.join();
        for (Thread p : processors) p.join();
    }
}
