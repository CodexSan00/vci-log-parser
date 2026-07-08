package vciparser.repository;

import vciparser.model.Vehicle;
import org.springframework.stereotype.Repository;
import java.sql.*;

@Repository
public class VehicleRepository {
    public static final String url = "jdbc:postgresql://localhost:5432/vcilog_db";
    public static final String user = "workshop_user";
    public static final String password = "workshop_password123";

    public void save(Vehicle vehicle){
        String insertVehicleSQL = "INSERT INTO vehicles (vin) VALUES (?) ON CONFLICT (vin) DO NOTHING";
        String insertDtcSQL = "INSERT INTO vehicle_dtcs (vehicle_vin, dtc_code) VALUES (?, ?)";

        try(Connection conn = DriverManager.getConnection(url, user, password)){
            conn.setAutoCommit(false);
            try(PreparedStatement pstmtVehicle = conn.prepareStatement(insertVehicleSQL)){
                pstmtVehicle.setString(1, vehicle.getVin());
                pstmtVehicle.executeUpdate();
            }
            if(!vehicle.getDtcCodes().isEmpty()){
                try(PreparedStatement pstmtDtc = conn.prepareStatement(insertDtcSQL)){
                    for(String code : vehicle.getDtcCodes()){
                        pstmtDtc.setString(1, vehicle.getVin());
                        pstmtDtc.setString(2, code);
                        pstmtDtc.addBatch();
                    }
                    pstmtDtc.executeBatch();
                }
            }
            conn.commit();
            System.out.println("[DB Success] Truck: " + vehicle.getVin() + " saved with DTCs.");

        } catch (SQLException e){
            System.out.println("[DB Error] Error: " + e.getMessage());
        }

    }

}
