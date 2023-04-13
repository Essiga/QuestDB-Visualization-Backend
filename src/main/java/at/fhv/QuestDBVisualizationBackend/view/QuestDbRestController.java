package at.fhv.QuestDBVisualizationBackend.view;


import io.questdb.client.Sender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/rest")
public class QuestDbRestController {

    @Autowired
    private Environment environment;

    private static final String TEST = "/test";

    @GetMapping(TEST)
    public String Test() throws SQLException {

        JSONArray result = new JSONArray();

        Properties properties = new Properties();
        properties.setProperty("user", environment.getProperty("questdb.username"));
        properties.setProperty("password", environment.getProperty("questdb.password"));
        properties.setProperty("sslmode", "disable");

        final Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://"+ environment.getProperty("questdb.ipaddress") +"/qdb", properties);
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM energy_data")) {
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
