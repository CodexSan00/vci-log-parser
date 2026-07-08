package vciparser.controller;

import vciparser.model.Vehicle;
import vciparser.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parser")

public class ParserController {

    private final ParserService parserService;

    @Autowired
    public ParserController(ParserService parserService){
        this.parserService = parserService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Vehicle> processReportText(@RequestBody String rawText){
        if(rawText == null || rawText.trim().isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Vehicle processedVehicle = parserService.parserReportText(rawText);
        return ResponseEntity.ok(processedVehicle);
    }

}
