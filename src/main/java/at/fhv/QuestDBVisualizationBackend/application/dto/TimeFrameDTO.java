package at.fhv.QuestDBVisualizationBackend.application.dto;

import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDateTime;


public class TimeFrameDTO {
    private Instant startDate;
    private Instant endDate;

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    //    private LocalDateTime startDate;
//    private LocalDateTime endDate;
//
//    public LocalDateTime getStartDate() {
//        return startDate;
//    }
//
//    public LocalDateTime getEndDate() {
//        return endDate;
//    }
}
