package at.fhv.QuestDBVisualizationBackend.view;


import at.fhv.QuestDBVisualizationBackend.application.TimeFrameConverter;
import at.fhv.QuestDBVisualizationBackend.application.dto.TimeFrameDTO;
import at.fhv.QuestDBVisualizationBackend.domain.repositories.MongoDbRepository;
import at.fhv.QuestDBVisualizationBackend.domain.repositories.QuestDbRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/rest/questdb")
public class QuestDbRestController {

    @Autowired
    private Environment environment;

    @Autowired
    private QuestDbRepository questDbRepository;

    private static final String MOVEMENT_GET_BY_TIMEFRAME = "/movement/getByTimeFrame";
    private static final String ENERGY_GET_BY_TIMEFRAME = "/energy/getByTimeFrame";

    @PostMapping(MOVEMENT_GET_BY_TIMEFRAME)
    public String getMovementDataByTimeFrame(@RequestBody TimeFrameDTO timeFrame) throws SQLException {
        long epochStartDate = TimeFrameConverter.convertToEpochMicro(timeFrame.getStartDate());
        long epochEndDate = TimeFrameConverter.convertToEpochMicro(timeFrame.getEndDate());

        JSONArray result = questDbRepository.getMovementDataByEpochTime(epochStartDate, epochEndDate);

        return result.toString();
    }

    @PostMapping(ENERGY_GET_BY_TIMEFRAME)
    public String getEnergyDataByTimeFrame(@RequestBody TimeFrameDTO timeFrame) throws SQLException {
        long epochStartDate = TimeFrameConverter.convertToEpochMilli(timeFrame.getStartDate());
        long epochEndDate = TimeFrameConverter.convertToEpochMilli(timeFrame.getEndDate());

        JSONArray result = questDbRepository.getEnergyDataByEpochTime(epochStartDate, epochEndDate);

        return result.toString();
    }
}
