package ie.domain;

import ie.domain.Manager;
import ie.domain.*;

import java.nio.file.DirectoryStream;
import java.util.*;

public class Basket extends TimerTask {
    private String restaurantId;
    private String restaurantName;
    private List<FoodMap> foods;
    private List<FoodMap> discountFoods;
    private String status;
    private int id;


    public int descreaseOrdinaryFood(String foodName){
        for(int i=0;i<foods.size();i++){
            FoodMap food = foods.get(i);
            if(food.getFoodName().equals(foodName)){
                if(food.getCount() == 1){
                    foods.remove(i);
                    if(foods.isEmpty() && discountFoods.isEmpty()) {
                        restaurantId = "null";
                        restaurantName = "null";
                    }
                }
                else
                    food.decreaseCount();
                return 0;
            }
        }
        return -1;
    }

    public int decreasePartyFood(String foodName){
        for(int i=0;i<discountFoods.size();i++){
            FoodMap food = discountFoods.get(i);
            if(food.getFoodName().equals(foodName)){
                if(food.getCount() == 1){
                    discountFoods.remove(i);
                    if(foods.isEmpty() && discountFoods.isEmpty()) {
                        restaurantId = "null";
                        restaurantName = "null";
                    }
                }
                else
                    food.decreaseCount();
                return 0;
            }
        }
        return -1;
    }


    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public List<FoodMap> getDiscountFoods() {
        return discountFoods;
    }

    public void setDiscountFoods(List<FoodMap> discountFoods) {
        this.discountFoods = discountFoods;
    }

    public FoodMap findOrdinadyFood(String food){
        for(int i=0;i<foods.size();i++){
            if(foods.get(i).getFoodName().equals(food)){
                return foods.get(i);
            }
        }
        return null;
    }

    public void addOrdinaryFoodToCart(String food, int price,int count){
        boolean find = false;
        for(int i=0;i<foods.size();i++){
            if(foods.get(i).getFoodName().equals(food)){
                foods.get(i).addToCount(count);
                find =true;
            }
        }
        if(!find){
            foods.add(new FoodMap(food,price,count));
        }
    }

    public FoodMap findDiscountFood(String food){
        for(int i=0;i<discountFoods.size();i++){
            if(discountFoods.get(i).getFoodName().equals(food)){
                return discountFoods.get(i);
            }
        }
        return null;
    }

    public void addDiscountFoodToCart(String food,int price, int count){
        boolean find = false;
        for(int i=0;i<discountFoods.size();i++){
            if(discountFoods.get(i).getFoodName().equals(food)){
                discountFoods.get(i).addToCount(count);
                find =true;
            }
        }
        if(!find){
            discountFoods.add(new FoodMap(food,price,count));
        }
    }

    private int remainingTime;
    private Timer timer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Basket(int _id) {
        foods = new ArrayList<>();
        discountFoods = new ArrayList<>();
        id = _id;
        restaurantId ="null";
    }

    public void emptyDiscountFoods(){
        discountFoods = new ArrayList<>();
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String _restaurantId) {
        this.restaurantId = _restaurantId;
    }

    public List<FoodMap> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodMap> foods) {
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
            Manager.getInstance().changebasketStatusInDb(Manager.getInstance().getClient().getUsername(),id,"Done");
            timer.cancel();
        }
        else {
            remainingTime = remainingTime - 1;

        }
    }
}
