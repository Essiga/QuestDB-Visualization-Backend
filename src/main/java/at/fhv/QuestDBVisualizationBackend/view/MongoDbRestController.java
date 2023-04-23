package at.fhv.QuestDBVisualizationBackend.view;

import at.fhv.QuestDBVisualizationBackend.application.TimeFrameConverter;
import at.fhv.QuestDBVisualizationBackend.application.dto.KukaInstructionDTO;
import at.fhv.QuestDBVisualizationBackend.application.dto.TimeFrameDTO;
import at.fhv.QuestDBVisualizationBackend.domain.repositories.MongoDbRepository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/rest/mongodb")
public class MongoDbRestController {

    @Autowired
    private Environment environment;

    @Autowired
    MongoDbRepository mongoDbRepository;

    private static final String GET_BY_TIMEFRAME = "getByTimeFrame";

    @PostMapping(GET_BY_TIMEFRAME)
    public List<KukaInstructionDTO> getDataByTimeFrame(@RequestBody TimeFrameDTO timeFrame) {

        long epochStartDate = TimeFrameConverter.convertToEpochMilli(timeFrame.getStartDate());
        long epochEndDate = TimeFrameConverter.convertToEpochMilli(timeFrame.getEndDate());

        List<KukaInstructionDTO> result = mongoDbRepository.getDataByEpochTime(epochStartDate, epochEndDate);
        return result;
    }
}
