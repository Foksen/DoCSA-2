package ru.mirea.task3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
public class FileProcessor implements Runnable {

    private static final long FILE_PROCESSING_COEFFICIENT = 10L;
    private static final int DELAY_ON_EMPTY_QUEUE = 100;

    private final FileType type;
    private final BlockingQueue<File> queue;

    public FileProcessor(FileType type, BlockingQueue<File> queue) {
        this.type = type;
        this.queue = queue;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                File file = null;
                synchronized (queue) {
                    for (File f : queue) {
                        if (f.type() == type) {
                            queue.remove(f);
                            file = f;
                            break;
                        }
                    }
                }
                if (file != null) {
                    long startTime = System.currentTimeMillis();
                    Thread.sleep(file.size() * FILE_PROCESSING_COEFFICIENT);
                    log.debug("File {} processing duration: {} ms", file, System.currentTimeMillis() - startTime);
                } else {
                    Thread.sleep(DELAY_ON_EMPTY_QUEUE);
                }
            }
        } catch (InterruptedException e) {
            log.debug("Processor was stopped");
        }
    }
}
