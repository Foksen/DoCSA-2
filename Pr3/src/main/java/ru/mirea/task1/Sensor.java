package ru.mirea.task1;

import io.reactivex.rxjava3.core.Observable;

public interface Sensor {
    Observable<Integer> getSensorData();
}
