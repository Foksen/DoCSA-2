package ru.mirea.task4;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class FileProcessingSimulator {

    private static final long SIMULATION_DURATION_SEC = 120;

    public static void main(String[] args) throws InterruptedException {
        FileGenerator fileGenerator = new FileGenerator();
        Observable<File> sharedStream = fileGenerator.fileStream()
                .publish()  // hot/connected!
                .autoConnect(FileType.values().length);

        CompositeDisposable composite = new CompositeDisposable();

        for (FileType type: FileType.values()) {
            Observable<File> streamForType = sharedStream.filter(f -> f.type() == type);
            FileProcessor processor = new FileProcessor(type);
            composite.add(processor.subscribe(streamForType));
        }

        Thread.sleep(SIMULATION_DURATION_SEC * 1000);

        composite.dispose();
    }
}
