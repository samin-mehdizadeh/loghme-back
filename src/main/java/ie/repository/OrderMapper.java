package ie.repository;

import ie.domain.Manager;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class OrderMapper {
    private static OrderMapper instance;

    public static OrderMapper getInstance(){
        if(instance == null){
            instance = new OrderMapper();
        }
        return instance;
    }
    public void insertOrder(Basket basket){
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            connection.setAutoCommit(false);
            PreparedStatement pStatement = connection.prepareStatement(
                    "insert into DiscountOrders(username, foodName, restaurantId, foodCount, orderId, foodPrice, status) values (?, ?, ?, ?, ?, ?, ?)");
            List<FoodMap> partyFood = basket.getDiscountFoods();
            List<FoodMap> ordinaryFood = basket.getFoods();
            for (int i = 0; i < partyFood.size(); i++) {
                FoodMap food = partyFood.get(i);
                pStatement.setString(1, Manager.getInstance().getClient().getUsername());
                pStatement.setString(2, food.getFoodName());
                pStatement.setString(3, basket.getRestaurantId());
                pStatement.setInt(4, food.getCount());
                pStatement.setInt(5, basket.getId());
                pStatement.setInt(6, food.getFoodPrice());
                pStatement.setString(7, basket.getStatus());
                pStatement.addBatch();
            }
            pStatement.executeBatch();
            pStatement.close();
            pStatement = connection.prepareStatement(
                   "insert into OrdinaryOrders(username, foodName, restaurantId, foodCount, orderId, foodPrice, status) values (?, ?, ?, ?, ?, ?, ?)");
            for (int i = 0; i < ordinaryFood.size(); i++) {
                FoodMap food = ordinaryFood.get(i);
                pStatement.setString(1, Manager.getInstance().getClient().getUsername());
                pStatement.setString(2, food.getFoodName());
                pStatement.setString(3, basket.getRestaurantId());
                pStatement.setInt(4, food.getCount());
                pStatement.setInt(5, basket.getId());
                pStatement.setInt(6, food.getFoodPrice());
                pStatement.setString(7, basket.getStatus());
                pStatement.addBatch();
            }

            pStatement.executeBatch();
            pStatement.close();
            connection.commit();
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }

   public List<Basket> getPreviousOrdersFromDb() {
       List<Basket> baskets=new ArrayList<>();
       try {
           Connection connection = ConnectionPool.getInstance().getConnection();
           Statement ordinaryStmt = connection.createStatement();
           Statement discountStmt = connection.createStatement();
           ResultSet ordinaryResult= ordinaryStmt.executeQuery("SELECT * FROM OrdinaryOrders where username = \"" + Manager.getInstance().getClient().getUsername()
                   + "\" ORDER BY orderId");
           int number = -1;
           Basket basket = null;
           while (ordinaryResult.next()) {
               int orderId = ordinaryResult.getInt("orderId");
               if(orderId!=number){
                   number = orderId;
                   String rid = ordinaryResult.getString("restaurantId");
                   basket = new Basket(number);
                   baskets.add(basket);
                   basket.setRestaurantId(rid);
                   basket.setRestaurantName(Manager.getInstance().getRestaurantById(rid).getName());
                   basket.setStatus(ordinaryResult.getString("status"));
                   ResultSet discountResult = discountStmt.executeQuery("SELECT * FROM DiscountOrders where username = \"" + Manager.getInstance().getClient().getUsername()
                           + "\" and orderId = "+number);
                   while(discountResult.next()){
                       String food = discountResult.getString("foodName");
                       int price =  discountResult.getInt("foodPrice");
                       int count = discountResult.getInt("foodCount");
                       basket.addDiscountFoodToCart(food,price,count);
                   }
                   discountResult.close();
               }
               else{
                   String food = ordinaryResult.getString("foodName");
                   int price =  ordinaryResult.getInt("foodPrice");
                   int count = ordinaryResult.getInt("foodCount");
                   if(basket!=null) {
                       basket.addOrdinaryFoodToCart(food,price,count);
                   }
               }
           }
            ordinaryResult.close();
            ordinaryStmt.close();
            discountStmt.close();
            connection.close();
       } catch (SQLException e) {
          System.out.println(e.getMessage());
       }

       return baskets;
   }

    public void changeBasketStatusInDb(String username,int orderId,String status){
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            connection.setAutoCommit(false);
            Statement stmt = connection.createStatement();
            String sql = "UPDATE DiscountOrders SET status =\""+ status+ "\" where username = \""+username + "\" and orderId = "+orderId;
            stmt.addBatch(sql);
            sql = "UPDATE OrdinaryOrders SET status =\""+ status+ "\" where username = \""+username + "\" and orderId = "+orderId;
            stmt.addBatch(sql);
            stmt.executeBatch();
            stmt.close();
            connection.commit();
            connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }
}
