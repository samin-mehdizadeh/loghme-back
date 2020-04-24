package ie.domain;

public class FoodMap {
    String foodName;
    int foodPrice;
    int count;

    public FoodMap(String name,int price,int _count){
        this.foodName = name;
        this.foodPrice = price;
        this.count = _count;

    }

    public void decreaseCount(){
        count = count-1;
    }
    public void increaseCount(){
        count = count + 1;
    }
    public void addToCount(int _count) {count = count+_count;}
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(int foodPrice) {
        this.foodPrice = foodPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
