package ie.domain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Restaurant {
    private String id;
    private String name;
    private  HashMap <String,Double> location;
    private String logo;
    private List< Food > menu;
    private List<DiscountFood> foodParty;

    public String getId(){
        return id;
    }

    public Restaurant(){
        menu = new ArrayList<Food>();
        foodParty = new ArrayList<DiscountFood>();
    }

    public List<DiscountFood> getFoodParty() {
        return foodParty;
    }

    public void setFoodParty(List<DiscountFood> _foodParty) {
        foodParty.clear();
        for(DiscountFood food: _foodParty)
            foodParty.add(food);
    }

    public void setId(String _id){
        this.id = _id;
    }

    public String getName(){
        return name;
    }

    public void setName(String _name){
        this.name = _name;
    }

    public HashMap <String,Double> getLocation(){
        return location;
    }

    public void setLocation(HashMap <String,Double> _location){
        this.location = _location;
    }

    public String getLogo(){
        return logo;
    }

    public void setLogo(String _logo){
        this.logo = _logo;
    }

    public List< Food > getMenu(){
        return menu;
    }

    public void setMenu(List< Food > _menu){
        menu.clear();
        for(Food food : _menu)
            menu.add(food);
    }

    public void addFood( Food _food) throws IOException {
        boolean repetitiveFood = false;
        for(int i = 0; i < menu.size(); i++){
            if(menu.get(i).getName().equals(_food.getName())){
                repetitiveFood = true;
            }
        }
        if(!repetitiveFood)
            menu.add(_food);
        else
            System.out.println("Food already exists in menu!");
    }

    public Food findOrdinaryFood(String food){
        for(int i = 0; i < menu.size(); i++){
            if(menu.get(i).getName().equals(food))
                return menu.get(i);
        }
        return null;
    }

    public DiscountFood findPartyFood(String food){
        for(int i = 0; i < foodParty.size(); i++){
            if(foodParty.get(i).getName().equals(food))
                return foodParty.get(i);
        }
        return null;
    }

    public Double FoodPopularityAverage(){
        Double sum = 0.0;
        for(int i = 0; i < menu.size(); i++){
            sum += menu.get(i).getPopularity();
        }
        return (Double) (sum / menu.size());
    }

    public void clearFoodParty(){
        foodParty.clear();
    }

    public Double Distance(){
        return Math.sqrt(Math.pow(location.get("x"),2.0) + Math.pow(location.get("y"),2.0));
    }


}

