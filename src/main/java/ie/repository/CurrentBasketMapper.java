package ie.repository;

import ie.domain.Basket;
import ie.domain.Food;
import ie.domain.FoodMap;
import ie.domain.Manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.List;

public class CurrentBasketMapper {

    private static CurrentBasketMapper instance;

    public static CurrentBasketMapper getInstance(){
        if(instance == null){
            instance = new CurrentBasketMapper();
        }
        return instance;
    }

    public void removeCurrentUserBasket(){
        try{
            String username = Manager.getInstance().getClient().getUsername();
            Connection connection = ConnectionPool.getInstance().getConnection();
            connection.setAutoCommit(false);
            Statement remove=connection.createStatement();
            remove.addBatch("DELETE from CurrentOrdinary where username =\""+ username + "\" ");
            remove.addBatch("DELETE from CurrentDiscount where username =\""+ username + "\"");
            remove.executeBatch();
            connection.commit();
            remove.close();
            connection.close();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void emptyPartyOrders(){
        try{
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("TRUNCATE TABLE CurrentDiscount");
            stmt.close();
            connection.close();

        }catch(Exception e){
            System.out.println("63");
            System.out.println(e.getMessage());
        }

    }

    public int getFoodCount(String username,String foodname,String type){
        int count =0;
        String tableName="";
        if(type.equals("ordinary")){
            tableName = "CurrentOrdinary";
        }
        else{
            tableName = "CurrentDiscount";
        }
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet Result = stmt.executeQuery("select foodCount from " + tableName + " WHERE username = \"" + username + "\" and foodName=\"" + foodname + "\"");
            while (Result.next()){
                count=Result.getInt("foodCount");
            }
            stmt.close();
            Result.close();
            connection.close();


        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return count;
    }

    public void decreaseFoodCount(String username,String foodname,String type){
        try {
            String tableName="";
            if(type.equals("ordinary")){
                tableName = "CurrentOrdinary";
            }
            else{
                tableName = "CurrentDiscount";
            }
            int count =  getFoodCount(username,foodname,type);
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            if(count>1)
                stmt.executeUpdate("UPDATE   " + tableName + " SET foodCount = foodCount-1 WHERE username = \"" + username + "\" and foodName=\"" + foodname + "\"");
            else
                stmt.executeUpdate("DELETE from "+tableName+" where username =\""+ username + "\" and foodName=\"" + foodname + "\"");

            stmt.close();
            connection.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public Basket getCurrentUserBasket(){
        String username = Manager.getInstance().getClient().getUsername();
        int id =OrderMapper.getInstance().getMaxOrderId(username);
        Basket current=new Basket(id+1);
        boolean findRestaurantId=false;
        try{
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement ofood = connection.createStatement();
            Statement dfood =connection.createStatement();
            ResultSet oResult = ofood.executeQuery("select * from CurrentOrdinary where username = \""+ username +"\"");
            while (oResult.next()){
                if(!findRestaurantId) {
                    findRestaurantId = true;
                    String RestaurantId = oResult.getString("restaurantId");
                    current.setRestaurantId(RestaurantId);
                    current.setRestaurantName(Manager.getInstance().getRestaurantById(RestaurantId).getName());
                }
                String foodName = oResult.getString("foodName");
                int price =  oResult.getInt("foodPrice");
                int count = oResult.getInt("foodCount");
                current.addOrdinaryFoodToCart(foodName,price,count);
            }
            ofood.close();
            oResult.close();
            ResultSet dResult = dfood.executeQuery("select * from CurrentDiscount where username = \""+ username +"\"");
            while (dResult.next()){
                if(!findRestaurantId) {
                    findRestaurantId = true;
                    String RestaurantId = dResult.getString("restaurantId");
                    current.setRestaurantId(RestaurantId);
                    current.setRestaurantName(Manager.getInstance().getRestaurantById(RestaurantId).getName());
                }
                String foodName = dResult.getString("foodName");
                int price =  dResult.getInt("foodPrice");
                int count = dResult.getInt("foodCount");
                current.addDiscountFoodToCart(foodName,price,count);
            }
            dfood.close();
            dResult.close();
            connection.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return current;

    }

    public boolean isRecordeExistInBasket(String username,String foodname){
        boolean exist=false;
        try{
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement checko = connection.createStatement();
            Statement checkd = connection.createStatement();
            ResultSet checkoResult = checko.executeQuery("select * from CurrentOrdinary where username = \""+ username +"\" and foodname= \""+foodname+"\"");
            while(checkoResult.next()) {
                exist=true;
            }
            checko.close();
            checkoResult.close();
            ResultSet checkdResult = checkd.executeQuery("select * from CurrentDiscount where username = \""+ username +"\" and foodname= \""+foodname+"\"");
            while(checkdResult.next()) {
                exist=true;
            }
            checkd.close();
            checkdResult.close();
        }catch(SQLException e){
            System.out.println("23");
            System.out.println(e.getMessage());
        }

        return exist;
    }

    public void insertFood(String username,String foodName,int foodCount,int foodPrice,String restaurantId,String type){
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            boolean foodExist = isRecordeExistInBasket(username,foodName);
            String tableName="";

            if(type.equals("ordinary")){
                tableName = "CurrentOrdinary";
            }
            else{
                tableName = "CurrentDiscount";
            }

            if(!foodExist) {
                    connection.setAutoCommit(false);
                    PreparedStatement pStatement = connection.prepareStatement(
                            "insert into  " +tableName +" (username, foodName, restaurantId, foodCount, foodPrice) values (?, ?, ?, ?, ?)");
                    pStatement.setString(1, username);
                    pStatement.setString(2, foodName);
                    pStatement.setString(3, restaurantId);
                    pStatement.setInt(4, foodCount);
                    pStatement.setInt(5, foodPrice);
                    pStatement.addBatch();
                    pStatement.executeBatch();
                    pStatement.close();
                    connection.commit();
            }
            else {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("UPDATE   " +tableName +" SET foodCount = foodCount+"+foodCount+" WHERE username = \""+username+"\" and foodName=\""+foodName+"\"");
                stmt.close();
            }
            connection.close();
        }catch(SQLException e){
            System.out.println("33");
            System.out.println(e.getMessage());
        }
    }

}
