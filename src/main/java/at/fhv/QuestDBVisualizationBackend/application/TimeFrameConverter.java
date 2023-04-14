package at.fhv.QuestDBVisualizationBackend.application;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public abstract class TimeFrameConverter {
    public static long convertToEpochMicro(Instant time){
        return TimeUnit.SECONDS.toMicros(time.getEpochSecond()) + TimeUnit.NANOSECONDS.toMicros(time.getNano());
    }

    public static long convertToEpochMilli(Instant time){
        return time.toEpochMilli();
    }

    public static long convertToEpochNano(Instant time){
        return TimeUnit.SECONDS.toNanos(time.getEpochSecond()) + (time.getNano());
    }

}
