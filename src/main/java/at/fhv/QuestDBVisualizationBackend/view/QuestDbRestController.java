package at.fhv.QuestDBVisualizationBackend.view;


import at.fhv.QuestDBVisualizationBackend.application.TimeFrameConverter;
import at.fhv.QuestDBVisualizationBackend.application.dto.TimeFrameDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/rest/questdb")
public class QuestDbRestController {

    @Autowired
    private Environment environment;

    private static final String MOVEMENT_GET_BY_TIMEFRAME = "/movement/getByTimeFrame";
    private static final String ENERGY_GET_BY_TIMEFRAME = "/energy/getByTimeFrame";


    private Connection connectToQuestDB() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", environment.getProperty("questdb.username"));
        properties.setProperty("password", environment.getProperty("questdb.password"));
        properties.setProperty("sslmode", "disable");

        return DriverManager.getConnection(
                "jdbc:postgresql://"+ environment.getProperty("questdb.ipaddress") +"/qdb", properties);
    }

    @PostMapping(MOVEMENT_GET_BY_TIMEFRAME)
    public String getMovementDataByTimeFrame(@RequestBody TimeFrameDTO timeFrame) throws SQLException {

        long epochStartDate = TimeFrameConverter.convertToEpochMicro(timeFrame.getStartDate());
        long epochEndDate = TimeFrameConverter.convertToEpochMicro(timeFrame.getEndDate());

        System.out.println(epochStartDate);

        JSONArray result;

        final Connection connection = connectToQuestDB();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM movement_data WHERE ValuesTS_PLC > ? AND ValuesTS_PLC < ?")) {
            result = executePreparedStatementForTimeFrame(epochStartDate, epochEndDate, preparedStatement);
        }
        connection.close();

        return result.toString();
    }

    @PostMapping(ENERGY_GET_BY_TIMEFRAME)
    public String getEnergyDataByTimeFrame(@RequestBody TimeFrameDTO timeFrame) throws SQLException {

        long epochStartDate = TimeFrameConverter.convertToEpochMilli(timeFrame.getStartDate());
        long epochEndDate = TimeFrameConverter.convertToEpochMilli(timeFrame.getEndDate());



        System.out.println("start before conversion: " + epochStartDate);
        epochStartDate = (epochStartDate + 11644473600000L)*10000;
        epochEndDate = (epochEndDate + 11644473600000L)*10000;

        System.out.println("start after conversion: " + epochStartDate);
        System.out.println(epochEndDate);

        JSONArray result;


        final Connection connection = connectToQuestDB();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM energy_data WHERE ValuesTS > ? AND ValuesTS < ?")) {
            result = executePreparedStatementForTimeFrame(epochStartDate, epochEndDate, preparedStatement);
        }
        connection.close();

        return result.toString();
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
}
