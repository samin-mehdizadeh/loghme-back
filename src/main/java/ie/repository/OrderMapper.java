package ie.repository;
import ie.domain.*;
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
                    "replace into DiscountOrders(username, foodName, restaurantId, foodCount, orderId, foodPrice, status) values (?, ?, ?, ?, ?, ?, ?)");
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
                   "replace into OrdinaryOrders(username, foodName, restaurantId, foodCount, orderId, foodPrice, status) values (?, ?, ?, ?, ?, ?, ?)");
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

   public List<Basket> getPreviousOrdersFromDb(String username) {
       List<Basket> baskets=new ArrayList<>();
       try {
           Connection connection = ConnectionPool.getInstance().getConnection();
           Statement ordinaryStmt = connection.createStatement();
           Statement discountStmt = connection.createStatement();
           Statement maxOrdinary =connection.createStatement();
           Statement maxParty = connection.createStatement();
           int maxo=-1;
           int maxp=-1;
           int max=-1;
           ResultSet rmaxOrdinary = maxOrdinary.executeQuery("select max(OrderId) as max from OrdinaryOrders where username = \""+ username +"\"");
           while(rmaxOrdinary.next()){
               maxo = rmaxOrdinary.getInt("max");
           }
           rmaxOrdinary.close();
           maxOrdinary.close();
           ResultSet rmaxParty = maxParty.executeQuery("select max(OrderId) as max from DiscountOrders where username = \""+ username +"\"");
           while(rmaxParty.next()){
               maxp = rmaxParty.getInt("max");
           }
           rmaxParty.close();
           maxParty.close();
           if(maxo>maxp)
               max=maxo;
           else
               max=maxp;
           if (max != -1) {
               for(int i=0;i<=max;i++){
                   Basket basket=new Basket(i);
                   ResultSet ordinaryResult= ordinaryStmt.executeQuery("SELECT * FROM OrdinaryOrders where username = \"" + username
                           + "\" and orderId = "+i);
                   boolean flag = true;
                   while (ordinaryResult.next()){
                       if(flag){
                           String rid = ordinaryResult.getString("restaurantId");
                           basket.setRestaurantId(rid);
                           basket.setRestaurantName(Manager.getInstance().getRestaurantById(rid).getName());
                           basket.setStatus(ordinaryResult.getString("status"));
                           flag = false;
                       }
                       String food = ordinaryResult.getString("foodName");
                       int price =  ordinaryResult.getInt("foodPrice");
                       int count = ordinaryResult.getInt("foodCount");
                       basket.addOrdinaryFoodToCart(food,price,count);
                   }
                   ordinaryResult.close();
                   ResultSet discountResult = discountStmt.executeQuery("SELECT * FROM DiscountOrders where username = \"" + username
                           + "\" and orderId = "+i);
                   while (discountResult.next()){
                       if(flag){
                           String rid = discountResult.getString("restaurantId");
                           basket.setRestaurantId(rid);
                           basket.setRestaurantName(Manager.getInstance().getRestaurantById(rid).getName());
                           basket.setStatus(discountResult.getString("status"));
                           flag = false;
                       }
                       String food = discountResult.getString("foodName");
                       int price =  discountResult.getInt("foodPrice");
                       int count = discountResult.getInt("foodCount");
                       basket.addDiscountFoodToCart(food,price,count);
                   }
                   discountResult.close();
                   if(!flag)
                       baskets.add(basket);

               }

           }
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

    public int recoverOrdersAndGetAdditionalPrice(String username){
        int price = 0;
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement ordinaryStmt = connection.createStatement();
            Statement discountStmt = connection.createStatement();
            ResultSet ordinaryResult = ordinaryStmt.executeQuery("SELECT * FROM OrdinaryOrders where username = \"" + username
                    + "\" and status!=\"Done\"");
            while (ordinaryResult.next()){
                price += ordinaryResult.getInt("foodCount")*ordinaryResult.getInt("foodPrice");
            }
            ordinaryResult.close();
            ordinaryStmt.close();
            ResultSet discountResult = discountStmt.executeQuery("SELECT * FROM DiscountOrders where username = \"" + username
                    + "\" and status!=\"Done\"");
            while (discountResult.next()){
                price += discountResult.getInt("foodCount")*discountResult.getInt("foodPrice");
            }
            discountResult.close();
            discountStmt.close();
            connection.setAutoCommit(false);
            Statement remove=connection.createStatement();
            remove.addBatch("DELETE from OrdinaryOrders where username =\""+ username + "\" and status!=\"Done\"");
            remove.addBatch("DELETE from DiscountOrders where username =\""+ username + "\" and status!=\"Done\"");
            remove.executeBatch();
            connection.commit();
            remove.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return price;
    }

    public int getMaxOrderId(String username){
        int max=-1;
        try{
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement maxOrdinary =connection.createStatement();
            Statement maxParty = connection.createStatement();
            int maxo=-1;
            int maxp=-1;
            ResultSet rmaxOrdinary = maxOrdinary.executeQuery("select max(OrderId) as max from OrdinaryOrders where username = \""+ username +"\"");
            while(rmaxOrdinary.next()){
                maxo = rmaxOrdinary.getInt("max");
            }
            rmaxOrdinary.close();
            maxOrdinary.close();
            ResultSet rmaxParty = maxParty.executeQuery("select max(OrderId) as max from DiscountOrders where username = \""+ username +"\"");
            while(rmaxParty.next()){
                maxp = rmaxParty.getInt("max");
            }
            rmaxParty.close();
            maxParty.close();
            if(maxo>maxp)
                max=maxo;
            else
                max=maxp;
            connection.close();
        }

        catch (SQLException e){
            e.printStackTrace();
        }
        return max;

    }
}
