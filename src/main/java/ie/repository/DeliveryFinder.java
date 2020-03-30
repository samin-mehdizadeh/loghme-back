package ie.repository;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliveryFinder extends TimerTask {
    List<Courier> couriers;
    private Basket basket;
    private Timer timerForSearch;
    private Timer timerForDelay;

    public DeliveryFinder(Basket _basket){
        basket = _basket;
    }

    public void search(){
        basket.setStatus("Finding Delivery");
        timerForSearch = new Timer();
        timerForSearch.schedule(this,0,30000);
        timerForDelay = new Timer();
    }

    public Courier findBestCourier() {
        double min=1000;
        Courier chosen=null;
        double restaurantX = basket.getRestaurant().getLocation().get("x");
        double restaurantY = basket.getRestaurant().getLocation().get("y");
        for (int i = 0; i < couriers.size(); i++) {
            double time = couriers.get(i).calculateDuration(0,0,restaurantX,restaurantY);
            if(i == 0){
                min = time;
                chosen = couriers.get(0);
            }
            else{
                if(time < min)
                    min = time;
                chosen = couriers.get(i);
            }
        }
        return chosen;
    }

    @Override
    public void run() {

        if(basket.getStatus().equals("Finding Delivery")){
            urlReader urlreader = new urlReader();
            try {
                String messengersStr = urlreader.readURL("http://138.197.181.131:8080/deliveries");
                ObjectMapper mapper =  new ObjectMapper();
                couriers = Arrays.asList(mapper.readValue(messengersStr,Courier[].class));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!(couriers.isEmpty())){
                basket.setStatus("Delivering");
                Courier courier = findBestCourier();
                double restaurantX = basket.getRestaurant().getLocation().get("x");
                double restaurantY = basket.getRestaurant().getLocation().get("y");
                basket.setRemainingTime((int)courier.calculateDuration(0,0,restaurantX,restaurantY));
                timerForSearch.cancel();
                basket.send();
            }

        }
    }
}
//