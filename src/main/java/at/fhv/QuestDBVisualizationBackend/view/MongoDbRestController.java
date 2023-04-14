package at.fhv.QuestDBVisualizationBackend.view;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/rest/mongodb")
public class MongoDbRestController {

    @GetMapping("/test")
    public String test(){

        return "HelloWorld :)";
    }
}
