package ie.repository;
import ie.service.serviceDTO.DiscountFoodProps;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ie.domain.*;
public class PartyFoodMapper {
    private static PartyFoodMapper instance;

    public static PartyFoodMapper getInstance(){
        if(instance == null){
            instance = new PartyFoodMapper();
        }
        return instance;
    }

    public void updateFoodcount(String foodName,String restaurantId,int newCount){
       try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            String sql = "UPDATE PartyFood SET count ="+ newCount+ " where restaurantId = \""+restaurantId + "\" and foodName = \""+foodName+"\"";
            System.out.println(sql);
            stmt.executeUpdate(sql);
            stmt.close();
            connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public List<DiscountFoodProps> getDiscounts(){
        List<DiscountFoodProps> discountFoods = new ArrayList<>();
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet discountsRes = stmt.executeQuery(
                    "select Restaurant.id,Restaurant.name,PartyFood.foodName,PartyFood.description,PartyFood.popularity,PartyFood.price," +
                            "PartyFood.oldPrice,PartyFood.count,PartyFood.image" +
                            " from Restaurant,PartyFood where Restaurant.id = PartyFood.restaurantId");

            while (discountsRes.next()){
                DiscountFoodProps df = new DiscountFoodProps();
                DiscountFood d = new DiscountFood();
                d.setPrice(discountsRes.getInt("PartyFood.price"));
                d.setOldPrice(discountsRes.getInt("PartyFood.oldPrice"));
                d.setCount(discountsRes.getInt("PartyFood.count"));
                d.setDescription(discountsRes.getString("PartyFood.description"));
                d.setImage(discountsRes.getString("PartyFood.image"));
                d.setName(discountsRes.getString("PartyFood.foodName"));
                d.setPopularity(discountsRes.getDouble("PartyFood.popularity"));
                String restaurantName = discountsRes.getString("Restaurant.name");
                String restaurantID = discountsRes.getString("Restaurant.id");
                df.setDiscountFood(d);
                df.setOwnerRestaurantName(restaurantName);
                df.setOwnerRestaurantID(restaurantID);
                discountFoods.add(df);
            }
            stmt.close();
            discountsRes.close();
            connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return discountFoods;
    }

    public DiscountFood getDiscount(String id, String food){
        DiscountFood discount = new DiscountFood();
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet discountsRes = stmt.executeQuery(
                    "select * " +
                            " from PartyFood where PartyFood.restaurantId = \"" + id + "\"" +
                    " and PartyFood.foodName = \"" + food + "\"");

            boolean empty = true;

            while (discountsRes.next()){
                discount.setPrice(discountsRes.getInt("price"));
                discount.setOldPrice(discountsRes.getInt("oldPrice"));
                discount.setCount(discountsRes.getInt("count"));
                discount.setDescription(discountsRes.getString("description"));
                discount.setImage(discountsRes.getString("image"));
                discount.setName(discountsRes.getString("foodName"));
                discount.setPopularity(discountsRes.getDouble("popularity"));
                empty = false;
            }
            if(empty == true){
                stmt.close();
                discountsRes.close();
                connection.close();
                return null;
            }
            stmt.close();
            discountsRes.close();
            connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return discount;
    }
}

