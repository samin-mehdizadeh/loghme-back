package ie.domain;

import java.util.HashMap;

public class Courier {
    private String id;
    private int velocity;
    private HashMap<String,Double> location;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public HashMap<String, Double> getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, Double> location) {
        this.location = location;
    }

    public double calculateDistance(double xh,double yh,double xr,double yr){
        double restaurantDistanceToHome = Math.sqrt(Math.pow((xr-xh),2.0) + Math.pow((yr-yh),2.0));
        double courierDistanceToRestaurant =  Math.sqrt(Math.pow((location.get("x")-xr),2.0) + Math.pow((location.get("y")-yr),2.0)) ;
        return restaurantDistanceToHome + courierDistanceToRestaurant ;
    }

    public double calculateDuration(double xh,double yh,double xr,double yr){
        return calculateDistance(xh,yh,xr,yr) / velocity ;
    }
}