package at.fhv.QuestDBVisualizationBackend.infrastructure;

import at.fhv.QuestDBVisualizationBackend.application.dto.KukaInstructionDTO;
import at.fhv.QuestDBVisualizationBackend.domain.repositories.MongoDbRepository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Component
public class MongoDbRepositoryImpl implements MongoDbRepository {
    @Autowired
    private Environment environment;


    @Override
    public List<KukaInstructionDTO> getDataByEpochTime(long startTime, long endTime) {
        String connectionString = environment.getProperty("mongodb.connectionstring");


        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        MongoClient mongoClient = MongoClients.create(settings);


        MongoDatabase db = mongoClient.getDatabase("context_data");
        MongoCollection collection = db.getCollection("kuka_instructions");


        BsonTimestamp startTimestamp = new BsonTimestamp((int) (startTime / 1000), (int) (endTime % 1000));
        BsonTimestamp endTimestamp = new BsonTimestamp((int) (endTime / 1000), (int) (endTime % 1000));

        ObjectId start = new ObjectId(startTimestamp.getTime(), startTimestamp.getInc());
        ObjectId end = new ObjectId(endTimestamp.getTime(), endTimestamp.getInc());

        Bson bsonFilter = Filters.and(
                Filters.eq("name", "fullAssembly"),
                Filters.gte("_id", start),
                Filters.lte("_id", end));
        FindIterable<Document> findIt = collection.find(bsonFilter);
        //FindIterable<Document> findIt = collection.find(new Document().append("battery", new Document().append("$gt", 39).append("$lt", 81)));
        //FindIterable<Document> findIt = collection.find(new Document().append("name", new Document().append("$eq", "execute"), new Document().append("serverTime.utcTime", new Document().append("$gt", startTime))));

        JSONArray result = new JSONArray();


        List<KukaInstructionDTO> kukaInstructions = new LinkedList<>();


        for (Document document : findIt) {
            KukaInstructionDTO kukaInstruction = new KukaInstructionDTO();
            kukaInstruction.setName(document.getString("name"));
            kukaInstruction.setMongoDbObjectId((ObjectId) document.get("_id"));
            kukaInstruction.setValue(((Document)document.get("value")).getBoolean("value"));
            kukaInstructions.add(kukaInstruction);

            JSONObject row = new JSONObject(document.toJson());
            result.put(row);
        }

        //after it finishes there should be all start and end timestamps in the list
        boolean positiveFound = false;
        List<KukaInstructionDTO> relevantData = new LinkedList<>();
        int counter = 0;
        while(kukaInstructions.size() > counter){
            if(positiveFound == false && kukaInstructions.get(counter).getValue() == true){
                positiveFound = true;
                kukaInstructions.get(counter).setStartTimeStamp(kukaInstructions.get(counter).getMongoDbObjectId().getTimestamp());
                relevantData.add(kukaInstructions.get(counter));
            } else if (positiveFound == true && kukaInstructions.get(counter).getValue() == false) {
                positiveFound = false;
                relevantData.get(relevantData.size()-1).setEndTimeStamp(kukaInstructions.get(counter).getMongoDbObjectId().getTimestamp());
                //relevantData.add(kukaInstructions.get(counter));
            }
            counter++;

        }

        for (KukaInstructionDTO kukaInstruction : relevantData) {
            Bson trayPosCapFilter = Filters.and(
                    Filters.eq("name", "trayPosCap"),
                    Filters.lte("_id", kukaInstruction.getMongoDbObjectId()));
            Bson sortDescendingId = Sorts.descending("_id");
            FindIterable<Document> allTrayPosCap = collection.find(trayPosCapFilter).sort(sortDescendingId);
            for (Document doc: allTrayPosCap) {
                System.out.println(((Document)doc.get("value")).getLong("value"));
            }

            kukaInstruction.setTrayPosCap(((Document)allTrayPosCap.first().get("value")).getLong("value"));

            Bson trayPosCapBearing = Filters.and(
                    Filters.eq("name", "trayPosBearing"),
                    Filters.lte("_id", kukaInstruction.getMongoDbObjectId()));
            FindIterable<Document> allTrayPosBearing = collection.find(trayPosCapBearing).sort(sortDescendingId);

            kukaInstruction.setTrayPosBearing(((Document)allTrayPosBearing.first().get("value")).getLong("value"));
        }

//        for (int i = 0; i < kukaInstructions.size(); i++) {
////            if(kukaInstructions.get(i).getValue().equals("0")){
////                kukaInstructions.remove(kukaInstructions.get(i));
////            }
//            while(positiveFound == false && kukaInstructions.size() > i) {
//                if(kukaInstructions.get(i).getValue() == true){
//                    relevantData.add(kukaInstructions.get(i));
//                    positiveFound = true;
//                } else {
//                    i++;
//                }
//            }
//            while (positiveFound == true && kukaInstructions.size() > i){
//                if(kukaInstructions.get(i).getValue() == false){
//                    relevantData.add(kukaInstructions.get(i));
//                    positiveFound = false;
//                } else {
//                    i++;
//                }
//            }
//        }

        return relevantData;
    }

}
