package ie.repository;
import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FoodPartyTimer extends TimerTask {
    private urlReader urlreader;
    private Timer timerForSearch;
    Manager manager;

    public void addDiscountFoodsToMenu(Restaurant restaurant) throws IOException {
        List <DiscountFood> foodParties = restaurant.getFoodParty();
        for(DiscountFood discountFood: foodParties){
            Food newFood = new Food();
            newFood.setName(discountFood.getName());
            newFood.setDescription(discountFood.getDescription());
            newFood.setPopularity(discountFood.getPopularity());
            newFood.setPrice(discountFood.getOldPrice());
            newFood.setImage(discountFood.getImage());
            restaurant.addFood(newFood);
        }
    }

    public void recoverRestaurants() throws IOException {
        List <Restaurant> allRestaurants =  manager.getRestaurants();
        for(Restaurant restaurant:allRestaurants) {
            if ((restaurant.getMenu().isEmpty()) && (restaurant.getFoodParty().size() > 0))
                addDiscountFoodsToMenu(restaurant);
            restaurant.clearFoodParty();
        }

    }


    public void start(){
        urlreader = new urlReader();
        timerForSearch = new Timer();
        manager = Manager.getInstance();
        timerForSearch.schedule(this,0,85000);
        FoodPartyCounter.getInstance().setTimer(85000);

    }

    @Override
    public void run() {
        //FoodPartyCounter.getInstance().stop();
        //FoodPartyCounter.getInstance().stop();
        FoodPartyCounter.getInstance().stop();
        manager.assignNewDiscountFoods();
        if(manager.basketIsEmpty())
            manager.assignNewBasket();
        try {
            recoverRestaurants();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = "";

        try {
            result = urlreader.readURL("http://138.197.181.131:8080/foodparty");
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Restaurant> foodPartyRestaurants = new ArrayList<Restaurant>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = result.replaceAll("menu","foodParty");
            foodPartyRestaurants = Arrays.asList(mapper.readValue(result, Restaurant[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Restaurant restaurant: foodPartyRestaurants){
            Restaurant foundedRestaurant = manager.getRestaurantById(restaurant.getId());
            if(foundedRestaurant != null)
                foundedRestaurant.setFoodParty(restaurant.getFoodParty());
            else
                manager.addRestaurant(restaurant);
        }
        System.out.println(result);
        FoodPartyCounter.getInstance().start();
    }
}
