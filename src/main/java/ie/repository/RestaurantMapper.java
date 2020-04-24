package ie.repository;
import ie.domain.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestaurantMapper {
    List<Restaurant> restaurants;
    private static RestaurantMapper instance;
    private RestaurantMapper() {
    }

    public static RestaurantMapper getInstance(){
        if(instance == null){
            instance = new RestaurantMapper();
        }
        return instance;
    }

    public void insertRestaurants(List<Restaurant> restaurants,String type) {
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            connection.setAutoCommit(false);

            if (type == "party") {
                Statement stmt = connection.createStatement();
                String sql = "TRUNCATE TABLE PartyFood";
                stmt.executeUpdate(sql);
                stmt.close();
            }

            PreparedStatement pStatement = connection.prepareStatement(
                    "insert ignore into Restaurant (id, name, location_x, location_y, logo) values (?, ?, ?, ?, ?)");
            for (Restaurant restaurant : restaurants) {
                pStatement.setString(1, restaurant.getId());
                pStatement.setString(2, restaurant.getName());
                pStatement.setDouble(3, restaurant.getLocation().get("x"));
                pStatement.setDouble(4, restaurant.getLocation().get("y"));
                pStatement.setString(5, restaurant.getLogo());
                pStatement.addBatch();
            }
            pStatement.executeBatch();
            pStatement.close();

           if (type == "ordinary") {
                PreparedStatement pStatementForOrdinaryFood = connection.prepareStatement(
                        "insert ignore into OrdinaryFood (restaurantId, foodName, description, popularity, price, image) values (?, ?, ?, ?, ?, ?)");
                for(Restaurant restaurant: restaurants){
                    for (Food food : restaurant.getMenu()) {
                        pStatementForOrdinaryFood.setString(1, restaurant.getId());
                        pStatementForOrdinaryFood.setString(2, food.getName());
                        pStatementForOrdinaryFood.setString(3, food.getDescription());
                        pStatementForOrdinaryFood.setDouble(4, food.getPopularity());
                        pStatementForOrdinaryFood.setInt(5, food.getPrice());
                        pStatementForOrdinaryFood.setString(6, food.getImage());
                        pStatementForOrdinaryFood.addBatch();
                    }
                }
                pStatementForOrdinaryFood.executeBatch();
                pStatementForOrdinaryFood.close();
           }
           else {
                PreparedStatement pStatementForPartyFood = connection.prepareStatement(
                        "insert ignore into PartyFood (restaurantId, foodName, description, popularity,price,oldPrice,count, image) values (? , ? , ?, ?, ?, ?, ?, ?)");
                for(Restaurant restaurant: restaurants) {
                    for (DiscountFood food : restaurant.getFoodParty()) {
                        pStatementForPartyFood.setString(1, restaurant.getId());
                        pStatementForPartyFood.setString(2, food.getName());
                        pStatementForPartyFood.setString(3, food.getDescription());
                        pStatementForPartyFood.setDouble(4, food.getPopularity());
                        pStatementForPartyFood.setInt(5, food.getPrice());
                        pStatementForPartyFood.setInt(6, food.getOldPrice());
                        pStatementForPartyFood.setInt(7, food.getCount());
                        pStatementForPartyFood.setString(8, food.getImage());
                        pStatementForPartyFood.addBatch();
                    }
                }
                pStatementForPartyFood.executeBatch();
                pStatementForPartyFood.close();
           }
           connection.commit();
           connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Restaurant> getRestaurantsFromDB(int page,int limit) throws SQLException, IOException {
        restaurants = new ArrayList<>();
        int offset = (page-1)*limit;
        Connection connection = ConnectionPool.getInstance().getConnection();
        Statement restaurantsStatement = connection.createStatement();
        Statement OrdinaryFoodStatement = connection.createStatement();
        ResultSet restaurantsResult = restaurantsStatement.executeQuery(
                "select * from Restaurant limit "+limit+" offset "+offset);
        while(restaurantsResult.next()) {
            Restaurant Ri = new Restaurant();
            Ri.setId(restaurantsResult.getString("id"));
            Ri.setName(restaurantsResult.getString("name"));
            HashMap <String,Double> location = new HashMap<>();
            location.put("x", restaurantsResult.getDouble("location_x"));
            location.put("y", restaurantsResult.getDouble("location_y"));
            Ri.setLocation(location);
            Ri.setLogo(restaurantsResult.getString("logo"));
            ResultSet OrdinaryFoodResult = OrdinaryFoodStatement.executeQuery(
                    "select * from OrdinaryFood where OrdinaryFood.restaurantId = \""+restaurantsResult.getString("id")+"\"");
            while(OrdinaryFoodResult.next()) {
                Food food = new Food();
                food.setName(OrdinaryFoodResult.getString("foodName"));
                food.setDescription(OrdinaryFoodResult.getString("description"));
                food.setPopularity(OrdinaryFoodResult.getDouble("popularity"));
                food.setPrice(OrdinaryFoodResult.getInt("price"));
                food.setImage(OrdinaryFoodResult.getString("image"));
                Ri.addFood(food);
            }
            OrdinaryFoodResult.close();
            restaurants.add(Ri);
        }
        restaurantsResult.close();
        restaurantsStatement.close();
        OrdinaryFoodStatement.close();
        connection.close();
        return restaurants;
    }


    public List<Restaurant> selectRestaurant(String restaurant, String food,int page,int limit) throws SQLException{
        List<Restaurant> searchedRestaurants = new ArrayList<>();
        int offset = (page-1)*limit;
        Connection connection = ConnectionPool.getInstance().getConnection();
        Statement statement = connection.createStatement();;
        ResultSet result = null;
        if(food.equals("")){
            result = statement.executeQuery("select id, name, logo from Restaurant" + " where Restaurant.name LIKE " + "'%" + restaurant + "%'"+ "limit "+limit+ " offset "+offset);

        }

        else if(restaurant.equals("")){
            result = statement.executeQuery("select distinct Restaurant.id, Restaurant.name, Restaurant.logo from Restaurant,OrdinaryFood" + " where Restaurant.id = OrdinaryFood.restaurantId" +
                    " and OrdinaryFood.foodName LIKE " + "'%" + food + "%'"+ "limit "+limit+ " offset "+offset);

        }

        else{
            result = statement.executeQuery("select distinct Restaurant.id, Restaurant.name, Restaurant.logo from Restaurant,OrdinaryFood" +
                    " where Restaurant.name LIKE " + "'%" + restaurant + "%'"+
                    " and OrdinaryFood.foodName LIKE " + "'%" + food + "%'"+
                    " and OrdinaryFood.restaurantId = Restaurant.id"
                    + "limit "+limit+ " offset "+offset);
        }

        while(result.next()) {
            Restaurant Ri = new Restaurant();
            Ri.setId(result.getString("id"));
            Ri.setName(result.getString("name"));
            Ri.setLogo(result.getString("logo"));
            searchedRestaurants.add(Ri);
        }
        statement.close();
        connection.close();
        return searchedRestaurants;

    }

    public Restaurant getRestaurantById(String id) throws SQLException, IOException {
        Restaurant restaurant = new Restaurant();
        Connection connection = ConnectionPool.getInstance().getConnection();
        Statement restaurantIdStatement = connection.createStatement();
        Statement OrdinaryFoodStatement = connection.createStatement();
        ResultSet restaurantIdResult = restaurantIdStatement.executeQuery(
                "select * from Restaurant where id = \"" + id + "\"");

        boolean empty = true;
        while( restaurantIdResult.next() ) {
            restaurant.setId(restaurantIdResult.getString("id"));
            restaurant.setName(restaurantIdResult.getString("name"));
            HashMap <String,Double> location = new HashMap<>();
            location.put("x", restaurantIdResult.getDouble("location_x"));
            location.put("y", restaurantIdResult.getDouble("location_y"));
            restaurant.setLocation(location);
            restaurant.setLogo(restaurantIdResult.getString("logo"));
            ResultSet OrdinaryFoodResult = OrdinaryFoodStatement.executeQuery(
                    "select * from OrdinaryFood where OrdinaryFood.restaurantId = \""+id+"\"");
            while(OrdinaryFoodResult.next()) {
                Food food = new Food();
                food.setName(OrdinaryFoodResult.getString("foodName"));
                food.setDescription(OrdinaryFoodResult.getString("description"));
                food.setPopularity(OrdinaryFoodResult.getDouble("popularity"));
                food.setPrice(OrdinaryFoodResult.getInt("price"));
                food.setImage(OrdinaryFoodResult.getString("image"));
                restaurant.addFood(food);
            }
            OrdinaryFoodResult.close();
            empty = false;
        }

        if( empty ) {
            restaurantIdStatement.close();
            OrdinaryFoodStatement.close();
            restaurantIdResult.close();
            connection.close();
            return null;
        }
        restaurantIdStatement.close();
        OrdinaryFoodStatement.close();
        restaurantIdResult.close();
        connection.close();
        return restaurant;

    }

}
