package io.agilehandy.demo;

import java.time.LocalDateTime;

public class TimeSeries {

    public LocalDateTime time;
    public Double value;
    public String key;

    public TimeSeries() {}

    public TimeSeries(LocalDateTime time, Double value, String key) {
        this.time = time;
        this.value = value;
        this.key = key;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "key: " + key + " time: " + time + " value " + value;
    }

}
