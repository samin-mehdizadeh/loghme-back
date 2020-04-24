package ie.domain;
import ie.repository.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    private static Manager instance;
    //private List<Restaurant> restaurants;
    //private List<Client> _users;
    private Client client;

    public void setClient(Client _client){
        this.client = _client;
    }

    public Client getClient(){
        return client;
    }

    private Manager() {
        //restaurants = new ArrayList<Restaurant>();
        //_users = new ArrayList<Client>();
    }

    public void updatePartyCount(String food, String id, int value){
        PartyFoodMapper.getInstance().updateFoodcount(food,id,value);
    }

    public static Manager getInstance() {
        if(instance == null){
            instance = new Manager();
        }
        return instance;
    }

    public int addUser(String name,String lastName,String phone,String email,String username,String password){
        int result = 1;
        try {
            Client newClient = new Client();
            newClient.setCredit(0);
            newClient.setEmailAddress(email);
            newClient.setUsername(username);
            newClient.setPassword(password);
            newClient.setName(name);
            newClient.setLastName(lastName);
            newClient.setPhoneNumber(phone);
            result = UserMapper.getInstance().insertUser(newClient);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;

    }

    public Food findOrdinaryFood(String restaurantId, String foodName){
        Food f = new Food();
        f = FoodMapper.getInstance().getFood(restaurantId,foodName);
        return f;
    }

    public int setUser(String username,String password){
        int result = 1;
        try {
            Client selectedClient = UserMapper.getInstance().selectUser(username,password);
            if(selectedClient == null){
                result = 0;
            }
            else{
                client = selectedClient;
                this.insertPreviousOrdersFromDb();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int addCredit(int credit){
        client.setCredit(client.getCredit() + credit);
        try {
            UserMapper.getInstance().addCredit(client,client.getCredit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client.getCredit();
    }



    public void changebasketStatusInDb(String username,int orderId,String status){
        OrderMapper.getInstance().changeBasketStatusInDb(username,orderId,status);
    }



    public void addRestaurants(List<Restaurant> _restaurants,String type){
        RestaurantMapper.getInstance().insertRestaurants(_restaurants,type);
        /*try {
            List<Restaurant> dbRestaurants = RestaurantMapper.getInstance().getRestaurantsFromDB();
            for(Restaurant restaurant : dbRestaurants){
                this.restaurants.add(restaurant);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }*/

    }

    public List<Restaurant> getSearchedRestaurants(String restaurant, String food){
        List<Restaurant> searchedRestaurants = new ArrayList<>();
        try {
            searchedRestaurants = RestaurantMapper.getInstance().selectRestaurant(restaurant,food);

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return searchedRestaurants;
    }

    public void insertBasketToDB(Basket basket){
        OrderMapper.getInstance().insertOrder(basket);
    }

    public void insertPreviousOrdersFromDb(){
        List<Basket> baskets= OrderMapper.getInstance().getPreviousOrdersFromDb();
        if(!baskets.isEmpty()){
            client.addBaskets(baskets);
        }
    }

    public List<Restaurant> getRestaurants(){
        List<Restaurant> dbRestaurants = new ArrayList<>();
        try {
            dbRestaurants = RestaurantMapper.getInstance().getRestaurantsFromDB();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return dbRestaurants;
    }



    public DiscountFood getPartyFood(String id, String food){
        DiscountFood df = new DiscountFood();
        df = PartyFoodMapper.getInstance().getDiscount(id,food);
        return df;
    }


    public Restaurant getRestaurantById(String id){
        Restaurant restaurant = new Restaurant();
        try{
            restaurant = RestaurantMapper.getInstance().getRestaurantById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return restaurant;
    }

    public List<DiscountFoodProps> getDiscounts(){
        List<DiscountFoodProps> discounts = PartyFoodMapper.getInstance().getDiscounts();
        return discounts;
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

    public int finalizeOrder(){ return client.finalizeOrder(); }
    public boolean basketIsEmpty(){ return client.basketIsEmpty();}
    public List<FoodMap> getClientOrdinaryCart() {return client.getCurrentBasket().getFoods();}
    public List<FoodMap> getClientPartyCart() {return client.getCurrentBasket().getDiscountFoods();}
    public String getClientRestaurantId() { return client.getCurrentBasket().getRestaurantId(); }
    public int calculatePrice() { return client.calculatePrice(); }
    public void assignNewBasket() { client.assignNewBasket();}
    public void assignNewDiscountFoods() {client.assignNewDiscountFoods();}
}
