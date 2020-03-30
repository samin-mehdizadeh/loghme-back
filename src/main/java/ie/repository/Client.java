package ie.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Client {
    private String name;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;
    private int credit;
    private int id;
    private List<Basket> baskets;
    private Basket currentBasket;

    public Basket getBasketById(String id){
        for(int i = 0; i < baskets.size(); i++){
            if(baskets.get(i).getId().equals(id))
                return baskets.get(i);
        }
        return null;
    }

    public Basket getCurrentBasket() {
        return currentBasket;
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

    public Client() {
        baskets = new ArrayList<Basket>();
        id = 0;
        currentBasket = new Basket(Integer.toString(id));
    }

    public boolean hasNoRestaurant(){
        if(currentBasket.getRestaurant() == null)
            return true;
        return false;
    }


    public int addPartyToCart(DiscountFood discountFood,Restaurant restaurant){
        if(hasNoRestaurant()){
            if(discountFood.getCount() > 0) {
                currentBasket.setRestaurant(restaurant);
                currentBasket.addDiscountFoodToCart(discountFood, 1);
                return 0;
            }
            return -2;
        }

        if (!restaurant.getId().equals(currentBasket.getRestaurant().getId()))
            return -1;
        if(currentBasket.getDiscountFoods().containsKey(discountFood)){
            if(currentBasket.getDiscountFoods().get(discountFood) == discountFood.getCount())
                return -3;
            else {
                currentBasket.addDiscountFoodToCart(discountFood,
                        currentBasket.getDiscountFoods().get(discountFood) + 1);
                return 0;
            }
        }
        else {
            currentBasket.addDiscountFoodToCart(discountFood, 1);
            return 0;
        }
    }

    public int addOrdinaryToCart(Food food,Restaurant restaurant){
        if(hasNoRestaurant()){
            currentBasket.setRestaurant(restaurant);
            currentBasket.addOrdinaryFoodToCart(food,1);
            return 0;
        }
        else {
            if (!restaurant.getId().equals(currentBasket.getRestaurant().getId())) {
                return -1;
            } else {
                if (currentBasket.getFoods().containsKey(food)) {
                    currentBasket.addOrdinaryFoodToCart(food, currentBasket.getFoods().get(food) + 1);
                } else {
                    currentBasket.addOrdinaryFoodToCart(food,1);
                }
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
        for (Map.Entry mapElement : currentBasket.getFoods().entrySet()) {
            Food key = (Food) mapElement.getKey();
            int value = (int) mapElement.getValue();
            price +=(key.getPrice())*value;

        }

        for (Map.Entry mapElement : currentBasket.getDiscountFoods().entrySet()) {
            DiscountFood key = (DiscountFood) mapElement.getKey();
            int value = (int) mapElement.getValue();
            price +=(key.getPrice())*value;

        }

        return price;
    }

    public int checkFinalizeOrder(){
        int  price = calculatePrice();
        if(currentBasket.getFoods().isEmpty() && currentBasket.getDiscountFoods().isEmpty()){
            currentBasket = null;
            return -2;
        }
        else if(price>credit) {
            currentBasket = null;
            return -1;
        }
        else {
            baskets.add(currentBasket);
            for (Map.Entry mapElement : currentBasket.getDiscountFoods().entrySet()) {
                DiscountFood key = (DiscountFood) mapElement.getKey();
                int value = (int) mapElement.getValue();
                key.decreaseCount(value);
            }
        }
        id += 1;
        credit = credit - price;
        return 0;
    }


    public boolean basketIsEmpty(){
        if(currentBasket.getFoods().isEmpty() && currentBasket.getDiscountFoods().isEmpty())
            return true;
        else
            return false;
    }

    public void assignNewBasket(){
        currentBasket = new Basket(Integer.toString(id));
    }

    public void assignNewDiscountFoods(){
        currentBasket.emptyDiscountFoods();
    }
}

