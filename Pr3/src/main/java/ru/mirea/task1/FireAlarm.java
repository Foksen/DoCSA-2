package ru.mirea.task1;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FireAlarm {

    private static final int TEMPERATURE_THRESHOLD = 25;
    private static final int CO2_THRESHOLD = 70;

    private final Sensor temperatureSensor;
    private final Sensor co2Sensor;

    public FireAlarm(Sensor temperatureSensor, Sensor co2Sensor) {
        this.temperatureSensor = temperatureSensor;
        this.co2Sensor = co2Sensor;
    }

    public void observe() {
        Observable.combineLatest(temperatureSensor.getSensorData(), co2Sensor.getSensorData(), SensorsData::new)
                .subscribe(new Observer<SensorsData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        log.info("Observation started");
                    }

                    @Override
                    public void onNext(@NonNull SensorsData sensorsData) {
                        log.debug("Data from sensors was received: {}", sensorsData);
                        if (TEMPERATURE_THRESHOLD < sensorsData.temperature) {
                            log.error("ALARM! TEMPERATURE EXCEEDS THRESHOLD VALUE ({} > {})",
                                    sensorsData.temperature, TEMPERATURE_THRESHOLD);
                        }
                        if (CO2_THRESHOLD < sensorsData.co2) {
                            log.error("ALARM! CO2 EXCEEDS THRESHOLD VALUE ({} > {})",
                                    sensorsData.co2, CO2_THRESHOLD);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        log.error("Unknown error", e);
                    }

                    @Override
                    public void onComplete() {
                        log.info("Observation finished");
                    }
                });
    }

    record SensorsData(int temperature, int co2) { }
}
