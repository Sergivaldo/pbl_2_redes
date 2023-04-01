package br.uefs.api_rest;

public class GasStation {
    private int[] locationCoordinatesGasStation;
    private String gasStationName;
    private String gasStationId;
    private int carsInLine;
    private int rechargeTimeForCar;

    public GasStation(int[] locationCoordinatesGasStation, String gasStationName, String gasStationId, int carsInLine, int rechargeTimeForCar) {
        this.locationCoordinatesGasStation = locationCoordinatesGasStation;
        this.gasStationName = gasStationName;
        this.gasStationId = gasStationId;
        this.carsInLine = carsInLine;
        this.rechargeTimeForCar = rechargeTimeForCar;
    }

    public float getWaitingTime(){
        return carsInLine * rechargeTimeForCar;
    }
    public int[] getLocationCoordinatesGasStation() {
        return locationCoordinatesGasStation;
    }

    public void setLocationCoordinatesGasStation(int[] locationCoordinatesGasStation) {
        this.locationCoordinatesGasStation = locationCoordinatesGasStation;
    }

    public String getGasStationName() {
        return gasStationName;
    }

    public void setGasStationName(String gasStationName) {
        this.gasStationName = gasStationName;
    }

    public String getGasStationId() {
        return gasStationId;
    }

    public void setGasStationId(String gasStationId) {
        this.gasStationId = gasStationId;
    }

    public int getCarsInLine() {
        return carsInLine;
    }

    public void setCarsInLine(int carsInLine) {
        this.carsInLine = carsInLine;
    }
}
