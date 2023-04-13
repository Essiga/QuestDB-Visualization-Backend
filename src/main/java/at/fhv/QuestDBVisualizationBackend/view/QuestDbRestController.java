package at.fhv.QuestDBVisualizationBackend.view;


import io.questdb.client.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.Properties;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/rest")
public class QuestDbRestController {

    @Autowired
    private Environment environment;

    private static final String TEST = "/test";

    @GetMapping(TEST)
    public String Test() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", environment.getProperty("questdb.username"));
        properties.setProperty("password", environment.getProperty("questdb.password"));
        properties.setProperty("sslmode", "disable");

        final Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://"+ environment.getProperty("questdb.ipaddress") +"/qdb", properties);
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM energy_data")) {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        }
        connection.close();


        return "test";
    }
}
