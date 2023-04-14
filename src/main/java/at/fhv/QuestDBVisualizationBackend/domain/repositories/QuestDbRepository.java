package at.fhv.QuestDBVisualizationBackend.domain.repositories;

import org.json.JSONArray;

import java.sql.SQLException;

public interface QuestDbRepository {
    JSONArray getEnergyDataByEpochTime(long startDate, long endDate) throws SQLException;
    JSONArray getMovementDataByEpochTime(long startDate, long endDate) throws SQLException;
}
