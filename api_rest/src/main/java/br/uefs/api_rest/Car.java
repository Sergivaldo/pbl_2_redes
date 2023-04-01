package br.uefs.api_rest;

public class Car {
    private int[] locationCoordinatesCar;
    private String idCar;
    private int battery;
    private float distanceForKMRateByPercentage;
    private float timePerKmTraveled;

    public float getTimePerKmTraveled() {
        return timePerKmTraveled;
    }

    public void setTimePerKmTraveled(float timePerKmTraveled) {
        this.timePerKmTraveled = timePerKmTraveled;
    }

    public float getDistanceForKMRateByPercentage() {
        return distanceForKMRateByPercentage;
    }

    public void setDistanceForKMRateByPercentage(float distanceForKMRateByPercentage) {
        this.distanceForKMRateByPercentage = distanceForKMRateByPercentage;
    }

    public Car(float timePerKmTraveled, int[] locationCoordinatesCar, String idCar, int battery, float distanceForKMRateByPercentage ) {
        this.timePerKmTraveled = timePerKmTraveled;
        this.locationCoordinatesCar = locationCoordinatesCar;
        this.idCar = idCar;
        this.battery = battery;
        this.distanceForKMRateByPercentage = distanceForKMRateByPercentage;
    }

    public float getMaximumDistanceHit(){
        return this.distanceForKMRateByPercentage * battery;
    }
    public int[] getLocationCoordinatesCar() {
        return locationCoordinatesCar;
    }

    public void setLocationCoordinatesCar(int[] locationCoordinatesCar) {
        this.locationCoordinatesCar = locationCoordinatesCar;
    }

    public String getIdCar() {
        return idCar;
    }

    public void setIdCar(String idCar) {
        this.idCar = idCar;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }
}
