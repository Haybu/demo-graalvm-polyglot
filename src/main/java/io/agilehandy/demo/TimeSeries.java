package io.agilehandy.demo;

import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TimeSeries {

    private LocalDateTime time;
    private Double value;
    private String key;

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
