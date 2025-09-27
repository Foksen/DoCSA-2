package ru.mirea.task4;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileProcessor {

    private static final long FILE_PROCESSING_COEFFICIENT = 10L;

    private final FileType type;

    public FileProcessor(FileType type) {
        this.type = type;
    }

    public Disposable subscribe(Observable<File> fileObservable) {
        return fileObservable
                .concatMap(file -> Observable
                        .just(file)
                        .delay(file.size() * FILE_PROCESSING_COEFFICIENT, java.util.concurrent.TimeUnit.MILLISECONDS)
                        .doOnNext(f -> log.debug("File {} processing duration: {} ms", f, f.size() * FILE_PROCESSING_COEFFICIENT)))
                .subscribe();
    }
}
