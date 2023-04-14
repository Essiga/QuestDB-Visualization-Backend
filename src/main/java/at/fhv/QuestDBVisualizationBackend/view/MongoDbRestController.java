package at.fhv.QuestDBVisualizationBackend.view;


import at.fhv.QuestDBVisualizationBackend.application.dto.TimeFrameDTO;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/rest/mongodb")
public class MongoDbRestController {

    @Autowired
    private Environment environment;

    private static final String GET_BY_TIMEFRAME = "getByTimeFrame";

    @PostMapping(GET_BY_TIMEFRAME)
    public String getDataByTimeFrame(TimeFrameDTO timeFrameDTO) {
        String connectionString = environment.getProperty("mongodb.connectionstring");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        MongoClient mongoClient = MongoClients.create(settings);


        MongoDatabase db = mongoClient.getDatabase("context_data");
        MongoCollection collection = db.getCollection("test");

        //Bson projectionFields = Projections.fields(Projections.include(""));
        //Bson bsonFilter = Filters.gt("uid", "ESP32_Acc_01_1780832907");
        //FindIterable<Document> findIt = collection.find(bsonFilter);
        FindIterable<Document> findIt = collection.find(new Document().append("battery", new Document().append("$gt", 39).append("$lt", 81)));

        JSONArray result = new JSONArray();

        for (Document document : findIt) {
            JSONObject row = new JSONObject(document);
            result.put(row);
        }

        return result.toString();
    }
}
