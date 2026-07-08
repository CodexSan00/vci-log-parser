package vciparser.model;

import java.util.List;

public class Vehicle {
    private String vin;
    private List<String> dtcCodes;

    public Vehicle(String vin, List<String> dtcCodes){
        this.vin = vin;
        this.dtcCodes = dtcCodes;
    }

    public String getVin(){ return vin; }
    public void setVin(String vin){ this.vin = vin; }

    public List<String> getDtcCodes(){ return dtcCodes; }
    public void setDtcCodes(List<String> dtcCodes){ this.dtcCodes = dtcCodes; }

}
