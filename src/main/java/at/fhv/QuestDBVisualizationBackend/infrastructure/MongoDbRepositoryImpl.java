package at.fhv.QuestDBVisualizationBackend.infrastructure;

import at.fhv.QuestDBVisualizationBackend.domain.repositories.MongoDbRepository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MongoDbRepositoryImpl implements MongoDbRepository {
    @Autowired
    private Environment environment;


    @Override
    public JSONArray getDataByEpochTime(long startTime, long endTime) {
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

        return result;
    }
}
