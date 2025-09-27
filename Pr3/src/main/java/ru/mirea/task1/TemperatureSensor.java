package ru.mirea.task1;

import io.reactivex.rxjava3.core.Observable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TemperatureSensor implements Sensor {

    private final Random random = new Random();

    @Override
    public Observable<Integer> getSensorData() {
        return Observable.interval(1, TimeUnit.SECONDS).map(t -> 15 + random.nextInt(16));
    }
}
