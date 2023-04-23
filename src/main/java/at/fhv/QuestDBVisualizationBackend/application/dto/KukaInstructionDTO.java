package at.fhv.QuestDBVisualizationBackend.application.dto;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class KukaInstructionDTO {
    private String name;
    private boolean value;
    private LocalDateTime sourceTime;
    private LocalDateTime serverTime;
    private long trayPosCap;
    private long trayPosBearing;
    private ObjectId mongoDbObjectId;
    private long startTimeStamp;
    private long endTimeStamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public LocalDateTime getSourceTime() {
        return sourceTime;
    }

    public void setSourceTime(LocalDateTime sourceTime) {
        this.sourceTime = sourceTime;
    }

    public LocalDateTime getServerTime() {
        return serverTime;
    }

    public void setServerTime(LocalDateTime serverTime) {
        this.serverTime = serverTime;
    }

    public long getTrayPosCap() {
        return trayPosCap;
    }

    public void setTrayPosCap(long trayPosCap) {
        this.trayPosCap = trayPosCap;
    }

    public long getTrayPosBearing() {
        return trayPosBearing;
    }

    public void setTrayPosBearing(long trayPosBearing) {
        this.trayPosBearing = trayPosBearing;
    }

    public ObjectId getMongoDbObjectId() {
        return mongoDbObjectId;
    }

    public void setMongoDbObjectId(ObjectId mongoDbObjectId) {
        this.mongoDbObjectId = mongoDbObjectId;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }
}
