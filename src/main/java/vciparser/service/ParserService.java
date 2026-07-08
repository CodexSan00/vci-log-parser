package vciparser.service;

import vciparser.model.Vehicle;
import org.springframework.stereotype.Service;
import vciparser.repository.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParserService {
    private final VehicleRepository vehicleRepository;
    private final Pattern vinPattern = Pattern.compile("[A-HJ-NPR-Z0-9]{17}");
    private final Pattern dtcPattern = Pattern.compile("\\b([PUBC]\\d{4})\\b");

    public ParserService(VehicleRepository vehicleRepository){
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle parserReportText(String rawText){
        String vin = "Not found";
        List<String> dtcCodes = new ArrayList<>();

        String[] lines = rawText.split("\\r?\n");

        for(String line : lines){
            Matcher vinMatcher = vinPattern.matcher(line);
            if(vin.equals("Not found") && vinMatcher.find()){
                vin = vinMatcher.group();
            }
            Matcher dtcMatcher = dtcPattern.matcher(line);
            if(dtcMatcher.find()){
                dtcCodes.add(dtcMatcher.group(1));
                System.out.println(dtcCodes.toString());
            }
        }
        Vehicle vehicle = new Vehicle(vin, dtcCodes);
        if(!vin.equals("Not found")){
            vehicleRepository.save(vehicle);
        }
        return vehicle;
    }

}
