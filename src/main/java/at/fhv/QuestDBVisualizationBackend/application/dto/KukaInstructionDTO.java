package at.fhv.QuestDBVisualizationBackend.application.dto;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class KukaInstructionDTO {
    private String name;
    private boolean value;
    private LocalDateTime sourceTime;
    private LocalDateTime serverTime;
    private int trayPosCap;
    private int trayPosBearing;
    private ObjectId mongoDbObjectId;

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

    public int getTrayPosCap() {
        return trayPosCap;
    }

    public void setTrayPosCap(int trayPosCap) {
        this.trayPosCap = trayPosCap;
    }

    public int getTrayPosBearing() {
        return trayPosBearing;
    }

    public void setTrayPosBearing(int trayPosBearing) {
        this.trayPosBearing = trayPosBearing;
    }

    public ObjectId getMongoDbObjectId() {
        return mongoDbObjectId;
    }

    public void setMongoDbObjectId(ObjectId mongoDbObjectId) {
        this.mongoDbObjectId = mongoDbObjectId;
    }
}
