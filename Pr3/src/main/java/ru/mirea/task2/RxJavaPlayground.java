package ru.mirea.task2;

import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
public class RxJavaPlayground {

    public static void main(String[] args) {
        Random random = new Random();

        log.debug("[stream1] Begin");
        Observable.range(0, 10)
                .map(i -> random.nextInt(1001))
                .filter(val -> val > 500)
                .subscribe(
                        i -> log.debug("{}", i),
                        e -> log.error("Error", e),
                        () -> log.debug("[stream1] End")
                );

        log.debug("[stream2] Begin");
        Observable<Integer> stream1 = Observable.range(0, 10).map(i -> random.nextInt(10));
        Observable<Integer> stream2 = Observable.range(0, 10).map(i -> random.nextInt(10));
        Observable.concat(stream1, stream2)
                .subscribe(
                        i -> log.debug("{}", i),
                        e -> log.error("Error", e),
                        () -> log.debug("[stream2] End")
                );

        log.debug("[stream3] Begin");
        Observable.range(0, 10)
                .map(i -> random.nextInt())
                .take(5)
                .subscribe(
                        i -> log.debug("{}", i),
                        e -> log.error("Error", e),
                        () -> log.debug("[stream3] End")
                );
    }
}
