package ie.repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class Manager {
    private static Manager instance;
    private List<Restaurant> restaurants;
    private Client client;

    public void setClient(Client _client){
        this.client = _client;
    }

    public Client getClient(){
        return client;
    }

    private Manager() {
        restaurants = new ArrayList<Restaurant>();
    }

    public static Manager getInstance(){
        if(instance == null){
            instance = new Manager();
        }
        return instance;
    }

    public Restaurant findRestaurant(String name){
        for(int i = 0; i < restaurants.size(); i++){
            if(restaurants.get(i).getName().equals(name)){
                return restaurants.get(i);
            }
        }
        return null;
    }

    public void setRestaurants(List<Restaurant> _restaurants){
        restaurants.clear();
        for(Restaurant restaurant : _restaurants){
            restaurants.add(restaurant);
        }
    }

    public List<Restaurant> getRestaurants(){
        return restaurants;
    }

    public void addRestaurant(Restaurant restaurant){
        restaurants.add(restaurant);
    }


    public Restaurant getRestaurantById(String id){
        for(int i = 0; i < restaurants.size(); i++){
            if(restaurants.get(i).getId().equals(id))
                return restaurants.get(i);
        }
        return null;
    }

    public int getSingleRestaurantHtml(String id){
        if(getRestaurantById(id) == null){
            return 404;
        }
        else if(getRestaurantById(id).Distance() > 170.0){
            return 403;
        }
        else{
            return 200;
        }
    }

    public int checkFinalize(){ return client.checkFinalizeOrder(); }
    public boolean basketIsEmpty(){ return client.basketIsEmpty();}
    public Map<Food, Integer> getClientOrdinaryCart() {return client.getCurrentBasket().getFoods();}
    public Map<DiscountFood, Integer> getClientPartyCart() {return client.getCurrentBasket().getDiscountFoods();}
    public Restaurant getClientRestaurant() { return client.getCurrentBasket().getRestaurant(); }
    public int calculatePrice() { return client.calculatePrice(); }
    public void assignNewBasket() { client.assignNewBasket();}
    public void assignNewDiscountFoods() {client.assignNewDiscountFoods();}
}
