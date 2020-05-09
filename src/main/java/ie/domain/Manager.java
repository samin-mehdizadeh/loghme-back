package ie.domain;
import ie.repository.*;
import ie.service.serviceDTO.DiscountFoodProps;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    private static Manager instance;
    private Client client;

    public void setClient(Client _client){
        this.client = _client;
        String username = client.getUsername();
        client.setCredit(UserMapper.getInstance().getCredit(username));
        setClientBasket();
    }


    public Client getClient(){
        return client;
    }

    public void removeCurrentUserBasketInDb(){
        CurrentBasketMapper.getInstance().removeCurrentUserBasket();
    }

    public void decreaseFoodCountInDb(String foodname,String type){
        CurrentBasketMapper.getInstance().decreaseFoodCount(client.getUsername(),foodname,type);
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

    public void addCurrentFoodToDb(String foodName,int foodCount,int foodPrice,String restaurantId,String type){
        CurrentBasketMapper.getInstance().insertFood(client.getUsername(),foodName,foodCount,foodPrice,restaurantId,type);
    }

    public void setClientBasket(){
        client.setCurrentBasket(CurrentBasketMapper.getInstance().getCurrentUserBasket());
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
        Food f = FoodMapper.getInstance().getFood(restaurantId,foodName);
        return f;
    }

    /*public int setUser(String username){
        int result = 1;
        try {
            Client selectedClient = UserMapper.getInstance().selectUser(username,"",0);
            if(selectedClient == null){
                result = 0;
            }
            else{
                setClient(selectedClient);
                int price = OrderMapper.getInstance().recoverOrdersAndGetAdditionalPrice(username);
                addCredit(price);
                insertPreviousOrdersFromDb(username);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }*/

    public int getMaxOrderId(String username){
        return OrderMapper.getInstance().getMaxOrderId(username);
    }

    public int addCredit(int credit){
        client.setCredit(client.getCredit() + credit);
        UserMapper.getInstance().addCredit(client.getUsername(),credit);
        return client.getCredit();
    }

    public void recoverOrdersInDb(){
        OrderMapper.getInstance().recoverOrdersInDb();
    }


    public void changebasketStatusInDb(String username,int orderId,String status){
        OrderMapper.getInstance().changeBasketStatusInDb(username,orderId,status);
    }



    public void addRestaurants(List<Restaurant> _restaurants,String type){
        RestaurantMapper.getInstance().insertRestaurants(_restaurants,type);

    }

    public List<Restaurant> getSearchedRestaurants(String restaurant, String food,int page,int limit){
        List<Restaurant> searchedRestaurants = new ArrayList<>();
        try {
            searchedRestaurants = RestaurantMapper.getInstance().selectRestaurant(restaurant,food,page,limit);

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return searchedRestaurants;


    }

    public void insertBasketToDB(Basket basket){
        OrderMapper.getInstance().insertOrder(basket);
    }

    public void insertPreviousOrdersFromDb(String username){
        List<Basket> baskets= OrderMapper.getInstance().getPreviousOrdersFromDb(username);
        if(!baskets.isEmpty()){
            client.addBasketsFromDb(baskets);
        }
    }

    public List<Restaurant> getRestaurants(int page,int limit){
        List<Restaurant> dbRestaurants = new ArrayList<>();
        try {
            dbRestaurants = RestaurantMapper.getInstance().getRestaurantsFromDB(page,limit);
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

    public void emptyPartyOrders(){
        CurrentBasketMapper.getInstance().emptyPartyOrders();
    }

    public int finalizeOrder(){ return client.finalizeOrder(); }
    public boolean basketIsEmpty(){ return client.basketIsEmpty();}
    public List<FoodMap> getClientOrdinaryCart() {return client.getCurrentBasket().getFoods();}
    public List<FoodMap> getClientPartyCart() {return client.getCurrentBasket().getDiscountFoods();}
    public String getClientRestaurantId() { return client.getCurrentBasket().getRestaurantId(); }
    public int calculatePrice() { return client.calculatePrice(); }
    //public void assignNewBasket() { client.assignNewBasket();}
    //public void assignNewDiscountFoods() {client.assignNewDiscountFoods();}
}
