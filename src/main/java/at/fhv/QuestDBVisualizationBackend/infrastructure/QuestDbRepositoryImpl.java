package at.fhv.QuestDBVisualizationBackend.infrastructure;

import at.fhv.QuestDBVisualizationBackend.domain.repositories.QuestDbRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class QuestDbRepositoryImpl implements QuestDbRepository {

    @Autowired
    private Environment environment;

    @Override
    public JSONArray getEnergyDataByEpochTime(long startDate, long endDate) throws SQLException {

        startDate = convertEnergyDataEpoch(startDate);
        endDate = convertEnergyDataEpoch(endDate);

        JSONArray result;


        final Connection connection = connectToQuestDB();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT Values_TS, Values_I_RMS, Values_W FROM kuka_energy WHERE Values_TS > ? AND Values_TS < ?")) {
            result = executePreparedStatementForTimeFrame(startDate, endDate, preparedStatement);
        }
        connection.close();


        return result;
    }

    @Override
    public JSONArray getMovementDataByEpochTime(long startDate, long endDate) throws SQLException {
        JSONArray result;
        final Connection connection = connectToQuestDB();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT Values_TS_PLC, Values_Actual_TCP_pose, Values_actual_torque FROM kuka_assembly WHERE Values_TS_PLC > ? AND Values_TS_PLC < ?")) {
            result = executePreparedStatementForTimeFrame(startDate, endDate, preparedStatement);
        }
        connection.close();

        return result;
    }

    private long convertEnergyDataEpoch(long date) {
        date = (date + 11644473600000L)*10000;
        return date;
    }

    private Connection connectToQuestDB() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", environment.getProperty("questdb.username"));
        properties.setProperty("password", environment.getProperty("questdb.password"));
        properties.setProperty("sslmode", "disable");

        return DriverManager.getConnection(
                "jdbc:postgresql://"+ environment.getProperty("questdb.ipaddress") +"/qdb", properties);
    }

    private JSONArray executePreparedStatementForTimeFrame(long epochStartDate, long epochEndDate, PreparedStatement preparedStatement) throws SQLException {
        JSONArray result = new JSONArray();

        preparedStatement.setLong(1, epochStartDate);
        preparedStatement.setLong(2, epochEndDate);
        try (ResultSet rs = preparedStatement.executeQuery()) {
            List<String> colNames = getColNames(rs);

            while (rs.next()) {
                JSONObject row = new JSONObject();
                colNames.forEach(cn -> {
                    try {
                        row.put(cn, rs.getObject(cn));
                    } catch (JSONException | SQLException e) {
                        e.printStackTrace();
                    }
                });
                result.put(row);
            }
        }
        return result;
    }

    private List<String> getColNames(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int numCols = md.getColumnCount();
        return IntStream.range(0, numCols)
                .mapToObj(i -> {
                    try {
                        return md.getColumnName(i + 1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return "?";
                    }
                })
                .collect(Collectors.toList());
    }

}
