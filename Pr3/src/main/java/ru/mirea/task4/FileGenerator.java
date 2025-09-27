package ru.mirea.task4;

import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class FileGenerator {

    private static final int GENERATING_MIN_DELAY_MS = 3000;
    private static final int GENERATING_MAX_DELAY_MS = 6000;
    private static final int MIN_FILE_SIZE = 100;
    private static final int MAX_FILE_SIZE = 500;

    private final Random random = new Random();

    @SuppressWarnings("BusyWait")
    public Observable<File> fileStream() {
        return Observable.<File>create(emitter -> {
            while (!emitter.isDisposed()) {
                File file = generateRandomFile();
                emitter.onNext(file);
                log.debug("New file {} was generated", file);
                int delay = random.nextInt(GENERATING_MAX_DELAY_MS - GENERATING_MIN_DELAY_MS + 1) + GENERATING_MIN_DELAY_MS;
                Thread.sleep(delay);
            }
            emitter.onComplete();
        }).share();
    }

    private File generateRandomFile() {
        int size = random.nextInt(MAX_FILE_SIZE - MIN_FILE_SIZE + 1) + MIN_FILE_SIZE;
        FileType type = FileType.values()[random.nextInt(FileType.values().length)];
        return new File(size, type);
    }
}
