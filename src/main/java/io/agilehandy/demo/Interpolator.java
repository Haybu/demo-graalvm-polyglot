package io.agilehandy.demo;

import java.util.List;

interface Interpolator {
    void echo();
    void dataframe();
    List<TimeSeries> interpolate(int seconds);
    int sum(int x, int y, int z);
}
