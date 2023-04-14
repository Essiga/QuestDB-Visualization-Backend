package at.fhv.QuestDBVisualizationBackend.view;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/rest/mongodb")
public class MongoDbRestController {

    @Autowired
    private Environment environment;

    @GetMapping("/test")
    public String test(){
        String connectionString = environment.getProperty("mongodb.connectionstring");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        MongoClient mongoClient = MongoClients.create(settings);

        return "hi";
    }
}
