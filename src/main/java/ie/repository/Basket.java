package ie.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Basket extends TimerTask {
    private Restaurant restaurant;
    private Map<Food, Integer> foods;
    private Map<DiscountFood, Integer> discountFoods;
    private String status;
    private String id;

    public Map<DiscountFood, Integer> getDiscountFoods() {
        return discountFoods;
    }

    public void setDiscountFoods(Map<DiscountFood, Integer> discountFoods) {
        this.discountFoods = discountFoods;
    }

    public void addOrdinaryFoodToCart(Food food, Integer foodCount){
        foods.put(food,foodCount);
    }

    public void addDiscountFoodToCart(DiscountFood food, Integer foodCount){
        discountFoods.put(food,foodCount);
    }

    private int remainingTime;
    private Timer timer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Basket(String _id) {
        foods = new HashMap<Food, Integer>();
        discountFoods = new HashMap<DiscountFood, Integer>();
        id = _id;
    }

    public void emptyDiscountFoods(){
        discountFoods = new HashMap<DiscountFood, Integer>();
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Map<Food, Integer> getFoods() {
        return foods;
    }

    public void setFoods(Map<Food, Integer> foods) {
        this.foods = foods;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void send(){
        if(status.equals("Delivering")){
            timer = new Timer();
            timer.schedule(this,0,1000);
        }
    }
    @Override
    public void run() {
        if(remainingTime == 0) {
            status = "Done";
            timer.cancel();
        }
        else {
            remainingTime = remainingTime - 1;

        }
    }
}
