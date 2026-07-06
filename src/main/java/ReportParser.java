import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportParser {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/vcilog_db";
    public static final String DB_USER = "workshop_user";
    public static final String DB_PASS = "workshop_password123";

    public static void main(String[] args){
        String filePath = "diagnostic_report.txt";
        String vin = "Not found";
        List<String> dtcCodes = new ArrayList<>();

        Pattern vinPattern = Pattern.compile("[A-HJ-NPR-Z0-9]{17}");
        Pattern dtcPattern = Pattern.compile("\\b([PUBC]\\d{4})\\b");

        initializeDatabase();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = br.readLine()) != null){
                Matcher vinMatcher = vinPattern.matcher(line);

                if(vin.equals("Not found") && vinMatcher.find()){
                    vin = vinMatcher.group();
                }
                Matcher dtcMatcher = dtcPattern.matcher(line);
                if (dtcMatcher.find()) {
                    dtcCodes.add(dtcMatcher.group(1));
                }
            }
            // --- SYSTEM CONSOLE OUTPUT ---
            System.out.println("===============================================");
            System.out.println("   VCI-LOG PROCESSING SYSTEM v1.0              ");
            System.out.println("===============================================");
            System.out.println("• Identified Vehicle (VIN): " + vin);
            System.out.println("• Detected Fault Codes (DTC): " + dtcCodes);
            System.out.println("===============================================");

            if (!vin.equals("Not Found")) {
                saveVehicleToDatabase(vin, dtcCodes);
            }

        } catch (IOException e) {
            System.err.println("Critical error while reading the diagnostic file: " + e.getMessage());
        }
    }


    private static void initializeDatabase(){
        String createVehiclesTable = "CREATE TABLE IF NOT EXISTS vehicles("
                + "id SERIAL PRIMARY KEY, "
                + "vin VARCHAR(50) UNIQUE NOT NULL"
                + ");";
        String createDtcsTable = "CREATE TABLE IF NOT EXISTS vehicle_dtcs ("
                + "id SERIAL PRIMARY KEY, "
                + "vehicle_id INT REFERENCES vehicles(id) ON DELETE CASCADE, "
                + "dtc_code VARCHAR(10) NOT NULL, "
                + "processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";
        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement()){
            stmt.execute(createVehiclesTable);
            stmt.execute(createDtcsTable);
            System.out.println("[DB] Database initialized successfully (Table checked/created).");
        } catch (SQLException e){
            System.err.println("[DB Error] Failed to initialize database: " + e.getMessage());
        }
    }

    private static void saveVehicleToDatabase(String vin, List<String> dtcCodes){
        String insertVehicleSQL = "INSERT INTO vehicles (vin) VALUES (?)"
                    + "ON CONFLICT (vin) DO UPDATE SET vin = EXCLUDED.vin RETURNING id;";
        String insertDtcSQL = "INSERT INTO vehicle_dtcs (vehicle_id, dtc_code) VALUES (?, ?);";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)){
            conn.setAutoCommit(false); //Disable auto-commit to handle this as a SINGLE transaction.
            int vehicleId= -1;

            //Persist the vehicle and fetch its ID
            try(PreparedStatement pstmt = conn.prepareStatement(insertVehicleSQL)){
                pstmt.setString(1, vin);
                try(var rs = pstmt.executeQuery()){
                    if(rs.next()){
                        vehicleId = rs.getInt(1);
                    }
                }
            }
            //Loop through every detected DTC and insert it as an individual row
            if(vehicleId != 1 && !dtcCodes.isEmpty()){
                try(PreparedStatement pstmt = conn.prepareStatement(insertDtcSQL)){
                    for(String code: dtcCodes){
                        pstmt.setInt(1, vehicleId);
                        pstmt.setString(2, code);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }
            //Commit transaction to savee everything cleanly.
            conn.commit();
            System.out.println("[DB] Data successfully persisted to PostgreSQL for VIN: " + vin);

        } catch(SQLException e){
            System.err.println("[DB Error] Failed to save vehicle data: " + e.getMessage());
        }
    }
}