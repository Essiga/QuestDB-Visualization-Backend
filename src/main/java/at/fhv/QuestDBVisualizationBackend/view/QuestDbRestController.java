package at.fhv.QuestDBVisualizationBackend.view;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/rest")
public class QuestDbRestController {

    private static final String TEST = "/test";

    @GetMapping(TEST)
    public String Test(){
        return "test";
    }
}
