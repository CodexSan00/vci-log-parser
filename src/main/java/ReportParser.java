import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportParser {
    public static void main(String[] args){
        String filePath = "mock_diagnostic_report.txt";
        String vin = "not found";

        //List of String to hold fault codes.
        List<String> dtcCodes = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;

            while((line = br.readLine()) != null){
                line = line.trim();
                //Find VIN
                if(line.contains("Chassis Number (VIN):")){
                    String[] parts = line.split(":");
                    if(parts.length > 1){
                        vin = parts[1].trim(); //Extract piece [1] and clean remaining spaces.
                    }
                }
                //Find Fault Codes (DTC)
                if(line.startsWith("P") || line.startsWith("U") || line.startsWith("B") || line.startsWith("C")){
                    String[] parts = line.split("\\s+");

                    //Standard DTCs are exactly 5 characters long (e.g., P0124)
                    if(parts[0].length() == 5){
                        dtcCodes.add(parts[0]);
                    }
                }
            }
            System.out.println("===============================================");
            System.out.println("   VCI-LOG PROCESSING SYSTEM v1.0              ");
            System.out.println("===============================================");
            System.out.println("• Identified Vehicle (VIN): " + vin);
            System.out.println("• Detected Fault Codes (DTC): " + dtcCodes);
            System.out.println("===============================================");
        } catch(IOException e){
            System.err.println("Critical error while reading the diagnostic file: " + e.getMessage());
        }
    }
}
