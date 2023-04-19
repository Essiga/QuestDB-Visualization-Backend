package at.fhv.QuestDBVisualizationBackend.infrastructure;

import at.fhv.QuestDBVisualizationBackend.domain.repositories.MongoDbRepository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.bson.codecs.configuration.*;
import org.bson.codecs.pojo.*;
import org.bson.types.ObjectId;

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
        MongoCollection collection = db.getCollection("kuka_instructions");

        //Bson projectionFields = Projections.fields(Projections.include(""));
        //Bson bsonFilter = Filters.gt("uid", "ESP32_Acc_01_1780832907");
        Bson bsonFilter = Filters.and(
                Filters.eq("name", "execute"),
                Filters.gte("serverTime.utcTime", startTime),
                Filters.lte("serverTime.utcTime", endTime));
        FindIterable<Document> findIt = collection.find(bsonFilter);
        //FindIterable<Document> findIt = collection.find(new Document().append("battery", new Document().append("$gt", 39).append("$lt", 81)));
        //FindIterable<Document> findIt = collection.find(new Document().append("name", new Document().append("$eq", "execute"), new Document().append("serverTime.utcTime", new Document().append("$gt", startTime))));

        JSONArray result = new JSONArray();

        for (Document document : findIt) {
            JSONObject row = new JSONObject(document.toJson());
            result.put(row);
        }

        return result;
    }
}
