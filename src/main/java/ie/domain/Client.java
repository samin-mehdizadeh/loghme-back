package ie.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.domain.Manager;
import ie.domain.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private String name;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;
    private int credit;
   // private int id;
    private String username;
    private String password;

    public Client() {
        baskets = new ArrayList<Basket>();
        //id = 0;
        //currentBasket = new Basket(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private List<Basket> baskets;
    private Basket currentBasket;

   /* public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Basket getBasketById(int id){
        for(int i = 0; i < baskets.size(); i++){
            if(baskets.get(i).getId() == id)
                return baskets.get(i);
        }
        return null;
    }

    public Basket getCurrentBasket() {
        return currentBasket;
    }

    public void addBasketsFromDb(List<Basket> _baskets){
        for(int i=0;i<_baskets.size();i++) {
            addBasket(_baskets.get(i));
        }
    }

    public void setCurrentBasket(Basket currentBasket) {
        this.currentBasket = currentBasket;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public List<Basket> getBaskets() {
        return baskets;
    }

    public void setBaskets(List<Basket> baskets) {
        this.baskets = baskets;
    }


    public boolean hasNoRestaurant(){
        if(currentBasket.getRestaurantId() == "null")
            return true;
        return false;
    }


    public int addPartyToCart(String discountFood,String restaurantId, int count){
        Restaurant restaurant = Manager.getInstance().getRestaurantById(restaurantId);
        DiscountFood dFood = Manager.getInstance().getPartyFood(restaurant.getId(),discountFood);
        if(hasNoRestaurant()){
            if(dFood.getCount() >= count) {
                currentBasket.setRestaurantId(restaurantId);
                currentBasket.setRestaurantName(restaurant.getName());
                currentBasket.addDiscountFoodToCart(discountFood,dFood.getPrice(),count);
                Manager.getInstance().addCurrentFoodToDb(discountFood,count,dFood.getPrice(),restaurantId,"party");
                return 0;
            }
            return -2;
        }

        if (!restaurantId.equals(currentBasket.getRestaurantId()))
            return -1;
        FoodMap food = currentBasket.findDiscountFood(discountFood);
        if(food!=null){
            if(food.getCount() + (count - 1) >= dFood.getCount()){
                System.out.println(food.getCount());
                System.out.println(dFood.getCount());
                System.out.println(count);
                return -3;
            }
            else {
                currentBasket.addDiscountFoodToCart(discountFood, dFood.getPrice(),count);
                Manager.getInstance().addCurrentFoodToDb(discountFood,count,dFood.getPrice(),restaurantId,"party");
                return 0;
            }
        }
        else {
            if(dFood.getCount() >= count) {
                currentBasket.addDiscountFoodToCart(discountFood,dFood.getPrice(),count);
                Manager.getInstance().addCurrentFoodToDb(discountFood,count,dFood.getPrice(),restaurantId,"party");
                return 0;
            }
            return -2;

        }
    }

    public int addOrdinaryToCart(String food,String restaurantId,int count){
        Restaurant restaurant = Manager.getInstance().getRestaurantById(restaurantId);
        Food oFood = Manager.getInstance().findOrdinaryFood(restaurantId,food);
        if(hasNoRestaurant()){
            currentBasket.setRestaurantId(restaurantId);
            currentBasket.setRestaurantName(restaurant.getName());
            currentBasket.addOrdinaryFoodToCart(food,oFood.getPrice(),count);
            Manager.getInstance().addCurrentFoodToDb(food,count,oFood.getPrice(),restaurantId,"ordinary");
            return 0;
        }
        else {
            if (!restaurantId.equals(currentBasket.getRestaurantId())) {
                System.out.println(currentBasket.getRestaurantId());
                return -1;
            } else {
                currentBasket.addOrdinaryFoodToCart(food, oFood.getPrice(),count);
                Manager.getInstance().addCurrentFoodToDb(food,count,oFood.getPrice(),restaurantId,"ordinary");
                return 0;
            }
        }
    }


    public void showBasket()throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(currentBasket.getFoods());
        System.out.println(json);
    }

    public int calculatePrice(){
        int price = 0;
        List<FoodMap> ordinary = currentBasket.getFoods();
        List<FoodMap> party =  currentBasket.getDiscountFoods();
        for(int i=0;i<ordinary.size();i++){
            int count = ordinary.get(i).getCount();
            int fPrice = ordinary.get(i).getFoodPrice();
            price += count * fPrice;

        }

        for(int i=0;i<party.size();i++){
            int count = party.get(i).getCount();
            int fPrice = party.get(i).getFoodPrice();
            price += count * fPrice;

        }


        return price;
    }

    public int decreaseOrdinaryFood(String foodName){
        Manager.getInstance().decreaseFoodCountInDb(foodName,"ordinary");
        return currentBasket.descreaseOrdinaryFood(foodName);
    }

    public int decreasePartyFood(String foodName){
        Manager.getInstance().decreaseFoodCountInDb(foodName,"party");
        return currentBasket.decreasePartyFood(foodName);
    }

    public int finalizeOrder(){
        int  price = calculatePrice();
        if(currentBasket.getFoods().isEmpty() && currentBasket.getDiscountFoods().isEmpty()){
            return -2;
        }
        else if(price>credit) {
            return -1;
        }
        else {
            addBasket(currentBasket);
            List<FoodMap> party =  currentBasket.getDiscountFoods();
            Restaurant restaurant = Manager.getInstance().getRestaurantById(currentBasket.getRestaurantId());
            for(int i=0;i<party.size();i++){
                DiscountFood key = Manager.getInstance().getPartyFood(restaurant.getId(),party.get(i).getFoodName());
                int value = party.get(i).getCount();
                Manager.getInstance().updatePartyCount(party.get(i).getFoodName(),restaurant.getId(),key.getCount() - value);
                key.decreaseCount(value);
            }
        }

        Manager.getInstance().addCredit(-price);
        DeliveryFinder deliveryChecker = new DeliveryFinder(currentBasket);
        deliveryChecker.search();
        Manager.getInstance().insertBasketToDB(currentBasket);
        Manager.getInstance().removeCurrentUserBasketInDb();
       // Manager.getInstance().getClient().assignNewBasket();
        return 0;
    }

    public void addBasket(Basket b){
        baskets.add(b);
    }


    public boolean basketIsEmpty(){
        if(currentBasket.getFoods().isEmpty() && currentBasket.getDiscountFoods().isEmpty())
            return true;
        else
            return false;
    }

   /* public void assignNewBasket(){
        currentBasket = new Basket(id);
    }

    public void assignNewDiscountFoods(){
        currentBasket.emptyDiscountFoods();
    }*/
}

