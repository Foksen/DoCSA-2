package ru.mirea.task3;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class FileGenerator implements Runnable {

    private static final int GENERATING_MIN_DELAY_MS = 3000;
    private static final int GENERATING_MAX_DELAY_MS = 6000;
    private static final int MIN_FILE_SIZE = 100;
    private static final int MAX_FILE_SIZE = 500;

    private final BlockingQueue<File> queue;
    private final Random random = new Random();

    public FileGenerator(BlockingQueue<File> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                File file = generateRandomFile();
                queue.put(file);
                log.debug("New file {} was generated and put into queue", file);
                Thread.sleep(random.nextInt(GENERATING_MAX_DELAY_MS - GENERATING_MIN_DELAY_MS + 1) + GENERATING_MIN_DELAY_MS);
            }
        } catch (InterruptedException e) {
            log.debug("Generator was stopped");
        }
    }

    private File generateRandomFile() {
        int size = random.nextInt(MAX_FILE_SIZE - MIN_FILE_SIZE + 1) + MIN_FILE_SIZE;
        FileType type = FileType.values()[random.nextInt(FileType.values().length)];
        return new File(size, type);
    }
}
