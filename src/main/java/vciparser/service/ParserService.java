package vciparser.service;

import vciparser.model.DtcError;
import vciparser.model.Vehicle;
import vciparser.repository.VehicleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParserService {
    private final Pattern vinPattern = Pattern.compile("[A-HJ-NPR-Z0-9]{17}");

    // Improved Regex for the DTC: Captures the code (P, U, B, C followed by 4 digits)
    // and in a second group captures all the text that follows (description).
    private final Pattern dtcLinePattern = Pattern.compile("\\b([PUBC]\\d{4})\\b\\s*(.*)");
    private final VehicleRepository vehicleRepository;
    @Autowired
    public ParserService(VehicleRepository vehicleRepository){
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle parserReportText(String rawText){
        String vin = "Not found";
        //First find VIN...
        Matcher vinMatcher = vinPattern.matcher(rawText);
        if(vinMatcher.find()){
            vin = vinMatcher.group();
        }

        Vehicle vehicle = new Vehicle(vin);
        String[] lines = rawText.split("\\r?\n");

        for(String line : lines){
            Matcher dtcMatcher = dtcLinePattern.matcher(line);
            if(dtcMatcher.find()){
                String code = dtcMatcher.group(1);
                String description = dtcMatcher.group(2);

                if(description == null || description.trim().isEmpty()){
                    description = "No description available";
                }
                DtcError error = new DtcError(code, description.trim(), vehicle);
                vehicle.getDtcs().add(error);
            }
        }
        if(!vin.equals("Not found")){
            vehicleRepository.save(vehicle);
        }
        return vehicle;
    }

}
