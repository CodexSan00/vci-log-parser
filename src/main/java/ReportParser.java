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

public class ReportParser {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/vcilog_db";
    public static final String DB_USER = "workshop_user";
    public static final String DB_PASS = "workshop_password123";

    public static void main(String[] args){
        String filePath = "mock_diagnostic_report.txt";
        String vin = "Not found";
        List<String> dtcCodes = new ArrayList<>();

        initializeDatabase();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = br.readLine()) != null){
                line = line.trim();

                if(line.contains("Chassis Number (VIN):")){
                    String[] parts = line.split(":");
                    if(parts.length > 1){
                        vin = parts[1].trim();
                    }
                }
                if (line.startsWith("P") || line.startsWith("U") || line.startsWith("B") || line.startsWith("C")) {
                    String[] parts = line.split("\\s+");
                    if (parts[0].length() == 5) {
                        dtcCodes.add(parts[0]);
                    }
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
        String createTableSQL = "CREATE TABLE IF NOT EXISTS vehicles("
                + "id SERIAL PRIMARY KEY, "
                + "vin VARCHAR(50) UNIQUE NOT NULL, "
                + "dtc_codes TEXT NOT NULL, "
                + "processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";
        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement()){
            stmt.execute(createTableSQL);
            System.out.println("[DB] Database initialized succesfully (Table checked/created).");
        } catch (SQLException e){
            System.err.println("[DB Error] Failed to initialize database: " + e.getMessage());
        }
    }

    private static void saveVehicleToDatabase(String vin, List<String> dtcCodes){
        String insertSQL = "INSERT INTO vehicles (vin, dtc_codes) VALUES (?, ?)"
                    + "ON CONFLICT (vin) DO UPDATE SET dtc_codes = EXCLUDED.dtc_codes, processed_at = CURRENT_TIMESTAMP;";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pstmt = conn.prepareStatement(insertSQL)){
            pstmt.setString(1, vin);
            pstmt.setString(2, dtcCodes.toString());

            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected > 0){
                System.out.println("[DB] Data successfully persisted to PostgreSQL for VIN: " + vin);
            }
        } catch(SQLException e){
            System.err.println("[DB Error] Failed to save vehicle data: " + e.getMessage());
        }
    }
}