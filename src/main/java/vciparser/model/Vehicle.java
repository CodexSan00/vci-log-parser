package vciparser.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    private String vin;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<DtcError> dtcs = new ArrayList<>();

    public Vehicle(){}

    public Vehicle(String vin){
        this.vin = vin;
    }

    public String getVin(){ return vin; }
    public void setVin(String vin){ this.vin = vin; }

    public List<DtcError> getDtcs(){ return dtcs; }
    public void setDtcs(List<DtcError> dtcs){ this.dtcs = dtcs; }

}
