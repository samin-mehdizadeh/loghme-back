package ie.repository;
import ie.domain.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FoodMapper {
    private static FoodMapper instance;

    public static FoodMapper getInstance(){
        if(instance == null){
            instance = new FoodMapper();
        }
        return instance;
    }


    public Food getFood(String id, String food){
        Food f = new Food();
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet foodRes = stmt.executeQuery(
                    "select * " +
                            " from OrdinaryFood where OrdinaryFood.restaurantId = \"" + id + "\"" +
                            " and OrdinaryFood.foodName = \"" + food + "\"");

            boolean empty = true;

            while (foodRes.next()){
                f.setPrice(foodRes.getInt("price"));
                f.setDescription(foodRes.getString("description"));
                f.setImage(foodRes.getString("image"));
                f.setName(foodRes.getString("foodName"));
                f.setPopularity(foodRes.getDouble("popularity"));
                empty = false;
            }
            if(empty == true){
                stmt.close();
                foodRes.close();
                connection.close();
                return null;
            }
            stmt.close();
            foodRes.close();
            connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return f;
    }
}
