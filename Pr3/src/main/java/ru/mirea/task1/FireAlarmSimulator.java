package ru.mirea.task1;

public class FireAlarmSimulator {

    public static void main(String[] args) throws InterruptedException {
        Sensor temperatureSensor = new TemperatureSensor();
        Sensor co2Sensor = new CO2Sensor();
        FireAlarm fireAlarm = new FireAlarm(temperatureSensor, co2Sensor);

        fireAlarm.observe();

        Thread.sleep(30000);
    }
}
