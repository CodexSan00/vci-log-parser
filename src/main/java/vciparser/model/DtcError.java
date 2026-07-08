package vciparser.model;

import jakarta.persistence.*;

@Entity
@Table(name="vehicle_dtcs")
public class DtcError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String description;

    @ManyToOne
    @JoinColumn(name = "vehicle_vin")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Vehicle vehicle;

    public DtcError(){}

    public DtcError(String code, String description, Vehicle vehicle){
        this.code = code;
        this.description = description;
        this.vehicle = vehicle;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public Vehicle getVehicle() { return vehicle; }
}
