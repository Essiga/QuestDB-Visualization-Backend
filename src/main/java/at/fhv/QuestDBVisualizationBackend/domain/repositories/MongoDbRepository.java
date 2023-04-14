package at.fhv.QuestDBVisualizationBackend.domain.repositories;

import org.json.JSONArray;

public interface MongoDbRepository {
    JSONArray getDataByEpochTime(long startTime, long endTime);
}
