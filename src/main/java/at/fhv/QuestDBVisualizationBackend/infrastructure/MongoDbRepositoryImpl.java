package at.fhv.QuestDBVisualizationBackend.infrastructure;

import at.fhv.QuestDBVisualizationBackend.domain.repositories.MongoDbRepository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.BsonTimestamp;
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

//        startTime = convertEnergyDataEpoch(startTime);
//        endTime = convertEnergyDataEpoch(endTime);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        MongoClient mongoClient = MongoClients.create(settings);


        MongoDatabase db = mongoClient.getDatabase("context_data");
        MongoCollection collection = db.getCollection("kuka_instructions");

        //Bson projectionFields = Projections.fields(Projections.include(""));
        //Bson bsonFilter = Filters.gt("uid", "ESP32_Acc_01_1780832907");
//        Bson bsonFilter = Filters.and(
//                Filters.eq("name", "execute"),
//                Filters.gte("serverTime.utcTime", startTime),
//                Filters.lte("serverTime.utcTime", endTime));
        BsonTimestamp startTimestamp = new BsonTimestamp((int) (startTime / 1000), (int) (endTime % 1000));
        BsonTimestamp endTimestamp = new BsonTimestamp((int) (endTime / 1000), (int) (endTime % 1000));

//        ObjectId objectIdStartDate = new ObjectId(String.valueOf(startTimestamp));
//        ObjectId objectIdEndDate = new ObjectId(String.valueOf(endTimestamp));
        ObjectId start = new ObjectId(startTimestamp.getTime(), startTimestamp.getInc());
        ObjectId end = new ObjectId(endTimestamp.getTime(), endTimestamp.getInc());

//        Bson bsonFilter = Filters.gt("_id", start);

        //Bson bsonFilter = Filters.gte("serverTime.utcTime", start);

        Bson bsonFilter = Filters.and(
                Filters.eq("name", "execute"),
                Filters.gte("_id", start),
                Filters.lte("_id", end));
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

    private long convertEnergyDataEpoch(long date) {
        date = (date + 11644473600000L)*10000;
        return date;
    }
}
