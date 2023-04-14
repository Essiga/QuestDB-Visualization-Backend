package at.fhv.QuestDBVisualizationBackend.view;


import at.fhv.QuestDBVisualizationBackend.application.dto.TimeFrameDTO;
import io.questdb.client.Sender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/rest")
public class QuestDbRestController {

    @Autowired
    private Environment environment;

    private static final String TEST = "/test";

    private long convertToEpochMicro(Instant time){
        return TimeUnit.SECONDS.toMicros(time.getEpochSecond()) + TimeUnit.NANOSECONDS.toMicros(time.getNano());
    }

    @PostMapping(TEST)
    public String Test(@RequestBody TimeFrameDTO timeFrame) throws SQLException {

        long epochStartDate = convertToEpochMicro(timeFrame.getStartDate());
        long epochEndDate = convertToEpochMicro(timeFrame.getEndDate());


        System.out.println("Debug:");
        System.out.println("-------------------------------");
        System.out.println("startDate: " + epochStartDate);
        System.out.println("endDate: " + epochEndDate);

        JSONArray result = new JSONArray();

        Properties properties = new Properties();
        properties.setProperty("user", environment.getProperty("questdb.username"));
        properties.setProperty("password", environment.getProperty("questdb.password"));
        properties.setProperty("sslmode", "disable");

        final Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://"+ environment.getProperty("questdb.ipaddress") +"/qdb", properties);
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM movement_data WHERE ValuesTS_PLC > ? AND ValuesTS_PLC < ?")) {
            preparedStatement.setLong(1, epochStartDate);
            preparedStatement.setLong(2, epochEndDate);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int numCols = md.getColumnCount();
                List<String> colNames = IntStream.range(0, numCols)
                        .mapToObj(i -> {
                            try {
                                return md.getColumnName(i + 1);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return "?";
                            }
                        })
                        .collect(Collectors.toList());


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

//                while (rs.next()) {
//                    System.out.println(rs.getString(1));
//                }
            }
        }
        connection.close();


        return result.toString();
    }
}
