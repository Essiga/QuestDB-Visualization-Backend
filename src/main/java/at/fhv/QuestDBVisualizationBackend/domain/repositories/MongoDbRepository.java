package at.fhv.QuestDBVisualizationBackend.domain.repositories;

import at.fhv.QuestDBVisualizationBackend.application.dto.KukaInstructionDTO;
import org.json.JSONArray;

import java.util.List;

public interface MongoDbRepository {
    List<KukaInstructionDTO> getDataByEpochTime(long startTime, long endTime);
}
